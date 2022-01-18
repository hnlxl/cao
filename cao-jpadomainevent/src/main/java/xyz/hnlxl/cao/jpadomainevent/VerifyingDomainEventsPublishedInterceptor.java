package xyz.hnlxl.cao.jpadomainevent;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import org.hibernate.EmptyInterceptor;
import org.springframework.data.domain.DomainEvents;
import org.springframework.data.util.AnnotationDetectionMethodCallback;
import org.springframework.util.ReflectionUtils;
import xyz.hnlxl.cao.domainbase.DomainSupportException;

/**
 * The interceptor that will verify whether the domain events has been published after prepare flush
 * 
 * @author hnlxl at 2021/11/29
 *
 */
public class VerifyingDomainEventsPublishedInterceptor extends EmptyInterceptor {
  private static final long serialVersionUID = 1L;
  // TODO AbstractAggregateRoot // 现在是由我控制的，所以没必要再通过DomainEvents来提取聚合的domainEvents。
  // 应当通过特定方法或自定义注解来提取，以避免对@DomainEvents造成干扰。

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public void preFlush(Iterator entities) {
    List<Object> errorEntities = new ArrayList<>();
    while (entities.hasNext()) {
      Object entity = (Object) entities.next();
      if (!domainEventsMethod(entity)
          .map(method -> (Collection<Object>) ReflectionUtils.invokeMethod(method, entity))
          .orElse(Collections.emptyList())
          .isEmpty()) {
        errorEntities.add(entity);
      }
    }

    if (!errorEntities.isEmpty()) {
      boolean isFirst = true;
      StringBuilder sb =
          new StringBuilder().append("Aggregates has unpublished events! Entities is: [");
      for (Object errorEntity : errorEntities) {
        if (isFirst) {
          sb.append(errorEntity.getClass().getName());
          isFirst = false;
        } else {
          sb.append(" | ").append(errorEntity.getClass().getName());
        }
      }
      sb.append("]. When enabling domain events, ")
          .append(
              "it must explicitly call \"repository.save\" method "
                  + "to publish the registered events.");
      throw new DomainSupportException(sb.toString());
    }
  }

  private Optional<Method> domainEventsMethod(final Object entity) {
    final AnnotationDetectionMethodCallback<DomainEvents> methodCallback =
        new AnnotationDetectionMethodCallback<>(DomainEvents.class, true);
    ReflectionUtils.doWithMethods(entity.getClass(), methodCallback);
    return Optional.ofNullable(methodCallback.getMethod())
        .map(
            method -> {
              ReflectionUtils.makeAccessible(method);
              return method;
            });
  }
}
