package org.example.tourismchengdu.service.impl;

import org.example.tourismchengdu.common.BusinessException;
import org.example.tourismchengdu.common.ResultCode;
import org.example.tourismchengdu.dto.mall.AddCartRequest;
import org.example.tourismchengdu.dto.mall.CartItemVO;
import org.example.tourismchengdu.dto.mall.UpdateCartRequest;
import org.example.tourismchengdu.entity.Cart;
import org.example.tourismchengdu.entity.Product;
import org.example.tourismchengdu.mapper.CartMapper;
import org.example.tourismchengdu.mapper.ProductMapper;
import org.example.tourismchengdu.service.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final CartMapper cartMapper;
    private final ProductMapper productMapper;

    public CartServiceImpl(CartMapper cartMapper, ProductMapper productMapper) {
        this.cartMapper = cartMapper;
        this.productMapper = productMapper;
    }

    @Override
    public List<CartItemVO> listCart(Long userId) {
        return cartMapper.findByUserId(userId).stream()
                .map(cart -> CartItemVO.from(cart, productMapper.findById(cart.getProductId())))
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CartItemVO addToCart(Long userId, AddCartRequest request) {
        Product product = requireAvailableProduct(request.getProductId());
        ensureStockEnough(product, request.getQuantity());

        Cart existing = cartMapper.findByUserIdAndProductId(userId, product.getId());
        if (existing != null) {
            int newQuantity = existing.getQuantity() + request.getQuantity();
            ensureStockEnough(product, newQuantity);
            existing.setProductName(product.getName());
            existing.setProductPrice(product.getPrice());
            existing.setQuantity(newQuantity);
            cartMapper.update(existing);
            return CartItemVO.from(existing, product);
        }

        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setProductId(product.getId());
        cart.setProductName(product.getName());
        cart.setProductPrice(product.getPrice());
        cart.setQuantity(request.getQuantity());
        cart.setSelected(1);
        cartMapper.insert(cart);
        return CartItemVO.from(cart, product);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CartItemVO updateCartItem(Long userId, Long cartId, UpdateCartRequest request) {
        Cart cart = requireUserCartItem(userId, cartId);
        Product product = requireAvailableProduct(cart.getProductId());
        ensureStockEnough(product, request.getQuantity());

        cart.setProductName(product.getName());
        cart.setProductPrice(product.getPrice());
        cart.setQuantity(request.getQuantity());
        if (request.getSelected() != null) {
            cart.setSelected(Boolean.TRUE.equals(request.getSelected()) ? 1 : 0);
        }
        cartMapper.update(cart);
        return CartItemVO.from(cart, product);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeCartItem(Long userId, Long cartId) {
        requireUserCartItem(userId, cartId);
        int deleted = cartMapper.deleteByIdAndUserId(cartId, userId);
        if (deleted <= 0) {
            throw new BusinessException(ResultCode.NOT_FOUND, "购物车项不存在");
        }
    }

    private Cart requireUserCartItem(Long userId, Long cartId) {
        Cart cart = cartMapper.findByIdAndUserId(cartId, userId);
        if (cart == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "购物车项不存在");
        }
        return cart;
    }

    private Product requireAvailableProduct(Long productId) {
        Product product = productMapper.findById(productId);
        if (product == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "商品不存在或已下架");
        }
        return product;
    }

    private void ensureStockEnough(Product product, int quantity) {
        if (product.getStock() == null || product.getStock() < quantity) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "商品库存不足：" + product.getName());
        }
    }
}
