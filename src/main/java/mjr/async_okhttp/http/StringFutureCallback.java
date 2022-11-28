package mjr.async_okhttp.http;

public class StringFutureCallback extends FutureCallbackBase<String>
{
    @Override
    protected String getResponseContent(String body)
    {
        return body;
    }
}
