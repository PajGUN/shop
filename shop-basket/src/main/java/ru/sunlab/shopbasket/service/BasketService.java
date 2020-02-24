package ru.sunlab.shopbasket.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sunlab.shopbasket.configuration.RabbitConfig;
import ru.sunlab.shopbasket.dto.*;
import ru.sunlab.shopbasket.dto.rabbit.ProductRmqDto;
import ru.sunlab.shopbasket.dto.rabbit.ProductsRmqDto;
import ru.sunlab.shopbasket.exception.BasketUnitNotFoundException;
import ru.sunlab.shopbasket.exception.ProductOutOfAllStoreException;
import ru.sunlab.shopbasket.model.*;
import ru.sunlab.shopbasket.repository.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BasketService {

    private static final String CACHE_NAME = "stores";

    private final BasketRepository basketRepository;
    private final ProductCountRepository productCountRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final StoreRepository storeRepository;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public BasketService(BasketRepository basketRepository, ProductCountRepository productCountRepository,
                         OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                         StoreRepository storeRepository, RabbitTemplate rabbitTemplate) {
        this.basketRepository = basketRepository;
        this.productCountRepository = productCountRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.storeRepository = storeRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Transactional
    public void add(BasketUnitDto basketUnitDto) {
        BasketUnit bu = MapperUtil.mapToBasketUnit(basketUnitDto);

        //Проверяем имеется ли подобный заказ у клиента
        BasketUnit buCheck = basketRepository.getByClientIdAndProductId(bu.getClientId(), bu.getProductId());
        if (buCheck == null) {
            basketRepository.save(bu);
        } else {
            buCheck.setQuantity(buCheck.getQuantity() + 1);
            basketRepository.save(buCheck);
            return;
        }
        //Проверяем интересуется ли ещё кто-то данным заказом
        List<ProductCount> pcListCheck = productCountRepository.findAllByProductId(bu.getProductId());
        if (pcListCheck.isEmpty()) {
            List<ProductCount> pcList = MapperUtil.mapToProductCountList(basketUnitDto.getProductCountDtos());
            productCountRepository.saveAll(pcList);
            return;
        }

        pcListCheck.forEach(pc->pc.setNumberOfInterestedClients(pc.getNumberOfInterestedClients()+1));
        productCountRepository.saveAll(pcListCheck);

    }

    @Transactional
    public List<BasketUnit> getBasketByClientId(Long clientId) {
        return basketRepository.findAllByClientId(clientId);
    }

    @Transactional
    public OrderDto createOrder(Long clientId, Long storeId) {
        //Проверяем а возможна ли покупка в принципе (товар в нужном количестве есть в магазинах!)
        List<Long> productIdsOutAllStore = productCountRepository.getProductIdsOutAllStore(clientId);
        if (!productIdsOutAllStore.isEmpty()) {
            //Покупка не возможна, товара в нужном количестве нет в наличии
            //Выкидываем ProductOutOfAllStoreException с перечислением всех отсутсвующих позиций
            Map<Long, Integer> map = new HashMap<>();
            for (Long productId : productIdsOutAllStore) {
                Integer count = productCountRepository.getProductCountInAllStores(productId);
                map.put(productId, count);
            }
            throw new ProductOutOfAllStoreException("Невозможно оформить заказ, недостаточно на складе", map);
        }

        Order ord = new Order(clientId, storeId);
        BigDecimal summaryCost = new BigDecimal(BigInteger.ZERO)
                .setScale(2, RoundingMode.HALF_UP);
        String storeName = getStoreMap().get(storeId);
        List<BasketUnit> basketUnits = getBasketByClientId(clientId);

        //Покупка возможна, проверяем наличие товара в выбранном магазине
        List<Long> productIdsOutStore = productCountRepository.getProductIdsOutStore(clientId, storeId);

        if (productIdsOutStore.isEmpty()) { //В выбранном магазине товар в наличии!
            Order order = orderRepository.save(ord);
            List<OrderItem> orderItems = new ArrayList<>(basketUnits.size());

            ProductsRmqDto productsRmqDto = new ProductsRmqDto();
            List<ProductRmqDto> productRmqDtos = new ArrayList<>(basketUnits.size());
            productsRmqDto.setProductRmqDtos(productRmqDtos);

            for (BasketUnit basketUnit : basketUnits) {
                ProductRmqDto productRmqDto = new ProductRmqDto();
                productRmqDto.setProductId(basketUnit.getProductId());
                productRmqDto.setQuantity(basketUnit.getQuantity());
                productRmqDto.setStoreId(storeId);
                productRmqDto.setOrderId(order.getId());
                productRmqDtos.add(productRmqDto);

                OrderItem orderItem = new OrderItem();
                orderItem.setOrderId(order.getId());
                orderItem.setProductId(basketUnit.getProductId());
                orderItem.setProductName(basketUnit.getProductName());
                orderItem.setQuantity(basketUnit.getQuantity());
                orderItem.setPrice(basketUnit.getProductPrice());
                orderItem.setStoreId(storeId);
                BigDecimal multiply = orderItem.getPrice()
                        .multiply(new BigDecimal(String.valueOf(orderItem.getQuantity())));
                summaryCost = summaryCost.add(multiply);
                orderItems.add(orderItem);
            }
            List<OrderItem> orderItemsSave = orderItemRepository.saveAll(orderItems);

            //Создаём DTO ответа
            OrderDto orderDto = new OrderDto();
            orderDto.setOrderNumber(order.getId());
            orderDto.setSummaryCost(summaryCost);
            orderDto.setStore(storeName);
            List<OrderItemDto> orderItemDtos = MapperUtil.mapToOrderItemDtoList(orderItemsSave);
            orderDto.setOrderItemDtos(orderItemDtos);

            //Очищаем корзину после формирования заказа
            basketRepository.deleteAllByClientId(clientId);

            //Уменьшаем количество интересующихся и количество товара
            List<Long> productIds = basketUnits.stream().map(BasketUnit::getProductId).collect(Collectors.toList());
            List<ProductCount> pcListAllStores = productCountRepository.findAllByProductIdIn(productIds);

            List<ProductCount> forDel = new ArrayList<>();
            List<ProductCount> forChange = new ArrayList<>();

            Map<Long, Integer> tempBasketQuantity = basketUnits.stream()
                    .collect(Collectors.toMap(BasketUnit::getProductId, BasketUnit::getQuantity));
            for (ProductCount productCount : pcListAllStores) {
                if (productCount.getNumberOfInterestedClients() - 1 == 0) {
                    forDel.add(productCount);
                } else {
                    if (productCount.getStoreId().equals(storeId)){
                        Integer basketQuantity = tempBasketQuantity.get(productCount.getProductId());
                        productCount.setQuantity(productCount.getQuantity() - basketQuantity);
                    }
                    productCount.setNumberOfInterestedClients(productCount.getNumberOfInterestedClients()-1);
                    forChange.add(productCount);
                }
            }
            if (!forChange.isEmpty()) {
                productCountRepository.saveAll(forChange);
            }
            if (!forDel.isEmpty()) {
                productCountRepository.deleteAll(forDel);
            }

            //отправляем на склад изменения количестова товаров
            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_DIRECT,
                    RabbitConfig.QUEUE_PRODUCTS, productsRmqDto);
            return orderDto;

        } else { //В выбранном магазине товара нет, выбираем там где больше и заказываем

            List<Long> productIds = basketUnits.stream().
                    map(BasketUnit::getProductId).collect(Collectors.toList());

            ProductsRmqDto productsRmqDto = new ProductsRmqDto();
            List<ProductRmqDto> productRmqDtos = new ArrayList<>(basketUnits.size());
            List<OrderItem> orderItems = new ArrayList<>(basketUnits.size());

            //По этой метке отслеживается необходимость доставки
            ord.setDeliveryTo(storeId);
            Order order = orderRepository.save(ord);

            //Обрабатываем лист с отсутствующими продуктами в магазине
            for (Long productId : productIdsOutStore) {
                BasketUnit basketUnit = basketUnits.stream().filter(bu -> bu.getProductId().equals(productId))
                        .findFirst().get();
                BigDecimal multiply = basketUnit.getProductPrice()
                        .multiply(new BigDecimal(String.valueOf(basketUnit.getQuantity())));
                summaryCost = summaryCost.add(multiply);

                List<Long> stores = productCountRepository.getAllStoreIdWithPositiveQuantity(productId);
                Integer productIdQuantity = basketUnit.getQuantity();
                while (productIdQuantity != 0) {
                    for (Long idStore : stores) {
                        ProductRmqDto productRmqDto = new ProductRmqDto();
                        productRmqDto.setProductId(productId);
                        productRmqDto.setStoreId(idStore);
                        productRmqDto.setOrderId(order.getId());

                        OrderItem orderItem = new OrderItem();
                        orderItem.setOrderId(order.getId());
                        orderItem.setProductId(productId);
                        orderItem.setProductName(basketUnit.getProductName());
                        orderItem.setPrice(basketUnit.getProductPrice());
                        orderItem.setStoreId(idStore);
                        ProductCount pc = productCountRepository.findByProductIdAndStoreId(productId, idStore);
                        if (productIdQuantity <= pc.getQuantity()) {
                            productRmqDto.setQuantity(productIdQuantity);
                            productRmqDtos.add(productRmqDto);

                            orderItem.setQuantity(productIdQuantity);
                            orderItems.add(orderItem);
                            productIdQuantity = 0;
                            break;
                        } else {
                            productRmqDto.setQuantity(pc.getQuantity());
                            productRmqDtos.add(productRmqDto);

                            orderItem.setQuantity(pc.getQuantity());
                            orderItems.add(orderItem);
                            productIdQuantity -= pc.getQuantity();
                        }
                    }
                }
                //Удаляем из корзины позиции которых нет в выбранном магазине
                basketUnits.removeIf(bu -> bu.getProductId().equals(productId));
            }

            //Обрабатываем лист в котором остались продукты выбранного магазина
            for (BasketUnit basketUnit : basketUnits) {
                ProductRmqDto productRmqDto = new ProductRmqDto();
                productRmqDto.setProductId(basketUnit.getProductId());
                productRmqDto.setQuantity(basketUnit.getQuantity());
                productRmqDto.setStoreId(storeId);
                productRmqDto.setOrderId(order.getId());
                productRmqDtos.add(productRmqDto);

                OrderItem orderItem = new OrderItem();
                orderItem.setOrderId(order.getId());
                orderItem.setProductId(basketUnit.getProductId());
                orderItem.setProductName(basketUnit.getProductName());
                orderItem.setQuantity(basketUnit.getQuantity());
                orderItem.setPrice(basketUnit.getProductPrice());
                orderItem.setStoreId(storeId);
                BigDecimal multiply = basketUnit.getProductPrice()
                        .multiply(new BigDecimal(String.valueOf(basketUnit.getQuantity())));
                summaryCost = summaryCost.add(multiply);
                orderItems.add(orderItem);
            }
            List<OrderItem> orderItemsSave = orderItemRepository.saveAll(orderItems);

            //Создаём DTO ответа
            List<OrderItemDto> orderItemDtos = MapperUtil.mapToOrderItemDtoList(orderItemsSave);
            OrderDto orderDto = new OrderDto();
            orderDto.setOrderNumber(order.getId());
            orderDto.setSummaryCost(summaryCost);
            orderDto.setStore(storeName);
            orderDto.setOrderItemDtos(orderItemDtos);

            //Очищаем корзину после формирования заказа
            basketRepository.deleteAllByClientId(clientId);

            //Уменьшаем количество интересующихся и количество товара
            List<ProductCount> pcList = productCountRepository.findAllByProductIdIn(productIds);

            List<ProductCount> forDel = new ArrayList<>();
            List<ProductCount> forChange = new ArrayList<>();

            for (ProductCount productCount : pcList) {
                if (productCount.getNumberOfInterestedClients() - 1 == 0) {
                    forDel.add(productCount);
                } else {
                    productCount.setNumberOfInterestedClients(productCount.getNumberOfInterestedClients() - 1);

                    OrderItem orderItem = orderItems.stream()
                            .filter(oi -> oi.getProductId().equals(productCount.getProductId()))
                            .filter(oi -> oi.getStoreId().equals(productCount.getStoreId()))
                            .findFirst().get();
                    productCount.setQuantity(productCount.getQuantity() - orderItem.getQuantity());
                    forChange.add(productCount);
                }
            }
            if (!forChange.isEmpty()) {
                productCountRepository.saveAll(forChange);
            }
            if (!forDel.isEmpty()) {
                productCountRepository.deleteAll(forDel);
            }

            productsRmqDto.setProductRmqDtos(productRmqDtos);
            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_DIRECT,
                    RabbitConfig.QUEUE_PRODUCTS, productsRmqDto);

            return orderDto;
        }
    }

    @Cacheable(CACHE_NAME)
    public Map<Long,String> getStoreMap(){
        List<Store> allStores = storeRepository.findAll();
        return allStores.stream().collect(Collectors.toMap(Store::getId, Store::getName));
    }

    @Transactional
    public void delete(BasketUnitDelDto basketUnitDelDto) {
        //Проверяем имеется ли подобный заказ у клиента
        BasketUnit buCheck = basketRepository.getByClientIdAndProductId(basketUnitDelDto.getClientId(),
                basketUnitDelDto.getProductId());
        if (buCheck == null) {
            throw new BasketUnitNotFoundException("Product id - "+basketUnitDelDto.getProductId()+
                    " was not found in basket's client id - "+basketUnitDelDto.getClientId());
        }
        basketRepository.delete(buCheck);

        //Проверяем интересуется ли ещё кто-то данным заказом
        List<ProductCount> pcListCheck = productCountRepository
                .findAllByProductId(basketUnitDelDto.getProductId());

        if (pcListCheck.get(0).getNumberOfInterestedClients()-1 == 0) {
            productCountRepository.deleteAll(pcListCheck);
        } else {
            pcListCheck.forEach(pc->pc.setNumberOfInterestedClients(pc.getNumberOfInterestedClients()-1));
            productCountRepository.saveAll(pcListCheck);
        }
    }

    public void changeGoodsQuantity(BasketUnitQuantityDto basketUnitQuantityDto) {
        //Проверяем имеется ли подобный заказ у клиента
        BasketUnit buCheck = basketRepository.getByClientIdAndProductId(basketUnitQuantityDto.getClientId(),
                basketUnitQuantityDto.getProductId());
        if (buCheck == null) {
            throw new BasketUnitNotFoundException("Product id - "+basketUnitQuantityDto.getProductId()+
                    " was not found in basket's client id - "+basketUnitQuantityDto.getClientId());
        }

        if (basketUnitQuantityDto.getAction()){
            buCheck.setQuantity(buCheck.getQuantity()+1);
        } else {
            if (buCheck.getQuantity()-1 == 0) return;
            buCheck.setQuantity(buCheck.getQuantity()-1);
        }
        basketRepository.save(buCheck);
    }
}
