package mjr.async_okhttp.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.moshi.Moshi;
import lombok.extern.slf4j.Slf4j;
import mjr.async_okhttp.models.CreatePremisesRequest;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class HttpClient
{
    private static final String BASE_URL = "http://localhost:8080/gvl2";
    private static final Moshi MOSHI = new Moshi.Builder().build();
    private final OkHttpClient client = new OkHttpClient();

    public <T> CompletableFuture<T> get(String url, Map<String, String> headers, Class<T> responseType) {
        var future = new CompletableFuture<T>();
        var requestBuilder = new Request.Builder()
            .url(BASE_URL + url);

        applyHeaders(requestBuilder, headers);

        var request = requestBuilder.build();

        client.newCall(request).enqueue(new Callback() {
             @Override
             public void onFailure(@NotNull Call call, @NotNull IOException e) {
                 future.completeExceptionally(e);
             }

             @Override
             public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                 if (!response.isSuccessful()) {
                     throw new IOException(String.format(
                         "Unexpected code from url %s: %s",
                         url,
                         response));
                 }

                 var body = response.body();
                 T responseObj = getResponseObject(body, responseType);

                 future.complete(responseObj);
             }
         });

        return future;
    }

    public <T> CompletableFuture<T> get(String url, Map<String, String> headers, Type responseType) {
        var future = new CompletableFuture<T>();
        var requestBuilder = new Request.Builder()
            .url(BASE_URL + url);

        applyHeaders(requestBuilder, headers);

        var request = requestBuilder.build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException(String.format(
                        "Unexpected code from url %s: %s",
                        url,
                        response));
                }

                var body = response.body();
                T responseObj = getResponseObject(body, responseType);

                future.complete(responseObj);
            }
        });

        return future;
    }

    public <T> CompletableFuture<T> post(String url, Map<String, String> headers, Object body, Class<T> responseType)
        throws Exception
    {
        var future = new CompletableFuture<T>();
        Request request;

        try {
            var requestBuilder = new Request.Builder()
                .url(BASE_URL + url);
            var requestBody = RequestBody.create(
                json(body), MediaType.parse("application/json"));

            applyHeaders(requestBuilder, headers);
            requestBuilder.post(requestBody);

            request = requestBuilder.build();
        } catch (Exception ex) {
            log.error("Error building HTTP request", ex);

            throw new Exception("Error building HTTP request", ex);
        }

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    var ex = new IOException(String.format(
                        "Unexpected code from url %s: %s",
                        url,
                        response));

                    log.error(response.body().string());

                    future.completeExceptionally(ex);
                }

                var body = response.body();
                T responseObj = getResponseObject(body, responseType);

                future.complete(responseObj);
            }
        });

        return future;
    }

    private static void applyHeaders(okhttp3.Request.Builder request, Map<String, String> headers) {
        if (headers == null) {
            return;
        }

        headers.forEach(request::addHeader);
    }

    private static <T> String json(T obj) throws JsonProcessingException
    {
        //var adapter = MOSHI.adapter(CreatePremisesRequest.class);
        var objectMapper = new ObjectMapper();

        //return adapter.toJson(obj);
        return objectMapper.writeValueAsString(obj);
    }

    private static <T> T getResponseObject(ResponseBody body, Class<T> responseType) throws IOException {
        T responseObj = null;

        if (responseType != null && body != null) {
            var bodyString = body.string();

            log.info("Body: " + bodyString);

            var adapter = MOSHI.adapter(responseType);

            try {
                responseObj = adapter.fromJson(bodyString);
            } catch (Exception ex) {
                log.error("Error while deserializing JSON body:\n" + bodyString, ex);
            }
        }

        return responseObj;
    }

    private static <T> T getResponseObject(ResponseBody body, Type responseType) throws IOException {
        T responseObj = null;

        if (responseType != null && body != null) {
            var bodyString = body.string();
            var adapter = MOSHI.adapter(responseType);

            try {
                responseObj = (T)adapter.fromJson(bodyString);
            } catch (IOException ex) {
                log.error("Error while deserializing JSON body:\n" + bodyString, ex);
            }
        }

        return responseObj;
    }
}
