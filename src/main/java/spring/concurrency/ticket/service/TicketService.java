package spring.concurrency.ticket.service;

import lombok.RequiredArgsConstructor;
import org.hibernate.PessimisticLockException;
import org.hibernate.dialect.lock.PessimisticEntityLockException;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.concurrency.ticket.domain.Ticket;
import spring.concurrency.ticket.repository.TicketRepository;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    @Transactional
    public void decreaseStock(Long couponId) {
        Ticket ticket = ticketRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰이 존재하지 않습니다."));

        ticket.decreaseStock(1);
        ticketRepository.saveAndFlush(ticket);
    }

    @Transactional
    public synchronized void decreaseStockWithSynchronized(Long couponId) {
        Ticket ticket = ticketRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰이 존재하지 않습니다."));

        ticket.decreaseStock(1);
        ticketRepository.saveAndFlush(ticket);
    }

    @Transactional
    public void decreaseStockWithOptimisticLock(Long couponId) {
        Ticket ticket = ticketRepository.findByIdWithOptimisticLock(couponId)
                        .orElseThrow(() -> new IllegalArgumentException("쿠폰이 존재하지 않습니다."));

        ticket.decreaseStock(1);
        ticketRepository.saveAndFlush(ticket);
    }

    @Transactional
    public void decreaseStockWithPerssimisticLock(Long couponId) {
        Ticket ticket = ticketRepository.findByIdWithPerssimisticLock(couponId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰이 존재하지 않습니다."));

        ticket.decreaseStock(1);
        ticketRepository.saveAndFlush(ticket);
    }
}
