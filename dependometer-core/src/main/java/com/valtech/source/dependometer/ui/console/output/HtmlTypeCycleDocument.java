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

import com.valtech.source.dependometer.app.core.provider.DependencyElementIf;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
final class HtmlTypeCycleDocument extends HtmlCycleDocument
{
   protected HtmlTypeCycleDocument(File directory, String typeName, boolean tangleDoc) throws IOException
   {
      super(directory, typeName, tangleDoc);
   }

   void addSingleCompilationUnitCyle(DependencyElementIf[] cycle) throws IOException
   {
      addCycle(cycle, "single compilation unit cycle");
   }

   void addSinglePackageCyle(DependencyElementIf[] cycle) throws IOException
   {
      addCycle(cycle, "single package cycle");
   }

   void addMultiplePackageCyle(DependencyElementIf[] cycle) throws IOException
   {
      addCycle(cycle, "multiple package cycle");
   }
}
