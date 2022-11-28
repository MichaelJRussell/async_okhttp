package mjr.async_okhttp.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class HttpClient
{
    private static final String BASE_URL = "http://localhost:8080/gvl2";
    private final OkHttpClient client = new OkHttpClient();

    public <T> CompletableFuture<HttpResponse<T>> get(String url, Map<String, String> headers, Type responseType) {
        var requestBuilder = new Request.Builder()
            .url(BASE_URL + url);

        applyHeaders(requestBuilder, headers);

        var request = requestBuilder.build();
        var call = client.newCall(request);

        return performCall(call, responseType);
    }

    public <T> CompletableFuture<HttpResponse<T>> post(String url, Map<String, String> headers, Object body, Class<T> responseType)
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

    public CompletableFuture<HttpResponse<String>> postForm(
        String url, Map<String, String> headers, Map<String, String> body)
        throws Exception
    {
        Request request;

        try {
            var requestBuilder = new Request.Builder()
                .url(BASE_URL + url);
            var formBodyBuilder = new FormBody.Builder();

            body.forEach(formBodyBuilder::add);

            applyHeaders(requestBuilder, headers);
            requestBuilder.post(formBodyBuilder.build());

            request = requestBuilder.build();
        } catch (Exception ex) {
            log.error("Error building HTTP request", ex);

            throw new Exception("Error building HTTP request", ex);
        }

        var call = client.newCall(request);

        return performCall(call);
    }

    private CompletableFuture<HttpResponse<String>> performCall(Call call) {
        var callback = new StringFutureCallback();

        call.enqueue(callback);

        return callback.getFuture();
    }

    private <T> CompletableFuture<HttpResponse<T>> performCall(Call call, Class<T> responseType) {
        var callback = new ClassFutureCallback<T>(responseType);

        call.enqueue(callback);

        return callback.getFuture();
    }

    private <T> CompletableFuture<HttpResponse<T>> performCall(Call call, Type responseType) {
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
