package xyz.hnlxl.cao.autoconfigure.jpadomainevent;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import xyz.hnlxl.cao.autoconfigure.jpadomainevent.ezio.Assassin;
import xyz.hnlxl.cao.autoconfigure.jpadomainevent.ezio.AssassinRepo;
import xyz.hnlxl.cao.autoconfigure.jpadomainevent.ezio.AssassinService;
import xyz.hnlxl.cao.domainbase.DomainSupportException;

@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
class VerifyingDomainEventsPublishedInterceptorTest {
  @Autowired
  private AssassinRepo assassinRepo;
  @Autowired
  private AssassinService assassinService;

  @BeforeEach
  public void setUp() {
    assassinRepo.save(Assassin.register("001"));
  }

  @AfterEach
  public void tearDown() {
    assassinRepo.deleteById("001");
  }

  @Test
  void test_saveExplicitly(CapturedOutput output) {
    assassinService.forTestExplicitSaving("001");
    assertThat(output.getOut(), containsString("forTestExplicitSave method done!"));
  }

  @Test
  void test_saveImplicitly() {
    Exception thrown = null;
    try {
      assassinService.forTestImplicitSaving("001");
    } catch (Exception e) {
      thrown = e;
    }

    assertNotNull(thrown);
    Throwable zeroThrown = thrown;
    while (zeroThrown.getCause() != null) {
      zeroThrown = zeroThrown.getCause();
    }

    assertThat(zeroThrown, instanceOf(DomainSupportException.class));
    assertEquals("Aggregates has unpublished events! Entities is: ["
        + "xyz.hnlxl.cao.autoconfigure.jpadomainevent.ezio.Assassin"
        + "]. When enabling domain events, it must explicitly call \"repository.save\" method"
        + " to publish the registered events.",
        zeroThrown.getMessage());
  }

}
