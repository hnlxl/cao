package xyz.hnlxl.cao.domainbase;

import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * A special event for storing ordinary events to the event stream.
 * 
 * @author hnlxl at 2021/12/12
 *
 */
@ToString()
@EqualsAndHashCode(callSuper = false)
public class SpecialEventForStoring {

  /**
   * The event stream identification.
   * 
   * <p>Usually, this is a combination of aggregate type and aggregate identification, or equivalent
   * to aggregate type.
   * 
   * <p>A unit of work may publish multiple events, but usually they all belong to the same event
   * stream.
   * 
   * @see AbstractAggregateRoot#eventStreamIdentification()
   */
  @Getter
  protected String eventStreamIdentification;

  /**
   * The list of the original events.
   * 
   * <p>Usually, this contains all original events of the current unit of work.
   */
  @Getter
  protected List<? extends AbstractDomainEvent> originalEvents = new ArrayList<>();

  /** All args constructor. */
  public SpecialEventForStoring(String eventStreamIdentification,
      List<? extends AbstractDomainEvent> originalEvents) {
    super();
    this.eventStreamIdentification = eventStreamIdentification;
    this.originalEvents = originalEvents;
  }

}
