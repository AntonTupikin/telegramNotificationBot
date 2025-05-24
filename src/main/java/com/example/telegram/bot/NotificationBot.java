package com.example.telegram.bot;

import com.example.telegram.config.BotConfig;
import com.example.telegram.service.ClientService;
import com.example.telegram.service.WeatherService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
@Slf4j
public class NotificationBot extends TelegramLongPollingBot {
    private final BotConfig config;
    private final ClientService clientService;
    private final WeatherService weatherService;
    private final java.util.Map<String, Boolean> waitingCity = new java.util.concurrent.ConcurrentHashMap<>();

    public NotificationBot(BotConfig config, ClientService clientService, WeatherService weatherService) {
        this.config = config;
        this.clientService = clientService;
        this.weatherService = weatherService;
    }

    @PostConstruct
    public void register() throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(this);
    }

    @Override
    public String getBotUsername() {
        return config.getUsername();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String text = update.getMessage().getText();
            try {
                if ("/start".equals(text)) {
                    String firstName = update.getMessage().getFrom().getFirstName();
                    String lastName = update.getMessage().getFrom().getLastName();
                    clientService.findByChatId(chatId)
                            .orElseGet(() -> clientService.createClient(firstName, lastName, chatId));
                    waitingCity.put(chatId, true);
                    execute(new SendMessage(chatId, "Добро пожаловать, " + firstName + "! В каком городе вы находитесь?"));
                } else if ("/weather".equals(text)) {
                    clientService.findByChatId(chatId).ifPresentOrElse(client -> {
                        if (client.getCity() == null || client.getCity().isBlank()) {
                            sendSafe(chatId, "Сначала сообщите ваш город командой /start.");
                        } else {
                            String forecast = weatherService.getTwoDayForecast(client.getCity());
                            sendSafe(chatId, forecast);
                        }
                    }, () -> sendSafe(chatId, "Сначала используйте команду /start."));
                } else if (waitingCity.getOrDefault(chatId, false)) {
                    clientService.updateCity(chatId, text);
                    waitingCity.remove(chatId);
                    sendSafe(chatId, "Город сохранен.");
                } else {
                    String name = clientService.findByChatId(chatId)
                            .map(c -> c.getName())
                            .orElse("");
                    sendSafe(chatId, "Привет, " + name + "! Хорошего дня!");
                }
            } catch (Exception e) {
                log.error("Failed to process update", e);
            }
        }
    }

    public void sendMessage(String chatId, String text) throws TelegramApiException {
        SendMessage message = new SendMessage(chatId, text);
        execute(message);
    }

    private void sendSafe(String chatId, String text) {
        try {
            sendMessage(chatId, text);
        } catch (TelegramApiException e) {
            log.error("Failed to send message", e);
        }
    }
}
