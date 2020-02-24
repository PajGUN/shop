package ru.sunlab.shop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sunlab.shop.dto.MapperUtil;
import ru.sunlab.shop.dto.ProductDto;
import ru.sunlab.shop.model.Product;
import ru.sunlab.shop.service.ProductService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/1.0/product")
public class ProductController {

    private final ProductService productService;
    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/add")
    public ResponseEntity<ProductDto> add(@Valid @RequestBody Product product){
        Product p = productService.add(product);
        if (p == null) return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        return ResponseEntity.ok(MapperUtil.mapToProductDto(p));
    }

    @GetMapping("/get/{productId}")
    public ResponseEntity<ProductDto> getById(@PathVariable Long productId){
        Product product = productService.getById(productId);
        return ResponseEntity.ok(MapperUtil.mapToProductDto(product));
    }

    @PutMapping("/update/{productId}")
    ResponseEntity<ProductDto> update(@PathVariable Long productId,
                                     @Valid @RequestBody Product product){
        Product p = productService.update(productId, product);
        return ResponseEntity.ok(MapperUtil.mapToProductDto(p));
    }

    @DeleteMapping("/delete/{productId}")
    ResponseEntity<ProductDto> delete(@PathVariable Long productId){
        Product product = productService.delete(productId);
        return ResponseEntity.ok(MapperUtil.mapToProductDto(product));
    }

    @GetMapping("/getbytype/{type}")
    ResponseEntity<List<ProductDto>> getByType(@PathVariable String type,
            @PageableDefault(sort = {"name"}, direction = Sort.Direction.ASC) Pageable pageable){
        List<Product> products = productService.getByType(type, pageable);
        if (products.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(MapperUtil.mapToListProductDto(products));
    }

    @GetMapping("/getallbyproductname/{productName}")
    public ResponseEntity<List<ProductDto>> getAllByProductName(@PathVariable String productName,
            @PageableDefault(sort = {"name"}, direction = Sort.Direction.ASC) Pageable pageable){
        List<Product> products = productService.getByProductByName(productName, pageable);
        if (products.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(MapperUtil.mapToListProductDto(products));
    }

}
