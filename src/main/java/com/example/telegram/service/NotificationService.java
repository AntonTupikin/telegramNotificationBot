package com.example.telegram.service;

import com.example.telegram.bot.NotificationBot;
import com.example.telegram.config.BotConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

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
            logger.error("Unable to send notification", e);
        }
    }
}
