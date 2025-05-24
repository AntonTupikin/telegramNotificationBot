package com.example.telegram.bot;

import com.example.telegram.config.BotConfig;
import com.example.telegram.service.ClientService;
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

    public NotificationBot(BotConfig config, ClientService clientService) {
        this.config = config;
        this.clientService = clientService;
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
                    if (clientService.findByChatId(chatId).isEmpty()) {
                        clientService.createClient(firstName, lastName, chatId);
                    }
                    execute(new SendMessage(chatId, "Добро пожаловать, " + firstName + "!"));
                } else {
                    String name = clientService.findByChatId(chatId)
                            .map(c -> c.getName())
                            .orElse("");
                    execute(new SendMessage(chatId, "Привет, " + name + "! Хорошего дня!"));
                }
            } catch (TelegramApiException e) {
                log.error("Failed to send message", e);
            }
        }
    }

    public void sendMessage(String chatId, String text) throws TelegramApiException {
        SendMessage message = new SendMessage(chatId, text);
        execute(message);
    }
}
