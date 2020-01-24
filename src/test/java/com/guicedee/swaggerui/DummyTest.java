package com.guicedee.swaggerui;


import io.undertow.Undertow;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

public class DummyTest
{
	@Test
	public void testAvailable() throws Exception
	{
		Undertow ut = com.guicedee.guicedservlets.undertow.GuicedUndertow.boot("0.0.0.0", 6000);

		HttpClient client = HttpClient.newBuilder()
				.build();
		HttpRequest request = HttpRequest.newBuilder(new URI("http://localhost:6000/swagger-ui/index.html"))
				.GET()
				.build();
		HttpResponse<String> resp =client.send(request, HttpResponse.BodyHandlers.ofString());
		if(resp.statusCode() / 100 != 2)
		{
			throw new Exception("Error Swagger UI not Available");
		}
		ut.stop();
	}
}
