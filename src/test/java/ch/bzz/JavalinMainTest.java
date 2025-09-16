package ch.bzz;

import io.javalin.Javalin;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JavalinMainTest {

    private static Javalin app;
    private static String baseUrl;

    @BeforeAll
    public static void startServer() {
        app = JavalinMain.setup();
        app.start(0); // random port
        baseUrl = "http://localhost:" + app.port();
    }

    @AfterAll
    public static void stopServer() {
        app.stop();
    }

    @Test
    public void testGetBooksWithLimit() {
        HttpResponse<JsonNode> response = Unirest.get(baseUrl + "/books?limit=10").asJson();
        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().isArray());
        assertEquals(10, response.getBody().getArray().length());
    }

    @Test
    public void testGetBooksWithoutLimit() {
        HttpResponse<JsonNode> response = Unirest.get(baseUrl + "/books").asJson();
        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().isArray());
    }

    @Test
    public void testGetBooksWithInvalidLimit_Negative() {
        HttpResponse<String> response = Unirest.get(baseUrl + "/books?limit=-5").asString();
        assertEquals(400, response.getStatus());
        assertEquals("Limit must be a positive number.", response.getBody());
    }

    @Test
    public void testGetBooksWithInvalidLimit_NotANumber() {
        HttpResponse<String> response = Unirest.get(baseUrl + "/books?limit=abc").asString();
        assertEquals(400, response.getStatus());
        assertEquals("Invalid limit format. Must be a number.", response.getBody());
    }
}
