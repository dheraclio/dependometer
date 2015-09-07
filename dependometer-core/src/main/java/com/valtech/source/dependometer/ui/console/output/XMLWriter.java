package com.valtech.source.dependometer.ui.console.output;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import com.valtech.source.dependometer.app.controller.compilationunit.HandleMultiplePackageCompilationUnitCycleCollectedEventIf;
import com.valtech.source.dependometer.app.controller.compilationunit.HandleSinglePackageCompilationUnitCycleCollectedEventIf;
import com.valtech.source.dependometer.app.controller.compilationunit.MultiplePackageCompilationUnitCycleCollectedEvent;
import com.valtech.source.dependometer.app.controller.compilationunit.SinglePackageCompilationUnitCycleCollectedEvent;
import com.valtech.source.dependometer.app.controller.layer.HandleLayerCycleCollectedEventIf;
import com.valtech.source.dependometer.app.controller.layer.LayerCycleCollectedEvent;
import com.valtech.source.dependometer.app.controller.main.DependometerContext;
import com.valtech.source.dependometer.app.controller.pack.HandlePackageCycleCollectedEventIf;
import com.valtech.source.dependometer.app.controller.pack.PackageCycleCollectedEvent;
import com.valtech.source.dependometer.app.controller.project.AnalysisFinishedEvent;
import com.valtech.source.dependometer.app.controller.project.CycleCollectedEvent;
import com.valtech.source.dependometer.app.controller.project.HandleAnalysisFinishedEventIf;
import com.valtech.source.dependometer.app.controller.subsystem.HandleSubsystemCycleCollectedEventIf;
import com.valtech.source.dependometer.app.controller.subsystem.SubsystemCycleCollectedEvent;
import com.valtech.source.dependometer.app.controller.type.HandleMultiplePackageTypeCycleCollectedEventIf;
import com.valtech.source.dependometer.app.controller.type.HandleSingleCompilationUnitTypeCycleCollectedEventIf;
import com.valtech.source.dependometer.app.controller.type.HandleSinglePackageTypeCycleCollectedEventIf;
import com.valtech.source.dependometer.app.controller.type.MultiplePackageTypeCycleCollectedEvent;
import com.valtech.source.dependometer.app.controller.type.SingleCompilationUnitTypeCycleCollectedEvent;
import com.valtech.source.dependometer.app.controller.type.SinglePackageTypeCycleCollectedEvent;
import com.valtech.source.dependometer.app.controller.verticalslice.HandleVerticalSliceCycleCollectedEventIf;
import com.valtech.source.dependometer.app.controller.verticalslice.VerticalSliceCycleCollectedEvent;
import com.valtech.source.dependometer.app.core.elements.EntityTypeEnum;
import com.valtech.source.dependometer.app.core.provider.ConfigurationProviderIf;
import com.valtech.source.dependometer.app.core.provider.DependencyElementIf;
import com.valtech.source.dependometer.app.core.provider.MetricIf;
import com.valtech.source.dependometer.app.core.provider.ProjectIf;
import com.valtech.source.dependometer.app.core.provider.ProviderFactory;
import com.valtech.source.dependometer.ui.console.Dependometer;

/**
 * @author oliver.rohr
 * 
 *         Output listener for dependometer that writes XML report.
 */

