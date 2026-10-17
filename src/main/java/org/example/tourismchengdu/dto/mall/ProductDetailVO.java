package org.example.tourismchengdu.dto.mall;

import org.example.tourismchengdu.entity.Product;
import org.example.tourismchengdu.util.JsonUtils;

import java.util.List;

public class ProductDetailVO extends ProductSummaryVO {

    private String description;
    private List<String> images;

    public static ProductDetailVO from(Product product) {
        ProductDetailVO vo = new ProductDetailVO();
        ProductSummaryVO summary = ProductSummaryVO.from(product);
        vo.setId(summary.getId());
        vo.setName(summary.getName());
        vo.setPrice(summary.getPrice());
        vo.setOriginalPrice(summary.getOriginalPrice());
        vo.setStock(summary.getStock());
        vo.setSalesCount(summary.getSalesCount());
        vo.setCoverImage(summary.getCoverImage());
        vo.setCategory(summary.getCategory());
        vo.setDescription(product.getDescription());
        vo.setImages(JsonUtils.parseStringList(product.getImages()));
        return vo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
