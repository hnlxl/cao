package xyz.hnlxl.cao.autoconfigure.jpadomainevent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import xyz.hnlxl.cao.domainbase.SpecialEventForStoring;
import xyz.hnlxl.cao.jpadomainevent.store.EventStreamItem;
import xyz.hnlxl.cao.jpadomainevent.store.StoreService;

/**
 * Event Listener's AutoConfiguration
 * 
 * @author hnlxl at 2022/01/18
 *
 */
@Configuration
@ConditionalOnClass(EventStreamItem.class)
@AutoConfigureAfter(JpaDomainEventSupportBeanAutoConfiguration.class)
public class JpaDomainEventSupportListenerAutoConfiguration {
  @Autowired
  private StoreService storeService;

  @TransactionalEventListener(fallbackExecution = true, phase = TransactionPhase.BEFORE_COMMIT)
  public void listenSpecialEventForStoring(SpecialEventForStoring event) {
    storeService.store(event);
  }

}
