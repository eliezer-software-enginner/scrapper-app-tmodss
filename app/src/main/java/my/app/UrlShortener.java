package my.app;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import my.app.activities.MainActivity;
import okhttp3.*;

import java.io.IOException;

public class UrlShortener {
    final OkHttpClient client = new OkHttpClient();
    // Objeto que define que o corpo é JSON
    final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    ObjectMapper mapper = new ObjectMapper();


    public record ResponseSuaURl(
            @JsonProperty("success") String success,
            @JsonProperty("short") String shortUrl
    ){}



    public String shortUrl(String currentLink){
        //https://mineurl.com/a63971

        String urlBase_SuaUrl = "https://suaurl.com/api/ApiNewShort/request";
        String jsonBodyString = String.format("{\"link\": \"%s\", \"typelink\": 1, \"shortlinkconfId\": 1, \"userId\": 89343}",currentLink) ;

        RequestBody body = RequestBody.create(jsonBodyString, JSON);
        // 2. Cria a requisição POST
        Request request = new Request.Builder()
                .url(urlBase_SuaUrl)
                .post(body)
                .build();

        if(MainActivity.IsTestMode){
            var response = "{\"success\":true,\"short\":\"413bf8\"}";
            try {
                var data = mapper.readValue(response, ResponseSuaURl.class);
                return "https://suaurl.com/" + data.shortUrl;
            }catch (Exception e){
                e.printStackTrace();
                throw new RuntimeException(e);
            }

        }else {

            // 3. Executa a requisição (síncrona)
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    // Lança exceção se o código de status não for 2xx
                    throw new IOException("Unexpected code " + response + ". Response body: " +
                            (response.body() != null ? response.body().string() : "Empty"));
                }

                // 4. Retorna o corpo da resposta
                try (ResponseBody responseBody = response.body()) {
                    if (responseBody == null) {
                        throw new IOException("Response body is null.");
                    }
                    //mapping
                    var data = mapper.readValue(responseBody.string(), ResponseSuaURl.class);
                    return "https://suaurl.com/" + data.shortUrl;
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
