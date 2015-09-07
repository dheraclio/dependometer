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

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
final class HtmlIndexDocument extends HtmlDocument
{
   private final DependencyElementIf[] elements;

   private final String typeName;

   protected HtmlIndexDocument(File directory, String typeName, DependencyElementIf[] elements) throws IOException
   {
      super(directory);
      assert AssertionUtility.checkArray(elements);
      this.elements = Arrays.copyOf( elements, elements.length );
      this.typeName = typeName;
      open("index-" + typeName + "s" + HTML_FILE_EXTENSION);
      writeTitle( this.typeName );
      writeIndex();
      close();
   }

   private void writeTitle(String type)
   {
      assert type != null;
      assert type.length() > 0;
      writeAnchoredTitle("all", "all " + type + "s");
   }

   private void writeIndex()
   {
      PrintWriter writer = getWriter();
      writer.println("<table>");

      for (int i = 0; i < elements.length; i++)
      {
         DependencyElementIf next = elements[i];
         writer.println("<tr>");
         HtmlDependencyElementDocument.writeCharacteristics(writer, next);
         writer.println("<td>");
         writeHRef(next.hashCode() + ".html#element", next.getFullyQualifiedName());
         writer.println("</td>");
         writer.println("<td>");
         if (next.hasDescription())
         {
            writer.println("\"" + next.getDescription() + "\"");
         }
         writer.println("</td>");
         writer.println("</tr>");
      }

      writer.println("</table>");
   }
}
