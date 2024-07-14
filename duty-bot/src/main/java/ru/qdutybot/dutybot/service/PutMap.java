package ru.qdutybot.dutybot.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.qdutybot.dutybot.data.ExcelData;
import ru.qdutybot.dutybot.data.ExcelRepository;

public class PutMap {
    @Autowired
    public ExcelRepository excelRepository;
    public PutMap(ExcelRepository excelRepository) {
        this.excelRepository=excelRepository;
    }
    public String putMap(String team, String message) {
        ExcelData excelData = new ExcelData();
        String current = new Monday().getMonday();
        try {
            excelData.setTg(excelRepository.findByName(message.substring(7)).getLast());
            excelData.setName(message.substring(7));
            excelData.setDate(current+"T00:00");
            excelData.setTeam(team);
            excelRepository.save(excelData);
            return "Дежурный " + message.substring(7) + ", в команду " + team + " успешно добавлен!";

        } catch (Exception e) {
            return  "Запись не сохранена!\nДанный сотрудник не найден в файле.";
        }
        //#TODO оповещение в чате
    }
}