public class XMLWriter implements HandleSingleCompilationUnitTypeCycleCollectedEventIf,
   HandleSinglePackageCompilationUnitCycleCollectedEventIf, HandleMultiplePackageTypeCycleCollectedEventIf,
   HandleMultiplePackageCompilationUnitCycleCollectedEventIf, HandlePackageCycleCollectedEventIf,
   HandleSubsystemCycleCollectedEventIf, HandleLayerCycleCollectedEventIf, HandleVerticalSliceCycleCollectedEventIf,
   HandleSinglePackageTypeCycleCollectedEventIf, HandleAnalysisFinishedEventIf
{
   public static final String FILE_ENCODING = "UTF-8";

   public static final String XML_ROOT = "dependometer-results";

   public static final String XML_PROJECT_NAME = "name";

   public static final String XML_PROJECT_TIME = "time";

   public static final String XML_METRIC = "metric";

   public static final String XML_OPTION = "option";

   public static final String XML_CYLE = "cycle";

   public static final String XML_CYLE_PARTICIPANT = "participant";

   public static final String XML_ID = "name";

   private static final String ERROR_WRITE_RESULTS = "Error while writing to dependometer results xml";

   private static final String XML_LEVEL = "level";

   private static final Attributes NO_ATTRIBS = new AttributesImpl();

   private static final String NS = "";

   private static final String CDATA = "CDATA";

   private static Logger logger = Logger.getLogger(XMLWriter.class.getName());

   private OutputStream out;

   private ConfigurationProviderIf configuration;

   private File outputFile;

   private ContentHandler writer;

   public XMLWriter(String[] arguments) throws IOException, SAXException
   {
      if (arguments.length < 1)
      {
         throw new IllegalArgumentException("Need at least one argument - output file path");
      }
      String filePath = arguments[0];

      outputFile = new File(filePath);

      DependometerContext context = Dependometer.getContext();
      context.getProjectManager().attach(this);
      context.getLayerManager().attach(this);
      context.getSubsystemManager().attach(this);
      context.getPackageManager().attach(this);
      context.getCompilationUnitManager().attach((HandleMultiplePackageCompilationUnitCycleCollectedEventIf)this);
      context.getCompilationUnitManager().attach((HandleSinglePackageCompilationUnitCycleCollectedEventIf)this);
      context.getTypeManager().attach((HandleSingleCompilationUnitTypeCycleCollectedEventIf)this);
      context.getTypeManager().attach((HandleSinglePackageTypeCycleCollectedEventIf)this);

      openResultFile(outputFile);
   }

   private void openResultFile(File outputFile) throws SAXException
   {
      try
      {
         out = new FileOutputStream(outputFile);

         OutputFormat of = new OutputFormat("XML", FILE_ENCODING, true);
         of.setIndent(1);
         of.setIndenting(true);
         XMLSerializer serializer = new XMLSerializer(out, of);

         writer = serializer.asContentHandler();
         writer.startDocument();

         startElement(XML_ROOT, NO_ATTRIBS);
      }
      catch (IOException io)
      {
         logger.error(ERROR_WRITE_RESULTS, io);
         close();
      }
   }

   private void startElement(String name, Attributes attribs) throws SAXException
   {
      writer.startElement(NS, "", name, attribs);
   }

   private void startElement(String name) throws SAXException
   {
      writer.startElement(NS, "", name, NO_ATTRIBS);
   }

   private void close()
   {
      try
      {
         if (out != null)
         {
            out.close();
         }
      }
      catch (IOException e)
      {
         logger.warn("Could not close '" + outputFile.getAbsolutePath() + "'", e);
      }
   }

   public void handleEvent(AnalysisFinishedEvent event)
   {
      logger.info("generating xml report ...");

      configuration = ProviderFactory.getInstance().getConfigurationProvider();

      ProjectIf project = event.getProject();

      try
      {
         AttributesImpl attribs = new AttributesImpl();
         attribs.addAttribute(NS, "", XML_PROJECT_NAME, CDATA, project.getFullyQualifiedName());
         attribs.addAttribute(NS, "", XML_PROJECT_TIME, CDATA, getTimestamp());

         startElement(EntityTypeEnum.PROJECT.getXmlName(), attribs);

         writeProjectMetrics(project);

         writeMetrics(project, project.getTypes(), EntityTypeEnum.TYPE );
         writeMetrics(project, project.getCompilationUnits(), EntityTypeEnum.COMPILATION_UNIT );
         writeMetrics(project, project.getPackages(), EntityTypeEnum.PACKAGE );
         writeMetrics(project, project.getSubsystems(), EntityTypeEnum.SUBSYSTEM );
         writeMetrics(project, project.getLayers(), EntityTypeEnum.LAYER );
         writeMetrics(project, project.getVerticalSlices(), EntityTypeEnum.VERTICAL_SLICE );

         endElement(EntityTypeEnum.PROJECT.getXmlName());
         endElement(XML_ROOT);

         writer.endDocument();

         out.flush();

         logger.info("xml report successfully written to '" + outputFile.getAbsolutePath() + "'");
      }
      catch (SAXException e)
      {
    	 logger.error(e.getMessage());
         throw new RuntimeException("Error while writing dependometer XML results!", e);
      }
      catch (IOException e)
      {
     	 logger.error(e.getMessage());
         e.printStackTrace();
      }
      finally
      {
         close();
      }
   }

   private void writeMetrics(ProjectIf project, DependencyElementIf[] elements, EntityTypeEnum element)
      throws SAXException
   {
      String elsName = element.getXmlName() + "s";
      startElement(elsName);

      for (DependencyElementIf type : elements)
      {
         String name = type.getFullyQualifiedName();
         String elName = element.getXmlName();

         AttributesImpl attribs = new AttributesImpl();
         attribs.addAttribute(NS, "", XML_ID, CDATA, name);
         startElement(elName, attribs);

         MetricIf[] metrics = type.getMetrics();

         for (MetricIf metric : metrics)
         {
            writeMetric(metric.getName(), metric.getValueAsString());
         }
         endElement(elName);
      }

      endElement(elsName);
   }

   private void writeProjectMetrics(ProjectIf project) throws SAXException
   {
      if (configuration.getMaxLayerCycles() >= -1)
      {
         writeOption("layer cycle detection enabled", "true");
      }
      else
      {
         writeOption("layer cycle detection enabled", "false");
      }

      if (configuration.getMaxSubsystemCycles() >= -1)
      {
         writeOption("subsystem cycle detection enabled", "true");
      }
      else
      {
         writeOption("subsystem cycle detection enabled", "false");
      }

      if (configuration.analyzeVerticalSlices())
      {
         writeOption("vertical slice analysis enabled", "true");

         if (configuration.getMaxVerticalSliceCycles() >= -1)
         {
            writeOption("vertical slice cycle detection enabled", "true");
         }
         else
         {
            writeOption("vertical slice cycle detection enabled", "false");
         }
      }
      else
      {
         writeOption("vertical slice analysis enabled", "false");
      }

      if (configuration.getMaxPackageCycles() >= -1)
      {
         writeOption("package cycle detection enabled", "true");
      }
      else
      {
         writeOption("package cycle detection enabled", "false");
      }

      if (configuration.getMaxCompilationUnitCycles() >= -1)
      {
         writeOption("compilation unit cycle detection enabled", "true");
      }
      else
      {
         writeOption("compilation unit cycle detection enabled", "false");
      }

      if (configuration.getMaxTypeCycles() >= -1)
      {
         writeOption("type cycle detection enabled", "true");
      }
      else
      {
         writeOption("type cycle detection enabled", "false");
      }

      int skip = configuration.getSkipPatterns().length;
      writeMetric("number of skip nodes", Integer.toString(skip));

      int ignore = configuration.getIgnore().length;
      writeMetric("number of ignore nodes", Integer.toString(ignore));

      int refactoring = configuration.getRefactorings().length;
      writeMetric("number of refactoring nodes", Integer.toString(refactoring));

      MetricIf[] metrics = project.getMetrics();
      for (int i = 0; i < metrics.length; i++)
      {
         MetricIf nextMetric = metrics[i];
         writeMetric(nextMetric.getName(), nextMetric.getValueAsString());
      }

   }

   private String getTimestamp()
   {
      return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(
         Calendar.getInstance().getTime());
   }

   private void writeMetric(String name, String value) throws SAXException
   {
      startElement(XML_METRIC, createAttributes(XML_ID, buildID(name)));
      // writer.writeAttribute("label", formatLabel(label));
      writeContent(value);
      endElement(XML_METRIC);
   }

   private void writeContent(String value) throws SAXException
   {
      writer.characters(value.toCharArray(), 0, value.length());
   }

   private void endElement(String name) throws SAXException
   {
      writer.endElement(NS, "", name);
   }

   private AttributesImpl createAttributes(String name, String value)
   {
      AttributesImpl attribs = new AttributesImpl();
      attribs.addAttribute(NS, "", name, CDATA, value);
      return attribs;
   }

   private void writeOption(String name, String value) throws SAXException
   {
      startElement(XML_OPTION, createAttributes(XML_ID, buildID(name)));
      writeContent(value);
      endElement(XML_OPTION);
   }

   private String buildID(String label)
   {
      String s[] = label.split(" ");
      String id = "";
      for (int i = 0; i < s.length; i++)
      {
         id += capitalize(s[i]);
      }
      return id;
   }

   private static String capitalize(String s)
   {
      if (s.length() == 0)
      {
         return "";
      }
      else
      {
         return s.substring(0, 1).toUpperCase() + s.substring(1);
      }
   }

   private synchronized void writeCycle(EntityTypeEnum entityType, CycleCollectedEvent cycleEvent)
   {
      DependencyElementIf[] cycleParticipants = cycleEvent.getCycle();
      try
      {
         startElement(XML_CYLE, createAttributes(XML_LEVEL, entityType.name()));
         writeCylceParticpants(cycleParticipants);
         endElement(XML_CYLE);
      }
      catch (SAXException e)
      {
         logger.error(ERROR_WRITE_RESULTS, e);
         close();
      }
   }

   private void writeCylceParticpants(DependencyElementIf[] participants) throws SAXException
   {
      for (DependencyElementIf el : participants)
      {
         startElement(XML_CYLE_PARTICIPANT);
         writeContent(el.getFullyQualifiedName());
         endElement(XML_CYLE_PARTICIPANT);
      }
   }

   public void handleEvent(SingleCompilationUnitTypeCycleCollectedEvent event)
   {
      writeCycle(EntityTypeEnum.TYPE, event);
   }

   public void handleEvent(SinglePackageTypeCycleCollectedEvent event)
   {
      writeCycle(EntityTypeEnum.TYPE, event);
   }

   public void handleEvent(MultiplePackageTypeCycleCollectedEvent event)
   {
      writeCycle(EntityTypeEnum.TYPE, event);
   }

   public void handleEvent(SinglePackageCompilationUnitCycleCollectedEvent event)
   {
      writeCycle(EntityTypeEnum.COMPILATION_UNIT, event);
   }

   public void handleEvent(MultiplePackageCompilationUnitCycleCollectedEvent event)
   {
      writeCycle(EntityTypeEnum.COMPILATION_UNIT, event);
   }

   public void handleEvent(PackageCycleCollectedEvent event)
   {
      writeCycle(EntityTypeEnum.PACKAGE, event);
   }

   public void handleEvent(SubsystemCycleCollectedEvent event)
   {
      writeCycle(EntityTypeEnum.SUBSYSTEM, event);
   }

   public void handleEvent(LayerCycleCollectedEvent event)
   {
      writeCycle(EntityTypeEnum.LAYER, event);
   }

   public void handleEvent(VerticalSliceCycleCollectedEvent event)
   {
      writeCycle(EntityTypeEnum.VERTICAL_SLICE, event);
   }
}