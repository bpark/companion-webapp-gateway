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
import io.vertx.rxjava.ext.web.RoutingContext;

/**
 * @author ksr
 */
public abstract class ResourceHandler {

    protected static final String MESSAGES_COLLECTION = "messages";

    protected Vertx vertx;

    protected MongoClient mongoClient;

    protected Router router;


    public ResourceHandler(Vertx vertx, MongoClient mongoClient, Router router) {
        this.vertx = vertx;
        this.mongoClient = mongoClient;
        this.router = router;
    }

    protected void responseJson(RoutingContext routingContext, int statusCode, JsonObject jsonObject) {
        routingContext.response()
                .setStatusCode(statusCode)
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(jsonObject.encode());
    }
}
