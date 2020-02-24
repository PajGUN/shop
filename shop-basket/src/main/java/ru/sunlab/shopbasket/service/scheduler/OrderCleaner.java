package ru.sunlab.shopbasket.service.scheduler;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.sunlab.shopbasket.service.OrderService;

@Service
public class OrderCleaner {

    private final OrderService orderService;

    @Autowired
    public OrderCleaner(OrderService orderService) {
        this.orderService = orderService;
    }

    /*
    * Шедулер, проверяет раз в минуту заказы, если есть неоплаченый товар с простоем 20 минут
    * то возвращаем прежнее количество товара и ордер в архив
    */
    @Scheduled(cron = "${cron.clean.order.period}")
    public void cleanOrders(){
        orderService.cleanOrders();
    }
}
