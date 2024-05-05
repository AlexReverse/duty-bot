package ru.qdutybot.dutybot.data;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Data
@Entity
@Getter
@Setter
public class ExcelData {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Date placedAt;
    private String date;
    private String name;
    private String tg;
    private String team;

    @PrePersist
    void placedAt(){
        this.placedAt=new Date();
    }
}
