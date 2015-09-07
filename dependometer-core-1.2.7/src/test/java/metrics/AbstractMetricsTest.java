package metrics;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;

import com.valtech.source.dependometer.app.configprovider.filebased.xml.ConfigurationProvider;
import com.valtech.source.dependometer.app.controller.main.DependencyManager;
import com.valtech.source.dependometer.app.core.elements.Project;
import com.valtech.source.dependometer.app.core.provider.ConfigurationProviderIf;
import com.valtech.source.dependometer.app.core.provider.ProviderFactory;
import com.valtech.source.dependometer.app.core.provider.TypeDefinitionIf;
import com.valtech.source.dependometer.app.typedefprovider.filebased.xml.TypeDefinitionProviderXML;
import com.valtech.source.dependometer.ui.console.Dependometer;

import common.AbstractDependometerTest;
import common.DependomterTestUtil;
import common.SimpleTestConfiguration;
import common.TestProviderFactory;

public abstract class AbstractMetricsTest extends AbstractDependometerTest
{
   private static final String METRIC_TESTS = "metrics";

   private static final Logger logger = Logger.getLogger(AbstractMetricsTest.class.getName());

   protected DependencyManager dependencyManager;

   protected Project project;

   @Override
   public void setUp()
   {
      dependencyManager = new DependencyManager(Dependometer.getContext(), new Date(new Date().getTime() + 15L * 60000L));
   }

   @Override
   protected void tearDown() throws Exception
   {
      super.tearDown();
      DependomterTestUtil.resetDependometer();
   }

   protected void runDependometerWithTestProject(String testProject)
   {
      runDependometerWithTestProject(testProject, false);
   }

   protected void runDependometerWithTestProject(String testProject, boolean loadConfig)
   {
      TestProviderFactory testProviderFactory = new TestProviderFactory();

      String testProjectDefinitionPath = METRIC_TESTS + "/" + testProject + ".xml";

      TypeDefinitionProviderXML typeDefinitionProvider = loadXMLTypeDefinitionProvider(testProjectDefinitionPath);

      if (logger.isDebugEnabled())
      {
         try
         {
            debugTypesDefinitions(testProjectDefinitionPath, typeDefinitionProvider.getTypeDefinitions());
         }
         catch (IOException e)
         {
            e.printStackTrace();
            fail();
         }
      }

      testProviderFactory.setTypeDefinitionProvider(typeDefinitionProvider);

      ConfigurationProviderIf testConfig = null;

      if (!loadConfig)
      {
         testConfig = new SimpleTestConfiguration();
      }
      else
      {
         testConfig = loadConfig(testProject);
      }
      testProviderFactory.setConfigurationProvider(testConfig);

      ProviderFactory.setInstance(testProviderFactory);

      System.getProperty(ProviderFactory.TYPE_DEFINITION_PROVIDER_KEY);

      try
      {
         
         dependencyManager.analyze();
         dependencyManager.start();
         dependencyManager.join();
      }
      catch (Exception e)
      {
         logger.fatal("Error while running dependomer", e);
         fail("Error while running dependomer: " + e);
      }

      project = dependencyManager.getProject();
   }
   

   private ConfigurationProviderIf loadConfig(String testProject)
   {
      File configFile = loadTestDataAsFile("/" + METRIC_TESTS + "/" + testProject + "-config.xml");
      ConfigurationProvider config = new ConfigurationProvider();
      try
      {
         config.readConfiguration(configFile);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail("Could not load configuration file '" + configFile.getAbsolutePath() + "' for test project "
            + testProject);
      }
      return config;
   }

   private void debugTypesDefinitions(String testProjectDefinitionPath, TypeDefinitionIf[] typeDefinitions)
   {
      logger.debug("Type definitions for test project '" + testProjectDefinitionPath + "':\n");
      for (TypeDefinitionIf td : typeDefinitions)
      {
         logger.debug(td);
      }
   }

}
