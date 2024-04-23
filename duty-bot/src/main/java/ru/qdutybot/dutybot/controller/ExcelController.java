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
    private Map<Date, ArrayList<String>> data = new LinkedHashMap<>();

    @Autowired
    public ExcelController(ExcelRepository excelRepository) {
        this.excelRepository = excelRepository;
    }

    @Bean
    private void excelParsing() {
        try {
            FileInputStream fileInputStream = new FileInputStream(Path.get("C:\\Users\\mrevt\\IdeaProjects\\duty-bot\\duty-bot\\дежурные.xlsx").toFile());
            Workbook workbook = new XSSFWorkbook(fileInputStream);
            Sheet sheet = workbook.getSheetAt(0);

            int i = 1;
            for (Row row : sheet) {
                for (Cell cell : row) {
                    ArrayList<String> string = new ArrayList<>();
                    string.add(sheet.getRow(i).getCell(1).toString());
                    string.add(sheet.getRow(i).getCell(2).toString());
                    data.put(sheet.getRow(i).getCell(0).getDateCellValue(), string);
                }
                i++;
            }

        } catch (NullPointerException e) {
            for (Map.Entry<Date, ArrayList<String>> entry : data.entrySet()) {
                ExcelData excelData = new ExcelData();
                excelData.setDate(entry.getKey());
                System.out.println(entry.getKey());
                ArrayList<String> strings = new ArrayList<>();
                strings.addAll(entry.getValue());
                excelData.setName(strings.get(0));
                excelData.setTg(strings.get(1));
                excelRepository.save(excelData);
            }
            log.info("---info saved from excel---");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
