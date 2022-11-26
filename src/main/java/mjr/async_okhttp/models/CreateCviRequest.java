package mjr.async_okhttp.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
public class CreateCviRequest
{
    private String type;
    private IdEntity vet;
    private List<IdEntity> animals;
    private IdEntity carrier;
    private IdEntity carrierPremises;
    private String carrierType;
    private IdEntity consignee;
    private IdEntity consigneePremises;
    private IdEntity consignor;
    private IdEntity consignorPremises;
    private IdEntity destination;
    private IdEntity destinationPremises;
    private int headCount;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime inspectionDate;
    private IdEntity origin;
    private IdEntity originPremises;
    private String purposeOfMovement;
    private String species;
}
