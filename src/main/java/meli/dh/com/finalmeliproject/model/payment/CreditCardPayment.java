package meli.dh.com.finalmeliproject.model.payment;

import lombok.*;
import meli.dh.com.finalmeliproject.model.CreditCard;
import meli.dh.com.finalmeliproject.model.PurchaseOrder;
import meli.dh.com.finalmeliproject.model.ShoppingCart;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class CreditCardPayment{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private double amountOfInstallment;

    private double installmentValue;

    @ManyToOne
    @JoinColumn(name = "id_purchase_order")
    private PurchaseOrder purchaseOrder;

    @ManyToOne
    @JoinColumn(name = "id_credit_card")
    private CreditCard creditCard;

}
