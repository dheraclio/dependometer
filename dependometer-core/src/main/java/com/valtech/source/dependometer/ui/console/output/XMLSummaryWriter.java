//package com.valtech.source.dependometer.ui.console.output;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//
//import org.apache.log4j.Logger;
//
//import com.valtech.source.dependometer.app.controller.main.DependometerContext;
//import com.valtech.source.dependometer.app.controller.project.AnalysisFinishedEvent;
//import com.valtech.source.dependometer.app.controller.project.HandleAnalysisFinishedEventIf;
//import com.valtech.source.dependometer.app.core.provider.ConfigurationProviderIf;
//import com.valtech.source.dependometer.app.core.provider.MetricIf;
//import com.valtech.source.dependometer.app.core.provider.ProjectIf;
//import com.valtech.source.dependometer.app.core.provider.ProviderFactory;
//import com.valtech.source.dependometer.ui.console.Dependometer;
//
//
///**
// * @author oliver.rohr
// *
// *         Output listener for dependometer. Writes summary files including dependometer metrics.
// */
//public class XMLSummaryWriter extends SingleFileWriter implements HandleAnalysisFinishedEventIf
//{
//   private static Logger logger = Logger.getLogger(XMLSummaryWriter.class.getName());
//
//   public XMLSummaryWriter(String[] arguments) throws IOException
//   {
//      super(arguments);
//      DependometerContext context = Dependometer.getContext();
//      context.getProjectManager().attach(this);
//   }
//
//   public void handleEvent(AnalysisFinishedEvent event)
//   {
//      logger.info("generating xml summary file ...");
//
//      ProjectIf project = event.getProject();
//
//      PrintWriter writer = getWriter();
//      writer.println("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
//      writer.println();
//
//      writer.print("<summary");
//      writer.print(" source=\"Dependometer\"");
//      writer.print(" project=\"" + project.getFullyQualifiedName() + "\"");
//      writer.print(" time=\"" + getTimestamp() + "\"");
//      writer.print(">");
//
//      writer.println();
//
//      ConfigurationProviderIf provider = ProviderFactory.getInstance().getConfigurationProvider();
//
//      if (provider.getMaxLayerCycles() >= -1)
//      {
//         writeOption("layer cycle detection enabled", "true");
//      }
//      else
//      {
//         writeOption("layer cycle detection enabled", "false");
//      }
//
//      if (provider.getMaxSubsystemCycles() >= -1)
//      {
//         writeOption("subsystem cycle detection enabled", "true");
//      }
//      else
//      {
//         writeOption("subsystem cycle detection enabled", "false");
//      }
//
//      if (provider.analyzeVerticalSlices())
//      {
//         writeOption("vertical slice analysis enabled", "true");
//
//         if (provider.getMaxVerticalSliceCycles() >= -1)
//         {
//            writeOption("vertical slice cycle detection enabled", "true");
//         }
//         else
//         {
//            writeOption("vertical slice cycle detection enabled", "false");
//         }
//      }
//      else
//      {
//         writeOption("vertical slice analysis enabled", "false");
//      }
//
//      if (provider.getMaxPackageCycles() >= -1)
//      {
//         writeOption("package cycle detection enabled", "true");
//      }
//      else
//      {
//         writeOption("package cycle detection enabled", "false");
//      }
//
//      if (provider.getMaxCompilationUnitCycles() >= -1)
//      {
//         writeOption("compilation unit cycle detection enabled", "true");
//      }
//      else
//      {
//         writeOption("compilation unit cycle detection enabled", "false");
//      }
//
//      if (provider.getMaxTypeCycles() >= -1)
//      {
//         writeOption("type cycle detection enabled", "true");
//      }
//      else
//      {
//         writeOption("type cycle detection enabled", "false");
//      }
//
//      int skip = provider.getSkipPatterns().length;
//      writeMetric("number of skip nodes", Integer.toString(skip));
//
//      int ignore = provider.getIgnore().length;
//      writeMetric("number of ignore nodes", Integer.toString(ignore));
//
//      int refactoring = provider.getRefactorings().length;
//      writeMetric("number of refactoring nodes", Integer.toString(refactoring));
//
//      MetricIf[] metrics = project.getMetrics();
//      for (int i = 0; i < metrics.length; i++)
//      {
//         MetricIf nextMetric = metrics[i];
//         writeMetric(nextMetric.getName(), nextMetric.getValueAsString());
//      }
//
//      writer.print("</summary>");
//      writer.flush();
//   }
//
//
//   private void writeMetric(String label, String value)
//   {
//      PrintWriter writer = getWriter();
//
//      writer.print(indent(1));
//      writer.print("<metric");
//      writer.print(" label=\"" + formatLabel(label) + "\"");
//      writer.print(" id=\"" + buildID(label) + "\"");
//      writer.print(">");
//      writer.print(value);
//      writer.println("</metric>");
//   }
//
//   private void writeOption(String label, String value)
//   {
//      PrintWriter writer = getWriter();
//
//      writer.print(indent(1));
//      writer.print("<option");
//      writer.print(" label=\"" + formatLabel(label) + "\"");
//      writer.print(" id=\"" + buildID(label) + "\"");
//      writer.print(">");
//      writer.print(value);
//      writer.println("</option>");
//   }
//
//   private String formatLabel(String label)
//   {
//      String s[] = label.split(" ");
//      String flabel = "";
//      for (int i = 0; i < s.length; i++)
//      {
//         flabel += capitalize(s[i]) + " ";
//      }
//      flabel.trim();
//      return flabel;
//   }
//
//   private String buildID(String label)
//   {
//      String s[] = label.split(" ");
//      String id = "Dependometer.";
//      for (int i = 0; i < s.length; i++)
//      {
//         id += capitalize(s[i]);
//      }
//      id.trim();
//      return id;
//   }
//
//   private static String capitalize(String s)
//   {
//      if (s.length() == 0)
//      {
//         return "";
//      }
//      else
//      {
//         return s.substring(0, 1).toUpperCase() + s.substring(1);
//      }
//   }
//
//}