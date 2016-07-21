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
package esiptestbed.mudrod.main;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import esiptestbed.mudrod.discoveryengine.DiscoveryEngineAbstract;
import esiptestbed.mudrod.discoveryengine.MetadataDiscoveryEngine;
import esiptestbed.mudrod.discoveryengine.OntologyDiscoveryEngine;
import esiptestbed.mudrod.discoveryengine.WeblogDiscoveryEngine;
import esiptestbed.mudrod.driver.ESDriver;
import esiptestbed.mudrod.driver.SparkDriver;
import esiptestbed.mudrod.integration.LinkageIntegration;

public class MudrodEngine {
  private Map<String, String> config = new HashMap<>();
  private ESDriver es = null;
  private SparkDriver spark = null;

  public MudrodEngine() {
    loadConfig();
    es = new ESDriver(config);
    spark = new SparkDriver();

  }

  public Map<String, String> getConfig() {
    return config;
  }

  public ESDriver getES() {
    return this.es;
  }

  public void loadConfig() {
    SAXBuilder saxBuilder = new SAXBuilder();
    InputStream configStream = MudrodEngine.class.getClassLoader()
        .getResourceAsStream("config.xml");

    Document document;
    try {
      document = saxBuilder.build(configStream);
      Element rootNode = document.getRootElement();
      List<Element> para_list = rootNode.getChildren("para");

      for (int i = 0; i < para_list.size(); i++) {
        Element para_node = para_list.get(i);
        config.put(para_node.getAttributeValue("name"),
            para_node.getTextTrim());
      }
    } catch (JDOMException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  public void start() {
    DiscoveryEngineAbstract wd = new WeblogDiscoveryEngine(config, es, spark);
    wd.preprocess();
    wd.process();

    DiscoveryEngineAbstract od = new OntologyDiscoveryEngine(config, es, spark);
    od.preprocess();
    od.process();

    DiscoveryEngineAbstract md = new MetadataDiscoveryEngine(config, es, spark);
    md.preprocess();
    md.process();

    LinkageIntegration li = new LinkageIntegration(config, es, spark);
    li.execute();
  }

  public void end() {
    es.close();
  }

  public static void main(String[] args) {
    if (args.length != 1) {
      System.out.println("Mudrod Engine expects one argument <logDirectory>");
      return;
    }
    String dataDir = args[0];
    if(!dataDir.endsWith("/")){
      dataDir += "/";
    }
    MudrodEngine me = new MudrodEngine();
    me.config.put("logDir", dataDir);

    me.config.put("ontologyInputDir", dataDir + "SWEET_ocean/");
    me.config.put("oceanTriples", dataDir + "Ocean_triples.csv");

    me.config.put("userHistoryMatrix", dataDir + "UserHistoryMatrix.csv");
    me.config.put("clickstreamMatrix", dataDir + "ClickstreamMatrix.csv");
    me.config.put("metadataMatrix", dataDir + "MetadataMatrix.csv");

    me.config.put("clickstreamSVDMatrix_tmp", dataDir + "clickstreamSVDMatrix_tmp.csv");
    me.config.put("metadataSVDMatrix_tmp", dataDir + "metadataSVDMatrix_tmp.csv");

    me.config.put("raw_metadataPath", dataDir + "RawMetadata");

    me.start();
    me.end();
  }
}