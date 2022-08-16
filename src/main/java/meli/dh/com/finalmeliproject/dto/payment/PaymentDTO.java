package meli.dh.com.finalmeliproject.dto.payment;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import meli.dh.com.finalmeliproject.model.payment.CreditCardPayment;
import meli.dh.com.finalmeliproject.model.payment.PixPayment;

@Getter @Setter
public class PaymentDTO {
    private double amountOfInstallment;
    private double installmentValue;

    public PaymentDTO(CreditCardPayment cardPayment) {
        setAmountOfInstallment(cardPayment.getAmountOfInstallment());
        setInstallmentValue(cardPayment.getInstallmentValue());
    }

    public PaymentDTO(PixPayment pixPayment) {
        setAmountOfInstallment(pixPayment.getPurchaseOrder().getShoppingCart().getTotalValue());
        setInstallmentValue(1);
    }
}
