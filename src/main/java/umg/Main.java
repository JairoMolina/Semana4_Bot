package umg;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import umg.botTelegram.primerBot;
import umg.botTelegram.tareaBot;

public class Main {

    public static void main(String[] args) {
        try {
            // Inicializa la API de bots de Telegram
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

            // Crea una instancia de tu bot
            tareaBot tareaBot = new tareaBot();

            // Registra tu bot para que empiece a recibir mensajes
            botsApi.registerBot(tareaBot);

            System.out.println("Funcionando...");
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
}