package com.guicedee.swaggerui;

import com.guicedee.guicedservlets.openapi.OpenAPIModule;
import com.guicedee.guicedservlets.openapi.services.IGuicedSwaggerConfiguration;

public class OpenAPIConfiguration implements IGuicedSwaggerConfiguration
{
	@Override
	public OpenAPIModule config(OpenAPIModule config)
	{
	//	config.setSwaggerUiMavenGroupAndArtifact("com.guicedee.servlets.guiced-swagger-ui");
	//	config.setSwaggerUiVersion("5.10.3");
		return config;
	}
}
