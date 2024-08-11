package ru.qdutybot.dutybot.controller;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.qdutybot.dutybot.Team;
import ru.qdutybot.dutybot.data.ExcelRepository;
import ru.qdutybot.dutybot.service.*;

import java.util.*;

@Getter
@Setter
@Slf4j
@Component
@EnableScheduling
public class DutyController extends TelegramLongPollingBot {

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
                tryExecute(new SetupCommand().setupCommand(chatId));
            } else {
                switch (message) {
                    case "/start" -> sendMessage(chatId, new StartCommand().startCommand(update.getMessage().getChat().getUserName()));
                    case "/help" -> sendMessage(chatId, new HelpCommand().getTextHelpCommand());
                    case "/duty" -> tryExecute(new DutyCommand().dutyCommand(chatId));
                    case "/setup" -> {
                        sendMessage(chatId, "Вы забыли ввести дежурного!");
                        sendMessage(chatId, new HelpCommand().getTextHelpCommand());
                    }
                    case "Asti" -> getFromMap(chatId, TEAM1);
                    case "SPECIAL" -> getFromMap(chatId, TEAM2);
                    case "LOGOS" -> getFromMap(chatId, TEAM3);
                    default -> sendMessage(chatId, new UnknownCommand().getTextUnknownCommand());
                }
            }

        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();

            switch (callbackData) {
                case "Asti" -> {
                    sendMessage(chatId, new PutMap(excelRepository).putMap(TEAM1, message));
                    sendMessage(Team.TEAM1.getL(), "Дежурный был изменен на " + message.substring(7));
                }
                case "SPECIAL" -> {
                    sendMessage(chatId, new PutMap(excelRepository).putMap(TEAM2, message));
                    sendMessage(Team.TEAM2.getL(), "Дежурный был изменен на " + message.substring(7));
                }
                case "LOGOS" -> {
                    sendMessage(chatId, new PutMap(excelRepository).putMap(TEAM3, message));
                    sendMessage(Team.TEAM3.getL(), "Дежурный был изменен на " + message.substring(7));
                }
            }
        }
    }

    private void tryExecute(SendMessage text) {
        try {
            execute(text);
        } catch (TelegramApiException e) {
            log.error("Error - ", e);
        }
    }

    private void getFromMap(Long chatId, String team) {
        String date = new Monday().getMonday();
        String text = excelRepository.findDuty(team, date+"T00:00").getLast();
        sendMessage(chatId, "Текущий дежурный в команде " + team + " - " + text);
    }

    /*
    Cron - настраиваемое оповещение в чатах
     */
    @Scheduled(cron = "* * 9 * * MON")
    private void sendInfo(){
        String date = new Monday().getMonday();

        String text = excelRepository.findDuty(String.valueOf(TEAM1), date+"T00:00").getLast();
        sendMessage(Team.TEAM1.getL(), "Дежурный на эту неделю в команду " + TEAM1 + " - " + text);

        text = excelRepository.findDuty(String.valueOf(TEAM2), date+"T00:00").getLast();
        sendMessage(Team.TEAM2.getL(), "Дежурный на эту неделю в команду " + TEAM2 + " - " + text);

        text = excelRepository.findDuty(String.valueOf(TEAM3), date+"T00:00").getLast();
        sendMessage(Team.TEAM3.getL(), "Дежурный на эту неделю в команду " + TEAM3 + " - " + text);
    }
    @Override
    public String getBotUsername() {
        return "${bot.name}";
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
