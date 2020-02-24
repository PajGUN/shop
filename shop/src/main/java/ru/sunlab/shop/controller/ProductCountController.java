package ru.sunlab.shop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sunlab.shop.dto.MapperUtil;
import ru.sunlab.shop.dto.ProductCountChangeDto;
import ru.sunlab.shop.dto.ProductCountDto;
import ru.sunlab.shop.dto.ProductCountViewDto;
import ru.sunlab.shop.model.ProductCount;
import ru.sunlab.shop.service.ProductCountService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/1.0/count")
public class ProductCountController {

    private final ProductCountService countService;
    @Autowired
    public ProductCountController(ProductCountService countService) {
        this.countService = countService;
    }

    @PostMapping("/add")
    ResponseEntity<ProductCountDto> add(@Valid  @RequestBody ProductCount count){
        ProductCount c = countService.save(count);
        if (c == null) return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        return ResponseEntity.ok(MapperUtil.mapToProductCountDto(c));
    }

    @GetMapping("/get/{countId}")
    ResponseEntity<ProductCountDto> getById(@PathVariable Long countId){
        ProductCount count = countService.getById(countId);
        return ResponseEntity.ok(MapperUtil.mapToProductCountDto(count));
    }

    @PutMapping("/update/{countId}")
    ResponseEntity<ProductCountDto> update(@PathVariable Long countId,
                                     @Valid @RequestBody ProductCount count){
        ProductCount c = countService.update(countId, count);
        return ResponseEntity.ok(MapperUtil.mapToProductCountDto(c));
    }

    @DeleteMapping("/delete/{countId}")
    ResponseEntity<ProductCountDto> delete(@PathVariable Long countId){
        ProductCount count = countService.delete(countId);
        return ResponseEntity.ok(MapperUtil.mapToProductCountDto(count));
    }

    @PutMapping("/changequantity")
    public ResponseEntity<List<ProductCountDto>> changeQuantity(@Valid @RequestBody List<ProductCountChangeDto> counts){
        List<ProductCount> productCounts = countService.changeQuantity(counts);
        if (productCounts.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(MapperUtil.mapToProductCountListDto(productCounts));
    }

    @GetMapping("/getallbyproductid/{productId}")
    public ResponseEntity<List<ProductCountViewDto>> getAllProductCountByProductId(
            @PathVariable Long productId){
        List<ProductCountViewDto> countDtos = countService.getAllProductCountByProductId(productId);
        if (countDtos.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(countDtos);
    }


}
