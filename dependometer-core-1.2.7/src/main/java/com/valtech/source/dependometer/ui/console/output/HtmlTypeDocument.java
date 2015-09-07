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

import com.valtech.source.dependometer.app.core.provider.DependencyElementIf;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
final class HtmlTypeDocument extends HtmlDependencyElementDocument
{
   protected HtmlTypeDocument(File directory, DependencyElementIf element, String metricDescriptionsDocumentName)
   {
      super(directory, element, metricDescriptionsDocumentName);
   }

   protected boolean showRelationQualifier()
   {
      return true;
   }

   protected String getContainsTypeName()
   {
      return null;
   }

   protected boolean showTypeRelations()
   {
      return false;
   }

   protected boolean showRelationQualifierForContained()
   {
      return false;
   }

   protected boolean showAllowedDependencies()
   {
      return false;
   }

   protected boolean showTypeRelationsInInnerDependencies()
   {
      return false;
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