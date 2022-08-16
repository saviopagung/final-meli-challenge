package meli.dh.com.finalmeliproject.repository;

import meli.dh.com.finalmeliproject.model.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICreditCardRepo extends JpaRepository<CreditCard, Long> {
    CreditCard findByNumber(String number);
}
