package com.app.cinema.bot;

import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import com.app.cinema.models.Movie;
import com.app.cinema.repositories.MovieRepository;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class CinemaBot extends TelegramLongPollingBot {


    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    private Set<String> userChatIds = ConcurrentHashMap.newKeySet();

    @Autowired
    MovieRepository movieRepository;

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println("onUpdateReceived");
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            userChatIds.add(chatId);
            String text = update.getMessage().getText();

            switch (text) {
                case "/start":
                    sendText(chatId, "Добро пожаловать! Напиши /movies чтобы получить список фильмов.");
                    break;
                case "/movies":
                    List<Movie> movies = (List<Movie>) movieRepository.findAll();
                    StringBuilder sb = new StringBuilder("🎬 Список фильмов:\n");
                    for (Movie m : movies) {
                        sb.append("• ").append(m.getName()).append(" (").append(m.getTimeDuration()).append(" мин)\n");
                    }
                    sendText(chatId, sb.toString());
                    break;
                default:
                    sendText(chatId, "Неизвестная команда.");
            }
        }
    }

    public void sendText(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }  
    
    public Set<String> getUserChatIds() {
        return userChatIds;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    public void notifyNewMovie(String movieName) {
        String message = "✅ Фильм \"" + movieName + "\" был успешно добавлен!";
        for (String chatId : userChatIds) {
            sendText(chatId, message);
        }
    }

    public void notifyDeleteMovie(String movieName) {
        String message = "❌ Фильм \"" + movieName + "\" был удален!";
        for (String chatId : userChatIds) {
            sendText(chatId, message);
        }
    }

    public void notifyUpdateMovie(String oldMovieName, String newMovieName) {
        String message = "🔄 Фильм \"" + oldMovieName + "\" был обновлен на " + newMovieName;
        for (String chatId : userChatIds) {
            sendText(chatId, message);
        }
    }
}
