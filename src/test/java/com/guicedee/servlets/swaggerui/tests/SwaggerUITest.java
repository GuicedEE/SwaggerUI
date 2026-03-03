package com.guicedee.servlets.swaggerui.tests;

import com.guicedee.client.IGuiceContext;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SwaggerUITest
{
    @Test
    public void testSwagger() throws URISyntaxException, IOException, InterruptedException
    {
        System.out.println("Starting...");
        IGuiceContext.registerModule("guiced.swagger.ui.tests");
        IGuiceContext.instance()
                     .inject();

        // Diagnostic: check classpath resolution
        System.out.println("Resource via TCCL: " + Thread.currentThread().getContextClassLoader().getResource("swagger/index.html"));
        var stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("swagger/index.html");
        System.out.println("ResourceAsStream via TCCL: " + stream);
        if (stream != null) stream.close();

        Thread.sleep(1000);

        //Do stuff
        HttpClient client = HttpClient.newBuilder()
                                      .connectTimeout(Duration.of(5, ChronoUnit.SECONDS))
                                      .build();
        HttpResponse<String> response = client.send(HttpRequest.newBuilder()
                                                       .GET()
                                                       .uri(new URI("http://localhost:8080/swagger/index.html"))
                                                       .build(),
                                            HttpResponse.BodyHandlers.ofString());
        System.out.println("Response status: " + response.statusCode());
        System.out.println("Response body (first 500): " + response.body().substring(0, Math.min(500, response.body().length())));

        assertEquals(200, response.statusCode());
    }

    public static void main(String[] args)
    {
        IGuiceContext.instance()
                     .inject();
    }
}
