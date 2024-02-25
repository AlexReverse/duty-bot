package ru.qdutybot.dutybot.controller;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;

@Getter
@Setter
@Slf4j
@Component
public class DutyBot extends TelegramLongPollingBot {

    private LinkedHashMap<String, String> map = new LinkedHashMap<>();
    private String message;


    public DutyBot(@Value("${bot.token}") String botToken) {
        super(botToken);
        List<BotCommand> commandList = new ArrayList<>();
        commandList.add(new BotCommand("/start", "стартовое сообщение"));
        commandList.add(new BotCommand("/help", "просмотр информации"));
        commandList.add(new BotCommand("/duty", "уточнение дежурного"));
        commandList.add(new BotCommand("/setup", "назначить дежурного"));
        try {
            this.execute(new SetMyCommands(commandList, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error: ", e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            message = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            if (message.matches("/setup\\s[а-яА-Я]+")) {
                setupCommand(chatId);
            } else {
                switch (message) {
                    case "/start" -> startCommand(chatId, update.getMessage().getChat().getUserName());
                    case "/help" -> helpCommand(chatId);
                    case "/duty" -> dutyCommand(chatId);
                    case "/setup" -> {
                        sendMessage(chatId, "Вы забыли ввести дежурного!");
                        helpCommand(chatId);
                    }
                    default -> unknownCommand(chatId);
                }
            }

        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();

            switch (callbackData) {
                case "TEAM1duty" -> sendMessage(chatId, getMap().get("TEAM1"));
                case "TEAM2duty" -> sendMessage(chatId, getMap().get("TEAM2"));
                case "TEAM3duty" -> sendMessage(chatId, getMap().get("TEAM3"));
                case "TEAM1set" -> {
                    getMap().put("TEAM1", message.substring(6));
                    sendMessage(chatId, "Дежурный успешно добавлен!");
                }
                case "TEAM2set" -> {
                    getMap().put("TEAM2", message.substring(6));
                    sendMessage(chatId, "Дежурный успешно добавлен!");
                }
                case "TEAM3set" -> {
                    getMap().put("TEAM3", message.substring(6));
                    sendMessage(chatId, "Дежурный успешно добавлен!");
                }
            }
        }
    }

    private void setupCommand(Long chatId) {
        SendMessage text = new SendMessage();
        text.setChatId(String.valueOf(chatId));
        text.setText("Выберите команду дежурного");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        var button1 = new InlineKeyboardButton();
        button1.setText("team1");
        button1.setCallbackData("TEAM1set");

        var button2 = new InlineKeyboardButton();
        button2.setText("team2");
        button2.setCallbackData("TEAM2set");

        var button3 = new InlineKeyboardButton();
        button3.setText("team3");
        button3.setCallbackData("TEAM3set");

        rowInline.add(button1);
        rowInline.add(button2);
        rowInline.add(button3);

        rowsInline.add(rowInline);

        markup.setKeyboard(rowsInline);
        text.setReplyMarkup(markup);
        try {
            execute(text);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки - ", e);
        }
    }

    private void dutyCommand(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Уточнение команды дежурного");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        var button1 = new InlineKeyboardButton();
        button1.setText("team1");
        button1.setCallbackData("TEAM1duty");

        var button2 = new InlineKeyboardButton();
        button2.setText("team2");
        button2.setCallbackData("TEAM2duty");

        var button3 = new InlineKeyboardButton();
        button3.setText("team3");
        button3.setCallbackData("TEAM3duty");

        rowInline.add(button1);
        rowInline.add(button2);
        rowInline.add(button3);

        rowsInline.add(rowInline);

        markup.setKeyboard(rowsInline);
        message.setReplyMarkup(markup);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки - ", e);
        }
    }

    @Override
    public String getBotUsername() {
        return "${bot.name}";
    }

    private void startCommand(Long chatId, String userName) {
        var text = """
                Добро пожаловать, %s!
                /help - для получения справки
                """;
        var formattedText = String.format(text, userName);
        sendMessage(chatId, formattedText);
    }

    private void helpCommand(Long chatId) {
        var text = """
                /duty для уточнения дежурного
                
                /setup {Имя дежурного} - для назнечения дежурного
                Пример: /setup Александр
                """;
        sendMessage(chatId, text);
    }

    private void unknownCommand(Long chatId) {
        sendMessage(chatId, "Не понимаю. У меня лапки!");
        helpCommand(chatId);
    }

    private void sendMessage(Long chatId, String text) {
        var chatIdStr = String.valueOf(chatId);
        var sendMessage = new SendMessage(chatIdStr, text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(String.valueOf(e));
        }
    }
}
