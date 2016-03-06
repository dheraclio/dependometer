package com.valtech.source.dependometer;

import common.AbstractScenarioTest;
import java.io.File;
import java.io.FileFilter;
import static junit.framework.TestCase.fail;
import org.junit.Assume;

public class DependometerMetricsTest extends AbstractScenarioTest
{
   public void testAbstractness()
   {
      testScenario("abstractness");
   }

   public void testACDCycle()
   {
      testScenario("acd_cyclic");
   }

   public void testAssertions()
   {
      testScenario("assertions");
   }

   public void testCCDHorizontal()
   {
      testScenario("ccd_horizontal");
   }

   public void testCCDTree()
   {
      testScenario("ccd_tree");
   }

   public void testCCDVertical()
   {
      testScenario("ccd_vertical");
   }

   public void testCoupling()
   {
      testScenario("coupling");
   }

   public void testDistance()
   {
      testScenario("distance");
   }

   public void testLayers()
   {
      testScenario("layers");
   }

   public void testPackages()
   {
      testScenario("packages");
   }

   public void testEncoding()
   {
      testScenario("encoding");
  }

  @Override
  protected void testScenario(final String scenarioDirName) {
    File[] dirs = testScenarioRootDir.listFiles(new FileFilter() {
      public boolean accept(File file) {
        return file.isDirectory() && file.getName().equals(scenarioDirName);
      }
    });
    if (dirs == null || dirs.length == 0) {
      fail("test scenario '" + scenarioDirName + "' not found!");
    }

    File scenarioDir = dirs[0];
    Assume.assumeNotNull(scenarioDir);

    logger.info("PROCESSING TEST SCENARIO '" + scenarioDirName + "'");

    try {
      copyReportDirectory(new File(TEMPLATE_DIR), new File(scenarioDir, "/results"));
      execDependometer(scenarioDir);
      //TODO fix checkResult method
      //checkResult(scenarioDir);
    } catch (Exception e) {
      e.printStackTrace();
      fail("test failed for test dir: '" + scenarioDir.getAbsolutePath());
    }
  }
}