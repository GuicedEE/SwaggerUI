package com.guicedee.servlets.swaggerui;

import com.guicedee.client.services.config.IGuiceScanModuleInclusions;

import java.util.Set;

public class SwaggerUIInclusion implements IGuiceScanModuleInclusions<SwaggerUIInclusion> {
    @Override
    public Set<String> includeModules() {
        return Set.of("com.guicedee.swaggerui");
    }
}
