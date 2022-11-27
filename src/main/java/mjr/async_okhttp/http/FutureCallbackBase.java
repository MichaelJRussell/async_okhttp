package mjr.async_okhttp.http;

import com.squareup.moshi.Moshi;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Slf4j
public abstract class FutureCallbackBase<T> implements Callback
{
    private static final Moshi MOSHI = new Moshi.Builder().build();
    private final CompletableFuture<HttpResponse<T>> future = new CompletableFuture<>();

    @Override
    public void onFailure(@NotNull Call call, @NotNull IOException e)
    {
        future.completeExceptionally(e);
    }

    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException
    {
        String body = response.body() == null ? null : response.body().string();

        response.body().close();

        if (!response.isSuccessful()) {
            var error = String.format(
                "Unexpected code from url %s: %s",
                call.request().url(),
                response);

            log.error(error + ": " + body);
        }

        T responseObj = getResponseContent(body);
        var httpResponse = new HttpResponse<>(response.code(), response.request().url().encodedPath(), responseObj);

        future.complete(httpResponse);
    }

    public CompletableFuture<HttpResponse<T>> getFuture() {
        return this.future;
    }

    protected abstract T getResponseContent(String body);

    protected Moshi getMoshi() {
        return MOSHI;
    }
}
