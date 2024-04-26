package ru.qdutybot.dutybot.controller;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.qdutybot.dutybot.Team;
import ru.qdutybot.dutybot.data.ExcelData;
import ru.qdutybot.dutybot.data.ExcelRepository;
import ru.qdutybot.dutybot.service.HelpCommand;
import ru.qdutybot.dutybot.service.InlineKeyboard;
import ru.qdutybot.dutybot.service.KeyboardMarkup;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Getter
@Setter
@Slf4j
@Component
public class DutyController extends TelegramLongPollingBot {

    private LinkedHashMap<String, String> map = new LinkedHashMap<>();
    private String message;

    @Autowired
    private ExcelRepository excelRepository;

    private final String TEAM1 = Team.TEAM1.getString();
    private final String TEAM2 = Team.TEAM2.getString();
    private final String TEAM3 = Team.TEAM3.getString();


    private HelpCommand helpCommand = new HelpCommand();

    public DutyController(@Value("${bot.token}") String botToken, ExcelRepository excelRepository) {
        super(botToken);
        this.excelRepository=excelRepository;


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

            if (message.matches("/setup\\s[а-яА-Я]+\\s[а-яА-Я]+")) {
                setupCommand(chatId);
            } else {
                switch (message) {
                    case "/start" -> startCommand(chatId, update.getMessage().getChat().getUserName());
                    case "/help" -> sendMessage(chatId, new HelpCommand().getTextHelpCommand());
                    case "/duty" -> dutyCommand(chatId);
                    case "/setup" -> {
                        sendMessage(chatId, "Вы забыли ввести дежурного!");
                        sendMessage(chatId, new HelpCommand().getTextHelpCommand());
                    }
                    case "Asti" -> getFromMap(chatId, TEAM1);
                    case "SPECIAL" -> getFromMap(chatId, TEAM2);
                    case "LOGOS" -> getFromMap(chatId, TEAM3);
                    default -> unknownCommand(chatId);
                }
            }

        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();

            switch (callbackData) {
                case "Asti" -> putMap(TEAM1, message, chatId);
                case "SPECIAL" -> putMap(TEAM2, message, chatId);
                case "LOGOS" -> putMap(TEAM3, message, chatId);
            }
        }
    }

    private void setupCommand(Long chatId) {
        SendMessage text = new SendMessage();
        text.setChatId(String.valueOf(chatId));
        text.setText("Укажите команду для дежурного");
        InlineKeyboardMarkup markup = new InlineKeyboard().getInlineKeyboardMarkup();
        text.setReplyMarkup(markup);
        tryExecute(text);
    }

    private void dutyCommand(Long chatId) {
        SendMessage text = new SendMessage();
        text.setChatId(String.valueOf(chatId));
        text.setText("Выберите команду дежурного");
        ReplyKeyboardMarkup reply = new KeyboardMarkup().getReplyKeyboardMarkup();
        text.setReplyMarkup(reply);
        tryExecute(text);
    }

    private void tryExecute(SendMessage text) {
        try {
            execute(text);
        } catch (TelegramApiException e) {
            log.error("Error - ", e);
        }
    }

    private void getFromMap(Long chatId, String team) {
        String text = getMap().getOrDefault(team, "не назначен!");
        sendMessage(chatId, "Текущий дежурный в команде " + team + " - " + text);
    }

    private void putMap(String team, String message, Long chatId) {
        getMap().put(team, message.substring(6));
        ExcelData excelData = new ExcelData();
        Date current = new Date();

        excelData.setName(message.substring(6));
        excelData.setDate(current);
        excelData.setTg(excelRepository.findByName(message.substring(6)).getTg());
        excelData.setTeam(team);

        sendMessage(chatId, "Дежурный " + getMap().get(team) + ", в команду " + team + " успешно добавлен!");
        //#TODO оповещение в чате
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

    private void unknownCommand(Long chatId) {
        sendMessage(chatId, "Не понимаю. У меня лапки!\nДля подсказки /help");
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
