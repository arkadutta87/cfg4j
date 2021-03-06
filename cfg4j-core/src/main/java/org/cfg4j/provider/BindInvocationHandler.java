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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.NoSuchElementException;

/**
 * Invocation handler for proxies created by {@link ConfigurationProvider#bind(String, Class)}. Uses provided
 * {@link ConfigurationProvider} for getting properties.
 */
class BindInvocationHandler {

  private final ConfigurationProvider simpleConfigurationProvider;
  private final String prefix;

  /**
   * Create invocation handler which fetches property from given {@code configurationProvider} using call to
   *
   * @param configurationProvider configuration provider to use for fetching properties
   */
  BindInvocationHandler(ConfigurationProvider configurationProvider, String prefix) {
    this.simpleConfigurationProvider = requireNonNull(configurationProvider);
    this.prefix = requireNonNull(prefix);
  }

  /**
   * Check if method is defined by Object class (e.g. {@link Object#hashCode()}.
   */
  private boolean isObjectMethod(Method method) {
    for (Method objectMethod : Object.class.getMethods()) {
      if (method.getName().equals(objectMethod.getName())) {
        if (equalParamTypes(objectMethod.getParameterTypes(), method.getParameterTypes())) {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * Check if two arrays of parameter types are equal.
   */
  private boolean equalParamTypes(Class<?>[] params1, Class<?>[] params2) {
    if (params1.length == params2.length) {
      for (int i = 0; i < params1.length; i++) {
        if (params1[i] != params2[i]) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

}
