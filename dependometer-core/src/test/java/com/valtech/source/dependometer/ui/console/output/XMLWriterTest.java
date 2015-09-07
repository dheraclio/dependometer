package com.valtech.source.dependometer.ui.console.output;

import java.io.File;
import java.io.IOException;

import org.xml.sax.SAXException;

import com.valtech.source.dependometer.app.controller.compilationunit.SinglePackageCompilationUnitCycleCollectedEvent;
import com.valtech.source.dependometer.app.controller.project.AnalysisFinishedEvent;
import com.valtech.source.dependometer.app.core.elements.ParsedCompilationUnit;
import com.valtech.source.dependometer.app.core.elements.Project;
import com.valtech.source.dependometer.app.core.provider.DependencyElementIf;
import com.valtech.source.dependometer.app.core.provider.ProviderFactory;
import common.AbstractDependometerTest;
import common.DependomterTestUtil;
import common.SimpleTestConfiguration;

public class XMLWriterTest extends AbstractDependometerTest
{
   private File xmlResult;

   private Project project;

   public void setUp()
   {
      super.setUp();
       ProviderFactory.reset();
      ((ProviderFactory)ProviderFactory.getInstance()).setConfigurationProvider(new SimpleTestConfiguration());

      xmlResult = generateTempFile(".xml");

      project = new Project(getClass().getSimpleName(), this);
   }

   public void tearDown()
   {
      DependomterTestUtil.resetDependometer();
      xmlResult.delete();
   }

   public void testXMLWriter() throws IOException, SAXException
   {

      xmlResult = File.createTempFile("dependometer-xml-results", ".xml");

      XMLWriter xmlWriter = new XMLWriter(new String[] {
         xmlResult.getAbsolutePath() });

      AnalysisFinishedEvent event = new AnalysisFinishedEvent(project);
      xmlWriter.handleEvent(event);

      assertTrue(xmlResult.exists());

      String fileContent = DependomterTestUtil.readFileAsString(xmlResult);
      // not empty
      assertTrue(fileContent.contains(project.getName()));

      System.out.println(xmlResult.getAbsolutePath());
   }

   public void testWriteCompilationUnitCycle() throws IOException, SAXException
   {
      XMLWriter xmlWriter = new XMLWriter(new String[] {
         xmlResult.getAbsolutePath() });

      DependencyElementIf[] participants = new DependencyElementIf[] {
         new ParsedCompilationUnit("cycletest.one.CompilationUnitA.java", "cycletest.one"),
         new ParsedCompilationUnit("cycletest.two.CompilationUnitB.java", "cycletest.two") };
      SinglePackageCompilationUnitCycleCollectedEvent cycleEvent = new SinglePackageCompilationUnitCycleCollectedEvent();
      cycleEvent.setCycle(participants);

      xmlWriter.handleEvent(cycleEvent);

      AnalysisFinishedEvent event = new AnalysisFinishedEvent(project);
      xmlWriter.handleEvent(event);

      assertTrue(xmlResult.exists());

      String fileContent = DependomterTestUtil.readFileAsString(xmlResult);
      // not empty
      assertTrue(fileContent.contains("<cycle level=\"COMPILATION_UNIT\">"));

      System.out.println(xmlResult.getAbsolutePath());

      System.out.println(fileContent);
   }
}
