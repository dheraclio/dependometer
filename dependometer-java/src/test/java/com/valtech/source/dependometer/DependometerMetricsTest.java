package com.valtech.source.dependometer;

import common.AbstractScenarioTest;


public class DependometerMetricsTest extends AbstractScenarioTest
{
   public void testAnnotations()
   {
      testScenario("annotations");
   }
   
   public void testArchitecture()
   {
      testScenario("architecture");
   }
   
   public void testCCD()
   {
      testScenario("ccd");
   }
   
   public void testCombination()
   {
      testScenario("combination");
   }
   
   public void testCylce()
   {
      testScenario("cycle");
   }
   
   public void testEnums()
   {
      testScenario("enums");
   }
   
   public void testException()
   {
      testScenario("exception");
   }
   
   public void testGenerics()
   {
      testScenario("generics");
   }
   
   public void testInheritance()
   {
      testScenario("inheritance");
   }
   
   public void testInlineable()
   {
      testScenario("inlineable");
   }
   
   public void testMetaAnnotaions()
   {
      testScenario("metaAnnotations");
   }
   
   public void testStaticImport()
   {
      testScenario("staticImport");
   }
   
   public void testPackages()
   {
      testScenario("packages");
   }
   
   public void testString()
   {
      testScenario("string");
   }
}