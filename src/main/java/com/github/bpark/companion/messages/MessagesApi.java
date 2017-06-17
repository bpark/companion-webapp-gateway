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

import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MessagesApi {

    private static final Logger logger = LoggerFactory.getLogger(MessagesApi.class);


    public static Router createNlpApi(Vertx vertx) {

        Router router = Router.router(vertx);

        new MessagesApi(vertx, router);

        return router;
    }

    private MessagesApi(Vertx vertx, Router router) {

        new MessageCreator(vertx, router);
        new MessageRetriever(vertx, router);

    }


}
