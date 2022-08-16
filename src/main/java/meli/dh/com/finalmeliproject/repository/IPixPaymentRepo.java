package meli.dh.com.finalmeliproject.repository;

import meli.dh.com.finalmeliproject.model.payment.PixPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPixPaymentRepo extends JpaRepository<PixPayment, Long> {
}
