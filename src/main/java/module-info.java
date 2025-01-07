import com.guicedee.servlets.swaggerui.SwaggerUIRegistration;

open module com.guicedee.swaggerui {
    requires com.guicedee.vertx;
    requires transitive io.vertx.core;

    provides com.guicedee.vertx.spi.VertxRouterConfigurator with SwaggerUIRegistration;

}
