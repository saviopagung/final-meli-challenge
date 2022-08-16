package meli.dh.com.finalmeliproject.repository;

import meli.dh.com.finalmeliproject.model.payment.CreditCardPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICreditCardPayment extends JpaRepository<CreditCardPayment, Long> {
}
