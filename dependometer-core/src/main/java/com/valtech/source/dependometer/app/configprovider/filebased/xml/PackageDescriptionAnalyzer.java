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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.valtech.source.ag.util.AssertionUtility;
import com.valtech.source.ag.util.DirectoryScanner;
import com.valtech.source.dependometer.app.core.provider.PackageFilterIf;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public final class PackageDescriptionAnalyzer
{
   private final static Logger s_Logger = Logger.getLogger(PackageDescriptionAnalyzer.class.getName());

   private static final char DOT_CHAR = '.';

   private final List<String> m_AdditionalInfo = new ArrayList<String>();

   private PackageFilterIf m_PackageFilter;

   private Map<String, String[]> m_PackageDependsUpon = new HashMap<String, String[]>();

   private Map<String, String> m_PackageToDescription = new HashMap<String, String>();

   private int m_IgnoredPackageDescriptions;

   private File[] sourceRootDirectories;

   public void setPackageFilter(PackageFilterIf filter)
   {
      assert filter != null;
      m_PackageFilter = filter;
   }

   public String[] getPackages()
   {
      return m_PackageDependsUpon.keySet().toArray(new String[0]);
   }

   public String[] packageDependsUpon(String packageName)
   {
      assert packageName != null;
      assert packageName.length() > 0;
      return m_PackageDependsUpon.get(packageName);
   }

   public String getPackageDescription(String packageName)
   {
      assert packageName != null;
      assert packageName.length() > 0;
      return m_PackageToDescription.get(packageName);
   }

   public void setInputDirectories(File[] directories)
   {
      assert AssertionUtility.checkArray(directories);
      sourceRootDirectories = Arrays.copyOf( directories, directories.length );
   }

   public void analyzePackageDescriptions()
   {
      DirectoryScanner sourceDirectoryScanner = new DirectoryScanner(new PackageDescriptionFilter(m_PackageFilter),
         true);
      s_Logger.info("searching package descriptions ...");
      for (int i = 0; i < sourceRootDirectories.length; ++i)
      {
         File nextDir = sourceRootDirectories[i];
         m_AdditionalInfo.add("searching package descriptions in = " + nextDir.getAbsolutePath());
         sourceDirectoryScanner.scan(nextDir);
      }

      List<String> relPaths = sourceDirectoryScanner.getRelPathsAsString();
      List<File> descriptions = sourceDirectoryScanner.getFiles();

      Map<String, PackageDescriptionReader> packageDescriptionReaders = new HashMap<String, PackageDescriptionReader>();

       // TODO loops over two Collection only synced by convention
      for ( int i = 0; i < relPaths.size(); i++  )
      {
         String nextPackageName = relPaths.get(i).replace(File.separatorChar, DOT_CHAR);
         File description = descriptions.get(i);

         PackageDescriptionReader alreadyContainedDescription = packageDescriptionReaders.get(nextPackageName);
         if (alreadyContainedDescription != null)
         {
            String message = "package description '" + descriptions.get(i) + "' already added from path '"
               + alreadyContainedDescription.getPackageDescription() + "' - ignoring";
            s_Logger.warn(message);
            m_AdditionalInfo.add(message);
            m_IgnoredPackageDescriptions++;
         }
         else
         {
            PackageDescriptionReader reader = new PackageDescriptionReader(description);
            packageDescriptionReaders.put(nextPackageName, reader);

            if (!reader.getPackageName().equals(nextPackageName))
            {
               packageDescriptionReaders.remove(nextPackageName);
               String message = "package description '" + reader.getPackageDescription()
                  + "' in wrong directory - ignoring";
               s_Logger.warn(message);
               m_AdditionalInfo.add(message);
            }
            else
            {
               String[] dependsUpon = reader.dependsUponPackages();
               m_PackageDependsUpon.put(nextPackageName, dependsUpon);
               String packageDescription = reader.getDescription();
               if (packageDescription != null)
               {
                  assert packageDescription.length() > 0;
                  m_PackageToDescription.put(nextPackageName, packageDescription);
               }
            }
         }
      }

      m_AdditionalInfo.add("package descriptions (found/ignored duplicates) = " + m_PackageToDescription.size() + "/"
         + m_IgnoredPackageDescriptions);
   }

   public String[] getAdditionalInfo()
   {
      return m_AdditionalInfo.toArray(new String[0]);
   }
}