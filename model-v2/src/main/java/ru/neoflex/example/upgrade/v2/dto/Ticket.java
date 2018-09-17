package ru.neoflex.example.upgrade.v2.dto;

import lombok.Data;

@Data
public class Ticket {
    String uuid;
    Long date;
    Subject subject;
    String description;
    String command;
}
