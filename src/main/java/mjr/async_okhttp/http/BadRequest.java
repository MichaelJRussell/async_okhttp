package mjr.async_okhttp.http;

public class BadRequest<T> extends HttpResponse<T>
{
    public BadRequest(String url, T content)
    {
        super(400, url, content);
    }
}
