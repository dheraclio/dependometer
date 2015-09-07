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
import java.io.IOException;

import org.apache.log4j.Logger;

import com.valtech.source.ag.util.PathUtility;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public abstract class Node
{
   protected static final String LINE_SEPARATOR = System.getProperty("line.separator");

   private final static Logger s_Logger = Logger.getLogger(Node.class.getName());

   private static LocationInfoIf s_LocationInfoProvider;

   private static PathUtility s_PathUtility;

   public static void setLocationInfoProvider(LocationInfoIf provider)
   {
      assert s_LocationInfoProvider == null;
      assert provider != null;
      s_LocationInfoProvider = provider;
   }

   protected static LocationInfoIf getLocationInfoProvider()
   {
      assert s_LocationInfoProvider != null;
      return s_LocationInfoProvider;
   }

   public static void setBaseDirectory(File baseDir)
   {
      assert s_PathUtility == null;
      assert baseDir != null;
      assert baseDir.isDirectory();

      s_PathUtility = new PathUtility(baseDir);
   }

   public static File getPath(String path) throws IOException
   {
      assert s_PathUtility != null;
      return s_PathUtility.getPath(path);
   }

   final protected Logger getLogger()
   {
      return s_Logger;
   }

   public static void reset()
   {
      s_LocationInfoProvider = null;
      s_PathUtility = null;
   }
}
