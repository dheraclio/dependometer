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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.valtech.source.dependometer.app.core.provider.ConfigurationProviderIf;
import com.valtech.source.dependometer.app.core.provider.ProviderFactory;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public abstract class SingleFileWriter extends OutputWriter
{
   private PrintWriter writer;

   private String filePath;

   protected SingleFileWriter(String[] arguments)
   {
      super(arguments);
      if (arguments.length < 1)
      {
         throw new IllegalArgumentException("Need at least one argument - output file path");
      }
      filePath = arguments[0];
      getWriter();
   }

   protected final PrintWriter getWriter()
   {
      if (writer == null)
      {
         ConfigurationProviderIf configurationProvider = ProviderFactory.getInstance().getConfigurationProvider();
         File file;
         try
         {
            file = configurationProvider.resolveRelativePath(filePath);
            writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
      }
      return writer;
   }

   protected final void close()
   {
      assert writer != null;
      writer.flush();
      writer.close();
      writer = null;
   }
}