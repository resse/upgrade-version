package ru.neoflex.example.upgrade.v1.dto;

import lombok.Data;

@Data
public class Ticket {
    String uuid;
    Long date;
    String subject;
    String description;
    String command;
}
