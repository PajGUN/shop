package ru.sunlab.shop.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductCountViewDto {

    private String store;

    //нет, мало, много
    private String count;
}
