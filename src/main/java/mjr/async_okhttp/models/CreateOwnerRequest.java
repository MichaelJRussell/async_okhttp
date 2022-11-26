package mjr.async_okhttp.models;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateOwnerRequest
{
    private String ownerEmail;
    private String cellPhone;
    private boolean mvlConnected;
    private String firstName;
    private String lastName;
    private String name;
    private CreatePremisesRequest primaryPremises;
}
