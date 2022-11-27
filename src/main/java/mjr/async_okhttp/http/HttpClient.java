package mjr.async_okhttp.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.moshi.Moshi;
import lombok.extern.slf4j.Slf4j;
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

    public <T> CompletableFuture<T> get(String url, Map<String, String> headers, Type responseType) {
        var requestBuilder = new Request.Builder()
            .url(BASE_URL + url);

        applyHeaders(requestBuilder, headers);

        var request = requestBuilder.build();
        var call = client.newCall(request);

        return performCall(call, responseType);
    }

    public <T> CompletableFuture<T> post(String url, Map<String, String> headers, Object body, Class<T> responseType)
        throws Exception
    {
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

        var call = client.newCall(request);

        return performCall(call, responseType);
    }

    private <T> CompletableFuture<T> performCall(Call call, Class<T> responseType) {
        var callback = new ClassFutureCallback<T>(responseType);

        call.enqueue(callback);

        return callback.getFuture();
    }

    private <T> CompletableFuture<T> performCall(Call call, Type responseType) {
        var callback = new TypeFutureCallback<T>(responseType);

        call.enqueue(callback);

        return callback.getFuture();
    }

    private static void applyHeaders(okhttp3.Request.Builder request, Map<String, String> headers) {
        if (headers == null) {
            return;
        }

        headers.forEach(request::addHeader);
    }

    private static <T> String json(T obj) throws JsonProcessingException
    {
        var objectMapper = new ObjectMapper();

        return objectMapper.writeValueAsString(obj);
    }
}
