package umg.principal.botTelegram;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import umg.principal.dao.CuestionarioDao;
import umg.principal.model.Cuestionario;
import umg.principal.model.User;

import umg.principal.service.CuestionarioService;
import umg.principal.service.UserService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class botCuestionario extends TelegramLongPollingBot {

    private Map<Long, String> estadoConversacion = new HashMap<>();
    User usuarioConectado = null;
    UserService userService = new UserService();
    private final Map<Long, Integer> indicePregunta = new HashMap<>();
    private final Map<Long, String> seccionActiva = new HashMap<>();
    private final Map<String, String[]> preguntas = new HashMap<>();
    Boolean enviar = false;

    public botCuestionario() {
        // Inicializa los cuestionarios con las preguntas.
        preguntas.put("SECTION_1", new String[]{
                "1.1 ¿Cuál es tu color favorito? 🎨",
                "1.2 ¿Estas feliz? 😁",
                "1.3 ¿Qué harías si ganaras la loteria? 💵"

        });
        preguntas.put("SECTION_2", new String[]{
                "2.1 ¿Te gusta el helado? 🍦",
                "2.2 ¿Frío o calor? ☃️🫠",
                "2.3 ¿Consideras el aguacate una furta? 🥑"
        });
        preguntas.put("SECTION_3", new String[]{
                "3.1 ¿Trabajas? 💼",
                "3.2 ¿A donde te gustaría viajar? 🧳",
                "3.3 ¿Te gusta la nieve? ❄️"
        });
        preguntas.put("SECTION_4", new String[]{
                "4.1 ¿Te gustaría viajar? ✈️",
                "4.2 ¿Cuál es tu edad? ❓",
                "4.3 ¿Te gusta tu carrera? 💻"
        });
    }

    @Override
    public String getBotUsername() {
        return " @Ricochets_bot";
    }

    @Override
    public String getBotToken() {
        return "7328736251:AAGBX39q7wnYccmYN6SNMZkwZgoXqXhPLFw";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Message message = update.getMessage();
            //obtener el nombre y apellido del usuario en una variable
            String userFirstName = update.getMessage().getFrom().getFirstName();
            String userLastName = update.getMessage().getFrom().getLastName();
            String nickName = update.getMessage().getFrom().getUserName();
            long chat_id = update.getMessage().getChatId();

            try {
                String state = estadoConversacion.getOrDefault(chat_id, "");
                usuarioConectado = userService.getUserByTelegramId(chat_id);

                // Verificación inicial del usuario, si usuarioConectado es nullo, significa que no tiene registro de su id de telegram en la tabla
                if (usuarioConectado == null && state.isEmpty()) {
                    sendText(chat_id, "Bienvenido " + formatUserInfo(userFirstName, userLastName) + ", no tienes un usuario registrado en el sistema. Por favor ingresa tu correo electrónico:");
                    estadoConversacion.put(chat_id, "ESPERANDO_CORREO");
                    return;}
                else if (!messageText.equals("/menu") && !seccionActiva.containsKey(chat_id)){
                    sendText(chat_id, "Hola " + formatUserInfo(userFirstName, userLastName) + ", Envia /menu para iniciar el cuestionario 😄");}

                if (messageText.equals("/menu")) {
                    sendMenu(chat_id);
                } else if (seccionActiva.containsKey(chat_id)) {
                    manejaCuestionario(chat_id, messageText);
                }

                // Manejo del estado ESPERANDO_CORREO
                if (state.equals("ESPERANDO_CORREO")) {
                    processEmailInput(chat_id, messageText);
                    return;
                }
            }catch (Exception e){
                sendText(chat_id, "Ocurrió un error al procesar tu mensaje. Por favor intenta de nuevo.");
            }}

        else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            inicioCuestionario(chatId, callbackData);
        }
    }

    private void sendMenu(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("SELECCIONA UNA SECCION:");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Crea los botones del menú
        rows.add(crearFilaBoton("SECCION 1", "SECTION_1"));
        rows.add(crearFilaBoton("SECCION 2", "SECTION_2"));
        rows.add(crearFilaBoton("SECCION 3", "SECTION_3"));
        rows.add(crearFilaBoton("SECCION 4", "SECTION_4"));

        markup.setKeyboard(rows);
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private List<InlineKeyboardButton> crearFilaBoton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(button);
        return row;
    }

    private void inicioCuestionario(long chatId, String section) {
        seccionActiva.put(chatId, section);
        indicePregunta.put(chatId, 0);
        enviarPregunta(chatId);
    }

    private void enviarPregunta(long chatId) {
        String seccion = seccionActiva.get(chatId);
        int index = indicePregunta.get(chatId);
        String[] questions = preguntas.get(seccion);

        if (index < questions.length) {
            sendText(chatId, questions[index]);
        } else {
            sendText(chatId, "¡Muchas gracias por completar el cuestionario! 😄");
            seccionActiva.remove(chatId);
            indicePregunta.remove(chatId);
        }
    }

    private void manejaCuestionario(long chatId, String response) {
        String section = seccionActiva.get(chatId);
        int index = indicePregunta.get(chatId);

        // Solo realizar validaciones para la sección 4, índice de edad (1)
        if (section.equals("SECTION_4") && index == 1) {
            try {
                int intResponse = Integer.parseInt(response);
                if (intResponse < 5 || intResponse > 95) {
                    sendText(chatId, "Tu respuesta fue: " + response);
                    sendText(chatId, "Porfavor ingresa tu edad real 🫠");
                    return;
                }
                enviarRespuesta(section, index, response, chatId);
                sendText(chatId, "Tu respuesta de edad ha sido registrada.");
            } catch (NumberFormatException e) {
                sendText(chatId, "Por favor, ingresa un número válido para la edad.");
                return;
            }
        } else {
            sendText(chatId, "Tu respuesta fue: " + response);
        }

        siguientepregunta(chatId, response, index);
    }

    private void enviarRespuesta(String seccion, Integer preguntaid, String response, Long telegramid) {
        if (seccion.equals("SECTION_4") && preguntaid == 1) { // Solo guardar respuestas de edad en sección 4
            CuestionarioService cuestionarioService = new CuestionarioService();
            Cuestionario cuestionario = new Cuestionario();
            cuestionario.setSeccion(seccion);
            cuestionario.setPreguntaid(preguntaid);
            cuestionario.setResponse(response);
            cuestionario.setTelegramid(telegramid);

            try {
                cuestionarioService.crearUsuario(cuestionario);
                System.out.println("Respuesta registrada exitosamente.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void siguientepregunta(long chatId, String response, int index) {
        indicePregunta.put(chatId, index + 1);
        enviarPregunta(chatId);
    }


    private String formatUserInfo(String firstName, String lastName) {
        return firstName + " " + lastName + " ";
    }


    private void processEmailInput(long chat_id, String email) {
        sendText(chat_id, "Recibo su Correo: " + email);
        estadoConversacion.remove(chat_id);
        try{
            usuarioConectado = userService.getUserByEmail(email);
        } catch (Exception e) {
            System.err.println("Error al obtener el usuario por correo: " + e.getMessage());
            e.printStackTrace();
        }


        if (usuarioConectado == null) {
            sendText(chat_id, "El correo no se encuentra registrado en el sistema, por favor contacte al administrador.");
        } else {
            usuarioConectado.setTelegramid(chat_id);
            try {
                userService.updateUser(usuarioConectado);
            } catch (Exception e) {
                System.err.println("Error al actualizar el usuario: " + e.getMessage());
                e.printStackTrace();
            }

            sendText(chat_id, "Usuario actualizado con éxito!");
        }
    }

    //función para enviar mensajes
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
}