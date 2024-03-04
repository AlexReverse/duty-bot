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
import ru.qdutybot.dutybot.data.Excel;
import ru.qdutybot.dutybot.data.ExcelRepository;

import java.io.FileInputStream;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@Slf4j
@Controller
public class ExcelController {
    private ExcelRepository excelRepository;

    @Autowired
    public ExcelController(ExcelRepository excelRepository){
        this.excelRepository=excelRepository;
    }

    @Bean
    private void excelParsing() {
        try {
            FileInputStream fileInputStream = new FileInputStream(Path.get("C:\\Users\\mrevt\\IdeaProjects\\duty-bot\\duty-bot\\дежурные.xlsx").toFile());
            Workbook workbook = new XSSFWorkbook(fileInputStream);
            Sheet sheet = workbook.getSheetAt(0);

            Map<String, String> data = new LinkedHashMap<>();
            int i = 0;
            for (Row row : sheet) {
                if (sheet.getRow(i).getCell(0) != null) {
                    for (Cell cell : row) {
                        String string = sheet.getRow(i).getCell(1).toString() + ": " + sheet.getRow(i).getCell(2).toString();
                        data.put(sheet.getRow(i).getCell(0).toString(), string);
                    }
                }
                i++;
            }
            for (Map.Entry<String, String> entry : data.entrySet()) {
                System.out.println(entry.getKey() + " - " + entry.getValue());
            }

        } catch (Exception e) {
            log.error(String.valueOf(e));
        }
    }
}
