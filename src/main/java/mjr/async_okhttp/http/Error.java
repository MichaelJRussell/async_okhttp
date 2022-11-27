package mjr.async_okhttp.http;

import lombok.Getter;

@Getter
public class Error
{
    private final int statusCode;
    private final String message;

    public Error(int code, String message)
    {
        this.statusCode = code;
        this.message = message;
    }
}
