import com.guicedee.servlets.swaggerui.SwaggerUIRegistration;

open module com.guicedee.guicedservlets.swaggerui {
    requires guiced.vertx;
    requires transitive io.vertx.core;

    provides com.guicedee.vertx.spi.VertxRouterConfigurator with SwaggerUIRegistration;

}
