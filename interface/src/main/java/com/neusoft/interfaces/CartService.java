package com.neusoft.interfaces;

import com.neusoft.bean.po.CartInfo;

import java.util.List;

public interface CartService {
    CartInfo ifCartExists(CartInfo cartInfo);

    void updateCart(CartInfo cartInfoDb);

    void saveCart(CartInfo cartInfo);

    void syncCache(String userId);

    List<CartInfo> getCartCache(String userId);

    void updateCartChecked(CartInfo cartInfo);

    void combinCart(List<CartInfo> cartInfos, Long id);

    List<CartInfo> getCartCacheByChecked(String userId);
}
