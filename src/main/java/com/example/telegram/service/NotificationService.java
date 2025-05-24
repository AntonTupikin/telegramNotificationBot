package com.example.telegram.service;

import com.example.telegram.bot.NotificationBot;
import com.example.telegram.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@Slf4j
public class NotificationService {

    private final NotificationBot bot;
    private final BotConfig config;

    public NotificationService(NotificationBot bot, BotConfig config) {
        this.bot = bot;
        this.config = config;
    }

    public void sendNotification(String text) {
        try {
            bot.sendMessage(config.getChatId(), text);
        } catch (TelegramApiException e) {
            log.error("Unable to send notification", e);
        }
    }
}
