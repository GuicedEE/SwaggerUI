package com.guicedee.swaggerui.test;


import com.guicedee.client.*;
import com.guicedee.guicedservlets.undertow.GuicedUndertow;
import io.undertow.Undertow;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

public class SwaggerUIModuleTest
{

	@Test
	void configureServlets() throws Exception
	{
		/*
		RESTContext.getProviders()
		           .add(JacksonJsonProvider.class.getCanonicalName());
		*/

		//LogFactory.configureConsoleColourOutput(Level.FINE);
		Undertow undertow = GuicedUndertow.boot("0.0.0.0", 6004);

		//Do stuff
		HttpClient client = HttpClient.newBuilder()
		                              .connectTimeout(Duration.of(5, ChronoUnit.SECONDS))
		                              .build();
		HttpResponse
						response = client.send(HttpRequest.newBuilder()
										.GET()
										.uri(new URI("http://localhost:6004/rest/openapi.json"))
										.build(),
						HttpResponse.BodyHandlers.ofString());
		
		System.out.println("Response from openapi.json - " + response);
		System.out.println("Response from openapi.json - " + response.body());
		
		assertEquals(200, response.statusCode(), "Hello World Rest not available");
		String resp = response.body()
						.toString();
		
		if (!resp.contains("\"openapi\" :"))
		{
			fail("Open API Swagger not available");
		}
		
		
		response = client.send(HttpRequest.newBuilder()
										.GET()
										.uri(new URI("http://localhost:6004/rest/api_docs/"))
										.build(),
						HttpResponse.BodyHandlers.ofString());
		
		System.out.println("Response from swagger - " + response);
		System.out.println("Response from swagger - " + response.body());
		
		undertow.stop();
	}
}
