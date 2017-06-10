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
import io.vertx.rxjava.ext.mongo.MongoClient;
import io.vertx.rxjava.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Single;

/**
 * @author ksr
 */
public class MessageRetriever extends ResourceHandler {

    private static final Logger logger = LoggerFactory.getLogger(MessagesApi.class);

    private static final String PATH_GET = "/:id";

    private static final String PARAM_ID = "id";


    public MessageRetriever(Vertx vertx, MongoClient mongoClient, Router router) {
        super(vertx, mongoClient, router);
        get(router);
    }

    private void get(Router router) {

        router.get(PATH_GET).handler(routingContext -> {
            String id = routingContext.request().getParam(PARAM_ID);

            logger.info("requested message for id {}", id);

            findOne(id).subscribe(result -> {

                responseJson(routingContext, 200, result);
            });
        });
    }

    private Single<JsonObject> findOne(String id) {
        return mongoClient.rxFindOne(MESSAGES_COLLECTION, new JsonObject().put("_id", id), null);
    }
}
