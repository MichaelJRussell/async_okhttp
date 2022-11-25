package mjr.async_okhttp.services;

import com.squareup.moshi.Types;
import lombok.extern.slf4j.Slf4j;
import mjr.async_okhttp.http.HttpClient;
import mjr.async_okhttp.models.GetTokenResponse;
import mjr.async_okhttp.models.Owner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class TestService
{
    @Autowired
    HttpClient httpClient;

    public void doTest() throws Exception {
        var token = getAuthToken().get();
        Map<String, String> headersForGet = Map.of(
            "Accept", "application/json",
            "x-auth-token", token);
        var owner = getOwner(headersForGet).get();

        log.info("Token: " + token);
        log.info("Owner id: " + owner.getId() + ", Prem: " + owner.getPrimaryPremises().getId());
    }

    private CompletableFuture<String> getAuthToken() throws Exception {
        var loginBody = new Object() {
            public final String username = "vet1";
            public final String password = "pass1234";
        };

        var response =
            httpClient.post("/api/gettoken", null, loginBody, GetTokenResponse.class);

        return response.thenApplyAsync(GetTokenResponse::getAuthToken);
    }

    private CompletableFuture<Owner> getOwner(Map<String, String> headers) throws Exception {
        Type listMyData = Types.newParameterizedType(List.class, Owner.class);
        var response =
            httpClient.get("/api/origin", headers, listMyData);

        return response.thenApplyAsync(r -> ((List<Owner>)r).get(0));
    }
}
