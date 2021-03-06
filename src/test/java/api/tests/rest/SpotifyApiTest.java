package api.tests.rest;

import api.Config;
import api.tests.models.Error;
import api.services.AuthService;
import api.services.SpotifyApiEndpointService;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SpotifyApiTest implements Config {

    private static final String TRACK_ID = "2TpxZ7JUBn3uw46aR7qd6V";
    private static final String NOT_ID = "fmvksv;vswengvvnvnv";
    private static String token;

    @BeforeClass
    public static void init() {
        System.setProperty("client_id", "e6726f7250e24b098f312c683d93c6a8");
        System.setProperty("client_secret", "e6c5c21ba8794c64a602c272cff4344e");
        token = AuthService.getToken("playlist-read-private");
    }


    @Test
    public void verifyStatusCodeExistingTrack() {
        String token = AuthService.getToken("playlist-read-private");
        var res = SpotifyApiEndpointService.given()
                .header("Authorization", "Bearer " + token)
                .pathParam("id", TRACK_ID)
                .get(TRACK_URL)
                .then()
                .statusCode(200)
                .assertThat()
                .body(Matchers.notNullValue());
    }

    @Test
    public void verifyStatusCodeNotExistingTrack() {
        String token = AuthService.getToken("playlist-read-private");
        var res = SpotifyApiEndpointService.given()
                .header("Authorization", "Bearer " + token)
                .pathParam("id", NOT_ID)
                .get(TRACK_URL)
                .then()
                .statusCode(400);
    }

    @SneakyThrows
    @Test
    public void verifyResponseBodyStructureNotExistingTrack() {
        Error notFound = new Error(400, "invalid id");
        String token = AuthService.getToken("playlist-read-private");
        var res = SpotifyApiEndpointService.given()
                .header("Authorization", "Bearer " + token)
                .pathParam("id", NOT_ID)
                .get(TRACK_URL)
                .then()
                .extract()
                .asString();
        JSONObject errorObject = new JSONObject(res).getJSONObject("error");
        assertEquals(notFound.getStatus(), errorObject.get("status"));
        assertEquals(notFound.getMessage(), errorObject.get("message"));
    }

    @SneakyThrows
    @Test
    public void verifyResponseBodyStructureExistingTrack() {
        var res = SpotifyApiEndpointService.given()
                .header("Authorization", "Bearer " + token)
                .pathParam("id", TRACK_ID)
                .get(TRACK_URL)
                .then()
                .extract()
                .asString();
        JSONObject trackObject = new JSONObject(res);
        assertEquals(TRACK_ID, trackObject.get("id"));
    }


}
