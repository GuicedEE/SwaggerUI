import com.guicedee.client.services.config.IGuiceScanModuleInclusions;
import com.guicedee.servlets.swaggerui.SwaggerUIInclusion;
import com.guicedee.servlets.swaggerui.SwaggerUIRegistration;

open module com.guicedee.swaggerui {
    requires com.guicedee.vertx.web;

    provides com.guicedee.vertx.web.spi.VertxRouterConfigurator with SwaggerUIRegistration;
    provides IGuiceScanModuleInclusions with SwaggerUIInclusion;

}
