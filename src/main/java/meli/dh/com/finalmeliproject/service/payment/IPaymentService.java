package meli.dh.com.finalmeliproject.service.payment;

import meli.dh.com.finalmeliproject.model.CreditCard;
import meli.dh.com.finalmeliproject.model.payment.CreditCardPayment;
import meli.dh.com.finalmeliproject.model.payment.PixPayment;

public interface IPaymentService {
    CreditCardPayment saveCreditCardPayment(CreditCardPayment creditCardPayment);
    PixPayment savePixPayment(PixPayment pixPayment);
    CreditCard findCreditCardByNumber(String number);
}
