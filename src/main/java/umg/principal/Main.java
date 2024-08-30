package umg.principal;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import umg.principal.botTelegram.BotRegistra;
import umg.principal.botTelegram.botCuestionario;
import umg.principal.botTelegram.botPregunton;
import umg.principal.botTelegram.tareaBot;
import umg.principal.model.User;
import umg.principal.service.UserService;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static void PruebaInsertaUsuario() {

        //invoca el servicio que manejará la lógica de negocio.
        UserService userService=new UserService();
        User user = new User();

        // Crear un nuevo usuarioUseruser=newUser();
        user.setCarne("0905-12-12345");
        user.setNombre("Andrea Lopez");
        user.setCorreo("ALopez@gmail.com");
        user.setSeccion("A");
        user.setTelegramid(1234567890L);
        user.setActivo("Y");

        try {
            userService.createUser(user);
            System.out.println("User created successfully!");
        } catch (SQLException e) {
            System.out.println("No jalo -.-!");
            e.printStackTrace();
        }
    }

    private static void PruebaActualizacionUsuario() {
        UserService servicioUsuaio = new UserService();

        User usurioObtenido;
        //obtener información del usuario por correo electrónico
        try {
            usurioObtenido = servicioUsuaio.getUserByCarne("0905-23-4651");
            System.out.println("Retrieved User: " + usurioObtenido.getNombre());
            System.out.println("Retrieved User: " + usurioObtenido.getCorreo());
            System.out.println("Retrieved User: " + usurioObtenido.getId());

            //actualizar información del usuario
            usurioObtenido.setCarne("0905-12-12345");
            usurioObtenido.setNombre("Andrea Ascoli");
            usurioObtenido.setCorreo("anAscoli@gmail.com");
            usurioObtenido.setSeccion("A");
            usurioObtenido.setTelegramid(1234567890L);
            usurioObtenido.setActivo("Y");

            servicioUsuaio.updateUser(usurioObtenido);
            System.out.println("User updated successfully!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void PruebaEliminarUsuario() {
        UserService servicioUsuaio = new UserService();
        try {
            servicioUsuaio.deleteUserByEmail("anAscoli@gmail.com");
            System.out.println("User deleted successfully!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void explicacionUsoMap() {
        // Creación de un HashMap, que es una implementación común de Map.
        Map<String, String> phoneBook = new HashMap<>();

        // 1. Insertar elementos en el Map usando el método put.
        phoneBook.put("Alice", "123-4567");
        phoneBook.put("Bob", "987-6543");
        phoneBook.put("Charlie", "555-7890");

        // 2. Recuperar un valor a partir de una clave usando el método get.
        String bobPhoneNumber = phoneBook.get("Bob");
        System.out.println("El número de Bob es: " + bobPhoneNumber);

        // 3. Comprobar si una clave existe en el Map.
        if (phoneBook.containsKey("Alice")) {
            System.out.println("El número de Alice es: " + phoneBook.get("Alice"));
        }

        // 4. Recorrer un Map usando un bucle for-each.
        // Se pueden recorrer las claves o los valores.
        System.out.println("\nLista completa de contactos:");
        for (Map.Entry<String, String> entry : phoneBook.entrySet()) {
            System.out.println("Nombre: " + entry.getKey() + ", Número: " + entry.getValue());
        }

        // 5. Eliminar un elemento del Map.
        phoneBook.remove("Charlie");
        System.out.println("\nDespués de eliminar a Charlie, la lista es:");
        for (Map.Entry<String, String> entry : phoneBook.entrySet()) {
            System.out.println("Nombre: " + entry.getKey() + ", Número: " + entry.getValue());
        }

        // 6. Tamaño del Map (número de pares clave-valor).
        System.out.println("\nEl número total de contactos es: " + phoneBook.size());
    }

    public static void main(String[] args) {
        try {
            // Inicializa la API de bots de Telegram
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

            //Crea una instancia de tu bot
            botCuestionario botEjecutandose = new botCuestionario();

            //Registra tu bot para que empiece a recibir mensajes
            botsApi.registerBot(botEjecutandose);

            System.out.println("Funcionando...");
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());

        }
    }
}