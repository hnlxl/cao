package xyz.hnlxl.cao.jpadomainevent.store;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * The JPA entity about item of event stream.
 * 
 * @author hnlxl at 2021/12/22
 *
 */
@Data
@Accessors(chain = true)
@Entity
@EntityListeners(AuditingEntityListener.class)
public class EventStreamItem {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Long sk; // surrogate key 代理主键 —— 此实体不会参与常规业务，故不再通过层超模型隐藏代理主键

  protected String stream; // event stram identification. 事件流标识
  protected Integer sequence; // sequence number in the event stream. 在事件流中的序列号
  /**
   * events serialized in JSON format.
   * 
   * <p>以JSON格式序列化的事件列表。
   * 
   * <p>First, serialize each event into JSON object. Then, combine each event and its read contract
   * into a Map. Then, combine all Maps into an array. Finally, convert the array into a JSON
   * string. Example: [{"rc":"xyz.hnlxl.xxx.SomeEvent","c":{"occurredOn":"...","field1":"..."}},
   * {"rc":"xyz.hnlxl.xxx.SomeAnotherEvent","c":{"occurredOn":"...","field5":"..."}} ]
   */
  @Column(length = 65535)
  protected String events;

  @CreatedDate
  protected LocalDateTime storedOn;
}
