package ru.sunlab.shopbasket.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "client_id")
    private Long clientId;

    @NotNull
    @Column(name = "store_id")
    private Long storeId;

    @NotNull
    @Column(name = "created_time")
    private LocalDateTime created;

    @Column(name = "delivery_to")
    private Long deliveryTo;

    @Column(name = "isArchive")
    private Boolean archive = false;

    @Column(name = "isPayment")
    private Boolean payment = false;

    public Order(@NotNull Long clientId, @NotNull Long storeId) {
        this.clientId = clientId;
        this.storeId = storeId;
        this.created = LocalDateTime.now();
    }
}