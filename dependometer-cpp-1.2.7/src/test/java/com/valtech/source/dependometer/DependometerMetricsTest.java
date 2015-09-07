package com.valtech.source.dependometer;

import common.AbstractScenarioTest;

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
}