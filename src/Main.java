import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;
import java.io.IOException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class Main {
    //Chave para acesso API
    private static final String API_KEY = "879665c9781549b778ab4847";
    //Link com chave para taxas de câmbio
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/879665c9781549b778ab4847/latest/USD";
    //Capturar registros
    static Scanner scanner = new Scanner(System.in);
    static Gson gson = new Gson();


    public static void menu() {
        System.out.println("Seja bem-vindo/a ao Conversor de Moeda");
        System.out.println("1) Dólar =>> Peso argentino");
        System.out.println("2) Peso argentino =>> Dólar");
        System.out.println("3) Dólar =>> Real brasileiro");
        System.out.println("4) Real brasileiro =>> Dólar");
        System.out.println("5) Dólar =>> Peso colombiano");
        System.out.println("6) Peso colombiano =>> Dólar");
        System.out.println("7) Sair");
        System.out.println("Escolha uma opção válida: ");
    }


    private static int opcaoValida() {
        while (!scanner.hasNextInt()){
            System.out.println("Escolha uma opção válida.");
            scanner.next();
        }
        return scanner.nextInt();
    }


    public static void main(String[] args) {
        boolean prosseguir = true;

        while (prosseguir) {
            menu();
            int opcao = opcaoValida();

            if (opcao == 7) {
                prosseguir = false;
                System.out.println("Até logo!!");
            } else if (opcao >= 1 && opcao <= 6) {
                String[] moedas = numeroDeConversao(opcao);
                converterMoedas(moedas[0], moedas[1]);
            } else {
                System.out.println("Opção inválida. Tente novamente!");
            }
        }
        scanner.close();
    }


    private static String[] numeroDeConversao(int opcao) {
        return switch(opcao) {
            case 1 -> new String[] {"USD", "ARS"};
            case 2 -> new String[] {"ARS", "USD"};
            case 3 -> new String[] {"USD", "BRL"};
            case 4 -> new String[] {"BRL", "USD"};
            case 5 -> new String[] {"USD", "COP"};
            case 6 -> new String[] {"COP", "USD"};
            default -> throw new IllegalArgumentException("Opção inválida.");
        };
    }


    private static double taxaDeCambio(String moeda1, String moeda2) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if(response.statusCode() != 200) {
            throw new RuntimeException("Erro HTTP: " + response.statusCode());
        }

        JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonObject taxas = jsonObject.getAsJsonObject("conversion_rates");

        double taxaMoeda1 = taxas.get(moeda1).getAsDouble();
        double taxaMoeda2 = taxas.get(moeda2).getAsDouble();

        return taxaMoeda2 / taxaMoeda1;
    }


    private static double analiseValor(){
        while (!scanner.hasNextDouble()){
            System.out.println("Escolha um valor válido.");
            scanner.next();
        }
        return scanner.nextDouble();
    }


    private static void converterMoedas(String moeda1, String moeda2) {
        try {
            double taxa = taxaDeCambio(moeda1, moeda2);

            System.out.println("Digite o valor que deseja converter: ");
            double valor = analiseValor();

            double resultado = valor * taxa;
            System.out.printf("%.2f %s = %.2f %s%n", valor, moeda1, resultado, moeda2);
        } catch (Exception e){
            System.out.println("Erro ao converter a moeda: " + e.getMessage());
        }
    }
}
