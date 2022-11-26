package mjr.async_okhttp.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter @Setter @ToString
public class Cvi
{
    private String type;
    private IdEntity vet;
    private List<Animal> animals;
    private Premises originPremises;
    private Premises destinationPremises;
}
