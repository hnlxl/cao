package xyz.hnlxl.cao.jpa;

import org.hibernate.dialect.H2Dialect;

/**
 * Customization of H2 Dialect For Unit Test.
 * 
 * <p>It is optional, will not be auto configured.configure manually on application.properties when
 * need.
 * 
 * @author hnlxl at 2025/01/10
 *
 */
public class CustomizedH2Dialect extends H2Dialect {

  @Override
  public int getDefaultTimestampPrecision() {
    // adapt LocalDateTime.now() after version 17
    return 9;
  }
}
