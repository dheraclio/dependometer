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
final class HtmlUnusedDependenciesDocument extends HtmlDocument
{
   private final String type;

   private final DirectedDependencyIf[] unused;

   protected HtmlUnusedDependenciesDocument(File directory, String typeName, DirectedDependencyIf[] unused)
      throws IOException
   {
      super(directory);
      assert AssertionUtility.checkArray(unused);
      type = typeName;
      this.unused = Arrays.copyOf( unused, unused.length );
      open(typeName + "-unused-dependencies.html");
      writeTitle();
      writeUnusedDependencies();
      close();
   }

   private void writeTitle()
   {
      writeAnchoredTitle("unused", "unused defined " + type + " dependencies");
   }

   private void writeUnusedDependencies()
   {
      PrintWriter writer = getWriter();
      writer.println("<table>");
      for (int i = 0; i < unused.length; i++)
      {
         DependencyElementIf from = unused[i].getFrom();
         DependencyElementIf to = unused[i].getTo();
         writer.println("<tr>");
         writer.println("<td>");
         writeHRef(from.hashCode() + ".html#element", from.getFullyQualifiedName());
         writer.println("</td>");
         writer.println("<td>");
         writer.println("=&gt;");
         writer.println("</td>");
         writer.println("<td>");
         writeHRef(to.hashCode() + ".html#element", to.getFullyQualifiedName());
         writer.println("</td>");
         writer.println("</tr>");
      }
      writer.println("</table>");
   }
}
