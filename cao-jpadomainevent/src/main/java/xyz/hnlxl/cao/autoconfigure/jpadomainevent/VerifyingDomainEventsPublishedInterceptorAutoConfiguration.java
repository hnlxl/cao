package xyz.hnlxl.cao.autoconfigure.jpadomainevent;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.hnlxl.cao.jpadomainevent.VerifyingDomainEventsPublishedInterceptor;

/**
 * The VerifyingDomainEventsPublishedInterceptor's AutoConfiguration.
 * 
 * @author hnlxl at 2022/1/14
 *
 */
@Configuration
@ConditionalOnClass(VerifyingDomainEventsPublishedInterceptor.class)
@AutoConfigureBefore(HibernateJpaAutoConfiguration.class)
public class VerifyingDomainEventsPublishedInterceptorAutoConfiguration {

  /** Javadoc omitted. */
  @Bean
  public HibernatePropertiesCustomizer hibernatePropertiesCustomizer() {
    return (hibernateProperties) -> {
      hibernateProperties.put("hibernate.session_factory.interceptor",
          new VerifyingDomainEventsPublishedInterceptor());
    };
  }
}
