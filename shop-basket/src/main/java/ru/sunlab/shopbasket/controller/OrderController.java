package ru.sunlab.shopbasket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sunlab.shopbasket.dto.CardOrderDto;
import ru.sunlab.shopbasket.dto.OrderDto;
import ru.sunlab.shopbasket.service.OrderService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/1.0/order")
public class OrderController {

    private final OrderService orderService;
    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/getactiveorders/{clientId}")
    public ResponseEntity<List<OrderDto>> getOrders(@PathVariable Long clientId){
        List<OrderDto> orderDto = orderService.getOrders(clientId);
        if (orderDto.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(orderDto);
    }

    @GetMapping("/getarchiveorders/{clientId}")
    public ResponseEntity<List<OrderDto>> getArchiveOrder(@PathVariable Long clientId,
                @PageableDefault(sort = {"id"},direction = Sort.Direction.DESC) Pageable pageable){
        List<OrderDto> orderDto = orderService.getArchiveOrders(clientId, pageable);
        if (orderDto.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(orderDto);
    }

    @PostMapping("/payorder")
    public ResponseEntity<Void> payForOrder(@Valid @RequestBody CardOrderDto cardOrderDto){
        orderService.payForOrder(cardOrderDto);
        return ResponseEntity.ok().build();
    }

}
