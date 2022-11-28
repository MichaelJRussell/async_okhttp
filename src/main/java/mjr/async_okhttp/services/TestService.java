package mjr.async_okhttp.services;

import com.squareup.moshi.Types;
import lombok.extern.slf4j.Slf4j;
import mjr.async_okhttp.http.ApiRequestException;
import mjr.async_okhttp.http.HttpClient;
import mjr.async_okhttp.http.HttpResponse;
import mjr.async_okhttp.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class TestService
{
    @Autowired
    HttpClient httpClient;

    public HttpResponse<Cvi> doTest()
    {
        try {
            return createCvi();
        } catch (ApiRequestException ex) {
            var error = ex.getError();

            return new HttpResponse<>(error.getStatusCode(), null, error);
        } catch (Exception ex) {
            log.error("Error creating CVI", ex);

            return new HttpResponse<>(500, null,
                new mjr.async_okhttp.http.Error(500, "Error creating CVI"));
        }
    }

    public String doPost() throws Exception
    {
        var loginBody = Map.of(
            "username", "vet1",
            "password", "pass1234"
        );
        var headers = Map.of(
            "Content-Type", "application/x-www-form-urlencoded",
            "Accept", "text/html,application/xhtml+xml,application/xml,*.*"
        );

        var response =
            httpClient.postForm("/login/authenticate", headers, loginBody)
                .get();

        // Status code for the form post is going to indicate success, so we need to check something else;
        // in the case of our GSP login, a failure redirects to another redirect page.
        if (response.getContent().contains("Redirecting you...")) {
            throw new ApiRequestException("Failed to log in through form");
        }

        var responseHeaders = response.getHeaders();
        String token = null;

        for (Map.Entry<String, String> entry : responseHeaders.entrySet()) {
            if (Objects.equals(entry.getKey(), "Set-Cookie")) {
                if (entry.getValue().startsWith("JSESSION")) {
                    token = entry.getValue();
                    break;
                }
            }
        }

        return token;
    }

    private HttpResponse<Cvi> createCvi() throws Exception {
        var token = getAuthToken();
        Map<String, String> acceptJsonHeaders = Map.of(
            "Accept", "application/json",
            "x-auth-token", token);
        Map<String, String> contentJsonHeaders = Map.of(
            "Content-Type", "application/json",
            "x-auth-token", token);
        var userInfoFuture = getUserInfo(acceptJsonHeaders);
        var ownerFuture = getOwner(acceptJsonHeaders);

        CompletableFuture.allOf(userInfoFuture, ownerFuture)
            .get();

        var userInfo = processResponse(userInfoFuture.get());
        var owner = processResponse(ownerFuture.get()).get(0);
        var destination = createDestination(owner.getId(), contentJsonHeaders).get();
        var animal = createAnimal(owner.getId(), contentJsonHeaders).get();
        var cviRequest = new CreateCviRequest();
        var animals = List.of(new IdEntity(animal.getId()));

        cviRequest.setVet(new IdEntity(userInfo.getVetId()));
        cviRequest.setAnimals(animals);
        cviRequest.setCarrier(new IdEntity(owner.getId()));
        cviRequest.setCarrierPremises(new IdEntity(owner.getPrimaryPremises().getId()));
        cviRequest.setConsignee(new IdEntity(owner.getId()));
        cviRequest.setConsigneePremises(new IdEntity(owner.getPrimaryPremises().getId()));
        cviRequest.setConsignor(new IdEntity(owner.getId()));
        cviRequest.setConsignorPremises(new IdEntity(owner.getPrimaryPremises().getId()));
        cviRequest.setDestination(new IdEntity(owner.getId()));
        cviRequest.setDestinationPremises(new IdEntity(destination.getId()));
        cviRequest.setOrigin(new IdEntity(owner.getId()));
        cviRequest.setOriginPremises(new IdEntity(owner.getPrimaryPremises().getId()));

        var cviResponse = createCviDraft(cviRequest, contentJsonHeaders);

        log.info("CVI: " + cviResponse.getContent());

        return cviResponse;
    }

    private String getAuthToken() throws Exception {
        var loginBody = new Object() {
            public final String username = "vet1";
            public final String password = "pass1234";
        };

        var response =
            httpClient.post("/api/gettoken", null, loginBody, GetTokenResponse.class);
        var tokenResponse = processResponse(response.get());

        return tokenResponse.getAuthToken();
    }

    private CompletableFuture<HttpResponse<UserInfo>> getUserInfo(Map<String, String> headers) {
        return httpClient.<UserInfo>get("/api/user/info", headers, Types.getRawType(UserInfo.class));
    }

    private CompletableFuture<HttpResponse<List<Owner>>> getOwner(Map<String, String> headers) {
        var listMyData = Types.newParameterizedType(List.class, Owner.class);

        return httpClient.<List<Owner>>get("/api/origin", headers, listMyData);
    }

    private CompletableFuture<Premises> createDestination(long ownerId, Map<String, String> headers) throws Exception {
        var request = new CreatePremisesRequest();

        request.setOwnerId(ownerId);
        request.setAddressLine1("314 E Fake st");
        request.setCity("Jacksonville");
        request.setPhone("5155555151");
        request.setCountry("USA");
        request.setCounty("");
        request.setState("FL");
        request.setPostalCode("32207");

        return httpClient.post("/api/premises", headers, request, Premises.class)
            .thenApply(HttpResponse::getContent);
    }

    private CompletableFuture<Animal> createAnimal(long ownerId, Map<String, String> headers) throws Exception {
        var request = new CreateAnimalRequest();

        request.setOwnerId(ownerId);
        request.setAge("12");
        request.setBreed("Alaskan Husky");
        request.setColor("Black/Tan");
        request.setDob(LocalDate.of(2020, 1, 28));
        request.setDobAccuracy("weeks");
        request.setGender("Female");
        request.setHeadCount(1);
        request.setIds("12345");
        request.setIdTypes("Microchip");
        request.setIdDates("2020-04-01");
        request.setName("Spot");
        request.setRemarks("Is a very good boy");
        request.setSpecies("Canine");

        return httpClient.post("/api/animal", headers, request, Animal.class)
            .thenApply(HttpResponse::getContent);
    }

    private HttpResponse<Cvi> createCviDraft(CreateCviRequest request, Map<String, String> headers) throws Exception {
        request.setType("CVI");
        request.setCarrierType("Automobile");
        request.setHeadCount(1);
        request.setInspectionDate(LocalDateTime.of(2022, 12, 1, 0, 0, 0));
        request.setPurposeOfMovement("Circus");
        request.setSpecies("Canine");

        return httpClient.post("/api/documents", headers, request, Cvi.class).get();
    }

    private static <T> T processResponse(HttpResponse<T> response) throws ApiRequestException
    {
        if (response.getError() != null) {
            throw new ApiRequestException("API call failed to URL" + response.getUrl(), response.getError());
        }

        return response.getContent();
    }
}
