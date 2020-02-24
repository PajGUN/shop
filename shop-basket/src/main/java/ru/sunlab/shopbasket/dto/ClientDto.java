package ru.sunlab.shopbasket.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ClientDto {

    private Long id;

    private String firstName;

    private String lastName;

    private String middleName;

    private LocalDate birthday;

    private LocalDate created;

    private String email;

    private String phoneNumber;
}
