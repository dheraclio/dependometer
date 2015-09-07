/*
 * Copyright 2009 Valtech GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.valtech.source.dependometer.ui.console.output;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.valtech.source.ag.util.AssertionUtility;
import com.valtech.source.dependometer.app.core.provider.DependencyElementIf;
import com.valtech.source.dependometer.app.core.provider.MetricIf;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
abstract class HtmlDependencyElementDocument extends HtmlDocument
{
   private final static int AFFERENT = 0;

   private final static int EFFERENT = 1;

   private final DependencyElementIf m_DependencyElement;

   private final DependencyElementIf m_BelongsTo;

   private final String m_TitleRemark;

   private final String m_MetricDescriptionsDocumentName;

   protected HtmlDependencyElementDocument(File directory, DependencyElementIf element,
      String metricDescriptionsDocumentName)
   {
      super(directory);
      assert element != null;
      assert metricDescriptionsDocumentName != null;
      assert metricDescriptionsDocumentName.length() > 0;

      m_DependencyElement = element;
      m_BelongsTo = m_DependencyElement.belongsToDependencyElement();
      m_MetricDescriptionsDocumentName = metricDescriptionsDocumentName;
      String[] characteristics = getCharacteristics(element);
      StringBuffer titleRemark = new StringBuffer();
      for (int i = 0; i < characteristics.length; i++)
      {
         titleRemark.append(characteristics[i]);
         titleRemark.append(' ');
      }

      m_TitleRemark = titleRemark.toString();
   }

   public final void write() throws IOException
   {
      open(m_DependencyElement.hashCode() + HTML_FILE_EXTENSION);

      writeTitle();
      writeLinks();
      writeDescription();
      writeContains();
      writeInnerDependencies();
      writeIncomingDependencies();
      writeOutgoingDependencies();
      writeAllowedDependencies();
      writeAdditionalSections();
      writeMetrics();

      close();
   }

   protected void writeAdditionalSections()
   {
      // May be overwritten
   }

   private void writeInnerDependencies()
   {
      String containsElementName = m_DependencyElement.getContainedElementName();
      if (m_DependencyElement.belongsToProject() && containsElementName != null)
      {
         DependencyElementIf[] contains = m_DependencyElement.containsDependencyElements();
         PrintWriter writer = getWriter();
         writer.println("<a name=\"inner_dependencies\"/><h3>inner " + m_DependencyElement.getElementName()
            + " dependencies</h3>");

         writer.println("<table>");
         for (int i = 0; i < contains.length; i++)
         {
            DependencyElementIf next = contains[i];
            DependencyElementIf[] nextEffs = next.getEfferents();
            int innerEfferents = 0;

            for (int j = 0; j < nextEffs.length; j++)
            {
               DependencyElementIf nextEfferent = nextEffs[j];
               if (m_DependencyElement.contains(nextEfferent))
               {
                  String bgcolor = COLOR_WHITE;
                  if (next.isForbiddenEfferent(nextEfferent))
                  {
                     bgcolor = COLOR_ORANGE;
                  }
                  writer.println("<tr>");
                  writer.println("<td bgcolor=\"" + bgcolor + "\">");
                  writer.print("&lt;" + containsElementName + "&gt;");
                  if (showRelationQualifierInInnerDependencies())
                  {
                     String qualifier = next.getEfferentRelationQualifier(nextEfferent);
                     getWriter().print(" (" + qualifier + ") ");
                  }
                  if (showTypeRelationsInInnerDependencies())
                  {
                     int relations = next.getNumberOfTypeRelationsForEfferent(nextEfferent);
                     writer.print(" [" + relations + "]");
                  }
                  writer.println("</td>");
                  writer.println("<td bgcolor=\"" + bgcolor + "\">");
                  writeHRef(next.hashCode() + ".html#element", getContainedElementName(next));
                  writer.println("</td>");
                  writer.println("<td bgcolor=\"" + bgcolor + "\">");
                  writeHRef(next.hashCode() + ".html#out_" + nextEfferent.hashCode(), "=&gt;");
                  writer.println("</td>");
                  writer.println("<td bgcolor=\"" + bgcolor + "\">");
                  writeHRef(nextEfferent.hashCode() + ".html#element", getContainedElementName(nextEfferent));
                  writer.println("</td>");
                  writer.println("</tr>");
                  ++innerEfferents;
               }
            }
            if (innerEfferents == 0)
            {
               writer.println("<tr>");
               writer.println("<td>");
               writer.print("&lt;" + containsElementName + "&gt;");
               if (showTypeRelationsInInnerDependencies())
               {
                  writer.println(" [0]");
               }
               writer.println("</td>");
               writer.println("<td>");
               writeHRef(next.hashCode() + ".html#element", getContainedElementName(next));
               writer.println("</td>");
               writer.println("<td>");
               writer.println("</td>");
               writer.println("<td>");
               writer.println("</td>");
               writer.println("</tr>");
            }
         }
         writer.println("</table>");
      }
   }

   protected final DependencyElementIf getDependencyElement()
   {
      return m_DependencyElement;
   }

   private void writeDescription()
   {
      if (m_DependencyElement.hasDescription())
      {
         PrintWriter writer = getWriter();
         String description = m_DependencyElement.getDescription();
         writer.println("<a name=\"description\"/><h3>description</h3>");
         writer.println("<table>");
         writer.println("<tr><td>");
         writer.println(description);
         writer.println("</td></tr>");
         writer.println("</table>");
      }
   }

   private void writeAllowedDependencies()
   {
      if (showAllowedDependencies() && m_DependencyElement.belongsToProject())
      {
         PrintWriter writer = getWriter();

         DependencyElementIf[] used = m_DependencyElement.getUsedAllowedEfferents();
         writer.println("<a name=\"used-allowed\"/><h3>used defined " + m_DependencyElement.getElementName()
            + " dependencies (" + used.length + ")</h3>");
         writer.println("<table>");
         for (int i = 0; i < used.length; i++)
         {
            writer.println("<tr><td>");
            writeHRef(used[i].hashCode() + ".html#element", used[i].getFullyQualifiedName());
            writer.println("</td></tr>");
         }
         writer.println("</table>");

         DependencyElementIf[] unused = m_DependencyElement.getUnusedAllowedEfferents();
         writer.println("<a name=\"unused-allowed\"/><h3>unused defined " + m_DependencyElement.getElementName()
            + " dependencies (" + unused.length + ")</h3>");
         writer.println("<table>");
         for (int i = 0; i < unused.length; i++)
         {
            writer.println("<tr><td>");
            writeHRef(unused[i].hashCode() + ".html#element", unused[i].getFullyQualifiedName());
            writer.println("</td></tr>");
         }
         writer.println("</table>");
      }
   }

   private void writeMetrics()
   {
      MetricIf[] metrics = m_DependencyElement.getMetrics();
      PrintWriter writer = getWriter();
      writer.print("<a name=\"metrics\"/><h3>");
      writer.print(m_DependencyElement.getElementName());
      writer.print(" metrics for &lt;&lt;");
      writer.print(m_DependencyElement.getFullyQualifiedName());
      writer.println("&gt;&gt;</h3>");

      writer.println("<table>");
      for (int i = 0; i < metrics.length; i++)
      {
         MetricIf nextMetric = metrics[i];
         writer.println("<tr>");
         writer.println("<td>");
         writer.println(nextMetric.getName());
         writer.println("</td>");
         writer.println("<td>");
         writer.println("=");
         writer.println("</td>");
         writer.println("<td>");
         writer.println(nextMetric.getValueAsString());
         writer.println("</td>");
         writer.println("<td>");
         writeHRef(m_MetricDescriptionsDocumentName + "#" + nextMetric.getName(), "[description]");
         writer.println("</td>");
         writer.println("</tr>");
      }
      writer.println("</table>");
   }

   private void writeIncomingDependencies()
   {
      DependencyElementIf[] afferents = m_DependencyElement.getAfferents();
      getWriter().println(
         "<a name=\"incoming_dependencies\"/><h3>afferent (incoming) " + m_DependencyElement.getElementName()
            + " dependencies (" + afferents.length + ")</h3>");
      writeDependencies(afferents, AFFERENT);
   }

   protected String getContainedElementName(DependencyElementIf contained)
   {
      assert contained != null;
      return contained.getName();
   }

   private void writeContains()
   {
      String containsElementName = m_DependencyElement.getContainedElementName();
      if (containsElementName != null)
      {
         DependencyElementIf[] contains = m_DependencyElement.containsDependencyElements();
         PrintWriter writer = getWriter();
         writer.println("<a name=\"contains\"/><h3> contained " + containsElementName + "s (" + contains.length
            + ")</h3>");

         writer.println("<table>");
         for (int i = 0; i < contains.length; i++)
         {
            DependencyElementIf next = contains[i];
            writer.println("<tr>");
            writeCharacteristics(writer, next);
            writer.println("<td>");
            writeHRef(next.hashCode() + ".html#element", getContainedElementName(next));
            writer.println("</td>");
            writer.println("</tr>");
         }
         writer.println("</table>");
      }
   }

   private void writeOutgoingDependencies()
   {
      if (m_DependencyElement.belongsToProject())
      {
         DependencyElementIf[] efferents = m_DependencyElement.getEfferents();
         getWriter().println(
            "<a name=\"outgoing_dependencies\"/><h3>efferent (outgoing) " + m_DependencyElement.getElementName()
               + " dependencies (" + efferents.length + ")</h3>");
         writeDependencies(efferents, EFFERENT);
      }
   }

   private void writeDependencies(DependencyElementIf[] elements, int relation)
   {
      assert AssertionUtility.checkArray(elements);

      String relationSymbol = null;
      String relationLinkPrefix = null;
      boolean checkForbiddenEfferent = false;
      boolean writeNotAnalyzed = false;
      boolean writeDependsUpon = false;

      switch (relation)
      {
         case AFFERENT:
            relationSymbol = "&lt=";
            relationLinkPrefix = "in_";
            break;
         case EFFERENT:
            relationSymbol = "=&gt";
            relationLinkPrefix = "out_";
            checkForbiddenEfferent = true;
            writeNotAnalyzed = true;
            writeDependsUpon = showComponentDependency();
            break;
         default:
            assert false;
            break;
      }

      PrintWriter writer = getWriter();
      writer.println("<table>");

      for (int i = 0; i < elements.length; i++)
      {
         String bgcolor = COLOR_WHITE;

         DependencyElementIf related = elements[i];
         DependencyElementIf relatedBelongsTo = related.belongsToDependencyElement();

         if (checkForbiddenEfferent)
         {
            if (m_DependencyElement.isForbiddenEfferent(related))
            {
               bgcolor = COLOR_ORANGE;
            }
         }
         else
         {
            if (related.isForbiddenEfferent(m_DependencyElement))
            {
               bgcolor = COLOR_ORANGE;
            }
         }

         writer.println("<tr>");
         writer.println("<td><i>");
         writeAnchor(relationLinkPrefix + related.hashCode());
         writer.print("&lt;" + m_DependencyElement.getElementName() + "&gt ");
         writeTypeRelations(related, relation);
         writeRelationQualifier(related, relation);
         if (writeDependsUpon)
         {
            writeDependsUpon(related);
         }
         writer.println("</i></td>");

         writer.println("<td bgcolor=\"" + bgcolor + "\"><i>");
         writeHRef(m_DependencyElement.hashCode() + ".html#element", m_DependencyElement.getFullyQualifiedName(),
            COLOR_PURPLE);
         writer.println("</i></td>");

         writer.println("<td bgcolor=\"" + bgcolor + "\">");
         if (m_BelongsTo != null && relatedBelongsTo != null && (m_BelongsTo != relatedBelongsTo))
         {
            writeHRef(m_BelongsTo.hashCode() + ".html#" + relationLinkPrefix + relatedBelongsTo.hashCode(),
               relationSymbol);
         }
         else
         {
            writer.println(relationSymbol);
         }

         writer.println("</td>");

         writer.print("<td bgcolor=\"" + bgcolor + "\"><i>");
         writeHRef(related.hashCode() + ".html#element", related.getFullyQualifiedName(), COLOR_PURPLE);
         if (writeNotAnalyzed)
         {
            writer.println(!related.belongsToProject() ? "&lt;external&gt;" : "");
         }
         writer.println("</i></td>");
         writer.println("</tr>");

         String containsElementName = m_DependencyElement.getContainedElementName();
         if (containsElementName != null)
         {
            writeCouplings(related, relation, containsElementName);
         }
      }

      writer.println("</table>");
   }

   private void writeDependsUpon(DependencyElementIf related)
   {
      assert related != null;
      if (related.wasDependsUponSet())
      {
         getWriter().print("(" + related.getDependsUpon() + ") ");
      }
   }

   private void writeTypeRelations(DependencyElementIf related, int relation)
   {
      assert related != null;
      assert relation == AFFERENT || relation == EFFERENT;

      if (showTypeRelations())
      {
         int numberOfTypeRelations = 0;

         switch (relation)
         {
            case AFFERENT:
               numberOfTypeRelations = m_DependencyElement.getNumberOfTypeRelationsForAfferent(related);
               break;
            case EFFERENT:
               numberOfTypeRelations = m_DependencyElement.getNumberOfTypeRelationsForEfferent(related);
               break;
            default:
               assert false;
               break;
         }

         getWriter().print("[" + numberOfTypeRelations + "] ");
      }
   }

   private void writeRelationQualifier(DependencyElementIf related, int relation)
   {
      assert related != null;
      assert relation == AFFERENT || relation == EFFERENT;

      if (showRelationQualifier())
      {
         String qualifier = null;

         switch (relation)
         {
            case AFFERENT:
               qualifier = m_DependencyElement.getAfferentRelationQualifier(related);
               break;
            case EFFERENT:
               qualifier = m_DependencyElement.getEfferentRelationQualifier(related);
               break;
            default:
               assert false;
               break;
         }

         getWriter().println("(" + qualifier + ") ");
      }
   }

   private void writeCouplings(DependencyElementIf related, int relation, String containedElementName)
   {
      assert related != null;
      assert containedElementName != null;
      PrintWriter writer = getWriter();

      DependencyElementIf.CouplingIf[] couplings = null;

      String relationSymbol = null;
      String relationLinkPrefix = null;

      switch (relation)
      {
         case AFFERENT:
            relationSymbol = "&lt=";
            relationLinkPrefix = "in_";
            couplings = m_DependencyElement.getAfferentCouplings(related);
            break;
         case EFFERENT:
            relationSymbol = "=&gt";
            relationLinkPrefix = "out_";
            couplings = m_DependencyElement.getEfferentCouplings(related);
            break;
         default:
            assert false;
            break;
      }

      for (int i = 0; i < couplings.length; i++)
      {
         DependencyElementIf.CouplingIf next = couplings[i];
         DependencyElementIf contained = next.getContained();
         DependencyElementIf[] notContained = next.getNotContainedRelatedElements();
         for (int j = 0; j < notContained.length; j++)
         {
            DependencyElementIf nextNotContained = notContained[j];

            writer.println("<tr>");
            writer.println("<td>");
            writer.println("&lt;" + containedElementName + "&gt ");

            if (showRelationQualifierForContained())
            {
               writer.println("(" + next.getRelationQualifier(nextNotContained) + ") ");
            }

            writer.println("</td>");
            writer.println("<td>");
            writeHRef(contained.hashCode() + ".html", getContainedElementName(contained));
            writer.println("</td>");
            writer.println("<td>");
            writeHRef(contained.hashCode() + ".html#" + relationLinkPrefix + nextNotContained.hashCode(),
               relationSymbol);
            writer.println("</td>");
            writer.println("<td>");
            writeHRef(nextNotContained.hashCode() + ".html", getContainedElementName(nextNotContained));
            writer.println("</td>");
            writer.println("</tr>");
         }
      }
   }

   private void writeTitle()
   {
      writeAnchoredTitle("element", m_DependencyElement.getElementName() + " &lt;&lt;"
         + m_DependencyElement.getFullyQualifiedName() + "&gt;&gt; " + m_TitleRemark);
   }

   private void writeLinks()
   {
      DependencyElementIf[] container = m_DependencyElement.containingDependencyElements();
      PrintWriter writer = getWriter();
      writer.println("<table>");

      for (int i = 0; i < container.length; i++)
      {
         DependencyElementIf next = container[i];
         writer.println("<tr><td>");
         writeHRef(next.hashCode() + ".html#element", "[to " + next.getElementName() + " "
            + next.getFullyQualifiedContainmentName() + "]");
         writer.println("</td></tr>");
      }

      String containsElementName = m_DependencyElement.getContainedElementName();

      if (containsElementName != null)
      {
         writer.println("<tr><td>");
         writeHRef(m_DependencyElement.hashCode() + ".html#contains", "[contained " + containsElementName + "s]");
         writer.println("</td></tr>");
      }

      if (m_DependencyElement.belongsToProject() && containsElementName != null)
      {
         writer.println("<tr><td>");
         writeHRef(m_DependencyElement.hashCode() + ".html#inner_dependencies", "[inner dependencies]");
         writer.println("</td></tr>");
      }

      writer.println("<tr><td>");
      writeHRef(m_DependencyElement.hashCode() + ".html#incoming_dependencies", "[afferent (incoming) dependencies]");
      writer.println("</td></tr>");

      if (m_DependencyElement.belongsToProject())
      {
         writer.println("<tr><td>");
         writeHRef(m_DependencyElement.hashCode() + ".html#outgoing_dependencies", "[efferent (outgoing) dependencies]");
         writer.println("</td></tr>");
      }

      if (showAllowedDependencies() && m_DependencyElement.belongsToProject())
      {
         writer.println("<tr><td>");
         writeHRef(m_DependencyElement.hashCode() + ".html#used-allowed", "[defined dependencies]");
         writer.println("</td></tr>");
      }

      writeAdditionalLinks();
      writer.println("<tr><td>");
      writeHRef(m_DependencyElement.hashCode() + ".html#metrics", "[metrics]");
      writer.println("</td></tr>");

      writer.println("</table>");
   }

   protected void writeAdditionalLinks()
   {
      // May be overwritten
   }

   static void writeCharacteristics(PrintWriter writer, DependencyElementIf element)
   {
      assert writer != null;

      String[] characteristics = getCharacteristics(element);
      for (int i = 0; i < characteristics.length; i++)
      {
         writer.println("<td>");
         writer.println(characteristics[i]);
         writer.println("</td>");
      }
   }

   private static String[] getCharacteristics(DependencyElementIf element)
   {
      assert element != null;
      List<String> characteristics = new ArrayList<String>(4);

      if (element.belongsToProject())
      {
         characteristics.add("&lt;internal&gt;");

         if (element.hasProjectInternalTypes())
         {
            characteristics.add(element.hasAccessibleTypes() ? "&lt;accessible&gt;" : "&lt;unaccessible&gt;");
            if (element.hasConcreteTypes())
            {
               characteristics.add("&lt;concrete&gt;");
            }
            else
            {
               characteristics.add("&lt;abstract&gt;");
            }
         }
         else
         {
            characteristics.add("&lt;not-implemented&gt;");
            characteristics.add("");
         }
      }
      else
      {
         characteristics.add("&lt;external&gt;");
         if (element.hasAccessibleTypes())
         {
            characteristics.add("&lt;accessible&gt;");
            characteristics.add("");
         }
         else
         {
            characteristics.add("&lt;not-implemented&gt;");
            characteristics.add("");
         }
      }

      if (element.wasRefactored())
      {
         characteristics.add("&lt;refactored&gt;");
      }
      else
      {
         characteristics.add("");
      }

      return characteristics.toArray(new String[0]);
   }

   protected abstract boolean showAllowedDependencies();

   protected abstract boolean showRelationQualifier();

   protected abstract boolean showRelationQualifierForContained();

   protected abstract boolean showTypeRelations();

   protected abstract boolean showComponentDependency();

   protected abstract boolean showTypeRelationsInInnerDependencies();

   protected abstract boolean showRelationQualifierInInnerDependencies();
}