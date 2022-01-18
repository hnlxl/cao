package xyz.hnlxl.cao.autoconfigure.jpadomainevent;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import xyz.hnlxl.cao.jpadomainevent.store.EventStreamItem;

/**
 * Special Repostiory's AutoConfiguration.
 * 
 * @author hnlxl at 2022/1/14
 *
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(EventStreamItem.class)
@AutoConfigureAfter(HibernateJpaAutoConfiguration.class)
@AutoConfigureBefore(JpaRepositoriesAutoConfiguration.class)
@Import(JpaBeanDefinitionRegistrar.class)
public class JpaDomainEventSupportRepoAutoConfiguration {
}
