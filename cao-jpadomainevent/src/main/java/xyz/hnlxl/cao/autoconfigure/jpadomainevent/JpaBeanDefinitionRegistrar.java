package xyz.hnlxl.cao.autoconfigure.jpadomainevent;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import xyz.hnlxl.cao.jpadomainevent.store.EventStreamItemRepo;

/**
 * Special Repostiory's register
 * 
 * @author hnlxl at 2021/12/22
 *
 */
public class JpaBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
  @Override
  public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
      BeanDefinitionRegistry registry) {
    AutoConfigurationPackages.register(registry, EventStreamItemRepo.class.getPackage().getName());
  }
}
