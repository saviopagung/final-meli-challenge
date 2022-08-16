package meli.dh.com.finalmeliproject.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class CreditCardDTO {
    String cardNumber;
    String cardPass;
    long cardCvc;
    double numberOfInstallments;
}
