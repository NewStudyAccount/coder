package com.admin.controller;

import com.admin.common.Result;
import com.admin.entity.Product;
import com.admin.entity.ProductSku;
import com.admin.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/list")
    public Result<List<Product>> list(@RequestParam(required = false) String name,
                                      @RequestParam(required = false) Long categoryId,
                                      @RequestParam(required = false) Integer status) {
        List<Product> list = productService.getList(name, categoryId, status);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<Product> getById(@PathVariable Long id) {
        Product product = productService.getById(id);
        return Result.success(product);
    }

    @PostMapping
    public Result<Void> add(@RequestBody Product product) {
        productService.add(product);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Product product) {
        productService.update(product);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteById(@PathVariable Long id) {
        productService.deleteById(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        productService.deleteBatch(ids);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        if (status == null) {
            return Result.error("状态不能为空");
        }
        productService.updateStatus(id, status);
        return Result.success();
    }

    @GetMapping("/{productId}/sku")
    public Result<List<ProductSku>> getSkuList(@PathVariable Long productId) {
        List<ProductSku> skuList = productService.getSkuList(productId);
        return Result.success(skuList);
    }
}
