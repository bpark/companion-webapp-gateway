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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author ksr
 */
public class MessageRetriever extends ResourceHandler {

    private static final Logger logger = LoggerFactory.getLogger(MessagesApi.class);

    private static final String PATH_GET = "/:id";

    private static final String PARAM_ID = "id";

    private enum MapKeys {nlp, wordnet, classification, sentiment, dialogmanager, nlg}


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

                    List<Observable<String>> observables =
                            Stream.of(MapKeys.values())
                                    .map(value -> map.rxGet(value.name()).toObservable())
                                    .collect(Collectors.toList());

                    return Observable.zip(observables, args -> {

                        JsonObject result = new JsonObject();

                        for (int i = 0; i < args.length; i++) {
                            String arg = (String) args[i];
                            MapKeys mapKey = MapKeys.values()[i];

                            result.put(mapKey.name(), new JsonObject(arg));
                        }

                        return result;
                    });

                });
    }

}

