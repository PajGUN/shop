package ru.sunlab.shopbasket.service;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.RabbitExceptionTranslator;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sunlab.shopbasket.configuration.RabbitConfig;
import ru.sunlab.shopbasket.dto.*;
import ru.sunlab.shopbasket.dto.rabbit.*;
import ru.sunlab.shopbasket.exception.ClientNotFoundException;
import ru.sunlab.shopbasket.exception.OrderNotFoundException;
import ru.sunlab.shopbasket.exception.RabbitException;
import ru.sunlab.shopbasket.model.*;
import ru.sunlab.shopbasket.repository.*;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderService {

    @Value("${time.wait.order.pay}")
    private Integer payTime;
    @Value("${time.delivery.in.hours}")
    private Integer deliveryTimeInHours;
    private static final String CACHE_NAME = "stores";

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductCountRepository productCountRepository;
    private final StoreRepository storeRepository;
    private final ClientRepository clientRepository;
    private final RabbitTemplate rabbitTemplate;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                        ProductCountRepository productCountRepository, StoreRepository storeRepository,
                        ClientRepository clientRepository, RabbitTemplate rabbitTemplate) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productCountRepository = productCountRepository;
        this.storeRepository = storeRepository;
        this.clientRepository = clientRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Transactional
    public void payForOrder(CardOrderDto cardOrderDto) {
        Optional<Order> orderOptional = orderRepository.findById(cardOrderDto.getOrderId());
        orderOptional.orElseThrow(()->new OrderNotFoundException("Order - "+
                cardOrderDto.getOrderId()+" not found! Payment not allowed"));
        Order order = orderOptional.get();
        //эта метка нужна чтобы шедулер/уборщик заказов не удалил заказ пока по нему идёт оплата
        order.setPayment(true);
        orderRepository.save(order);

        BigDecimal summaryCost = orderItemRepository.getSummaryCost(cardOrderDto.getOrderId());
        CardOrderRmqDto cardOrderRmqDto = MapperUtil.mapToCardOrderRmqDto(cardOrderDto);
        cardOrderRmqDto.setSummaryCost(summaryCost);
        cardOrderRmqDto.setOrderCreatedTime(order.getCreated());

        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_DIRECT,
                RabbitConfig.QUEUE_BANK, cardOrderRmqDto);
    }

    @Transactional
    public void bankListener(CardOrderAnswerRmqDto cardOrderAnswerRmqDto, Channel channel, long tag) {
        Optional<Order> orderOptional = orderRepository.findById(cardOrderAnswerRmqDto.getOrderId());
        if (orderOptional.isEmpty()){
            //не существует заказа по которому прошла оплата, нужно отменять платёж
            try {
                channel.basicReject(tag, false);
            } catch (IOException ex) {
                throw new RabbitException("Rabbit's basicReject not sent");
            }
            if (cardOrderAnswerRmqDto.getResult()) {
                //Отправляем в банк сообщение об отмене платежа
                //Логика по отправке сообщения в банк
            }
            log.error("Order - "+ cardOrderAnswerRmqDto.getOrderId() +" not found!");
            return;
        }

        Order order = orderOptional.get();
        if (!cardOrderAnswerRmqDto.getResult()) {
            order.setPayment(false);
            orderRepository.save(order);
            //Оповещаем пользователя что платёж не прошёл, как вариант можем напрвлять в очередь
            //которую просматривает браузер
            return;
        }

        order.setArchive(true);
        orderRepository.save(order);

        Optional<Client> clientOptional = clientRepository.findById(order.getClientId());
        clientOptional.orElseThrow(()-> new ClientNotFoundException("Client - " +
                order.getClientId() + " not found!"));
        Client client = clientOptional.get();

        BigDecimal summaryCost = new BigDecimal(BigInteger.ZERO)
                .setScale(2, RoundingMode.HALF_UP);
        List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(order.getId());
        List<OrderItemRmqDto> orderItemRmqDtos = new ArrayList<>(orderItems.size());
        for (OrderItem orderItem : orderItems) {
            BigDecimal multiply = orderItem.getPrice()
                    .multiply(new BigDecimal(String.valueOf(orderItem.getQuantity())));
            summaryCost = summaryCost.add(multiply);
            orderItemRmqDtos.add(MapperUtil.mapToOrderItemRmqDto(orderItem));
        }
        OrderRmqDto orderRmqDto = new OrderRmqDto();
        orderRmqDto.setOrderId(order.getId());
        orderRmqDto.setCreated(order.getCreated());
        orderRmqDto.setStoreId(order.getStoreId());
        orderRmqDto.setStoreName(getStoreMap().get(order.getStoreId()));
        orderRmqDto.setClientId(order.getClientId());
        orderRmqDto.setFirstName(client.getFirstName());
        orderRmqDto.setMiddleName(client.getMiddleName());
        orderRmqDto.setEmail(client.getEmail());
        orderRmqDto.setPhoneNumber(client.getPhoneNumber());
        orderRmqDto.setSummaryCost(summaryCost);
        orderRmqDto.setOrderItemRmqDtos(orderItemRmqDtos);

        if (order.getDeliveryTo() == null) {
            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_EXT_SERVICES,
                    "*", orderRmqDto);
        } else {
            orderRmqDto.setDeliveryTimes(order.getCreated().plusHours(deliveryTimeInHours));
            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_EXT_SERVICES,
                    "delivery", orderRmqDto);
        }
        try {
            channel.basicAck(tag, false);
        } catch (IOException e) {
            throw new RabbitException("Rabbit's basicAck not sent to BankListener");

        }
    }

    @Transactional
    public void cleanOrders() {
        LocalDateTime ldt = LocalDateTime.now().minusMinutes(payTime);
        List<Order> orders = orderRepository.getAllByTimeBefore(ldt);
        if (orders.isEmpty()) return;

        ProductsRmqDto productsRmqDto = new ProductsRmqDto();
        productsRmqDto.setAction(true);
        List<ProductRmqDto> productRmqDtoList = new ArrayList<>();
        productsRmqDto.setProductRmqDtos(productRmqDtoList);
        for (Order order : orders) {
            order.setArchive(true);
            List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(order.getId());
            for (OrderItem orderItem : orderItems) {
                ProductRmqDto productRmqDto = new ProductRmqDto();
                productRmqDto.setProductId(orderItem.getProductId());
                productRmqDto.setQuantity(orderItem.getQuantity());
                productRmqDto.setStoreId(orderItem.getStoreId());
                productRmqDto.setOrderId(orderItem.getOrderId());
                productRmqDtoList.add(productRmqDto);

                ProductCount pc = productCountRepository.findByProductIdAndStoreId(
                        orderItem.getProductId(), order.getStoreId());
                if (pc == null) continue;
                pc.setQuantity(pc.getQuantity() + orderItem.getQuantity());
                productCountRepository.save(pc);
            }
        }
        orderRepository.saveAll(orders);

        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_DIRECT,
                RabbitConfig.QUEUE_PRODUCTS, productsRmqDto);
    }

    @Transactional
    public List<OrderDto> getArchiveOrders(Long clientId, Pageable pageable) {
        Page<Order> orders = orderRepository.getArchiveOrders(clientId, pageable);
        return getOrderDtos(orders.getContent());
    }

    @Transactional
    public List<OrderDto> getOrders(Long clientId) {
        List<Order> orders = orderRepository.getActiveOrders(clientId);
        return getOrderDtos(orders);
    }

    private List<OrderDto> getOrderDtos(List<Order> orders) {
        List<OrderDto> orderDtos = new ArrayList<>(orders.size());
        for (Order order : orders) {
            OrderDto orderDto = new OrderDto();

            String storeName = getStoreMap().get(order.getStoreId());
            List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(order.getId());
            List<OrderItemDto> orderItemDtos = new ArrayList<>();
            BigDecimal summaryCost = new BigDecimal(BigInteger.ZERO)
                    .setScale(2, RoundingMode.HALF_UP);
            for (OrderItem orderItem : orderItems) {
                OrderItemDto orderItemDto = new OrderItemDto();
                orderItemDto.setProductName(orderItem.getProductName());
                orderItemDto.setPrice(orderItem.getPrice());
                orderItemDto.setQuantity(orderItem.getQuantity());
                BigDecimal multiply = orderItem.getPrice()
                        .multiply(new BigDecimal(String.valueOf(orderItem.getQuantity())));
                summaryCost = summaryCost.add(multiply);
                orderItemDtos.add(orderItemDto);
            }
            orderDto.setStore(storeName);
            orderDto.setOrderNumber(order.getId());
            orderDto.setSummaryCost(summaryCost);
            orderDto.setOrderItemDtos(orderItemDtos);
            orderDtos.add(orderDto);
        }
        return orderDtos;
    }

    @Cacheable(CACHE_NAME)
    public Map<Long,String> getStoreMap(){
        List<Store> allStores = storeRepository.findAll();
        return allStores.stream().collect(Collectors.toMap(Store::getId, Store::getName));
    }
}
