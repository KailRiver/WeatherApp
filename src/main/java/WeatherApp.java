import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WeatherApp {

    public static void main(String[] args) {
        // URL, с которого мы хотим получить данные
        String urlString = "https://api.weather.yandex.ru/graphql/query";

        // Наш ключ API
        String accessKey = "7aad0818-79c3-40db-9709-3afb02e6635f";

        // Запрос weatherByPoint
        String weatherByPoint = "{ \"query\": \"{ weatherByPoint(request: { lat: 51.4018, lon: 39.1238 }) { now { temperature humidity pressure precStrength windSpeed cloudiness } } }\" }";

        // Получение и вывод ответа
        String response = sendAndReceiveRequest(urlString, accessKey, weatherByPoint);
        if (response != null) {
            parseAndPrintWeather(response);
        }
    }

    private static String sendAndReceiveRequest(String urlString, String accessKey, String jsonInputString) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("X-Yandex-Weather-Key", accessKey);
            connection.setDoOutput(true);

            // Отправка запроса
            try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Чтение ответа
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) { // 200
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response.toString();
        }
    } else {
        System.out.println("Request failed with response code: " + responseCode);
    }
} catch (Exception e) {
        e.printStackTrace();
        } finally {
                if (connection != null) {
        connection.disconnect();
            }
                    }
                    return null;
                    }

private static void parseAndPrintWeather(String response) {
    JSONObject jsonResponse = new JSONObject(response);
    JSONObject now = jsonResponse.getJSONObject("data").getJSONObject("weatherByPoint").getJSONObject("now");

    // Получение текущей даты и времени
    LocalDateTime currentDateTime = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    String formattedDateTime = currentDateTime.format(formatter);

    // Вывод даты и времени
    System.out.println("\nCurrent Date and Time: " + formattedDateTime);

    // Извлечение данных о погоде
    double temperature = now.getDouble("temperature");
    double humidity = now.getDouble("humidity");
    double pressure = now.getDouble("pressure");
    String precStrength = now.getString("precStrength");
    double windSpeed = now.getDouble("windSpeed");
    String cloudiness = now.getString("cloudiness"); // Изменено на getString

    // Вывод данных о погоде
    System.out.println("Current Weather:");
    System.out.println("Temperature: " + temperature + "°C");
    System.out.println("Humidity: " + humidity + "%");
    System.out.println("Pressure: " + pressure + " hPa");
    System.out.println("Precipitation Strength: " + precStrength);
    System.out.println("Wind Speed: " + windSpeed + " m/s");
    System.out.println("Cloudiness: " + cloudiness);


}
}
