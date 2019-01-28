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
package org.cfg4j.provider;

import static java.util.Objects.requireNonNull;

import com.github.drapostolos.typeparser.NoSuchRegisteredParserException;
import com.github.drapostolos.typeparser.TypeParser;
import com.github.drapostolos.typeparser.TypeParserException;
import com.google.gson.Gson;

import org.cfg4j.source.ConfigurationDataWrapper;
import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.context.environment.Environment;
import org.cfg4j.source.context.environment.MissingEnvironmentException;
import org.cfg4j.validator.BindingValidator;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;

/**
 * Basic implementation of {@link ConfigurationProvider}. To construct this provider use {@link ConfigurationProviderBuilder}.
 */
class SimpleConfigurationProvider implements ConfigurationProvider {

  private final ConfigurationDataWrapper configurationDataWrapper;
  private final Environment environment;

  /**
   * {@link ConfigurationProvider} backed by provided {@link ConfigurationSource} and using {@code environment} to select environment. To
   * construct this provider use {@link ConfigurationProviderBuilder}.
   *
   * @param configurationDataWrapper source for configuration
   * @param environment {@link Environment} to use
   */
  SimpleConfigurationProvider(ConfigurationDataWrapper configurationDataWrapper, Environment environment) {
    this.configurationDataWrapper = requireNonNull(configurationDataWrapper);
    this.environment = requireNonNull(environment);
  }

  public <T> T extract(Class<T> type) {
    return configurationDataWrapper.extract(environment, type);
  }

  @Override
  public String toString() {
    return "SimpleConfigurationProvider{" +
      "configurationSource=" + configurationDataWrapper +
      ", environment=" + environment +
      '}';
  }

}
