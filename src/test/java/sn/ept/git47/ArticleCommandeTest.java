package sn.ept.git47;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ArticleCommandeTest {
    private String ARTICLE_COMMANDE_ENDPOINT = "http://192.168.1.10:8080/projet_matiere-1.0-SNAPSHOT/api/article_commandes";

    @Test
    public void getAll() {
        Response response = RestAssured.get(ARTICLE_COMMANDE_ENDPOINT);

        Assert.assertEquals(response.getStatusCode(), 200);
    }
}
