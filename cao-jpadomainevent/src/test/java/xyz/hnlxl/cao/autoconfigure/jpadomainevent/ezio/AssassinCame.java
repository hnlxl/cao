package xyz.hnlxl.cao.autoconfigure.jpadomainevent.ezio;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import xyz.hnlxl.cao.domainbase.AbstractDomainEvent;

/** An assassin came */
@Getter
@Setter(AccessLevel.PROTECTED)
@ToString()
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class AssassinCame extends AbstractDomainEvent {
  protected String code;
}
