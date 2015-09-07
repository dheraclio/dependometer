/*
 * Valtech Public L I C E N S E (VPL) 1.0.2
 * 
 * dependometer Copyright � 2007 Valtech GmbH
 * 
 * dependometer software is made available free of charge under the following conditions.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 * 
 * 1.1.All copies and redistributions of source code must retain the above copyright notice, this list of conditions and
 * the following disclaimer.
 * 
 * 1.2.Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 * following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * 1.3.The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
 * This product includes software developed by Valtech http://www.valtech.de/. This acknowledgement must appear in the
 * software itself, if and wherever such third-party acknowledgments normally appear.
 * 
 * 1.4.The names "Valtech" and "dependometer" must not be used to endorse or promote products derived from this software
 * without prior written permission. For written permission, please contact kmc@valtech.de <mailto:kmc@valtech.de>
 * 
 * BECAUSE THIS SOFTWARE IS LICENSED FREE OF CHARGE IT IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL VALTECH GMBH OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. LEGAL LIABILITY PROVIDED UNDER GERMAN LAW FOR
 * INTENDED DAMAGES, BAD FAITH OR GROSS NEGLIGENCE REMAINS UNAFFECTED.
 */

package com.valtech.source.ag.cla;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public final class CommandLineParser
{
   private static CommandLineParser s_CommandLineParser;

   private static String s_LineSeparator = System.getProperty("line.separator");

   private boolean m_Initialized;

   private HashSet<String> m_UnrecognizedOptions = new HashSet<String>();

   private HashSet<String> m_MisplacedOptions = new HashSet<String>();

   private ArrayList<String> m_ProcessedArguments = new ArrayList<String>();

    // todo refactor away
    private int m_NumberOfArguments;

   private String[] argumentDescriptions = new String[0];

   private boolean m_AdditionalArgumentsAllowed;

   private CallbackIf m_CallbackIf;

   private CommandLineParser()
   {
      m_CallbackIf = new DefaultReporter();
      clear();
   }

   public static CommandLineParser getInstance()
   {
      if (s_CommandLineParser == null)
      {
         s_CommandLineParser = new CommandLineParser();
      }

      return s_CommandLineParser;
   }

   public void clear()
   {
      m_UnrecognizedOptions.clear();
      m_MisplacedOptions.clear();
      m_ProcessedArguments.clear();
      m_NumberOfArguments = 0;
      argumentDescriptions = new String[0];
      m_AdditionalArgumentsAllowed = false;
      m_Initialized = true;

      Option.init();
   }

   public void setCallbackIf(CallbackIf callbackIf)
   {
      m_CallbackIf = callbackIf;
   }

   public void addOption(String option, String description, boolean mandatory)
   {
      new Option(option, description, mandatory);
   }

   public void addPropertyOption(String propertyKey, String description, boolean mandatory)
   {
      new PropertyOption(propertyKey, description, mandatory);
   }

   public void addArgumentOption(String option, String description, boolean mandatory)
   {
      new ArgumentOption(option, description, mandatory);
   }

   public void addOrCombinedOption(String option, String or[], String description, boolean mandatory)
   {
      new OrCombinedOption(option, or, description, mandatory);
   }

   public void addArguments(String[] argDescriptions, boolean additionalArgumentsAllowed)
   {
      assert m_Initialized;

      if (argDescriptions == null)
      {
         m_NumberOfArguments = 0;
      }
      else
      {
         m_NumberOfArguments = argDescriptions.length;
         argumentDescriptions = Arrays.copyOf( argDescriptions, argDescriptions.length );
      }

      m_AdditionalArgumentsAllowed = additionalArgumentsAllowed;
   }

   public boolean parse(String args[])
   {
      assert args != null;

      boolean commandLineSyntaxIsCorrect = true;
      boolean processingArguments = false;

      m_CallbackIf.commandLine(args);
      for (int i = 0; i < args.length; ++i)
      {
         String next = args[i];

         assert next != null;
         assert next.length() > 0;

         if (next.startsWith(Option.HYPHEN))
         {
            if (processingArguments)
            {
               m_MisplacedOptions.add(next);
            }

            int returnedIndex = Option.processNextArgument(args, i);
            if (returnedIndex == -1)
            {
               m_UnrecognizedOptions.add(next);
            }
            else
            {
               i = returnedIndex;
            }
         }
         else
         {
            if (!processingArguments)
            {
               processingArguments = true;
            }

            m_ProcessedArguments.add(next);
         }
      }

      commandLineSyntaxIsCorrect = Option.areStatesValid(m_CallbackIf);

      if (!m_UnrecognizedOptions.isEmpty())
      {
         Iterator unrecognizedOptionsIterator = m_UnrecognizedOptions.iterator();
         while (unrecognizedOptionsIterator.hasNext())
         {
            m_CallbackIf.unrecognizedOption((String)unrecognizedOptionsIterator.next());
         }

         commandLineSyntaxIsCorrect = false;
      }

      if (!m_MisplacedOptions.isEmpty())
      {
         Iterator misplacedOptionsIterator = m_MisplacedOptions.iterator();
         while (misplacedOptionsIterator.hasNext())
         {
            m_CallbackIf.misplacedOption((String)misplacedOptionsIterator.next());
         }

         commandLineSyntaxIsCorrect = false;
      }

      String[] providedArguments = new String[m_ProcessedArguments.size()];
      m_ProcessedArguments.toArray(providedArguments);

      if (m_ProcessedArguments.size() < m_NumberOfArguments)
      {
         m_CallbackIf.missingArguments( argumentDescriptions, providedArguments);
         commandLineSyntaxIsCorrect = false;
      }
      else if (m_ProcessedArguments.size() > m_NumberOfArguments && !m_AdditionalArgumentsAllowed)
      {
         m_CallbackIf.toManyArguments( argumentDescriptions, providedArguments);
         commandLineSyntaxIsCorrect = false;
      }

      return commandLineSyntaxIsCorrect;
   }

   public boolean wasOptionProvided(String option)
   {
      return Option.wasOptionProvided(option);
   }

   public boolean hasOptionAnArgument(String option)
   {
      return Option.hasOptionAnArgument(option);
   }

   public String getArgumentForOption(String option)
   {
      return Option.getArgumentForOption(option);
   }

   public boolean hasArguments()
   {
      return m_ProcessedArguments.size() > 0;
   }

   public String[] getArguments()
   {
      assert hasArguments();

      String[] args = new String[m_ProcessedArguments.size()];
      return (String[])m_ProcessedArguments.toArray(args);
   }

   public String usage()
   {
      StringBuffer buffer = new StringBuffer();

      String[] optionDescriptions = Option.getOptionDescriptions();
      if (optionDescriptions.length > 0)
      {
         buffer.append("Options:");
         buffer.append(s_LineSeparator);
         for (int i = 0; i < optionDescriptions.length; ++i)
         {
            buffer.append(optionDescriptions[i]);
            buffer.append(s_LineSeparator);
         }
      }

      if ( argumentDescriptions.length > 0)
      {
         buffer.append("Arguments:");
         buffer.append(s_LineSeparator);

         for (int i = 0; i < argumentDescriptions.length; ++i)
         {
            buffer.append( argumentDescriptions[i]);
            buffer.append(s_LineSeparator);
         }

         if (m_AdditionalArgumentsAllowed)
         {
            buffer.append("-- additional arguments allowed --");
         }
         else
         {
            buffer.append("-- no additional arguments allowed --");
         }
      }

      return buffer.toString();
   }
}
