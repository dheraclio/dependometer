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

import org.apache.log4j.Logger;

import com.valtech.source.dependometer.app.core.provider.ConfigurationProviderIf;
import com.valtech.source.dependometer.app.core.provider.ProviderFactory;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public abstract class SingleDirectoryWriter extends OutputWriter
{
   private final static Logger logger = Logger.getLogger(SingleDirectoryWriter.class.getName());

   private final File targetDirectory;

   protected SingleDirectoryWriter(String[] arguments) throws IOException
   {
      super(arguments);
      if (arguments.length == 0)
      {
         throw new IllegalArgumentException("Need as first argument a valid output directory path");
      }

      ConfigurationProviderIf configurationProvider = ProviderFactory.getInstance().getConfigurationProvider();
      targetDirectory = configurationProvider.resolveRelativePath(arguments[0]);

      if (!targetDirectory.exists())
      {
         targetDirectory.mkdirs();
         logger.info(targetDirectory.getAbsoluteFile() + " did not exist and was created");
      }
      else if (!targetDirectory.isDirectory())
      {
         throw new IllegalArgumentException(targetDirectory.getAbsolutePath() + " is no directory!");
      }
   }

   protected File getTargetDirectory()
   {
      assert targetDirectory != null;
      return targetDirectory;
   }
}