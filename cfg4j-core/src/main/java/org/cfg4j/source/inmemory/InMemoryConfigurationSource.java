/*
 * Copyright 2015-2016 Norbert Potocki (norbert.potocki@nort.pl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cfg4j.source.inmemory;

import static java.util.Objects.requireNonNull;

import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.context.environment.Environment;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Simple in-memory {@link ConfigurationSource}.
 */
public class InMemoryConfigurationSource implements ConfigurationSource {

  private Map<String, Properties> properties;

  /**
   * Create in-memory configuration source with given {@code properties}.
   *
   * @param properties properties to seed source.
   */
  public InMemoryConfigurationSource(Map<String, Properties> properties) {
    this.properties = requireNonNull(properties);
  }

  @Override
  public Map<String, Properties> getConfiguration(Environment environment) {
    Map<String, Properties> cloneMap = new HashMap<>();
    cloneMap.putAll(properties);
//    return (Map<String, Properties>) properties.clone();
    return cloneMap;
  }

  @Override
  public void init() {
    properties = new HashMap<>();
    // NOP
  }

  @Override
  public String toString() {
    return "InMemoryConfigurationSource{" +
        "properties=" + properties +
        '}';
  }
}
