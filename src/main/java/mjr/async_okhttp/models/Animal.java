package mjr.async_okhttp.models;

import com.squareup.moshi.Json;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class Animal
{
    private long id;
    @Json(name = "origin_id")
    private long ownerId;
    private String name;
}
