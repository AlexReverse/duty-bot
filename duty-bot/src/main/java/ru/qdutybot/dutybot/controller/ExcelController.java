package ru.qdutybot.dutybot.controller;


import lombok.extern.slf4j.Slf4j;
import okio.Path;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import ru.qdutybot.dutybot.data.ExcelData;
import ru.qdutybot.dutybot.data.ExcelRepository;

import java.io.FileInputStream;
import java.util.*;

@Component
@Slf4j
@Controller
public class ExcelController {
    private ExcelRepository excelRepository;
    private Map<String, ArrayList<String>> data = new LinkedHashMap<>();
    private Integer team;

    @Autowired
    public ExcelController(ExcelRepository excelRepository) {
        this.excelRepository = excelRepository;
    }

    @Bean
    private void excelParsing() {
        for (int team = 0; team < 3; team++) {
            try (FileInputStream fileInputStream = new FileInputStream(Path.get("C:\\Users\\mrevt\\IdeaProjects\\duty-bot\\duty-bot\\дежурные.xlsx").toFile())) {
                Workbook workbook = new XSSFWorkbook(fileInputStream);

                Sheet sheet = workbook.getSheetAt(team);
                int i = 1;
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        ArrayList<String> string = new ArrayList<>();
                        string.add(sheet.getRow(i).getCell(1).toString());
                        string.add(sheet.getRow(i).getCell(2).toString());
                        data.put(String.valueOf((sheet.getRow(i).getCell(0)).getLocalDateTimeCellValue()), string);
                    }
                    i++;
                }

            } catch (NullPointerException e) {
                for (Map.Entry<String, ArrayList<String>> entry : data.entrySet()) {
                    ExcelData excelData = new ExcelData();
                    ArrayList<String> strings = new ArrayList<>();
                    strings.addAll(entry.getValue());

                    excelData.setDate(entry.getKey());
                    excelData.setName(strings.get(0));
                    excelData.setTg(strings.get(1));

                    switch (team) {
                        case 0 -> excelData.setTeam("Asti");
                        case 1 -> excelData.setTeam("SPECIAL");
                        case 2 -> excelData.setTeam("LOGOS");
                    }

                    excelRepository.save(excelData);
                }
                log.info("---info saved from excel---");
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }
}
