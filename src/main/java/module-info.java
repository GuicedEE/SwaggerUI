import com.guicedee.servlets.swaggerui.SwaggerUIRegistration;

open module com.guicedee.guicedservlets.swaggerui {
    requires guiced.vertx;
    requires io.vertx;

    provides com.guicedee.vertx.spi.VertxRouterConfigurator with SwaggerUIRegistration;

}
