package xyz.hnlxl.cao.jpadomainevent.store;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import xyz.hnlxl.cao.domainbase.AbstractDomainEvent;
import xyz.hnlxl.cao.domainbase.SpecialEventForStoring;

@DataJpaTest
class StoreServiceTest {
  @Autowired
  private EventStreamItemRepo eventStreamItemRepo;
  @Autowired
  JpaTransactionManager jpaTransactionManager;
  @Autowired
  TransactionDefinition transactionDefinition;

  @Test
  void testStore_nonConcurrent() {
    final DummyEvent dummyEvent1 = new DummyEvent(1, "a");
    final DummyEvent dummyEvent2 = new DummyEvent(2, "b");
    final List<DummyEvent> dummyEvents = new ArrayList<>();
    dummyEvents.add(dummyEvent1);
    dummyEvents.add(dummyEvent2);
    final SpecialEventForStoring dummySpecialEventForStoring =
        new SpecialEventForStoring("dummyStream", dummyEvents);


    StoreService storeService = new StoreService(eventStreamItemRepo);
    storeService.store(dummySpecialEventForStoring);
    storeService.store(dummySpecialEventForStoring);


    List<EventStreamItem> eventStreamItems = eventStreamItemRepo.findAll();
    assertEquals(2, eventStreamItems.size());

    EventStreamItem item1 = eventStreamItems.get(0);
    assertEquals("dummyStream", item1.getStream());
    assertEquals(Integer.valueOf(1), item1.getSequence());
    final DocumentContext parsedEvents = JsonPath.parse(item1.getEvents());
    assertEquals("xyz.hnlxl.cao.jpadomainevent.store.StoreServiceTest$DummyEvent",
        parsedEvents.read("$[0].rc"));
    assertEquals(Integer.valueOf(1), parsedEvents.read("$[0].c.intValue"));
    assertEquals("a", parsedEvents.read("$[0].c.strValue"));
    assertEquals("xyz.hnlxl.cao.jpadomainevent.store.StoreServiceTest$DummyEvent",
        parsedEvents.read("$[1].rc"));
    assertEquals(Integer.valueOf(2), parsedEvents.read("$[1].c.intValue"));
    assertEquals("b", parsedEvents.read("$[1].c.strValue"));
    assertTrue(item1.getStoredOn().until(LocalDateTime.now(), ChronoUnit.SECONDS) <= 3);

    EventStreamItem item2 = eventStreamItems.get(1);
    assertEquals(Integer.valueOf(2), item2.getSequence());
  }

  @Test
  void testStore_concurrent() {
    eventStreamItemRepo.deleteAll();



    List<DummyEvent> dummyEvents = new ArrayList<>();
    dummyEvents.add(new DummyEvent(100, "don't care about content."));
    final SpecialEventForStoring dummySpecialEventForStoring =
        new SpecialEventForStoring("dummyStream", dummyEvents);
    final AtomicInteger counter = new AtomicInteger(0);
    final StoreService storeService = new StoreService(eventStreamItemRepo);
    final int runNum = 100;

    for (int i = 1; i <= runNum; i++) {
      new Thread(() -> {
        TransactionStatus transactionStatus =
            jpaTransactionManager.getTransaction(transactionDefinition);
        try {
          storeService.store(dummySpecialEventForStoring);
          jpaTransactionManager.commit(transactionStatus);
        } catch (TransactionException e) {
          jpaTransactionManager.rollback(transactionStatus);
          e.printStackTrace();
        }
        counter.addAndGet(1);
      }).start();
    }
    Awaitility.await().atMost(5, TimeUnit.SECONDS)
        .untilAtomic(counter, equalTo(Integer.valueOf(runNum)));

    List<EventStreamItem> sortedItems =
        eventStreamItemRepo.findAll(Example.of(new EventStreamItem().setStream("dummyStream")),
            Sort.by(Order.asc(EventStreamItem_.SEQUENCE)));
    assertEquals(runNum, sortedItems.size());
    boolean isIncreasing = true;
    EventStreamItem lastItem = sortedItems.get(0);
    for (int no = 2; no <= runNum; no++) {
      EventStreamItem thisItem = sortedItems.get(no - 1);
      isIncreasing = isIncreasing
          && (lastItem.getSequence() + 1 == thisItem.getSequence())
          && (ChronoUnit.MILLIS.between(lastItem.getStoredOn(), thisItem.getStoredOn()) >= 0);
      if (!isIncreasing) {
        break;
      } else {
        lastItem = thisItem;
      }
    }
    assertTrue(isIncreasing);


    eventStreamItemRepo.deleteAll();
  }

  @AllArgsConstructor
  @Getter
  @ToString
  @EqualsAndHashCode(callSuper = false)
  static class DummyEvent extends AbstractDomainEvent {
    protected Integer intValue;
    protected String strValue;
  }
}
