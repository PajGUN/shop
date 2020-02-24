package ru.sunlab.shop.service;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sunlab.shop.configuration.RabbitConfig;
import ru.sunlab.shop.dto.ProductCountChangeDto;
import ru.sunlab.shop.dto.ProductCountViewDto;
import ru.sunlab.shop.dto.rabbit.ProductRmqDto;
import ru.sunlab.shop.dto.rabbit.ProductsRmqDto;
import ru.sunlab.shop.exception.ProductCountNotFoundException;
import ru.sunlab.shop.exception.ProductNotFoundException;
import ru.sunlab.shop.exception.RabbitException;
import ru.sunlab.shop.model.Product;
import ru.sunlab.shop.model.ProductCount;
import ru.sunlab.shop.model.Store;
import ru.sunlab.shop.repository.ProductCountRepository;
import ru.sunlab.shop.repository.ProductRepository;
import ru.sunlab.shop.repository.StoreRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductCountService {

    private static final String CACHE_NAME = "stores";

    private final ProductCountRepository countRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public ProductCountService(ProductCountRepository countRepository,
                               ProductRepository productRepository,
                               StoreRepository storeRepository,
                               RabbitTemplate rabbitTemplate) {
        this.countRepository = countRepository;
        this.productRepository = productRepository;
        this.storeRepository = storeRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Transactional
    public List<ProductCountViewDto> getAllProductCountByProductId(Long productId) {
        List<ProductCount> productCounts = countRepository.findAllByProductId(productId);
        List<ProductCountViewDto> listClientViewDto = new ArrayList<>();
        for (ProductCount productCount : productCounts) {
            ProductCountViewDto clientViewDto = new ProductCountViewDto();
            clientViewDto.setStore(getStoreMap().get(productCount.getStoreId()));
            String count;
            if (productCount.getQuantity() == 0) count = "нет";
            else if (productCount.getQuantity() <= 5) count = "мало";
            else count = "много";
            clientViewDto.setCount(count);
            listClientViewDto.add(clientViewDto);
        }
        return listClientViewDto;
    }

    @Cacheable(CACHE_NAME)
    public Map<Long,String> getStoreMap(){
        List<Store> allStores = storeRepository.findAll();
        return allStores.stream().collect(Collectors.toMap(Store::getId, Store::getName));
    }

    @Transactional
    public ProductCount save(ProductCount count) {
        Optional<Product> productOptional = productRepository.findById(count.getProductId());
        productOptional.orElseThrow(()-> new ProductNotFoundException("ProductCount id - " +
                + count.getProductId() + " was not found! The SAVE operation is not possible"));
        return countRepository.save(count);
    }

    @Transactional
    public ProductCount getById(Long countId) {
        Optional<ProductCount> pcOptional = countRepository.findById(countId);
        return pcOptional.orElseThrow(()-> new ProductCountNotFoundException("ProductCount id - "+
                countId +" was not found!"));
    }

    @Transactional
    public ProductCount update(Long countId, ProductCount count) {
        Optional<ProductCount> pcOptional = countRepository.findById(countId);
        if (pcOptional.isPresent()){
            count.setId(countId);
            return countRepository.save(count);
        } else {
            throw new ProductCountNotFoundException("ProductCount id - "+
                    countId +" was not found!");
        }
    }

    @Transactional
    public ProductCount delete(Long countId) {
        Optional<ProductCount> pcOptional = countRepository.findById(countId);
        if (pcOptional.isPresent()){
            countRepository.deleteById(countId);
            return pcOptional.get();
        } else {
            throw new ProductCountNotFoundException("ProductCount id - "+
                    countId +" was not found!");
        }
    }

    @Transactional
    public void processingOrderItems(ProductsRmqDto products, Channel channel, long tag){
        List<ProductCount> productCounts = new ArrayList<>(products.getProductRmqDtos().size());
        List<ProductRmqDto> errorProdCounts = new ArrayList<>();

        for (ProductRmqDto product : products.getProductRmqDtos()) {
            ProductCount pc = countRepository.findByProductIdAndStoreId(product.getProductId(), product.getStoreId());
            if (products.getAction()){
                //Прибавляем
                pc.setQuantity(pc.getQuantity() + product.getQuantity());
            } else {
                //Вычитаем
                Integer quantity = pc.getQuantity() - product.getQuantity();
                if (quantity < 0) {
                    product.setQuantity(quantity);
                    errorProdCounts.add(product);
                    continue;
                }
                pc.setQuantity(quantity);
            }
            productCounts.add(pc);
        }
        countRepository.saveAll(productCounts);

        if (!errorProdCounts.isEmpty()){
            for (ProductRmqDto errorPC : errorProdCounts) {
                log.error("Product id - "+ errorPC.getProductId() + " in Store - "+
                        errorPC.getStoreId() + " has negative quantity - "+errorPC.getQuantity()+
                        ". Order id - "+errorPC.getOrderId());
            }
            //отправляем email или иным способом уведомляем менеджеров что клиент
            //не сможет купить товар
        }
        try {
            channel.basicAck(tag,false);
        } catch (IOException e) {
            throw new RabbitException("Rabbit's basicReject was not sent");
        }
    }

    /*
    *   Метод для изменения количества товаров на складе. Если мы прибавляем товар то проблем нет.
    *   При вычитании товара (был утерен, сломан, съеден крысами) может возникнуть ситуация что товар уже купили,
    *   в этом случае менеджер ручками разбирается что и как.
    */
    @Transactional
    public List<ProductCount> changeQuantity(List<ProductCountChangeDto> counts) {
        List<ProductCount> productCounts = new ArrayList<>(counts.size());
        List<ProductCountChangeDto> errorProdCounts = new ArrayList<>();

        for (ProductCountChangeDto productCount : counts) {
            ProductCount pc = countRepository.findByProductIdAndStoreId(productCount.getProductId(),
                    productCount.getStoreId());
            if (productCount.getAction()){
                pc.setQuantity(pc.getQuantity() + productCount.getQuantity());
            } else {
                Integer quantity = pc.getQuantity() - productCount.getQuantity();
                if (quantity < 0) {
                    productCount.setQuantity(quantity);
                    errorProdCounts.add(productCount);
                    continue;
                }
                pc.setQuantity(quantity);
            }
            productCounts.add(pc);
        }
        countRepository.saveAll(productCounts);

        if (!errorProdCounts.isEmpty()){
            for (ProductCountChangeDto errorPC : errorProdCounts) {
                log.error("Product id - "+ errorPC.getProductId() + " in Store - "+
                        errorPC.getStoreId() + " has negative quantity - "+errorPC.getQuantity());
            }
            //отправляем email или иным способом уведомляем менеджеров что клиент
            //не сможет купить товар
        }
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_DIRECT, RabbitConfig.QUEUE_BASKET,counts);
        return productCounts;
    }
}
