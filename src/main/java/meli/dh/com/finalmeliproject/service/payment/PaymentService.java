package meli.dh.com.finalmeliproject.service.payment;

import meli.dh.com.finalmeliproject.exception.NotFoundExceptionImp;
import meli.dh.com.finalmeliproject.model.CreditCard;
import meli.dh.com.finalmeliproject.model.payment.CreditCardPayment;
import meli.dh.com.finalmeliproject.model.payment.PixPayment;
import meli.dh.com.finalmeliproject.repository.ICreditCardPayment;
import meli.dh.com.finalmeliproject.repository.ICreditCardRepo;
import meli.dh.com.finalmeliproject.repository.IPixPaymentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService implements IPaymentService {

    @Autowired
    private ICreditCardRepo creditCardRepo;

    @Autowired
    private ICreditCardPayment creditCardPaymentRepo;

    @Autowired
    private IPixPaymentRepo pixPaymentRepo;

    @Override
    public CreditCardPayment saveCreditCardPayment(CreditCardPayment creditCardPayment) {
        return creditCardPaymentRepo.save(creditCardPayment);
    }

    @Override
    public PixPayment savePixPayment(PixPayment pixPayment) {
        return pixPaymentRepo.save(pixPayment);
    }

    @Override
    public CreditCard findCreditCardByNumber(String number){
        CreditCard creditCard = creditCardRepo.findByNumber(number);

        if (creditCard == null){
            throw new NotFoundExceptionImp("Credit Cart not exist");
        }

        return  creditCard;
    }
}
