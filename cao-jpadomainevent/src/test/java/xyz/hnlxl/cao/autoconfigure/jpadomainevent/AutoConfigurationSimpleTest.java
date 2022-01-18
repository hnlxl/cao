package xyz.hnlxl.cao.autoconfigure.jpadomainevent;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import xyz.hnlxl.cao.jpadomainevent.store.EventStreamItemRepo;
import xyz.hnlxl.cao.jpadomainevent.store.StoreService;

@SpringBootTest
class AutoConfigurationSimpleTest {

  @Autowired
  private ApplicationContext context;

  @Test
  void test() {
    assertNotNull(context.getBean(EventStreamItemRepo.class));
    assertNotNull(context.getBean(StoreService.class));
  }

}
