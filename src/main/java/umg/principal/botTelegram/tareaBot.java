package umg.principal.botTelegram;

//Importaciones para almacenar los ID en una lista
import java.util.ArrayList;
import java.util.List;

//Importaciones para utilzzar la fecha
import java.text.SimpleDateFormat;
import java.util.Date;

//Importaciones para utilizar la tasa de cambio
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class tareaBot extends TelegramLongPollingBot {

    @Override
    public String getBotUsername() {
        return "Ricochets_bot";
    }

    @Override
    public String getBotToken() {
        return "7328736251:AAGBX39q7wnYccmYN6SNMZkwZgoXqXhPLFw";
    }
    //Lista tipo Long para ID
    private List<Long> userId = new ArrayList();

    // Lista de amigos
    private List<Long> friendsID = List.of(
            1533824724L, //Mi ID xD
            1262374416L,
            6688363556L,
            6597569075L
            //Falta ID de Marito -.-
    );

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();
            String[] parts = messageText.split(" ", 2);

            System.out.println("User id: " + chatId + " Message: " + messageText);

            String userName = update.getMessage().getFrom().getFirstName();

            if (!userId.contains(chatId)) {
                userId.add(chatId);
                System.out.println("Nuevo ID almacenado: " + chatId);
            }

            switch (parts[0]) {
                case "/start":
                    sendWelcomeMessage(chatId);
                    break;
                case "/info":
                    sendInfoMessage(chatId);
                    break;
                case "/progra":
                    sendPrograMessage(chatId);
                    break;
                case "/hola":
                    sendHolaMessage(chatId, userName);
                    break;
                case "/cambio":
                    if (parts.length == 2) {
                        sendCambioMessage(chatId, parts[1]);
                    } else {
                        sendText(chatId, "Por favor, proporciona el monto a convertir. Ejemplo: /cambio 100");
                    }
                    break;
                case "/grupal":
                    if (parts.length == 2) {
                        sendGrupalMessage(parts[1]); // Enviar el mensaje a amigos
                    } else {
                        sendText(chatId, "Por favor, proporciona el mensaje que deseas enviar. Ejemplo: /grupal Tu mensaje aquí");
                    }
                    break;
            }
        }
    }

    //Mensaje de bienvenida y opciones
    private void sendWelcomeMessage(Long chatId) {
        String welcomeText = "¡Bienvenido a Ricochet Bot!\n\nAquí te presento algunos comandos:\n" +
                "/info - Información personal\n" +
                "/progra - Comentario sobre la clase\n" +
                "/hola - Saludo casual al usuario con fecha\n" +
                "/cambio - Tasa de cabio de euro a quetzal\n" +
                "/grupal - Mensaje para todo el grupo";

        sendText(chatId, welcomeText);
    }

    public void sendText(Long who, String what){
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString())
                .text(what).build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    //Método que muestra el contenido de /info
    private void sendInfoMessage(Long chatId) {
        String infoText = "INFORMACIÓN\n\n" +
                "Nombre: Jairo Leonel Molina Hernández\n" +
                "Carné: 0905-23-4651\n" +
                "Semestre: 4to. Semestre";

        //Muchas gracias por verificar el código ingeniero :D

        sendText(chatId, infoText);
    }

    //Metodo que muestra el contenido de /progra
    private void sendPrograMessage(Long chatId) {
        String prograText = "COMENTARIO\n\n" +
                "Un saludo ingeniero Ruldin, quiero agradecerle por su manera de impartir el curso de progrmación, su forma dinámica,  " +
                "su humor y su agradable forma de explicar hace que aún los temas (que para mi son) mas complejos sean entretenidos y curiosos.\n" +
                "Y respecto a este tema de los bots en Telegram estoy muy interesado; ver las infinitas posibilidades hace llamar mi atención.\n" +
                "Este bot lo hice con mis mejores intenciones y esfuerzo, quise poner en practica un poco la codificación en ingles " +
                "pues como usted dijo \"Saber ingles es muy importante\"\nMuchas gracias por leer.";

        sendText(chatId, prograText);
    }

    //Metodo que muestra el contendio de /hola
    private void sendHolaMessage(Long chatId, String userName) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE d 'de' MMMM 'de' yyyy");
        String currentDate = dateFormat.format(new Date());

        String holaText = "SALUDO Y FECHA\n\nUn saludo " + userName + " la fecha de hoy es " + currentDate + " te deseo un excelente día.";

        sendText(chatId, holaText);
    }

    //Metodo que muestra el contenido de /cambio
    private void sendCambioMessage(Long chatId, String amountText) {
        try {
            double amount = Double.parseDouble(amountText);
            double rate = getExchangeRate("EUR", "GTQ");
            double result = amount * rate;

            String responseText = String.format("TASA DE CAMBIO \n\n%.2f euros son %.2f quetzales.", amount, result);
            sendText(chatId, responseText);
        } catch (NumberFormatException e) {
            sendText(chatId, "Por favor, proporciona un número válido para convertir.");
        } catch (Exception e) {
            sendText(chatId, "Hubo un problema al obtener la tasa de cambio. Inténtalo de nuevo más tarde.");
        }
    }

    //Metodo que consulta la API de cambio
    private double getExchangeRate(String fromCurrency, String toCurrency) throws Exception {
        String apiKey = "4e32a4d142c57b6e64c3f6ce";  //
        String url = String.format("https://api.exchangerate-api.com/v4/latest/%s", fromCurrency);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JSONObject jsonResponse = new JSONObject(response.body());
        return jsonResponse.getJSONObject("rates").getDouble(toCurrency);
    }

    // Método que muestra el contenido de grupal
    private void sendGrupalMessage(String message) {
        for (Long friendID : friendsID) {

            sendText(friendID, message);
        }
    }
}