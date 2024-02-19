package ru.qdutybot.dutybot.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Slf4j
@Component
public class DutyBot extends TelegramLongPollingBot {

    public LinkedHashMap<String, String> map;

    private static final Logger LOG = LoggerFactory.getLogger(DutyBot.class);


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
            String message = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            if (message.contains("/setup")) {
                setupCommand(chatId, message);
            }

            switch (message) {
                case "/start" -> startCommand(chatId, update.getMessage().getChat().getUserName());
                case "/help" -> helpCommand(chatId);
                case "/duty" -> dutyCommand(chatId);
                //#TODO
                default -> unknownCommand(chatId);
            }

        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            Long messageId = Long.valueOf(update.getCallbackQuery().getMessage().getMessageId());
            Long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (callbackData.equals("TEAM1")) {
                sendMessage(chatId, "team 1");
            } else if (callbackData.equals("TEAM2")) {
                sendMessage(chatId, "team 2");
            } else if (callbackData.equals("TEAM3")) {
                sendMessage(chatId, "team 3");
            }
        }
    }

    private void setupCommand(Long chatId, String message) {
        var team = "test"; //#TODO
        message = message.substring(6);
        String text = "Дежурный " + message +" успешно добавлен";
        sendMessage(chatId, text);
        map.put(team, message);
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
        button1.setCallbackData("TEAM1");

        var button2 = new InlineKeyboardButton();
        button2.setText("team2");
        button2.setCallbackData("TEAM2");

        var button3 = new InlineKeyboardButton();
        button3.setText("team3");
        button3.setCallbackData("TEAM3");

        rowInline.add(button1);
        rowInline.add(button2);
        rowInline.add(button3);

        rowsInline.add(rowInline);

        markup.setKeyboard(rowsInline);
        message.setReplyMarkup(markup);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            LOG.error("Ошибка отправки - ", e);
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
    }

    private void sendMessage(Long chatId, String text) {
        var chatIdStr = String.valueOf(chatId);
        var sendMessage = new SendMessage(chatIdStr, text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            LOG.error("Ошибка отправки - ", e);
        }
    }
}
