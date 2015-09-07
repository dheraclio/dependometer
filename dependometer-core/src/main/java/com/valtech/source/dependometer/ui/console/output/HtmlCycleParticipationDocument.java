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

import com.valtech.source.ag.util.AssertionUtility;
import com.valtech.source.dependometer.app.core.provider.DependencyElementIf;
import com.valtech.source.dependometer.app.core.provider.DirectedDependencyIf;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
class HtmlCycleParticipationDocument extends HtmlDocument
{
   private final String m_Type;

   private boolean m_FirstCycleParticipation = true;

   protected HtmlCycleParticipationDocument(File directory, String typeName) throws IOException
   {
      super(directory);
      m_Type = typeName;
      open(typeName + "-cycle-participation.html");
      writeTitle();
      writeLinks();
   }

   private void writeTitle()
   {
      writeAnchoredTitle("participation", m_Type + " cycle participation");
   }

   private void writeLinks()
   {
      writeHRef(getFileName() + "#not-completely-analyzed", "[not completely analyzed]");
      writeHRef(getFileName() + "#directly-participating-in-cycles", "[directly participating in cycles]");
      writeHRef(getFileName() + "#cycle-participations", "[cycle-participations]");
   }

   final void setCycleParticipants(DependencyElementIf[] participants, DependencyElementIf[] notCompletelyAnalyzed)
   {
      synchronized (this)
      {
         assert AssertionUtility.checkArray(participants);
         assert AssertionUtility.checkArray(notCompletelyAnalyzed);
         PrintWriter writer = getWriter();
         writer.println("<a name=\"not-completely-analyzed\"/><h3> " + m_Type
            + "s not completely analyzed during cycle analysis (" + notCompletelyAnalyzed.length + ")</h3>");

         writer.println("<table>");
         for (int i = 0; i < notCompletelyAnalyzed.length; i++)
         {
            DependencyElementIf next = notCompletelyAnalyzed[i];
            writer.println("<tr>");
            writer.println("<td>");
            writeHRef(next.hashCode() + ".html#element", next.getFullyQualifiedName());
            writer.println("</td>");
            writer.println("</tr>");
         }
         writer.println("</table>");

         writer.println("<a name=\"directly-participating-in-cycles\"/><h3> " + m_Type
            + "s directly participating in cycles (" + participants.length + ")</h3>");
         writer.println("<table>");
         for (int i = 0; i < participants.length; i++)
         {
            DependencyElementIf next = participants[i];
            writer.println("<tr>");
            writer.println("<td>");
            writeHRef(next.hashCode() + ".html#element", next.getFullyQualifiedName());
            writer.println("</td>");
            writer.println("</tr>");
         }
         writer.println("</table>");
      }
   }

   final void addCycleParticipation(int count, DirectedDependencyIf[] dependencies)
   {
      synchronized (this)
      {
         assert AssertionUtility.checkArray(dependencies);

         PrintWriter writer = getWriter();
         if (m_FirstCycleParticipation)
         {
            writer.println("<a name=\"cycle-participations\"/>");
            m_FirstCycleParticipation = false;
         }

         writer.println("<h3> " + m_Type + " cycle participation (" + count + ")</h3>");
         writer.println("<table>");
         for (int i = 0; i < dependencies.length; i++)
         {
            DirectedDependencyIf nextCycleParticipation = dependencies[i];
            DependencyElementIf from = nextCycleParticipation.getFrom();
            DependencyElementIf to = nextCycleParticipation.getTo();
            int typeRelations = nextCycleParticipation.getNumberOfTypeRelations();
            String bgcolor = COLOR_WHITE;
            if (nextCycleParticipation.isViolation())
            {
               bgcolor = COLOR_ORANGE;
            }
            else
            {
               writer.println("<tr>");
            }
            writer.println("<td>");
            writer.println("[" + typeRelations + "]");
            writer.println("</td>");
            writer.println("<td bgcolor=\"" + bgcolor + "\">");
            writeHRef(from.hashCode() + ".html#element", from.getFullyQualifiedName());
            writer.println("</td>");
            writer.println("<td bgcolor=\"" + bgcolor + "\">");
            writeHRef(from.hashCode() + ".html#" + "out_" + to.hashCode(), "=&gt;");
            writer.println("</td>");
            writer.println("<td bgcolor=\"" + bgcolor + "\">");
            writeHRef(to.hashCode() + ".html#element", to.getFullyQualifiedName());
            writer.println("</td>");
            writer.println("</tr>");
         }
         writer.println("</table>");
         writer.flush();
      }
   }
}
