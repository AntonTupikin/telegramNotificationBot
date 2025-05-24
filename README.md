# Telegram Notification Service

Сервис для отправки уведомлений в Telegram и получения обратных сообщений. Реализован на базе Spring Boot и библиотеки `org.telegram.telegrambots`.

## Запуск

Перед запуском необходимо указать данные бота в переменных окружения:

```bash
export TELEGRAM_BOT_USERNAME=your_bot_username
export TELEGRAM_BOT_TOKEN=your_bot_token
export TELEGRAM_CHAT_ID=your_chat_id
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/telegram
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=postgres
```

После чего приложение можно запустить командой:

```bash
mvn spring-boot:run
```

Сервис предоставляет HTTP‑endpoint `POST /api/notify`, принимающий текст уведомления в теле запроса и отправляющий его в Telegram.

После запуска можно открыть [Swagger UI](http://localhost:8080/swagger-ui.html) для тестирования API.

После команды `/start` бот сохраняет информацию о пользователе в базе. Дальнейшие сообщения он приветствует по имени и желает хорошего дня.
