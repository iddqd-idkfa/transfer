package iddqd_idkfa.transfer;

import io.restassured.RestAssured;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

public class TransferTest {

    @BeforeClass
    public static void setup() {
        Application.startBeforeTest();
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8000;
    }

    @Test
    public void testFetchAll() {
        final int id = get("/transfers").then()
                .assertThat()
                .statusCode(200)
                .extract()
                .jsonPath().getInt("find { it.comment=='Lip Augmentation costs' }.id");
        get("/transfers/" + id).then()
                .assertThat()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("from", equalTo(0))
                .body("to", equalTo(1))
                .body("amount", equalTo(300))
                .body("currency", equalTo("EUR"));
    }

    @Test
    public void testFetchFirst() {
        get("/transfers/0").then()
                .assertThat()
                .statusCode(200)
                .body("id", equalTo(0))
                .body("from", equalTo(0))
                .body("to", equalTo(1))
                .body("amount", equalTo(300))
                .body("currency", equalTo("EUR"))
                .body("comment", equalTo("Lip Augmentation costs"));
    }

    @Test
    public void testFetchNonExistent() {
        get("/transfers/123").then()
                .assertThat()
                .statusCode(404);
    }

    @Test
    public void testNewSuccess() {
        given().body("{\n" +
                "    \"from\": \"1\",\n" +
                "    \"to\": \"0\",\n" +
                "    \"amount\": \"1234\",\n" +
                "    \"currency\": \"USD\",\n" +
                "    \"comment\": \"Lip Augmentation cost was more than expected\"\n" +
                "}")
                .when()
                .post("transfers")
                .then()
                .assertThat()
                .statusCode(201);
    }

    @Test
    public void testNewFail() {
        given().body("{\n" +
                "    \"from\": \"1\",\n" +
                "    \"to\": \"0\",\n" +
                "    \"amount\": \"1234\",\n" +
                "    \"currency\": \"V-BUCKS\",\n" +
                "    \"comment\": \"do you accept v-bucks instead?\"\n" +
                "}")
                .when()
                .post("transfers")
                .then()
                .assertThat()
                .statusCode(400);
    }

    @Test
    public void testRunSuccess() throws InterruptedException {
        Thread.sleep(1000); //need to wait for locks to be released, if any transfers were executed previously
        put("transfers/0")
                .then()
                .assertThat()
                .body("status", equalTo("SUCCESS"));
    }

    @Test
    public void testRunFail() throws InterruptedException {
        Thread.sleep(1000); //need to wait for locks to be released, if any transfers were executed previously
        put("transfers/3")
                .then()
                .assertThat()
                .body("status", equalTo("FAIL"));
    }


    @AfterClass
    public static void teardown() {
        RestAssured.delete();
    }

}
