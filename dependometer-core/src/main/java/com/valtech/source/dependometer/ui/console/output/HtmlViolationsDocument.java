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
import java.util.Arrays;

import com.valtech.source.ag.util.AssertionUtility;
import com.valtech.source.dependometer.app.core.provider.DependencyElementIf;
import com.valtech.source.dependometer.app.core.provider.DirectedDependencyIf;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
final class HtmlViolationsDocument extends HtmlDocument
{
   private final String type;

   private final DirectedDependencyIf[] forbidden;

   protected HtmlViolationsDocument(File directory, String typeName, DirectedDependencyIf[] forbidden)
      throws IOException
   {
      super(directory);
      assert typeName != null;
      assert typeName.length() > 0;
      assert AssertionUtility.checkArray(forbidden);
      type = typeName;
      this.forbidden = Arrays.copyOf( forbidden, forbidden.length );
      open(typeName + "-violations.html");
      writeTitle();
      writeViolations();
      close();
   }

   private void writeTitle()
   {
      writeAnchoredTitle("violations", type + " violations");
   }

   private void writeViolations()
   {
      PrintWriter writer = getWriter();
      writer.println("<table>");
      for (int i = 0; i < forbidden.length; i++)
      {
         DirectedDependencyIf nextViolation = forbidden[i];
         DependencyElementIf from = nextViolation.getFrom();
         DependencyElementIf to = nextViolation.getTo();
         int typeRelations = nextViolation.getNumberOfTypeRelations();

         writer.println("<tr>");
         writer.println("<td>");
         writer.println("[" + typeRelations + "]");
         writer.println("</td>");
         writer.println("<td>");
         writeHRef(from.hashCode() + ".html#element", from.getFullyQualifiedName());
         writer.println("</td>");
         writer.println("<td>");
         writeHRef(from.hashCode() + ".html#" + "out_" + to.hashCode(), "=&gt;");
         writer.println("</td>");
         writer.println("<td>");
         writeHRef(to.hashCode() + ".html#element", to.getFullyQualifiedName());
         if (!to.belongsToProject())
         {
            writer.println("&lt;external&gt;");
         }
         writer.println("</td>");
         writer.println("</tr>");
      }
      writer.println("</table>");
   }
}