import com.guicedee.servlets.swaggerui.SwaggerUIRegistration;

open module com.guicedee.swaggerui {
    requires com.guicedee.vertx.web;

    provides com.guicedee.vertx.web.spi.VertxRouterConfigurator with SwaggerUIRegistration;

}
