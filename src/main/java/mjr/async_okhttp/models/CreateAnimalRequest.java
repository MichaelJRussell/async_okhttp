package mjr.async_okhttp.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter @Setter @ToString
public class CreateAnimalRequest
{
    @JsonProperty("origin_id")
    private long ownerId;
    private String age;
    private String breed;
    private String color;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate dob;
    private String dobAccuracy;
    private String gender;
    private int headCount;
    private String ids;
    private String idTypes;
    private String idDates;
    private boolean isGroup = false;
    private String name;
    private String remarks;
    private String species;
}
