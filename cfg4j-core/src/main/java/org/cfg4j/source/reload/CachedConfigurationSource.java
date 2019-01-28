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
package org.cfg4j.source.reload;

import static java.util.Objects.requireNonNull;

import com.google.gson.Gson;

import org.cfg4j.provider.ConfigMeta;
import org.cfg4j.provider.ConfigurationProviderBuilder;
import org.cfg4j.source.ConfigurationDataWrapper;
import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.context.environment.Environment;
import org.cfg4j.source.context.environment.MissingEnvironmentException;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A {@link ConfigurationSource} that caches configuration between calls to the {@link #reload(Environment)} method.
 */
public class CachedConfigurationSource implements ConfigurationDataWrapper {

  private static final Logger LOG = LoggerFactory.getLogger(CachedConfigurationSource.class);

  private final ConfigurationSource underlyingSource;

  private Map<Class<?>, ConfigMetaInfo> configMetaData;
  private Map<String, Map<Class<?>, Object>> dataCache;

  private ReadWriteLock readWriteLock;

  /**
   * Create a new cached configuration source backed by {@code underlyingSource}.
   *
   * @param underlyingSource source used to load data into cache.
   */
  public CachedConfigurationSource(ConfigurationSource underlyingSource, String basePathToConfigMetaClasses) {
    this.underlyingSource = requireNonNull(underlyingSource);

    this.configMetaData = extractConfigMetaData(basePathToConfigMetaClasses);
    this.readWriteLock = new ReentrantReadWriteLock();

    dataCache = new HashMap<>();
  }

  private Map<Class<?>, ConfigMetaInfo> extractConfigMetaData(String basePathToConfigMetaClasses) {
    Map<Class<?>, ConfigMetaInfo> metaMap = new HashMap<>();

    try {
      Set<Class<?>> classes = getClasses(basePathToConfigMetaClasses);

      for (Class<?> aClass : classes) {
        ConfigMeta[] annotationsByType = aClass.getAnnotationsByType(ConfigMeta.class);

        if (annotationsByType == null || annotationsByType.length == 0) {
          throw new RuntimeException("ConfigMeta Annotation not present : Fatal Error");
        }

        ConfigMeta configMeta = annotationsByType[0];

        String configKey = configMeta.configKey();
        String bindedFileName = configMeta.bindedFileName();

        metaMap.put(aClass, new ConfigMetaInfo(configKey, bindedFileName));

      }
    }
    catch (Exception e) {
      LOG.error("Error while parsing the basePackageName : " + basePathToConfigMetaClasses + ", Error : " + e);
    }

    return metaMap;
  }

  private Set<Class<?>> getClasses(String packageName) throws ClassNotFoundException, IOException {

    Reflections reflections = new Reflections(packageName);
    return reflections.getTypesAnnotatedWith(ConfigMeta.class);
  }

  public <T> T extract(Environment environment, Class<T> type) {

    T data = null;
    readWriteLock.readLock().lock();
    if (dataCache.containsKey(environment.getName())) {

      Map<Class<?>, Object> classObjectMap = dataCache.get(environment.getName());
      data = (T) classObjectMap.get(type);
    }

    readWriteLock.readLock().unlock();
    return data;
  }

  @Override
  public void init() {
    underlyingSource.init();
  }

  /**
   * Reload configuration set for a given {@code environment} from this source in a form of {@link Properties}.
   *
   * @param environment environment to reload
   * @throws MissingEnvironmentException when requested environment couldn't be found
   * @throws IllegalStateException when unable to fetch configuration
   */
  public void reload(Environment environment) {
    ConfigurationSource.ConfigurationState configurationState = underlyingSource.getConfiguration(environment);

    if (configurationState.isStateChanged()) {
      Map<String, Properties> configuration = configurationState.getData();

      Map<Class<?>, Object> cachedData = new HashMap<>();
      configMetaData.forEach((aClass, classMeta) -> {
        String configKey = classMeta.getConfigKey();
        String bindedFileName = classMeta.getBindedFileName();

        Object obj = configuration.get(bindedFileName).get(configKey);

        Gson gson = new Gson();
        String jsonObj = gson.toJson(obj);
        Object finalCachedObj = gson.fromJson(jsonObj, aClass);
        cachedData.put(aClass, finalCachedObj);
      });

      readWriteLock.writeLock().lock();
      dataCache.put(environment.getName(), cachedData);
      readWriteLock.writeLock().unlock();

    }
  }
}
