package xyz.hnlxl.cao.autoconfigure.jpadomainevent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import xyz.hnlxl.cao.jpadomainevent.store.EventStreamItem;
import xyz.hnlxl.cao.jpadomainevent.store.EventStreamItemRepo;
import xyz.hnlxl.cao.jpadomainevent.store.StoreService;

/**
 * General Bean's AutoConfiguration.
 * 
 * @author hnlxl at 2021/12/22
 *
 */
@Configuration
@ConditionalOnClass(EventStreamItem.class)
@AutoConfigureAfter(JpaRepositoriesAutoConfiguration.class)
@EnableJpaAuditing
public class JpaDomainEventSupportBeanAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public StoreService storeService(@Autowired EventStreamItemRepo eventStreamItemRepo) {
    return new StoreService(eventStreamItemRepo);
  }
}
