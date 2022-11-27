package mjr.async_okhttp.http;

import lombok.Getter;

@Getter
public class ApiRequestException extends Exception
{
    private final Error error;

    public ApiRequestException(String errorMessage)
    {
        super(errorMessage);
        this.error = null;
    }

    public ApiRequestException(String errorMessage, Error error)
    {
        super(errorMessage);
        this.error = error;
    }
}
