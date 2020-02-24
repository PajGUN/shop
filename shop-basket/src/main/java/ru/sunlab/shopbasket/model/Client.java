package ru.sunlab.shopbasket.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "client")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "Имя не указано")
    @Size(min = 2, max = 20)
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "Фамилия не указана")
    @Size(min = 2, max = 30)
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "middle_name")
    private String middleName;

    @NotNull
    @Past
    @Column(name = "birthday_date", nullable = false)
    private LocalDate birthday;

    @Column(name = "created_date")
    private LocalDate created;

    @NotNull
    @Email(message = "Адрес электронной почты не верен")
    @Column(name = "email", nullable = false)
    private String email;

    @NotNull
    @Pattern(regexp = "[0-9]{10}")
    @Column(name = "phone_num", nullable = false)
    private String phoneNumber;

    public Client() {
        this.created = LocalDate.now();
    }
}
