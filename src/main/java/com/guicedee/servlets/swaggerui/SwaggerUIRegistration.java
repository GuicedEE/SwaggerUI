package com.guicedee.servlets.swaggerui;

import com.guicedee.vertx.spi.VertxRouterConfigurator;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

public class SwaggerUIRegistration implements VertxRouterConfigurator {
    @Override
    public Router builder(Router builder) {
        builder.route("/swagger/*").handler(StaticHandler
                .create("swagger")
                .setFilesReadOnly(true));
        return builder;
    }
}
