package mjr.async_okhttp.services;

import com.squareup.moshi.Types;
import lombok.extern.slf4j.Slf4j;
import mjr.async_okhttp.http.HttpClient;
import mjr.async_okhttp.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class TestService
{
    @Autowired
    HttpClient httpClient;

    public Cvi doTest() throws Exception {
        var token = getAuthToken().get();
        Map<String, String> acceptJsonHeaders = Map.of(
            "Accept", "application/json",
            "x-auth-token", token);
        Map<String, String> contentJsonHeaders = Map.of(
            "Content-Type", "application/json",
            "x-auth-token", token);
        var userInfo = getUserInfo(acceptJsonHeaders).get();
        var owner = getOwner(acceptJsonHeaders).get();
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

        var cvi = createCviDraft(cviRequest, contentJsonHeaders).get();

        log.info("CVI: " + cvi);

        return cvi;
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

    private CompletableFuture<UserInfo> getUserInfo(Map<String, String> headers) {
        return httpClient.get("/api/user/info", headers, UserInfo.class);
    }

    private CompletableFuture<Owner> getOwner(Map<String, String> headers) {
        var listMyData = Types.newParameterizedType(List.class, Owner.class);
        var response =
            httpClient.get("/api/origin", headers, listMyData);

        return response.thenApplyAsync(r -> ((List<Owner>)r).get(0));
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

        return httpClient.post("/api/premises", headers, request, Premises.class);
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

        return httpClient.post("/api/animal", headers, request, Animal.class);
    }

    private CompletableFuture<Cvi> createCviDraft(CreateCviRequest request, Map<String, String> headers) throws Exception {
        request.setType("CVI");
        request.setCarrierType("Automobile");
        request.setHeadCount(1);
        request.setInspectionDate(LocalDateTime.of(2022, 12, 1, 0, 0, 0));
        request.setPurposeOfMovement("Circus");
        request.setSpecies("Canine");

        return httpClient.post("/api/documents", headers, request, Cvi.class);
    }
}
