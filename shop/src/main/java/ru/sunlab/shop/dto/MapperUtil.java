package ru.sunlab.shop.dto;

import ru.sunlab.shop.model.Product;
import ru.sunlab.shop.model.ProductCount;
import ru.sunlab.shop.model.Store;

import java.util.ArrayList;
import java.util.List;

public class MapperUtil {

    public static ProductDto mapToProductDto(Product product) {
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setArticleNumber(product.getArticleNumber());
        productDto.setType(product.getType().name());
        productDto.setName(product.getName());
        productDto.setDescription(product.getDescription());
        productDto.setPrice(product.getPrice());
        return productDto;
    }

    public static List<ProductDto> mapToListProductDto(List<Product> products) {
        List<ProductDto> productDtos = new ArrayList<>(products.size());
        for (Product product : products) {
            productDtos.add(mapToProductDto(product));
        }
        return productDtos;
    }

    public static StoreDto mapToStoreDto(Store store) {
        StoreDto storeDto = new StoreDto();
        storeDto.setId(store.getId());
        storeDto.setName(store.getName());
        storeDto.setAddress(store.getAddress());
        return storeDto;
    }

    public static List<StoreDto> mapToListStoreDto(List<Store> stores) {
        List<StoreDto> storeDtos = new ArrayList<>(stores.size());
        for (Store store : stores) {
            storeDtos.add(mapToStoreDto(store));
        }
        return storeDtos;
    }

    public static ProductCountDto mapToProductCountDto(ProductCount productCount) {
        ProductCountDto productCountDto = new ProductCountDto();
        productCountDto.setId(productCount.getId());
        productCountDto.setProductId(productCount.getProductId());
        productCountDto.setQuantity(productCount.getQuantity());
        productCountDto.setStoreId(productCount.getStoreId());

        return productCountDto;
    }

    public static List<ProductCountDto> mapToProductCountListDto(List<ProductCount> productCounts){
        List<ProductCountDto> productCountDtos = new ArrayList<>(productCounts.size());
        for (ProductCount productCount : productCounts) {
            productCountDtos.add(mapToProductCountDto(productCount));
        }
        return productCountDtos;
    }
}
