package mjr.async_okhttp.http;

public class ServerError<T> extends HttpResponse<T>
{
    public ServerError(String url, T content)
    {
        super(500, url, content);
    }
}
