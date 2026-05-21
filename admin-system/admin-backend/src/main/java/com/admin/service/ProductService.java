package com.admin.service;

import com.admin.entity.Product;
import com.admin.entity.ProductSku;
import com.admin.mapper.ProductMapper;
import com.admin.mapper.ProductSkuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductSkuMapper productSkuMapper;

    public List<Product> getList(String name, Long categoryId, Integer status) {
        return productMapper.selectList(name, categoryId, status);
    }

    public Product getById(Long id) {
        Product product = productMapper.selectById(id);
        if (product != null) {
            product.setSkuList(productSkuMapper.selectByProductId(id));
        }
        return product;
    }

    @Transactional
    public void add(Product product) {
        product.setStatus(product.getStatus() != null ? product.getStatus() : 1);
        product.setSort(product.getSort() != null ? product.getSort() : 0);
        product.setStock(product.getStock() != null ? product.getStock() : 0);
        product.setSales(product.getSales() != null ? product.getSales() : 0);
        productMapper.insert(product);
        saveSkus(product);
    }

    @Transactional
    public void update(Product product) {
        productMapper.update(product);
        productSkuMapper.deleteByProductId(product.getId());
        saveSkus(product);
    }

    private void saveSkus(Product product) {
        List<ProductSku> skuList = product.getSkuList();
        if (skuList != null && !skuList.isEmpty()) {
            for (ProductSku sku : skuList) {
                sku.setProductId(product.getId());
                sku.setStatus(sku.getStatus() != null ? sku.getStatus() : 1);
            }
            productSkuMapper.batchInsert(skuList);
        }
    }

    @Transactional
    public void deleteById(Long id) {
        productSkuMapper.deleteByProductId(id);
        productMapper.deleteById(id);
    }

    @Transactional
    public void deleteBatch(List<Long> ids) {
        for (Long id : ids) {
            productSkuMapper.deleteByProductId(id);
        }
        productMapper.deleteBatch(ids);
    }

    @Transactional
    public void updateStatus(Long id, Integer status) {
        productMapper.updateStatus(id, status);
    }

    public List<ProductSku> getSkuList(Long productId) {
        return productSkuMapper.selectByProductId(productId);
    }
}
