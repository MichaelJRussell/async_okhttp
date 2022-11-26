package mjr.async_okhttp.models;

import com.squareup.moshi.Json;
import lombok.Data;

import java.util.List;

@Data
public class UserInfo {
    private long appUserId;
    private Long vetId;
    private Long clinicId;
    private String name;
    @Json(name = "userRole")
    private List<String> userRoles;
}
