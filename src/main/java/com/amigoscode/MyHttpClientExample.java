package com.amigoscode;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpHeaders;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class MyHttpClientExample {
    public static void main(String[] args) {
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(20))
                .build();

        // Создаем запрос
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode.com/todos/1"))
                .build();

        // Отправляем асинхронный запрос
        CompletableFuture<HttpResponse<String>> responseFuture =
                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        // Обрабатываем ответ
        responseFuture.thenAccept(response -> {
            int statusCode = response.statusCode();
            HttpHeaders headers = response.headers();
            String responseBody = response.body();

            System.out.println("Status Code: " + statusCode);
            System.out.println("Response Headers: " + headers);
            System.out.println("Response Body: " + responseBody);
        }).join();
    }
}

