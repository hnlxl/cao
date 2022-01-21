package xyz.hnlxl.cao.domainbase;

import java.time.LocalDateTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Convenience base class for domain events.
 * 
 * <p>The convenient function is:<ul> <li>Provide the occurredOn field and its constructor. The
 * occurredOn defaults to current time. </ul>
 * 
 * <p>The occurredOn is used as log, not identification. However, it can be used as reference for
 * identification.It usually does not participate in {@link Object#equals(Object)} and
 * {@link Object#hashCode()}, because it is a log field. However, in some cases it will be a data
 * field and therefore need to participate in equality comparison. The subclass needs to determine
 * whether the occurredOn participates in equality comparison, and then set the
 * <code>callSuper</code> property of <code>@lombok.EqualsAndHashCode</code> to true or false.
 * 
 * @author hnlxl at 2021/11/18
 *
 */

@Getter
@ToString
@EqualsAndHashCode
public abstract class AbstractDomainEvent {

  /**
   * The time when the event occurred.
   */
  protected LocalDateTime occurredOn;

  /**
   * The default constructor.
   * 
   * <p>This constructor will set the default value for the generic field.
   * 
   * <p><strong>Note: Constructor calls super() by default, so if the subclass has no special
   * constructors, this constructor will always be called.</strong>
   */
  public AbstractDomainEvent() {
    this(LocalDateTime.now());
  }

  public AbstractDomainEvent(LocalDateTime occurredOn) {
    this.occurredOn = occurredOn != null ? occurredOn : LocalDateTime.now();
  }
}
