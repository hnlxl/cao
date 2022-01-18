package xyz.hnlxl.cao.jpadomainevent.store;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import xyz.hnlxl.cao.domainbase.DomainSupportException;
import xyz.hnlxl.cao.domainbase.SpecialEventForStoring;

/**
 * Service about store event
 * 
 * @author hnlxl at 2021/12/22
 *
 */
public class StoreService {
  private static final String KEY_READ_CONTRACT = "rc";
  private static final String KEY_CONTENT = "c";

  private EventStreamItemRepo repo;
  private ObjectMapper objectMapper;

  /** Constructor. */
  public StoreService(EventStreamItemRepo repo) {
    super();
    this.repo = repo;
    this.objectMapper = JsonMapper.builder()
        .addModule(new Jdk8Module())
        .addModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .build();
  }

  /**
   * Store a SpecialEventForStoring.
   */
  public void store(SpecialEventForStoring specialEventForStoring) {
    final String stream = specialEventForStoring.getEventStreamIdentification();
    repo.tryMakeSpecialStreamLock(stream);


    EventStreamItem entity = new EventStreamItem();
    entity.setStream(stream);

    entity.setSequence(repo.findMaxSequenceOfStream(stream) + 1);

    List<?> packagedEvents = specialEventForStoring.getOriginalEvents().stream()
        .map(originalEvent -> {
          Map<String, Object> oneEventElement = new HashMap<>();
          oneEventElement.put(KEY_READ_CONTRACT, originalEvent.getClass().getName());
          oneEventElement.put(KEY_CONTENT, originalEvent);
          return oneEventElement;
        }).collect(Collectors.toList());
    String eventsJson = "";
    try {
      eventsJson = objectMapper.writeValueAsString(packagedEvents);
    } catch (JsonProcessingException e) {
      throw new DomainSupportException("Can't serialize event when storing", e);
    }
    entity.setEvents(eventsJson);


    repo.save(entity);
    repo.markReleaseSpecialStreamLock(stream);
  }
}
