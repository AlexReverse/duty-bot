package ru.qdutybot.dutybot.data;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExcelRepository extends CrudRepository<ExcelData, Long> {

    @Query(nativeQuery = true, value = "select distinct tg from excel_data where name=:name")
    public List<String> findByName(@Param("name") String name);

    @Query(nativeQuery = true, value = "select concat(name, ', телеграм - ' , tg) from excel_data where team in(:team) and date in(:date)")
    public List<String> findDuty(@Param("team") String team, @Param("date") String date);
}
