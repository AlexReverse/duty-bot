package ru.qdutybot.dutybot.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

public class DutyCommand {
    public SendMessage dutyCommand(Long chatId) {
        SendMessage text = new SendMessage();
        text.setChatId(String.valueOf(chatId));
        text.setText("Выберите команду дежурного ниже из списка");
        ReplyKeyboardMarkup reply = new KeyboardMarkup().getReplyKeyboardMarkup();
        text.setReplyMarkup(reply);
        return text;
    }
}
