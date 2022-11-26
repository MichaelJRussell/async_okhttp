package mjr.async_okhttp.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreatePremisesRequest
{
    @JsonProperty("origin_id")
    private long ownerId;
    private String country;
    private String addressLine1;
    private String city;
    private String state;
    private String postalCode;
    private String county;
    private String phone;
    private boolean requireNationalId = false;
    private boolean isPrimary = false;
}
