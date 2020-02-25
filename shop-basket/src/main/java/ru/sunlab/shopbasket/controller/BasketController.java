package ru.sunlab.shopbasket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sunlab.shopbasket.dto.*;
import ru.sunlab.shopbasket.model.BasketUnit;
import ru.sunlab.shopbasket.model.Order;
import ru.sunlab.shopbasket.service.BasketService;

import javax.annotation.security.PermitAll;
import java.util.List;

@RestController
@RequestMapping("api/1.0/basket")
public class BasketController {

    private final BasketService basketService;

    @Autowired
    public BasketController(BasketService basketService) {
        this.basketService = basketService;
    }

    @PostMapping("/addgoods")
    public ResponseEntity<Void> add(@RequestBody BasketUnitDto basketUnitDto){
        basketService.add(basketUnitDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deletegoods")
    public ResponseEntity<Void> delete(@RequestBody BasketUnitDelDto basketUnitDelDto){
        basketService.delete(basketUnitDelDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/quantitygoods")
    public ResponseEntity<Void> changeGoodsQuantity(@RequestBody BasketUnitQuantityDto basketUnitQuantityDto){
        basketService.changeGoodsQuantity(basketUnitQuantityDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get/{clientId}")
    public ResponseEntity<List<BasketViewDto>> getBasket(@PathVariable Long clientId){
        List<BasketUnit> basketList = basketService.getBasketByClientId(clientId);
        if (basketList.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(MapperUtil.mapToBasketViewDto(basketList));
    }

    @GetMapping("/createorder/{clientId}")
    public ResponseEntity<OrderDto> createOrder(@PathVariable Long clientId, @RequestBody Long storeId){
        OrderDto orderDto = basketService.createOrder(clientId, storeId);
        return ResponseEntity.ok(orderDto);
    }
}
