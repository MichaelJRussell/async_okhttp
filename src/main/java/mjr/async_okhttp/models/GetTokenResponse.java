package mjr.async_okhttp.models;

import com.squareup.moshi.Json;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GetTokenResponse
{
    @Json(name = "access_token")
    private String authToken;
}
