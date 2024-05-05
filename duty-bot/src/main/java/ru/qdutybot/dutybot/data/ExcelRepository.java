package ru.qdutybot.dutybot.data;

import org.springframework.data.repository.CrudRepository;

public interface ExcelRepository extends CrudRepository<ExcelData, Long> {
    ExcelData findByName(String name);
    ExcelData findByTeam(String team);
//    ExcelData findByTeamAndName(String team, String name);
//    ExcelData findByTeamAndDate(String team, String date);
}
