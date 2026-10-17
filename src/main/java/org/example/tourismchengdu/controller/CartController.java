package org.example.tourismchengdu.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.tourismchengdu.common.Result;
import org.example.tourismchengdu.dto.mall.AddCartRequest;
import org.example.tourismchengdu.dto.mall.CartItemVO;
import org.example.tourismchengdu.dto.mall.UpdateCartRequest;
import org.example.tourismchengdu.security.UserContext;
import org.example.tourismchengdu.service.CartService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@Tag(name = "商城-购物车", description = "购物车管理")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public Result<List<CartItemVO>> listCart() {
        return Result.success(cartService.listCart(UserContext.getUserId()));
    }

    @PostMapping
    public Result<CartItemVO> addToCart(@Valid @RequestBody AddCartRequest request) {
        return Result.success(cartService.addToCart(UserContext.getUserId(), request));
    }

    @PutMapping("/{id}")
    public Result<CartItemVO> updateCartItem(@PathVariable Long id,
                                             @Valid @RequestBody UpdateCartRequest request) {
        return Result.success(cartService.updateCartItem(UserContext.getUserId(), id, request));
    }

    @DeleteMapping("/{id}")
    public Result<Void> removeCartItem(@PathVariable Long id) {
        cartService.removeCartItem(UserContext.getUserId(), id);
        return Result.success();
    }
}
