package xyz.hnlxl.cao.autoconfigure.jpadomainevent.ezio;

import java.util.Optional;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import xyz.hnlxl.cao.domainbase.AbstractAggregateRoot;

/** Assassin. */
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Entity
public class Assassin extends AbstractAggregateRoot<Assassin> {
  @Id
  protected String code;
  protected Boolean alive = Boolean.TRUE;

  @Override
  protected Optional<String> eventStreamIdentification() {
    return Optional.of("assassin-" + code);
  }

  public static Assassin register(String code) {
    return new Assassin().setCode(code);
  }

  public void leap() {
    setAlive(false);
    registerEvent(new AssassinDied(getCode(), "leap of free"));
  }

  public void leapOfFaith() {
    registerEvent(new AssassinCame(getCode()));
  }

  public void reload() {
    setAlive(true);
    registerEvent(new AssassinResurrected(getCode()));
  }
}
