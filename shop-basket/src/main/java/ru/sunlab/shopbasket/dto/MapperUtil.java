package ru.sunlab.shopbasket.dto;

import ru.sunlab.shopbasket.dto.rabbit.CardOrderRmqDto;
import ru.sunlab.shopbasket.dto.rabbit.OrderItemRmqDto;
import ru.sunlab.shopbasket.model.*;

import java.util.ArrayList;
import java.util.List;

public class MapperUtil {

    public static ClientDto mapToClientDto(Client client){
        ClientDto clientDto = new ClientDto();
        clientDto.setId(client.getId());
        clientDto.setFirstName(client.getFirstName());
        clientDto.setLastName(client.getLastName());
        clientDto.setMiddleName(client.getMiddleName());
        clientDto.setBirthday(client.getBirthday());
        clientDto.setCreated(client.getCreated());
        clientDto.setEmail(client.getEmail());
        clientDto.setPhoneNumber(client.getPhoneNumber());
        return clientDto;
    }

    public static List<ClientDto> mapToListClientDto(List<Client> clients) {
        ArrayList<ClientDto> clientDtos = new ArrayList<>(clients.size());
        for (Client client : clients) {
            clientDtos.add(mapToClientDto(client));
        }
        return clientDtos;
    }



    public static BasketUnit mapToBasketUnit(BasketUnitDto basketUnitDto){
        BasketUnit basketUnit = new BasketUnit();
        basketUnit.setClientId(basketUnitDto.getClientId());
        basketUnit.setProductId(basketUnitDto.getProductId());
        basketUnit.setProductName(basketUnitDto.getProductName());
        basketUnit.setProductPrice(basketUnitDto.getProductPrice());
//        basketUnit.setQuantity(1);
        return basketUnit;
    }

    public static List<ProductCount> mapToProductCountList(List<ProductCountDto> productCountDtos){
        List<ProductCount> productCounts = new ArrayList<>(productCountDtos.size());
        for (ProductCountDto productCountDto : productCountDtos) {
            ProductCount productCount = new ProductCount();
            productCount.setProductId(productCountDto.getProductId());
            productCount.setQuantity(productCountDto.getQuantity());
            productCount.setStoreId(productCountDto.getStoreId());
            productCount.setNumberOfInterestedClients(1);
            productCounts.add(productCount);
        }
        return productCounts;
    }

    public static List<BasketViewDto> mapToBasketViewDto(List<BasketUnit> basketList) {
        List<BasketViewDto> basketViewDtos = new ArrayList<>(basketList.size());
        for (BasketUnit basketUnit : basketList) {
            BasketViewDto basketViewDto = new BasketViewDto();
            basketViewDto.setProductName(basketUnit.getProductName());
            basketViewDto.setProductPrice(basketUnit.getProductPrice());
            basketViewDto.setQuantity(basketUnit.getQuantity());
            basketViewDtos.add(basketViewDto);
        }
        return basketViewDtos;
    }

    public static List<OrderItemDto> mapToOrderItemDtoList(List<OrderItem> orderItems) {
        List<OrderItemDto> orderItemDtos = new ArrayList<>(orderItems.size());
        for (OrderItem orderItem : orderItems) {
            OrderItemDto orderItemDto = new OrderItemDto();
            orderItemDto.setProductName(orderItem.getProductName());
            orderItemDto.setQuantity(orderItem.getQuantity());
            orderItemDto.setPrice(orderItem.getPrice());
            orderItemDtos.add(orderItemDto);
        }
        return orderItemDtos;
    }

    public static CardOrderRmqDto mapToCardOrderRmqDto(CardOrderDto cardOrderDto){
        CardOrderRmqDto cardOrderRmqDto = new CardOrderRmqDto();
        cardOrderRmqDto.setOrderId(cardOrderDto.getOrderId());
        cardOrderRmqDto.setCardNumber(cardOrderDto.getCardNumber());
        cardOrderRmqDto.setCardHolderName(cardOrderDto.getCardHolderName());
        cardOrderRmqDto.setValidMonth(cardOrderDto.getValidMonth());
        cardOrderRmqDto.setValidYear(cardOrderDto.getValidYear());
        cardOrderRmqDto.setCvc(cardOrderDto.getCvc());
        return cardOrderRmqDto;
    }

    public static OrderItemRmqDto mapToOrderItemRmqDto(OrderItem orderItem){
        OrderItemRmqDto orderItemRmqDto = new OrderItemRmqDto();
        orderItemRmqDto.setProductId(orderItem.getProductId());
        orderItemRmqDto.setProductName(orderItem.getProductName());
        orderItemRmqDto.setQuantity(orderItem.getQuantity());
        orderItemRmqDto.setPrice(orderItem.getPrice());
        orderItemRmqDto.setStoreId(orderItem.getStoreId());
        return orderItemRmqDto;
    }
}
