package iddqd_idkfa.transfer;

import io.restassured.RestAssured;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

public class APITest {

    @BeforeClass
    public static void setup() {
        Application.startBeforeTest();
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8000;
    }

    @Test
    public void testWelcomeMessage() {
        get("/").then()
                .assertThat()
                .statusCode(200)
                .and()
                .body(equalTo("transfer api - welcome"));
    }

    @AfterClass
    public static void teardown() {
        RestAssured.delete();
    }
}
