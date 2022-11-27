package mjr.async_okhttp.http;

public class Ok<T> extends HttpResponse<T>
{
    public Ok(String url, T content)
    {
        super(200, url, content);
    }
}
