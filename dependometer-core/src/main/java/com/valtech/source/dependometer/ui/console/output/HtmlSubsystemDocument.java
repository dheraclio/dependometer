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
import java.io.PrintWriter;

import com.valtech.source.dependometer.app.core.provider.DependencyElementIf;
import com.valtech.source.dependometer.app.core.provider.PackageFilterIf;
import com.valtech.source.dependometer.app.core.provider.ProjectIf;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
final class HtmlSubsystemDocument extends HtmlDependencyElementDocument
{
   private final ProjectIf m_Project;

   protected HtmlSubsystemDocument(File directory, DependencyElementIf element, ProjectIf project,
      String metricDescriptionsDocumentName)
   {
      super(directory, element, metricDescriptionsDocumentName);
      assert project != null;
      m_Project = project;
   }

   protected boolean showRelationQualifier()
   {
      return false;
   }

   protected boolean showTypeRelations()
   {
      return true;
   }

   protected boolean showRelationQualifierForContained()
   {
      return false;
   }

   protected boolean showAllowedDependencies()
   {
      return true;
   }

   protected void writeAdditionalLinks()
   {
      PrintWriter writer = getWriter();
      writer.println("<tr><td>");
      writeHRef(getFileName() + "#filter", "[package-filter]");
      writer.println("</td></tr>");
   }

   protected void writeAdditionalSections()
   {
      super.writeAdditionalSections();
      PrintWriter writer = getWriter();
      PackageFilterIf filter = m_Project.getSubsystemPackageFilter(getDependencyElement());
      writer.println("<a name=\"filter\"/><h3>package filter</h3>");
      writer.println("<table>");
      String[] included = filter.getIncludePatterns();
      for (int i = 0; i < included.length; i++)
      {
         writer.println("<tr><td>");
         writer.println("include package = " + included[i]);
         writer.println("</td></tr>");
      }
      String[] excluded = filter.getExcludePatterns();
      for (int i = 0; i < excluded.length; i++)
      {
         writer.println("<tr><td>");
         writer.println("exclude package = " + excluded[i]);
         writer.println("</td></tr>");
      }
      writer.println("</table>");
   }

   protected boolean showTypeRelationsInInnerDependencies()
   {
      return true;
   }

   protected boolean showComponentDependency()
   {
      return false;
   }

   protected boolean showRelationQualifierInInnerDependencies()
   {
      return false;
   }
}