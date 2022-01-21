/*
 * @formatter:off
 * Copyright 2016-2021 the original author or authors.
 * Copyright 2021 hnlxl
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * @formatter:on
 */

package xyz.hnlxl.cao.domainbase;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.data.domain.DomainEvents;
import org.springframework.util.Assert;

/**
 * Convenience base class for aggregate roots that exposes a {@link #registerEvent(Object)} to
 * capture domain events and expose them via {@link #domainEvents()}. The implementation is using
 * the general event publication mechanism implied by {@link DomainEvents} and
 * {@link AfterDomainEventPublication}.
 * 
 * <p>Difference with {@link org.springframework.data.domain.AbstractAggregateRoot}: <ul> <li>Limit
 * the normal event's class to {@link xyz.hnlxl.cao.domainbase.AbstractDomainEvent} <li>Add some
 * methods to aid unit testing <li>Add some behaviors to aid event store. </ul>
 * 
 * @author hnlxl at 2021/11/18
 *
 * @param <A> The aggregate itself
 */
public abstract class AbstractAggregateRoot<A extends AbstractAggregateRoot<A>> {

  private final transient @Transient List<AbstractDomainEvent> domainEvents = new ArrayList<>();

  /**
   * Registers the given event object for publication on a call to a Spring Data repository's save
   * methods.
   * 
   * @param event must not be {@literal null}.
   * @return the event that has been added.
   * @see #andEvent(AbstractDomainEvent)
   */
  protected <T extends AbstractDomainEvent> T registerEvent(T event) {

    Assert.notNull(event, "Domain event must not be null!");

    this.domainEvents.add(event);
    return event;
  }

  /**
   * Clears all domain events currently held. Usually invoked by the infrastructure in place in
   * Spring Data repositories.
   */
  @AfterDomainEventPublication
  protected void clearDomainEvents() {
    this.domainEvents.clear();
  }

  /**
   * All domain events currently captured by the aggregate, maybe with an additional special event
   * for storing them to the event stream
   * 
   * <p>If exist,the special event is at the end of the collection.
   */
  @DomainEvents
  protected Collection<Object> domainEventsThatCanBeStored() {
    Optional<String> optionalStreamId = this.eventStreamIdentification();
    if (optionalStreamId.isPresent() && this.domainEvents.size() > 0) {
      List<Object> eventsWithSpecial = new ArrayList<>(this.domainEvents.size() + 1);
      eventsWithSpecial.addAll(this.domainEvents);
      // Do not directly set `this.domainEvents` or its unmodifiable variant to the special event.
      // During the testing, it was found that the transactional event listener could not receive
      // the expected data.
      List<AbstractDomainEvent> unTransientEvents = new ArrayList<>(this.domainEvents.size());
      unTransientEvents.addAll(this.domainEvents);
      eventsWithSpecial.add(new SpecialEventForStoring(optionalStreamId.get(), unTransientEvents));
      return Collections.unmodifiableList(eventsWithSpecial);
    } else {
      return Collections.unmodifiableList(this.domainEvents);
    }

  }

  /**
   * Return the optional event stream identification.
   * 
   * <p>This method can return empty, if the aggregate does not want to store any events.
   * 
   * <p>Usually, this is a combination of aggregate type and aggregate identification, or equivalent
   * to aggregate type.
   * 
   * <p>A unit of work may publish multiple events, but usually they all belong to the same event
   * stream.
   * 
   * @see SpecialEventForStoring#eventStreamIdentification
   */
  protected abstract Optional<String> eventStreamIdentification();

  /**
   * All domain events currently captured by the aggregate.
   */
  protected Collection<AbstractDomainEvent> domainEvents() {
    return Collections.unmodifiableList(domainEvents);
  }

  /**
   * Adds all events contained in the given aggregate to the current one.
   * 
   * @param aggregate must not be {@literal null}.
   * @return the aggregate
   */
  @SuppressWarnings("unchecked")
  protected final A andEventsFrom(A aggregate) {

    Assert.notNull(aggregate, "Aggregate must not be null!");

    this.domainEvents.addAll(aggregate.domainEvents());

    return (A) this;
  }

  /**
   * Adds the given event to the aggregate for later publication when calling a Spring Data
   * repository's save-method. Does the same as {@link #registerEvent(AbstractDomainEvent)} but
   * returns the aggregate instead of the event.
   * 
   * @param event must not be {@literal null}.
   * @return the aggregate
   * @see #registerEvent(AbstractDomainEvent)
   */
  @SuppressWarnings("unchecked")
  protected final A andEvent(AbstractDomainEvent event) {

    registerEvent(event);

    return (A) this;
  }

  /**
   * Just to aid unit testing. Confirm if the event has been captured.
   * 
   * @param reference The reference event
   * @param maxElapsedMillis The maximum allowable elapsed time, in milliseconds. When less than or
   *        equal to zero, this parameter will be ignored. Otherwise, the result is true not only
   *        requires that the event has been captured, but also requires that it has been recently
   *        captured
   * @return whether the event has been captured
   */
  public boolean hasCapturedEvent(AbstractDomainEvent reference, long maxElapsedMillis) {
    if (maxElapsedMillis <= 0) {
      return this.domainEvents.contains(reference);
    } else {
      LocalDateTime now = LocalDateTime.now();
      return this.domainEvents.stream().anyMatch(domainEvent -> {
        return domainEvent.equals(reference)
            && domainEvent.getOccurredOn().isBefore(now)
            && domainEvent.getOccurredOn().until(now, ChronoUnit.MILLIS) <= maxElapsedMillis;
      });
    }
  }
}
