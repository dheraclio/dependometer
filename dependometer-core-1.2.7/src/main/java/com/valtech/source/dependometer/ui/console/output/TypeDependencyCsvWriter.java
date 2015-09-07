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

import java.io.IOException;
import java.io.PrintWriter;

import com.valtech.source.dependometer.app.controller.project.HandleProjectInfoCollectedEventIf;
import com.valtech.source.dependometer.app.controller.project.ProjectInfoCollectedEvent;
import com.valtech.source.dependometer.app.core.provider.DependencyElementIf;
import com.valtech.source.dependometer.app.core.provider.ProjectIf;
import com.valtech.source.dependometer.ui.console.Dependometer;

/**
 * Dumps all type dependencies into a comma separated file (configurable). Makes some queries easier
 * ("who uses type xyz?").
 * 
 * @author Klaus Mirschenz (klaus.mirschenz@valtech.de)
 */
public final class TypeDependencyCsvWriter extends SingleFileWriter implements HandleProjectInfoCollectedEventIf
{
   private static final String SEPARATOR = ",";

   private static final String TITLE = "From Type" + SEPARATOR + "Relation" + SEPARATOR + "To Type" + SEPARATOR
      + "is forbidden";

   private ProjectIf m_Project;

   private int m_LineCounter = 0;

   public TypeDependencyCsvWriter(String[] arguments) throws IOException
   {
      super(arguments);
      if (arguments.length > 1)
      {
         throw new IllegalArgumentException("may not process more than one argument");
      }

      Dependometer.getContext().getProjectManager().attach(this);
   }

   public void handleEvent(ProjectInfoCollectedEvent event)
   {
      assert event != null;
      m_Project = event.getProject();
      PrintWriter writer = getWriter();

      writer.println("### " + getClass().getName() + " - " + getTimestamp() + " ###");
      writer.println();

      DependencyElementIf[] types = m_Project.getTypes();
      if (types.length > 0)
      {
         writeHeader();

         for (int i = 0; i < types.length; i++)
         {
            if (types[i].belongsToProject())
            {
               DependencyElementIf[] toTypes = types[i].getEfferents();
               for (int j = 0; j < toTypes.length; j++)
               {
                  writer.println(types[i].getFullyQualifiedName() + SEPARATOR
                     + types[i].getEfferentRelationQualifier(toTypes[j]) + SEPARATOR
                     + toTypes[j].getFullyQualifiedName() + SEPARATOR
                     + (types[i].isForbiddenEfferent(toTypes[j]) ? "x" : ""));
                  m_LineCounter++;

               }
            }
         }
         writer.println();
      }

      writer.println("# summary #");
      writer.println();
      writer.println("" + m_LineCounter + " type relation(s) found");
      getLogger().info("writing type dependency csv file ...");
      close();
   }

   private void writeHeader()
   {
      PrintWriter writer = getWriter();
      writer.println("# type dependencies #");
      writer.println();
      writer.print(TITLE);
      writer.println();
   }

}