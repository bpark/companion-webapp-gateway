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
package com.github.bpark.companion;

import com.github.bpark.companion.messages.MessagesApi;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.handler.BodyHandler;
import io.vertx.rxjava.ext.web.handler.TimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestApiVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(RestApiVerticle.class);

    private static final String MESSAGES_API = "/messages";

    @Override
    public void start() throws Exception {
        super.start();

        Router router = Router.router(vertx);

        router.route("/").handler(TimeoutHandler.create(5000));

        router.route().handler(BodyHandler.create());

        router.mountSubRouter(MESSAGES_API, MessagesApi.createNlpApi(vertx));

        vertx.createHttpServer().requestHandler(router::accept).listen(8080);

        logger.info("{} started.", RestApiVerticle.class);
    }
}
