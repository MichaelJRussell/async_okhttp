package mjr.async_okhttp.http;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClassFutureCallback<T> extends FutureCallbackBase<T> {
    private final Class<T> responseType;

    public ClassFutureCallback(Class<T> responseType) {
        this.responseType = responseType;
    }

    @Override
    protected T getResponseContent(String body)
    {
        return getResponseContent(body, responseType);
    }

    private T getResponseContent(String body, Class<T> responseType) {
        T responseObj = null;

        if (responseType != null && body != null && body.length() > 0) {
            try {
                var adapter = getMoshi().adapter(responseType);

                responseObj = adapter.fromJson(body);
            } catch (Exception ex) {
                log.error("Error while deserializing JSON body:\n" + body, ex);
            }
        }

        return responseObj;
    }
}
