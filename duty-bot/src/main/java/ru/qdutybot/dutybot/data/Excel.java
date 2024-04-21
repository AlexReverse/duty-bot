package ru.qdutybot.dutybot.data;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Data
@Entity(name = "excelData")
@Getter
@Setter
public class Excel {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Date placedAt;
    private Date date;
    private String name;
    private String tg;

    @PrePersist
    void placedAt(){
        this.placedAt=new Date();
    }
}
