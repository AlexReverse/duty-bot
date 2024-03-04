package ru.qdutybot.dutybot.data;

import jakarta.persistence.*;
import lombok.Data;
import org.apache.poi.ss.usermodel.Cell;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
@Entity
public class Excel {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Date placedAt;
    //private Map<String , String> data = new LinkedHashMap<>();

//    public void addExcel(Map<String, String> map) {
//        this.data.putAll(map);
//    }

    @PrePersist
    void placedAt(){
        this.placedAt=new Date();
    }
}
