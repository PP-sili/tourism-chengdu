package org.example.tourismchengdu.service;

import org.example.tourismchengdu.dto.mall.AddCartRequest;
import org.example.tourismchengdu.dto.mall.CartItemVO;
import org.example.tourismchengdu.dto.mall.UpdateCartRequest;

import java.util.List;

public interface CartService {

    List<CartItemVO> listCart(Long userId);

    CartItemVO addToCart(Long userId, AddCartRequest request);

    CartItemVO updateCartItem(Long userId, Long cartId, UpdateCartRequest request);

    void removeCartItem(Long userId, Long cartId);
}
