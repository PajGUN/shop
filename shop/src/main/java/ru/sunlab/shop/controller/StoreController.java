package ru.sunlab.shop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sunlab.shop.dto.MapperUtil;
import ru.sunlab.shop.dto.StoreDto;
import ru.sunlab.shop.model.Store;
import ru.sunlab.shop.service.StoreService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/1.0/store")
public class StoreController {

    private final StoreService storeService;

    @Autowired
    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @PostMapping("/add")
    ResponseEntity<StoreDto> add(@Valid @RequestBody Store store){
        Store s = storeService.save(store);
        if (s == null) return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        return ResponseEntity.ok(MapperUtil.mapToStoreDto(s));
    }

    @GetMapping("/get/{storeId}")
    ResponseEntity<StoreDto> getById(@PathVariable Long storeId){
        Store store = storeService.getById(storeId);
        return ResponseEntity.ok(MapperUtil.mapToStoreDto(store));
    }

    @PutMapping("/update/{storeId}")
    ResponseEntity<StoreDto> update(@PathVariable Long storeId,
                                     @Valid @RequestBody Store store){
        Store s = storeService.update(storeId, store);
        return ResponseEntity.ok(MapperUtil.mapToStoreDto(s));
    }

    @DeleteMapping("/delete/{storeId}")
    ResponseEntity<StoreDto> delete(@PathVariable Long storeId){
        Store store = storeService.delete(storeId);
        return ResponseEntity.ok(MapperUtil.mapToStoreDto(store));
    }

    @GetMapping("/all")
    public ResponseEntity<List<StoreDto>> getAllStores(){
        List<Store> stores = storeService.getAllStores();
        if (stores.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(MapperUtil.mapToListStoreDto(stores));
    }
}
