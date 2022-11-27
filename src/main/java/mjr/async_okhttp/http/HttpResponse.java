package mjr.async_okhttp.http;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class HttpResponse<T>
{
    private final String url;
    private final Map<String, String> headers = new HashMap<>();
    private final int statusCode;
    private T content;
    private Error error;

    public HttpResponse(int status, String url)
    {
        this.statusCode = status;
        this.url = url;
        this.content = null;
        this.error = null;
    }

    public HttpResponse(int status, String url, T content)
    {
        this.url = url;

        if (content != null) {
            if (status >= 400 && (content.getClass() == String.class)) {
                this.error = new Error(status, content.toString());
                this.content = null;
            } else {
                this.content = content;
                this.error = null;
            }
        } else if (status >= 400) {
            this.error = new Error(status, "");
        }

        this.statusCode = status;
    }

    public HttpResponse(int status, String url, Error error)
    {
        this.url = url;
        this.statusCode = status;
        this.error = error;
    }

    public void addHeader(String key, String value)
    {
        this.headers.put(key, value);
    }

    public void removeHeader(String key)
    {
        this.headers.remove(key);
    }

    public Map<String, String> getHeaders()
    {
        return this.headers;
    }
}
