/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you 
 * may not use this file except in compliance with the License. 
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
package esiptestbed.mudrod.weblog.pre;

import java.util.Map;

import org.elasticsearch.index.query.QueryBuilders;

import esiptestbed.mudrod.discoveryengine.DiscoveryStepAbstract;
import esiptestbed.mudrod.driver.ESDriver;
import esiptestbed.mudrod.driver.SparkDriver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Supports ability to remove raw logs after processing is finished
 */
public class RemoveRawLog extends DiscoveryStepAbstract {
  private static final long serialVersionUID = 1L;
  private static final Logger LOG = LoggerFactory.getLogger(RemoveRawLog.class);

  /**
   * Constructor supporting a number of parameters documented below.
   * @param config a {@link java.util.Map} containing K,V of type String, String respectively.
   * @param es the {@link esiptestbed.mudrod.driver.ESDriver} used to persist log files.
   * @param spark the {@link esiptestbed.mudrod.driver.SparkDriver} used to process input log files.
   */
  public RemoveRawLog(Map<String, String> config, ESDriver es,
      SparkDriver spark) {
    super(config, es, spark);
  }

  @Override
  public Object execute() {
    LOG.info("*****************Clean raw log starts******************");
    startTime = System.currentTimeMillis();
    es.deleteAllByQuery(config.get("indexName"), HTTP_type,
        QueryBuilders.matchAllQuery());
    es.deleteAllByQuery(config.get("indexName"), FTP_type,
        QueryBuilders.matchAllQuery());
    endTime = System.currentTimeMillis();
    es.refreshIndex();
    LOG.info("*****************Clean raw log ends******************Took {}s", (endTime - startTime) / 1000);
    return null;
  }

  @Override
  public Object execute(Object o) {
    return null;
  }

}
