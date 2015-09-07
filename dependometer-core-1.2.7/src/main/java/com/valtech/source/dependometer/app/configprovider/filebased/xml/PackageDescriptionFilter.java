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
package com.valtech.source.dependometer.app.configprovider.filebased.xml;

import java.io.File;

import com.valtech.source.ag.util.BasicDirectoryScanner.FilterIf;
import com.valtech.source.dependometer.app.core.provider.PackageFilterIf;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
final class PackageDescriptionFilter implements FilterIf
{
   private static final char DOT_CHAR = '.';

   private static final String PACKAGE_DESCRIPTION_FILE_NAME = "PackageDescription.xml";

   private PackageFilterIf m_PackageFilter;

   PackageDescriptionFilter(PackageFilterIf packageFilter)
   {
      assert packageFilter != null;
      m_PackageFilter = packageFilter;
   }

   public boolean acceptFile(File file, String relPath)
   {
      assert file != null;
      assert !file.isDirectory();
      assert relPath != null;

      if (file.getName().equals(PACKAGE_DESCRIPTION_FILE_NAME)
         && m_PackageFilter.match(relPath.replace(File.separatorChar, DOT_CHAR)))
      {
         return true;
      }

      return false;
   }
}