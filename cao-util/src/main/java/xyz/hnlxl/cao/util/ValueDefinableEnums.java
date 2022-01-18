package xyz.hnlxl.cao.util;

/**
 * 可以定义值的枚举的辅助工具
 * 
 * @author hnlxl at 2021/11/15
 *
 */
public class ValueDefinableEnums {
  /**
   * 从一个自然值得到枚举常量，如果值无效则返回null
   * 
   * @param enumClass 枚举类的类型
   * @param value 自然值
   * @return 对应的枚举常量，或者null。
   */
  public static <E extends Enum<?> & ValueDefinable<V>, V extends Comparable<V>> E valueOf(
      Class<E> enumClass, V value) {
    E[] enumConstants = enumClass.getEnumConstants();
    for (E e : enumConstants) {
      if (e.getValue().compareTo(value) == 0) {
        return e;
      }
    }
    return null;
  }
}
