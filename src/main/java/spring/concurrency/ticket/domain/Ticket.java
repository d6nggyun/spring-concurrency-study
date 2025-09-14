package spring.concurrency.ticket.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ticket {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Version
    private Long version;

    private long availableStock;

    private Ticket(String name, long availableStock) {
        this.name = name;
        this.availableStock = availableStock;
    }

    public static Ticket create(String name, long availableStock) {
        return new Ticket(name, availableStock);
    }

    public void decreaseStock(long amount) {
        validateStock(amount);
        this.availableStock--;
    }

    private void validateStock(long amount) {
        if (availableStock - amount < 0) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }
    }
}
