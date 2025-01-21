
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONObject;

public class CurrencyConverter {

    public static void main(String[] args) {
        // Clave API
        String apiKey = "47e7cfb15420b5035450b8f0916383d2";

        // Verificar que la clave no esté vacía
        if (apiKey.isEmpty()) {
            System.out.println("Error: La clave API no puede estar vacía.");
            return;
        }

        try (Scanner scanner = new Scanner(System.in)) {
            // Pedir al usuario la moneda base y la moneda objetivo
            System.out.print("Ingrese la moneda base (por ejemplo, USD): ");
            String fromCurrency = scanner.nextLine().toUpperCase();

            System.out.print("Ingrese la moneda objetivo (por ejemplo, EUR): ");
            String toCurrency = scanner.nextLine().toUpperCase();

            System.out.print("Ingrese la cantidad a convertir: ");
            double amount = scanner.nextDouble();

            // URL de la API (solo admite EUR como base en la versión gratuita)
            String apiUrl = "http://api.exchangeratesapi.io/v1/latest?access_key=" + apiKey;

            // Conectar a la API
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Leer la respuesta de la API
            StringBuilder response;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }

            // Convertir la respuesta en JSON
            JSONObject jsonResponse = new JSONObject(response.toString());

            // Verificar si la API respondió con éxito
            if (!jsonResponse.getBoolean("success")) {
                System.out.println("Error: No se pudo obtener las tasas de cambio.");
                return;
            }

            // Extraer tasas de cambio desde "rates"
            JSONObject rates = jsonResponse.getJSONObject("rates");

            // Obtener la tasa de cambio desde EUR
            double fromRate = rates.has(fromCurrency) ? rates.getDouble(fromCurrency) : 0.0;
            double toRate = rates.has(toCurrency) ? rates.getDouble(toCurrency) : 0.0;

            // Validar que las tasas sean correctas
            if (fromRate == 0.0 || toRate == 0.0) {
                System.out.println("Error: No se encontraron tasas de cambio para las monedas ingresadas.");
                return;
            }

            // Convertir la moneda
            double convertedAmount = (amount / fromRate) * toRate;

            // Mostrar el resultado
            System.out.printf("La cantidad de %.2f %s equivale a %.2f %s.%n",
                    amount, fromCurrency, convertedAmount, toCurrency);
        } catch (Exception e) {
            System.out.println("Ocurrió un error: " + e.getMessage());
        }
    }
}
