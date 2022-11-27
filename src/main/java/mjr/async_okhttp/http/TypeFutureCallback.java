package mjr.async_okhttp.http;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;

@Slf4j
public class TypeFutureCallback<T> extends FutureCallbackBase<T> {
    private final Type responseType;

    public TypeFutureCallback(Type responseType) {
        this.responseType = responseType;
    }

    @Override
    protected T getResponseContent(String body)
    {
        return getResponseContent(body, responseType);
    }

    private T getResponseContent(String body, Type responseType) {
        T responseObj = null;

        if (responseType != null && body != null && body.length() > 0) {
            try {
                JsonAdapter<T> adapter = getMoshi().adapter(responseType);
                responseObj = adapter.fromJson(body);
            } catch (Exception ex) {
                log.error("Error while deserializing JSON body:\n" + body, ex);
            }
        }

        return responseObj;
    }
}
