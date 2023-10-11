package sn.ept.git47;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import sn.ept.git47.categorie.Categorie;
import sn.ept.git47.categorie.CategorieRequest;
import sn.ept.git47.categorie.CategorieResponse;
import sn.ept.git47.marque.Marque;
import sn.ept.git47.marque.MarqueRequest;
import sn.ept.git47.marque.MarqueResponse;

import java.util.ArrayList;
import java.util.List;

public class CategorieTest {
    private String CATEGORIE_ENDPOINT = "http://192.168.1.10:8080/projet_matiere-1.0-SNAPSHOT/api/categories";
    private List<Categorie> categorieList = new ArrayList<>();
    private Categorie categorie;

    @Test
    public void getAll() throws JsonProcessingException {
        Response response = RestAssured.get(CATEGORIE_ENDPOINT);
        ObjectMapper objectMapper = new ObjectMapper();
        categorieList = objectMapper.readValue(response.getBody().asString(), new TypeReference<List<Categorie>>() {});
        Assert.assertEquals(response.getStatusCode(), 200);
    }

    @Test(dependsOnMethods = { "getAll" })
    public void createNewCategorie() throws JsonProcessingException {
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(new CategorieRequest(generateUniqueMarqueName()))
                .post(CATEGORIE_ENDPOINT);
        ObjectMapper objectMapper = new ObjectMapper();
        categorie = objectMapper.readValue(response.getBody().asString(), new TypeReference<CategorieResponse>() {}).getCategorie();
        Assert.assertEquals(response.getStatusCode(), 201);
    }

    @Test(dependsOnMethods = { "createNewCategorie" })
    public void createNewCategorie_With_NameConflict() throws JsonProcessingException {
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(new CategorieRequest(categorie.getNom()))
                .post(CATEGORIE_ENDPOINT);

        Assert.assertEquals(response.getStatusCode(), 409);
    }

    @Test(dependsOnMethods = { "createNewCategorie_With_NameConflict" })
    public void updateCategorie() {
        categorie.setNom(categorie.getNom() + " Updated");
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(new CategorieRequest(categorie.getNom()))
                .put(CATEGORIE_ENDPOINT + "/" + categorie.getId());

        Assert.assertEquals(response.getStatusCode(), 200);
    }

    @Test(dependsOnMethods = { "updateCategorie" })
    public void updateCategorie_With_NameConflict() {
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(new MarqueRequest(categorie.getNom()))
                .put(CATEGORIE_ENDPOINT + "/" + categorie.getId());

        Assert.assertEquals(response.getStatusCode(), 409);
    }

    @Test(dependsOnMethods = { "updateCategorie_With_NameConflict" })
    public void deleteCategorie() {
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .delete(CATEGORIE_ENDPOINT + "/" + categorie.getId());

        Assert.assertEquals(response.getStatusCode(), 200);
    }

    @Test(dependsOnMethods = { "deleteCategorie" })
    public void updateCategorie_With_UnexistingID() {
        categorie.setNom(categorie.getNom() + " Up");
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(new MarqueRequest(categorie.getNom()))
                .put(CATEGORIE_ENDPOINT + "/" + categorie.getId());

        Assert.assertEquals(response.getStatusCode(), 404);
    }

    @Test(dependsOnMethods = { "updateCategorie_With_UnexistingID" })
    public void deleteCategorie_With_UnexistingID() {
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .delete(CATEGORIE_ENDPOINT + "/" + categorie.getId());

        Assert.assertEquals(response.getStatusCode(), 404);
    }

    @AfterClass
    public void cleanUp() {
        categorieList = null;
        categorie = null;
    }

    private boolean categorieNameExists(String nom) {
        for (Categorie categorie : categorieList) {
            if (categorie.getNom().equals(nom)) {
                return true;
            }
        }
        return false;
    }

    private String generateUniqueMarqueName() {
        String prefix = "NewCategorie";

        // Start with an index of 1
        int index = 1;

        // Generate a new name by appending the index to the prefix
        String newCategorieName = prefix + index;

        // Keep generating new names until you find one that doesn't exist in the list
        while (categorieNameExists(newCategorieName)) {
            index++;
            newCategorieName = prefix + index;
        }

        return newCategorieName;
    }
}
