/*
 * Copyright 2017 Kurt Sparber
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.bpark.companion.messages;

import com.github.bpark.companion.models.nlp.AnalyzedText;
import com.github.bpark.companion.models.nlp.Sentence;
import com.github.bpark.companion.models.nlp.TaggedText;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.eventbus.Message;
import io.vertx.rxjava.ext.mongo.MongoClient;
import io.vertx.rxjava.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Single;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ksr
 */
public class MessageCreator extends ResourceHandler {

    private static final Logger logger = LoggerFactory.getLogger(MessageCreator.class);

    private static final String PATH_CREATE = "/";

    private static final String NLP_ADDRESS = "nlp.analyze";
    private static final String WORDNET_ADDRESS = "wordnet.analysis";
    private static final String SENTIMENT_ADDRESS = "sentiment.calculate";

    private static final String PARAM_ID = "id";


    public MessageCreator(Vertx vertx, MongoClient mongoClient, Router router) {
        super(vertx, mongoClient, router);
        create(router);
    }

    private void create(Router router) {

        router.post(PATH_CREATE).handler(routingContext -> {
            String content = routingContext.getBodyAsString();

            logger.info("requested analysis for text {}", content);

            JsonObject analyzed = new JsonObject();

            nlp(content).subscribe(nlpMsg -> {

                String analyzedMessage = nlpMsg.body();
                analyzed.put("nlp", new JsonObject(analyzedMessage));
                AnalyzedText analyzedText = Json.decodeValue(analyzedMessage, AnalyzedText.class);

                List<Observable<Message<String>>> sentimentObservables = sentiment(analyzedText);
                List<Observable<Message<String>>> wordnetObservables = wordnet(analyzedText);

                Observable<Boolean> sentiment = zip(sentimentObservables).flatMap(results -> {
                    analyzed.put("sentiment", new JsonArray(results));
                    return Observable.just(true);
                });

                Observable<Boolean> wordnet = zip(wordnetObservables).flatMap(results -> {
                    analyzed.put("wordnet", new JsonArray(results));
                    return Observable.just(true);
                });

                Observable.zip(sentiment, wordnet, (s, w) -> true).subscribe(results -> {

                    store(analyzed).subscribe(id -> {
                        responseJson(routingContext, 201, new JsonObject().put(PARAM_ID, id));
                    });
                });

            });

        });
    }

    private Observable<Message<String>> nlp(String content) {
        return vertx.eventBus().<String>rxSend(NLP_ADDRESS, content).toObservable();
    }

    private List<Observable<Message<String>>> wordnet(AnalyzedText analyzedText) {

        List<Sentence> sentences = analyzedText.getSentences();

        return sentences.stream().map(sentence -> {
            String[] tokens = sentence.getTokens();
            String[] posTags = sentence.getPosTags();

            TaggedText taggedText = new TaggedText(tokens, posTags);

            return vertx.eventBus().<String>rxSend(WORDNET_ADDRESS, Json.encode(taggedText)).toObservable();

        }).collect(Collectors.toList());

    }

    private List<Observable<Message<String>>> sentiment(AnalyzedText analyzedText) {

        List<Sentence> sentences = analyzedText.getSentences();

        return sentences.stream().map(sentence -> {
            String[] tokens = sentence.getTokens();

            return vertx.eventBus().<String>rxSend(SENTIMENT_ADDRESS, Json.encode(tokens)).toObservable();

        }).collect(Collectors.toList());

    }

    @SuppressWarnings("unchecked")
    private Observable<List<JsonObject>> zip(List<Observable<Message<String>>> observables) {
        return Observable.zip(observables, objects -> {

            List<JsonObject> analyses = new ArrayList<>();

            for (Object object : objects) {
                analyses.add(new JsonObject(((Message<String>)object).body()));
            }

            return analyses;
        });
    }

    private Single<String> store(JsonObject content) {
        return mongoClient.rxInsert(MESSAGES_COLLECTION, content);
    }
}
