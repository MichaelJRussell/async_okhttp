package mjr.async_okhttp.http;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class TypeFutureCallback<T> implements Callback {
    private static final Moshi MOSHI = new Moshi.Builder().build();
    private final CompletableFuture<T> future = new CompletableFuture<>();
    private final Type responseType;

    public TypeFutureCallback(Type responseType) {
        this.responseType = responseType;
    }

    @Override
    public void onFailure(@NotNull Call call, @NotNull IOException e)
    {
        future.completeExceptionally(e);
    }

    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException
    {
        if (!response.isSuccessful()) {
            var ex = new IOException(String.format(
                "Unexpected code from url %s: %s",
                call.request().url(),
                response));

            log.error(response.body().string());

            future.completeExceptionally(ex);
        }

        var body = response.body();
        T responseObj = getResponseObject(body);

        future.complete(responseObj);
    }

    public CompletableFuture<T> getFuture() {
        return this.future;
    }

    private <T> T getResponseObject(ResponseBody body) {
        T responseObj = null;

        if (responseType != null && body != null) {
            String bodyString = null;

            try {
                bodyString = body.string();

                JsonAdapter<T> adapter = MOSHI.adapter(responseType);

                responseObj = adapter.fromJson(bodyString);
            } catch (Exception ex) {
                if (bodyString == null) {
                    log.error("Error reading body text from response", ex);
                } else {
                    log.error("Error while deserializing JSON body:\n" + bodyString, ex);
                }
            }
        }

        return responseObj;
    }
}
