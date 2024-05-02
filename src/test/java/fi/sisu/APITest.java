package fi.sisu;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;

import static org.junit.jupiter.api.Assertions.*;

class APITest {

    @Test
    void testGetJsonObjectFromApi() {
        JsonElement jsonElement = API.getJsonFromApi("notRealURL.com");
        assertEquals(new JsonNull(), jsonElement);
    }
}
