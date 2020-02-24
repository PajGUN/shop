package ru.sunlab.shopbasket.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "basket_unit")
public class BasketUnit {

    public BasketUnit() {
        this.quantity = 1;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull
    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @NotNull
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @NotEmpty
    @Column(name = "product_name", nullable = false)
    private String productName;

    @NotNull
    @Column(name = "price", nullable = false)
    private BigDecimal productPrice;

    @NotNull
    @Column(name = "quantity_buy", nullable = false)
    private Integer quantity;
}
