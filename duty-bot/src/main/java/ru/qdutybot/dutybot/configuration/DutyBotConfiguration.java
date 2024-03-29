package ru.qdutybot.dutybot.configuration;


import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.qdutybot.dutybot.controller.DutyController;

@Configuration
public class DutyBotConfiguration {

    @Bean
    public TelegramBotsApi telegramBotsApi(DutyController dutyController) {
        try {
            var api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(dutyController);
            return api;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient();
    }
}