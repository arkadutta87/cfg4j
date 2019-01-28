package org.cfg4j.source.reload;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.context.environment.DefaultEnvironment;
import org.cfg4j.source.context.environment.Environment;
import org.cfg4j.source.context.environment.MissingEnvironmentException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Properties;


@RunWith(MockitoJUnitRunner.class)
public class CachedConfigurationSourceTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Mock
  private ConfigurationSource delegateSource;
  private CachedConfigurationSource cachedConfigurationSource;

  @Before
  public void setUp() throws Exception {
    cachedConfigurationSource = new CachedConfigurationSource(delegateSource, "");
  }

  @Test
  public void initPropagatesMissingEnvExceptions() throws Exception {
    doThrow(new MissingEnvironmentException("")).when(delegateSource).init();

    expectedException.expect(MissingEnvironmentException.class);
    cachedConfigurationSource.init();
  }

  @Test
  public void initPropagatesIllegalStateExceptions() throws Exception {
    doThrow(new IllegalStateException("")).when(delegateSource).init();

    expectedException.expect(IllegalStateException.class);
    cachedConfigurationSource.init();
  }


  @Test
  public void reloadPropagatesMissingEnvExceptions() throws Exception {
    when(delegateSource.getConfiguration(any(Environment.class))).thenThrow(new MissingEnvironmentException(""));

    expectedException.expect(MissingEnvironmentException.class);
    cachedConfigurationSource.reload(new DefaultEnvironment());
  }

  @Test
  public void reloadPropagatesIllegalStateExceptions() throws Exception {
    when(delegateSource.getConfiguration(any(Environment.class))).thenThrow(new IllegalStateException(""));

    expectedException.expect(IllegalStateException.class);
    cachedConfigurationSource.reload(new DefaultEnvironment());
  }
}