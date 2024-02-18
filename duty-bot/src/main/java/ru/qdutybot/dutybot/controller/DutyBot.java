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
        try{
            this.execute(new SetMyCommands(commandList, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error: ", e);
        }
    }
    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        var message = update.getMessage().getText();
        var chatId = update.getMessage().getChatId();
        switch (message) {
            case "/start" -> startCommand(chatId, update.getMessage().getChat().getUserName());
            case "/help" -> helpCommand(chatId);
            case "/duty" -> dutyCommand(chatId);
            case "/setup" -> setupCommand(chatId, message);
            //#TODO
            default -> unknownCommand(chatId);
        }
    }

    private void setupCommand(Long chatId, String string) {
        var team = "test"; //#TODO
        var text = "Дежурный успешно добавлен";
        map.put(team, string);
        sendMessage(chatId, text);
    }

    private void dutyCommand(Long chatId) {
        var text = "Текущий дежурный %s";

        String string = map.lastEntry().getValue();
        sendMessage(chatId, text+string);
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
                /setup для назнечения дежурного
                """;
        sendMessage(chatId, text);
    }

    private void unknownCommand(Long chatId) {
        sendMessage(chatId, "Не понимаю. У меня лапки!");
    }

    private void sendMessage(Long chatId, String text){
        var chatIdStr = String.valueOf(chatId);
        var sendMessage = new SendMessage(chatIdStr, text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            LOG.error("Ошибка отправки - ", e);
        }
    }
}
