package xyz.hnlxl.cao.autoconfigure.jpadomainevent.ezio;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** A Application Service for test. */
@Service
@Slf4j
public class AssassinService {
  @Autowired
  private AssassinRepo assassinRepo;

  /** A method for testing explicit saving. */
  @Transactional
  public void forTestExplicitSaving(String code) {
    assassinRepo.findById(code).ifPresent((assassin) -> {
      assassin.leapOfFaith();
      assassinRepo.save(assassin);
    });
    log.info("forTestExplicitSave method done!");
  }

  /** A method for testing implicit saving. */
  @Transactional
  public void forTestImplicitSaving(String code) {
    assassinRepo.findById(code).ifPresent((assassin) -> {
      assassin.leap();
    });
  }

  /** A method for testing storing domain event's committed case. */
  @Transactional
  public void forTestDomainEventStoringCommit(String code) {
    assassinRepo.findById(code).ifPresent((assassin) -> {
      assassin.leapOfFaith();
      assassin.leap();
      assassinRepo.save(assassin);
      assassin.reload();
      assassinRepo.save(assassin);
    });
  }

  /** A method for testing storing domain event's rolled back case. */
  @Transactional
  public void forTestDomainEventStoringRollback(String code) {
    assassinRepo.findById(code).ifPresent((assassin) -> {
      assassin.leap();
      assassinRepo.save(assassin);
      throw new RuntimeException("A manual error for triggering a rollback");
    });
  }
}
