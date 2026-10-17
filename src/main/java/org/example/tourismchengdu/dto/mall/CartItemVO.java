package org.example.tourismchengdu.dto.mall;

import org.example.tourismchengdu.entity.Cart;
import org.example.tourismchengdu.entity.Product;

import java.math.BigDecimal;

public class CartItemVO {

    private Long id;
    private Long productId;
    private String productName;
    private BigDecimal productPrice;
    private Integer quantity;
    private Boolean selected;
    private String coverImage;
    private Integer stock;

    public static CartItemVO from(Cart cart, Product product) {
        CartItemVO vo = new CartItemVO();
        vo.setId(cart.getId());
        vo.setProductId(cart.getProductId());
        vo.setProductName(cart.getProductName());
        vo.setProductPrice(cart.getProductPrice());
        vo.setQuantity(cart.getQuantity());
        vo.setSelected(cart.getSelected() != null && cart.getSelected() == 1);
        if (product != null) {
            vo.setCoverImage(product.getCoverImage());
            vo.setStock(product.getStock());
        }
        return vo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
