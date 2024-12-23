package org.example;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.sql.*;
public class Main {
    public static void main(String[] args) {
        String botToken = "7867698817:AAFQhPGUh6zbI2AAa8n6LmLt3TAXrzIXLk8";
        try (TelegramBotsLongPollingApplication bot = new TelegramBotsLongPollingApplication()) {
            bot.registerBot(botToken, new SkyBot(botToken));
            System.out.println(WebScraper.getCondizioniMeteo("Padova", "25/12/2025")+" "+WebScraper.getMeteoTempMaxGiorno("Padova", "25/12/2024")+ " "+WebScraper.getMeteoTempMinGiorno("Padova", "25/12/2024")+" "+WebScraper.getMeseAnno("25/12/2024"));
            System.out.println("SkyMeteo Bot è in esecuzione");
            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}