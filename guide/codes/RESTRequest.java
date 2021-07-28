
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/***
 * NOTE: use http://rest-assured.io/ instead
 */
public class RESTRequest {
    private static final String IP = "172.25.96.238";
    private static final int PORT = 8080;
    private static final String REST_URL = "http://" + IP + ":" + PORT + "/displays/10";

    private static final int LIMIT = 100;
    private static final int DELAY_MILLIS = 50;

    public static void main(String[] args) throws IOException {

        String json;
        URL url = new URL(REST_URL);

        for (int i = 0; i < LIMIT; i++) {
            try {
                json = getJsonString("subheading", "hello " + (i + 1));
                System.out.println(json);
                sendHttpRequestAndPrintResponse(url, json);
                Thread.sleep(DELAY_MILLIS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void sendHttpRequestAndPrintResponse(URL url, String json) throws IOException {
        HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
        httpCon.setRequestMethod("POST");
        httpCon.setRequestProperty("Content-Type", "application/json");
        httpCon.setDoOutput(true);

        OutputStream out = httpCon.getOutputStream();
        out.write(json.getBytes());

        System.out.println(httpCon.getResponseCode());
        System.out.println(httpCon.getResponseMessage());
        out.close();
    }

    private static String getJsonString(String property, String value) {
        return "{\"" + property + "\":\"" + value + "\"}";
    }
}