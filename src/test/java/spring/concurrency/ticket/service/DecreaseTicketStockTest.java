package spring.concurrency.ticket.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spring.concurrency.ticket.domain.Ticket;
import spring.concurrency.ticket.repository.TicketRepository;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DecreaseTicketStockTest {

    private static final Logger log = LoggerFactory.getLogger(DecreaseTicketStockTest.class);
    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketRepository ticketRepository;

    private Ticket ticket;

    @BeforeEach
    void setUp() {
        ticket = ticketRepository.save(Ticket.create("Festival Ticket", 300L));
    }

    @Test
    void decreaseTicketStockBy300Person() throws InterruptedException {
        int threadCount = 300;
        testConcurrency(() -> ticketService.decreaseStock(ticket.getId()), threadCount);
    }

    @Test
    void decreaseTicketStockWithSynchronizedBy300Person() throws InterruptedException {
        int threadCount = 300;
        testConcurrency(() -> ticketService.decreaseStockWithSynchronized(ticket.getId()), threadCount);
    }

    @Test
    void decreaseTicketStockWithOptimisticLockBy300Person() throws InterruptedException {
        int threadCount = 300;
        testConcurrency(() -> ticketService.decreaseStockWithOptimisticLock(ticket.getId()), threadCount);
    }

    @Test
    void decreaseTicketStockWithPerssimisticLockBy300Person() throws InterruptedException {
        int threadCount = 300;
        testConcurrency(() -> ticketService.decreaseStockWithPerssimisticLock(ticket.getId()), threadCount);
    }

    private void testConcurrency(Runnable task, int threadCount) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    task.run();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();
        executorService.shutdown();

        Ticket savedticket = ticketRepository.findById(ticket.getId()).orElseThrow(IllegalArgumentException::new);
        assertThat(savedticket.getAvailableStock()).isZero();
        log.info("잔여 쿠폰 수량: {}", savedticket.getAvailableStock());
    }
}
