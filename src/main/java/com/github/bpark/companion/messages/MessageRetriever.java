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

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

/**
 * @author ksr
 */
public class MessageRetriever extends ResourceHandler {

    private static final Logger logger = LoggerFactory.getLogger(MessagesApi.class);

    private static final String PATH_GET = "/:id";

    private static final String PARAM_ID = "id";


    public MessageRetriever(Vertx vertx, Router router) {
        super(vertx, router);
        get(router);
    }

    private void get(Router router) {

        router.get(PATH_GET).handler(routingContext -> {
            String id = routingContext.request().getParam(PARAM_ID);

            logger.info("requested message for id {}", id);

            readMessage(id).subscribe(result -> responseJson(routingContext, 200, result));
        });
    }

    private Observable<JsonObject> readMessage(String id) {
        return vertx.sharedData().<String, String>rxGetClusterWideMap(id)
                .toObservable()
                .flatMap(map -> {
                    Observable<String> nlp = map.rxGet("nlp").toObservable();
                    Observable<String> wordnet = map.rxGet("wordnet").toObservable();
                    Observable<String> classification = map.rxGet("classification").toObservable();
                    Observable<String> sentiment = map.rxGet("sentiment").toObservable();

                    return Observable.zip(nlp, sentiment, wordnet, classification, (n, s, w, c) -> {

                        logger.info("received nlp value {}", n);
                        logger.info("received wordnet value {}", w);
                        logger.info("received classification value {}", c);
                        logger.info("received sentiment value {}", s);

                        JsonObject result = new JsonObject();
                        result.put("nlp", new JsonObject(n));
                        result.put("wordnet", new JsonObject(w));
                        result.put("classification", new JsonObject(c));
                        result.put("sentiment", new JsonObject(s));

                        return result;
                    });
                });
    }

}

