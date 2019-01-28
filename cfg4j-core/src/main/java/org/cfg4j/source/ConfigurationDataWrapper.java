package org.cfg4j.source;

import static java.util.Objects.requireNonNull;

import org.cfg4j.source.context.environment.Environment;

public interface ConfigurationDataWrapper {

  void init();

  <T> T extract(Environment environment, Class<T> type);

   void reload(Environment environment);

  public class ConfigMetaInfo{

    private String configKey;
    private String bindedFileName;

    public ConfigMetaInfo(String configKey, String bindedFileName){
      this.configKey = requireNonNull(configKey);
      this.bindedFileName = requireNonNull(bindedFileName);
    }

    public String getConfigKey() {
      return configKey;
    }

    public String getBindedFileName() {
      return bindedFileName;
    }
  }
}
