package fi.sisu;

import com.google.gson.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * Class for extracting data from the Sisu API.
 */
public class API {

    // Cache for API responses so queries to the same url don't have to be done again
    private final static HashMap<String, JsonElement> apiCache = new HashMap<>();

    /**
     * Returns a JsonObject that is extracted from the Sisu API.
     *
     * @param urlString URL as String for retrieving information from the Sisu
     * API.
     * @return JsonObject.
     */
    public static JsonElement getJsonFromApi(String urlString) {
        // Check whether this url is already cached and can be returned from memory
        if (apiCache.containsKey(urlString)) {
            return apiCache.get(urlString);
        }
        // Try to connect to the API and fetch data from it
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() != 200) {
                throw new RuntimeException("Connection failed: " + connection.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            connection.disconnect();

            // Return the data as a JsonObject
            JsonElement jsonElement = JsonParser.parseString(sb.toString());
            apiCache.put(urlString, jsonElement);
            return jsonElement;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JsonNull();
    }
}
