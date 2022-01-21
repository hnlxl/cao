package xyz.hnlxl.cao.autoconfigure.jpadomainevent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import xyz.hnlxl.cao.autoconfigure.jpadomainevent.ezio.Assassin;
import xyz.hnlxl.cao.autoconfigure.jpadomainevent.ezio.AssassinRepo;
import xyz.hnlxl.cao.autoconfigure.jpadomainevent.ezio.AssassinService;
import xyz.hnlxl.cao.jpa.JpaUtil;
import xyz.hnlxl.cao.jpadomainevent.store.EventStreamItem;
import xyz.hnlxl.cao.jpadomainevent.store.EventStreamItemRepo;
import xyz.hnlxl.cao.jpadomainevent.store.EventStreamItem_;

@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
class DomainEventStoreSupportTest {
  @Autowired
  private AssassinRepo assassinRepo;
  @Autowired
  private AssassinService assassinService;
  @Autowired
  private EventStreamItemRepo eventStreamItemRepo;

  @BeforeEach
  public void setUp() {
    assassinRepo.save(Assassin.register("110"));
  }

  @AfterEach
  public void tearDown() {
    assassinRepo.deleteById("110");
  }

  @Test
  void test_commit() {
    assassinService.forTestDomainEventStoringCommit("110");


    Page<EventStreamItem> page =
        eventStreamItemRepo.findAll(Example.of(new EventStreamItem().setStream("assassin-110")),
            JpaUtil.safelyAll(Sort.by(Order.desc(EventStreamItem_.SEQUENCE))));
    assertTrue(page.getTotalElements() >= 2);
    final EventStreamItem penultimateItem = page.getContent().get(1);
    final EventStreamItem lastItem = page.getContent().get(0);
    final LocalDateTime now = LocalDateTime.now();

    assertTrue(ChronoUnit.SECONDS.between(penultimateItem.getStoredOn(), now) <= 3);
    assertTrue(ChronoUnit.SECONDS.between(lastItem.getStoredOn(), now) <= 3);
    assertTrue(
        ChronoUnit.SECONDS.between(penultimateItem.getStoredOn(), lastItem.getStoredOn()) >= 0);

    DocumentContext penultimateEvents = JsonPath.parse(penultimateItem.getEvents());
    assertEquals("xyz.hnlxl.cao.autoconfigure.jpadomainevent.ezio.AssassinCame",
        penultimateEvents.read("$[0].rc"));
    assertEquals("110", penultimateEvents.read("$[0].c.code"));
    assertTrue(ChronoUnit.SECONDS
        .between(LocalDateTime.parse(penultimateEvents.read("$[0].c.occurredOn")), now) <= 5);
    assertEquals("xyz.hnlxl.cao.autoconfigure.jpadomainevent.ezio.AssassinDied",
        penultimateEvents.read("$[1].rc"));
    assertEquals("leap of free", penultimateEvents.read("$[1].c.reason"));
    DocumentContext lastEvents = JsonPath.parse(lastItem.getEvents());
    assertEquals("xyz.hnlxl.cao.autoconfigure.jpadomainevent.ezio.AssassinResurrected",
        lastEvents.read("$[0].rc"));
  }

  @Test
  void test_rollback(CapturedOutput output) {
    final String code = "110";
    final String streamId = "assassin-110";
    assassinService.forTestDomainEventStoringCommit(code); // 添加干扰项
    final Example<EventStreamItem> byStreamExample =
        Example.of(new EventStreamItem().setStream(streamId));
    final long itemCountBefore = eventStreamItemRepo.count(byStreamExample);
    final Integer maxSequenceBefore = eventStreamItemRepo.findMaxSequenceOfStream(streamId);
    assertTrue(itemCountBefore > 0);
    assertTrue(maxSequenceBefore > 0);

    assertThrows(RuntimeException.class, () -> {
      assassinService.forTestDomainEventStoringRollback("110");
    }, "A manual error for triggering a rollback");


    assertEquals(Boolean.TRUE, assassinRepo.findById("110").get().getAlive());
    assertEquals(itemCountBefore, eventStreamItemRepo.count(byStreamExample));
    assertEquals(maxSequenceBefore, eventStreamItemRepo.findMaxSequenceOfStream(streamId));
  }
}
