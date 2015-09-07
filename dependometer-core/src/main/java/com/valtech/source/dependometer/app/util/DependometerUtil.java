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
package com.valtech.source.dependometer.app.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

/**
 * @author oliver.rohr
 * 
 */
public class DependometerUtil
{
   private static Logger logger = Logger.getLogger(DependometerUtil.class.getName());

   public static String getVersionInfo(Class caller)
   {
      StringBuffer versionString = new StringBuffer();
      Package pack = caller.getPackage();
      if (pack != null)
      {
         String info = pack.getImplementationTitle();
         if (info != null)
         {
            versionString.append(info);
         }

         info = pack.getImplementationVersion();
         if (info != null)
         {
            if (versionString.length() > 0)
            {
               versionString.append(' ');
            }
            versionString.append(info);
         }

         info = pack.getImplementationVendor();
         if (info != null)
         {
            if (versionString.length() > 0)
            {
               versionString.append(", ");
            }
            versionString.append(info);
         }

      }
      else
      {
         versionString.append("n/a");
      }
      return versionString.toString();
   }

   public static void copyResultTemplateToDir(File targetDir)
   {
      final String resultTemplateLoc = "/result-template";
      final String indexHtml = "index.html";
      final String logo = "dependometer/logo.gif";
      final String css = "dependometer/stylesheet.css";
      final String readme = "/README";

      try
      {
         logger.info("Copy result template from to '" + targetDir.getAbsolutePath() + "'");

         IOUtil.writeResourceToFile(resultTemplateLoc + "/" + indexHtml, new File(targetDir, indexHtml));
         new File(targetDir, "dependometer").mkdir();
         IOUtil.writeResourceToFile(resultTemplateLoc + "/" + logo, new File(targetDir, logo));
         IOUtil.writeResourceToFile(resultTemplateLoc + "/" + css, new File(targetDir, css));

         InputStream readmeIn = DependometerUtil.class.getResourceAsStream(readme);
         if (readmeIn != null)
         {
            IOUtil.copyStreamToFile(readmeIn, new File(targetDir, "dependometer" + readme));
         }
      }
      catch (IOException e)
      {
    	 logger.error(e.getMessage());
         throw new RuntimeException(e);
      }
   }

}
