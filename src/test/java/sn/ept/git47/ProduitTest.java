package sn.ept.git47;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

public class MagasinTest {
    private String API_URL = "http://192.168.1.10:8080/projet_matiere-1.0-SNAPSHOT/api/marques";

    @Test
    public void testGetAllMarques() {
        Response response = RestAssured.get(API_URL);

        Assert.assertEquals(response.getStatusCode(), 200);
    }
}
