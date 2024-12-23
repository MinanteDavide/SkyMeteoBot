package org.example;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class SkyBot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;

    public SkyBot(String botToken) {
        telegramClient = new OkHttpTelegramClient(botToken);
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String input = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            String risposta= "\"Benvenuto! Usa /meteo [città] [data] per ottenere le previsioni meteo.\"";
            if (input.startsWith("/meteo")) {
                String[] parti = input.split(" ");
                if (parti.length == 3) {
                    String citta = parti[1];
                    String data = parti[2];
                    int avg=WebScraper.getAvgTemperatura(WebScraper.getMeteoTempMaxGiorno(citta,data), WebScraper.getMeteoTempMinGiorno(citta,data));
                    risposta = "Nel giorno "+data.substring(0,2)+" "+WebScraper.getMeseAnno(data)+" a "
                            +citta+"\nè presente una temperatura media di "+avg+"°C, una temperatura massima di "+WebScraper.getMeteoTempMaxGiorno(citta,data)
                            +"°C, e una temperatura minima di "+WebScraper.getMeteoTempMinGiorno(citta,data)+"°C.\n\nDurante la giornata la condizione atmosferica è "+WebScraper.getCondizioniMeteo(citta,data);
                } else {
                    risposta = "Formato errato. Usa /meteo [città] [data].";
                }
            }

            if (update.hasMessage() && update.getMessage().hasText()) {
                SendMessage message = SendMessage.builder()
                        .chatId(update.getMessage().getChatId())
                        .text("Risposta: " + update.getMessage().getText())
                        .build();
                message.setChatId(update.getMessage().getChatId().toString());
                message.setText(risposta);

                try {
                    telegramClient.execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}