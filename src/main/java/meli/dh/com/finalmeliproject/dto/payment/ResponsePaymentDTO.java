package meli.dh.com.finalmeliproject.dto.payment;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import meli.dh.com.finalmeliproject.model.PurchaseOrder;
import meli.dh.com.finalmeliproject.model.ShoppingCart;
import meli.dh.com.finalmeliproject.model.enums.OrderStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter @Setter
public class ResponsePaymentDTO {

    private PaymentDTO payment;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime dateOrder;

    private ShoppingCart shoppingCart;

    public ResponsePaymentDTO(PurchaseOrder purchaseOrder, PaymentDTO payment){
        setPayment(payment);
        setDateOrder(purchaseOrder.getDateOrder());
        setShoppingCart(purchaseOrder.getShoppingCart());
    }
}
