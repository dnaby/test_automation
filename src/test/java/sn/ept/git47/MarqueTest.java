package sn.ept.git47;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.*;
import sn.ept.git47.marque.Marque;
import sn.ept.git47.marque.MarqueRequest;
import sn.ept.git47.marque.MarqueResponse;

import java.util.ArrayList;
import java.util.List;

public class MarqueTest {
    private String MARQUE_ENDPOINT = "http://192.168.1.10:8080/projet_matiere-1.0-SNAPSHOT/api/marques";
    private List<Marque> marqueList = new ArrayList<>();
    private Marque marque;

    @Test
    public void getAll() throws JsonProcessingException {
        Response response = RestAssured.get(MARQUE_ENDPOINT);
        ObjectMapper objectMapper = new ObjectMapper();
        marqueList = objectMapper.readValue(response.getBody().asString(), new TypeReference<List<Marque>>() {});
        Assert.assertEquals(response.getStatusCode(), 200);
    }

    @Test(dependsOnMethods = { "getAll" })
    public void createNewMarque() throws JsonProcessingException {
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(new MarqueRequest(generateUniqueMarqueName()))
                .post(MARQUE_ENDPOINT);
        ObjectMapper objectMapper = new ObjectMapper();
        marque = objectMapper.readValue(response.getBody().asString(), new TypeReference<MarqueResponse>() {}).getMarque();
        Assert.assertEquals(response.getStatusCode(), 201);
    }

    @Test(dependsOnMethods = { "createNewMarque" })
    public void createNewMarque_With_NameConflict() throws JsonProcessingException {
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(new MarqueRequest(marque.getNom()))
                .post(MARQUE_ENDPOINT);

        Assert.assertEquals(response.getStatusCode(), 409);
    }

    @Test(dependsOnMethods = { "createNewMarque_With_NameConflict" })
    public void updateMarque() {
        marque.setNom(marque.getNom() + " Updated");
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(new MarqueRequest(marque.getNom()))
                .put(MARQUE_ENDPOINT + "/" + marque.getId());

        Assert.assertEquals(response.getStatusCode(), 200);
    }

    @Test(dependsOnMethods = { "updateMarque" })
    public void updateMarque_With_NameConflict() {
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(new MarqueRequest(marque.getNom()))
                .put(MARQUE_ENDPOINT + "/" + marque.getId());

        Assert.assertEquals(response.getStatusCode(), 409);
    }

    @Test(dependsOnMethods = { "updateMarque_With_NameConflict" })
    public void deleteMarque() {
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .delete(MARQUE_ENDPOINT + "/" + marque.getId());

        Assert.assertEquals(response.getStatusCode(), 200);
    }

    @Test(dependsOnMethods = { "deleteMarque" })
    public void updateMarque_With_UnexistingID() {
        marque.setNom(marque.getNom() + " Up");
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(new MarqueRequest(marque.getNom()))
                .put(MARQUE_ENDPOINT + "/" + marque.getId());

        Assert.assertEquals(response.getStatusCode(), 404);
    }

    @Test(dependsOnMethods = { "updateMarque_With_UnexistingID" })
    public void deleteMarque_With_UnexistingID() {
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .delete(MARQUE_ENDPOINT + "/" + marque.getId());

        Assert.assertEquals(response.getStatusCode(), 404);
    }

    @AfterClass
    public void cleanUp() {
        marqueList = null;
        marque = null;
    }

    private boolean marqueNameExists(String nom) {
        for (Marque marque : marqueList) {
            if (marque.getNom().equals(nom)) {
                return true;
            }
        }
        return false;
    }

    private String generateUniqueMarqueName() {
        String prefix = "NewMarque";

        // Start with an index of 1
        int index = 1;

        // Generate a new name by appending the index to the prefix
        String newMarqueName = prefix + index;

        // Keep generating new names until you find one that doesn't exist in the list
        while (marqueNameExists(newMarqueName)) {
            index++;
            newMarqueName = prefix + index;
        }

        return newMarqueName;
    }
}
