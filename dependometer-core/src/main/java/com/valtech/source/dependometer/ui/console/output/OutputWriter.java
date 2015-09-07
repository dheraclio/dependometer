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

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;

import org.apache.log4j.Logger;

import com.valtech.source.ag.util.AssertionUtility;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
abstract class OutputWriter
{
   private final static Logger s_Logger = Logger.getLogger(OutputWriter.class.getName());

   private final static String s_TimeStamp = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM)
      .format(Calendar.getInstance().getTime());

   protected final static String TAB = "    ";

   private final String[] arguments;

   protected String indent(int indent)
   {
      assert indent >= 0;

      StringBuffer indention = new StringBuffer();

      for (int i = 0; i < indent; ++i)
      {
         indention.append(TAB);
      }

      return indention.toString();
   }

   protected static Logger getLogger()
   {
      return s_Logger;
   }

   protected OutputWriter(String[] arguments)
   {
      assert AssertionUtility.checkArray(arguments);
      this.arguments = Arrays.copyOf( arguments, arguments.length );
   }

   protected final String getTimestamp()
   {
      return s_TimeStamp;
   }

   protected final String[] getArguments()
   {
      return arguments;
   }
}