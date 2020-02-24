package ru.sunlab.shop.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sunlab.shop.exception.ProductCountNotFoundException;
import ru.sunlab.shop.exception.StoreNotFoundException;
import ru.sunlab.shop.model.ProductCount;
import ru.sunlab.shop.model.Store;
import ru.sunlab.shop.repository.StoreRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class StoreService {

    private final StoreRepository storeRepository;

    @Autowired
    public StoreService(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    @Transactional
    public Store save(Store store) {
        return storeRepository.save(store);
    }

    @Transactional
    public Store getById(Long storeId) {
        Optional<Store> storeOptional = storeRepository.findById(storeId);
        return storeOptional.orElseThrow(()-> new StoreNotFoundException("Store id - "+
                storeId + " was not found!"));
    }

    @Transactional
    public Store update(Long storeId, Store store) {
        Optional<Store> storeOptional = storeRepository.findById(storeId);
        if (storeOptional.isPresent()){
            store.setId(storeId);
            return storeRepository.save(store);
        } else {
            throw new StoreNotFoundException("Store id - "+ storeId + " was not found");
        }
    }

    @Transactional
    public Store delete(Long storeId) {
        Optional<Store> storeOptional = storeRepository.findById(storeId);
        if (storeOptional.isPresent()){
            storeRepository.deleteById(storeId);
            return storeOptional.get();
        } else {
            throw new StoreNotFoundException("Store id - "+ storeId + " was not found");
        }
    }

    @Transactional
    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }
}
