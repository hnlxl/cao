package xyz.hnlxl.cao.jpadomainevent.store;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Special lock to solve concurrency problems when storing event.
 * 
 * <p>解决存储事件时并发性问题的特别锁。
 * 
 * <p>这是一个纯粹的数据库中的表，不是一个实体，把它定义为实体仅仅是为了单元测试方便。
 * 
 * <p>它只有一个主键列，表示事件流标识。在EventStreamItem实体保存过程中，开始时在此表插入一行，结束时再删除。通过上述操作，使得保存事件（也可以认为是，一个事件流追加新的事件）时，
 * 当前事务能够锁定一个事件流的所有数据。
 * 
 * <p>即使不使用该特别锁，也可以以如下方式进行常规加锁：SELECT * FROM EventStreamItem WHERE stream = ? FOR UPDATE。
 * 但是常规加锁方式，在从0到1的过程中加的是间隙锁，存在死锁问题。
 * 
 * @author hnlxl at 2022/12/29
 *
 */
@Entity
final class StoringEventSpecialLock {
  // 若将EventStream实体化并作为主实体，则可通过它来做乐观锁并发控制，但出现并发异常后应当做重试而不是抛出异常。
  // （FOR UPDATE主观锁是不可用的，从0到1的过程中可能因为Hibernate缓存仍然有幻读问题）。
  // TODO 需要评估“特殊锁”和“EventStream实体化+乐观锁”这两种方式哪种更好。
  @Id
  private String stream;
}
