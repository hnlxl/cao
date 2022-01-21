package xyz.hnlxl.cao.jpa;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * Utilities about Spring Data JPA.
 * 
 * @author hnlxl at 2021/11/29
 *
 */
public final class JpaUtil {
  /**
   * 以分页请求的方式安全地请求全部
   * 
   * <p>以特定的分页方式——大小达到极限的第一页——请求查询。以达到如下效果：查询全部，但当个数超过最大个数时抛弃后面的元素。
   */
  public static PageRequest safelyAll(Sort sort, int maxSize) {
    return PageRequest.of(0, maxSize, sort);
  }

  public static PageRequest safelyAll(Sort sort) {
    return PageRequest.of(0, 100, sort);
  }
}
