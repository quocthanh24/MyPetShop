package com.thanhluu.tlcn.Controller.Customer;

import com.thanhluu.tlcn.DTO.response.Cart.Item.ItemResp;
import com.thanhluu.tlcn.Service.Customer.ICartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers/cart/items")
public class CartItemController {
  @Autowired
  private ICartItemService cartItemService;

  @PutMapping("/{itemId}")
  public ResponseEntity<ItemResp> updateItemInCart(@PathVariable String itemId, @RequestParam Integer quantity) {
    return new ResponseEntity<>(cartItemService.updateItem(itemId, quantity), HttpStatus.OK);
  }

  @DeleteMapping("/{itemId}")
  public ResponseEntity<?> deleteItemFromCart(@PathVariable String itemId) {
    String msg = cartItemService.deleteItem(itemId);
    return new ResponseEntity<>(msg, HttpStatus.OK);
  }

}
