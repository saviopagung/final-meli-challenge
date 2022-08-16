package meli.dh.com.finalmeliproject.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreditCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String number;

    private String pass;

    private long cvc;

    @ManyToOne
    @JoinColumn(name = "id_buyer")
    private Buyer buyer;
}
