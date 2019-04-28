package iddqd_idkfa.transfer;

import io.restassured.RestAssured;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

public class AccountTest {

    @BeforeClass
    public static void setup() {
        Application.startBeforeTest();
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8000;
    }

    @Test
    public void testfetchAll() {
        final int id = get("/accounts").then()
                .assertThat()
                .statusCode(200)
                .extract()
                .jsonPath().get("find { it.name=='Igor Bogdanoff' }.id");
        get("accounts/" + id).then()
                .assertThat()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("balance", equalTo(4567))
                .body("currency", equalTo("EUR"));
    }

    @Test
    public void testFetchFirst() {
        get("/accounts/0").then()
                .assertThat()
                .statusCode(200)
                .body("id", equalTo(0))
                .body("name", equalTo("Igor Bogdanoff"))
                .body("currency", equalTo("EUR"));
    }

    @Test
    public void testFetchNonExistent() {
        get("/accounts/123").then()
                .assertThat()
                .statusCode(404);
    }

    @Test
    public void testNewSuccess() {
        given().body("{\n" +
                "    \"name\": \"Todd Howard\",\n" +
                "    \"balance\": \"50000\",\n" +
                "    \"currency\": \"USD\"\n" +
                "}")
                .when()
                .post("accounts")
                .then()
                .assertThat()
                .statusCode(201);
    }

    @Test
    public void testNewFail() {
        given().body("{\n" +
                "    \"name\": \"Sean Murray\",\n" +
                "    \"balance\": \"1\",\n" +
                "    \"currency\": \"V-BUCKS\"\n" +
                "}")
                .when()
                .post("accounts")
                .then()
                .assertThat()
                .statusCode(400);
    }

    @Test
    public void testEditSuccess() {
        given().body("{\n" +
                "    \"balance\": \"0\",\n" +
                "    \"currency\": \"GBP\"\n" +
                "}")
                .when()
                .put("accounts/2")
                .then()
                .assertThat()
                .statusCode(200)
                .body("id", equalTo(2))
                .body("name", equalTo("Berkan Denizyaran"))
                .body("balance", equalTo(0))
                .body("currency", equalTo("GBP"));
    }

    @Test
    public void testEditFail() {
        given().body("{\n" +
                "    \"currency\": \"V-BUCKS\"\n" +
                "}")
                .when()
                .put("accounts/2")
                .then()
                .assertThat()
                .statusCode(400);
    }

    @Test
    public void testDeleteSuccess() {
        delete("/accounts/2").then()
                .assertThat()
                .statusCode(204);
        get("/accounts/2").then()
                .assertThat()
                .statusCode(404);
    }

    @Test
    public void testDeleteFail() {
        delete("/accounts/123").then()
                .assertThat()
                .statusCode(404);
    }

    @AfterClass
    public static void teardown() {
        //Application.stopAfterTest();
        RestAssured.delete();
    }
}
