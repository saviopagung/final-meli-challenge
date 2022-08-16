package meli.dh.com.finalmeliproject.service.shoppingCart;

import meli.dh.com.finalmeliproject.dto.payment.CreditCardDTO;
import meli.dh.com.finalmeliproject.dto.payment.PixDTO;
import meli.dh.com.finalmeliproject.dto.payment.ResponsePaymentDTO;
import meli.dh.com.finalmeliproject.dto.shoppingCart.PurchaseOrderDto;
import meli.dh.com.finalmeliproject.dto.shoppingCart.ResponseShoppingCartDto;
import meli.dh.com.finalmeliproject.model.PurchaseOrder;
import meli.dh.com.finalmeliproject.model.ShoppingCart;

public interface IShoppingCartService {

    ResponseShoppingCartDto shoppingCart(PurchaseOrderDto dto);
    ShoppingCart findShoppingCartProductsById(long id);
    ResponsePaymentDTO paymentShoppingCart(CreditCardDTO creditCardDTO, long orderId);
    ResponsePaymentDTO paymentShoppingCart(PixDTO pixDTO, long orderId);
}
