package net.rakugakibox.spring.boot.logback.access;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Simple suite to debug problems related to shared resources between tests.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        ConfigurationFileAutoDetectingTest.class,
        FallbackConfigurationFileAutoDetectingTest.class
})
public class TestSuite {

}
