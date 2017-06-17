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

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Single;

import java.util.UUID;

/**
 * @author ksr
 */
public class MessageCreator extends ResourceHandler {

    private static final Logger logger = LoggerFactory.getLogger(MessageCreator.class);

    private static final String PATH_CREATE = "/";

    private static final String NLP_ADDRESS = "nlp.analyze";
    private static final String WORDNET_ADDRESS = "wordnet.analysis";
    private static final String CLASSIFICATION_ADDRESS = "classification.BASIC";

    private static final String MESSAGE_KEY = "message";

    private static final String PARAM_ID = "id";


    public MessageCreator(Vertx vertx, Router router) {
        super(vertx, router);
        create(router);
    }

    private void create(Router router) {

        router.post(PATH_CREATE).handler(routingContext -> {
            String content = routingContext.getBodyAsString();

            logger.info("requested analysis for text {}", content);

            String id = UUID.randomUUID().toString();

            storeText(id, content).flatMap(this::nlp).flatMap(nlpId -> {
                Observable<String> wordnetObservable = wordnet(id);
                Observable<String> classificationObservable = classification(id);

                return Observable.zip(wordnetObservable, classificationObservable, (w, c) -> w);

            }).subscribe(combinedId -> responseJson(routingContext, 201, new JsonObject().put(PARAM_ID, id)));

        });
    }

    private Observable<String> nlp(String content) {
        return vertx.eventBus().<String>rxSend(NLP_ADDRESS, content)
                .flatMap(msg -> Single.just(msg.body()))
                .toObservable();
    }

    private Observable<String> wordnet(String id) {

        return vertx.eventBus().<String>rxSend(WORDNET_ADDRESS, id)
                .flatMap(msg -> Single.just(msg.body()))
                .toObservable();
    }

    private Observable<String> classification(String id) {

        return vertx.eventBus().<String>rxSend(CLASSIFICATION_ADDRESS, id)
                .flatMap(msg -> Single.just(msg.body()))
                .toObservable();
    }

    private Observable<String> storeText(String id, String content) {
        logger.info("generated id: {}", id);
        return vertx.sharedData().<String, String>rxGetClusterWideMap(id)
                .flatMap(map -> map.rxPut(MESSAGE_KEY, content))
                .flatMap(a -> Single.just(id))
                .toObservable();
    }

}
