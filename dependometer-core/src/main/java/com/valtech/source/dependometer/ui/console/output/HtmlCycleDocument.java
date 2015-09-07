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

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
class HtmlCycleDocument extends HtmlDocument
{
   private final static int MAX_CYCLES = 100;

   private long m_CurrentNumberOfCycles;

   private final String m_TypeName;

   private int m_CurrentNumberOfFiles;

   private boolean m_CreateNewFile;

   private String createFileName(boolean tangleDoc)
   {
      String fileName = m_TypeName + (tangleDoc ? "-tangles" : "-cycles")
         + (m_CurrentNumberOfFiles == 0 ? "" : Integer.toString(m_CurrentNumberOfFiles)) + HTML_FILE_EXTENSION;
      ++m_CurrentNumberOfFiles;
      return fileName;
   }

   protected HtmlCycleDocument(File directory, String typeName, boolean tangleDoc) throws IOException
   {
      super(directory);
      assert typeName != null;
      assert typeName.length() > 0;
      m_TypeName = typeName;
      open(createFileName(tangleDoc));
      writeTitle();
   }

   private void writeTitle()
   {
      writeAnchoredTitle("cycles", m_TypeName + " cycles");
   }

   final void addCycle(DependencyElementIf[] cycle) throws IOException
   {
      addCycle(cycle, "cycle");
   }

   final synchronized void addCycle(DependencyElementIf[] cycle, String cycleType) throws IOException
   {
      assert AssertionUtility.checkArray(cycle);
      assert cycleType != null;

      ++m_CurrentNumberOfCycles;

      if (m_CreateNewFile)
      {
         String currentName = getFileName();
         String newName = createFileName(false);
         createForwardLink(newName);
         close();
         open(newName);
         writeTitle();
         createBackwardLink(currentName);
         m_CreateNewFile = false;

      }
      else if ((m_CurrentNumberOfCycles) % MAX_CYCLES == 0)
      {
         m_CreateNewFile = true;
         writeAnchor("last");
      }

      PrintWriter writer = getWriter();

      writer.println("<h3> " + cycleType + " (" + m_CurrentNumberOfCycles + ")</h3>");
      writer.println("<table>");
      for (int i = 0; i < cycle.length; i++)
      {
         DependencyElementIf next = cycle[i];
         int usesIndex = i < cycle.length - 1 ? i + 1 : 0;
         DependencyElementIf uses = cycle[usesIndex];
         int typeRelations = next.getNumberOfTypeRelationsForEfferent(uses);
         String bgcolor = COLOR_WHITE;
         if (next.isForbiddenEfferent(uses))
         {
            bgcolor = COLOR_ORANGE;
         }

         writer.println("<tr>");
         writer.println("<td>");
         writer.println("[" + typeRelations + "]");
         writer.println("</td>");
         writer.println("<td bgcolor=\"" + bgcolor + "\">");
         writeHRef(next.hashCode() + ".html#element", next.getFullyQualifiedName());
         writer.println("</td>");
         writer.println("<td bgcolor=\"" + bgcolor + "\">");
         writeHRef(next.hashCode() + ".html#" + "out_" + uses.hashCode(), "[explain]");
         writer.println("</td>");
         writer.println("</tr>");
      }
      writer.println("</table>");
      writer.flush();
   }

   final synchronized void addTangle(DependencyElementIf[] tangle) throws IOException
   {
      PrintWriter writer = getWriter();

      writer.println("<p><h3>");
      writer.print("The following ");
      writer.print(tangle.length);
      writer.print(' ');
      writer.print(tangle[0].getElementName()); // add plural 's'
      writer.print("s depend on each other:</h3>");
      for (int i = 0; i < tangle.length; i++)
      {
         writer.println("<br/>");
         writeHRef(tangle[i].hashCode() + ".html#element", tangle[i].getFullyQualifiedName());
      }
      writer.println("</p>");

      writer.flush();
   }

   private void createForwardLink(String fileName)
   {
      assert fileName != null;
      assert fileName.length() > 0;
      getWriter().println("</br>");
      writeHRef(fileName + "#cycles", "forward &gt;&gt;");
   }

   private void createBackwardLink(String fileName)
   {
      assert fileName != null;
      assert fileName.length() > 0;
      getWriter().println("</br>");
      writeHRef(fileName + "#last", "&lt;&lt; backward");
   }
}