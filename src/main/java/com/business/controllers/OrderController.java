package com.business.controllers;

import com.business.entities.Orders;
import com.business.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * DELETE /api/orders/{id}
     * 删除指定 ID 的订单
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable("id") int orderId) {
        if (!orderRepository.existsById(orderId)) {
            // 如果找不到，返回 404
            return ResponseEntity.notFound().build();
        }
        orderRepository.deleteById(orderId);
        // 删除成功，返回 204 No Content
        return ResponseEntity.noContent().build();
    }

    /**
     * PATCH /api/orders/{id}?quantity=新的数量
     * 更新指定 ID 订单的数量，并重算 totalAmmout
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Orders> updateOrderQuantity(
            @PathVariable("id") int orderId,
            @RequestParam("quantity") int newQuantity) {

        return orderRepository.findById(orderId)
                .map(order -> {
                    // 更新数量
                    order.setoQuantity(newQuantity);
                    // 重新计算总价
                    order.setTotalAmmout(order.getoPrice() * newQuantity);
                    Orders updated = orderRepository.save(order);
                    return ResponseEntity.ok(updated);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}