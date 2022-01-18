package xyz.hnlxl.cao.jpadomainevent.store;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * EventStreamItem's Repo
 * 
 * @author hnlxl at 2021/12/22
 *
 */
public interface EventStreamItemRepo extends JpaRepository<EventStreamItem, Long> {

  /**
   * Try make a special lock to an event stream
   * 
   * <p>Don't forget to mark the release of this lock, because only committing transaction cannot
   * release this lock.
   * 
   * @see #markReleaseSpecialStreamLock(String)
   */
  @Modifying
  @Query(nativeQuery = true, value = "INSERT INTO storing_event_special_lock VALUES (?1)")
  void tryMakeSpecialStreamLock(String stream);

  /**
   * mark the release of an event stream's special lock.
   * 
   * @see #tryMakeSpecialStreamLock(String)
   */
  @Modifying
  @Query(nativeQuery = true, value = "DELETE FROM storing_event_special_lock WHERE stream=?1")
  void markReleaseSpecialStreamLock(String stream);

  @Query("SELECT COALESCE(MAX(sequence),0) FROM EventStreamItem WHERE stream=?1")
  Integer findMaxSequenceOfStream(String stream);
}
