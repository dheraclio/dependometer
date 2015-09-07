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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
class Option
{
   // TYPES
   abstract static class OptionIdentifierCheck
   {
      private Option m_CurrentOption;

      void setCurrentOption(Option option)
      {
         m_CurrentOption = option;
      }

      Option getCurrentOption()
      {
         return m_CurrentOption;
      }

      abstract boolean checkOptionIdentifier(Option currentOption);
   }

   final static String HYPHEN = "-";

   private static boolean s_WasSyntaxCorrect;

   private static Set<String> s_OptionIdentifier = new HashSet<String>();

   private static List<OptionIdentifierCheck> s_OptionIdentifierChecks = new ArrayList<OptionIdentifierCheck>();

   private static Option s_Head;

   private static Option s_Tail;

   private static Map<String, String> s_OptionToArgument = new HashMap<String, String>();

   private static Set<String> s_ProcessedOptions = new HashSet<String>();

   private String m_OptionIdentifier;

   private String m_Description;

   private boolean m_Mandatory;

   private boolean m_Processed;

   private boolean m_DuplicateOption;

   private Option m_Successor;

   Option(String optionIdentifier, String description, boolean mandatory)
   {
      assert optionIdentifier != null;
      assert optionIdentifier.length() > 0;
      assert optionIdentifier.startsWith(HYPHEN);
      assert !s_OptionIdentifier.contains(optionIdentifier);

      m_OptionIdentifier = optionIdentifier;
      s_OptionIdentifier.add(m_OptionIdentifier);
      m_Description = description;
      m_Mandatory = mandatory;
      linkOption(this);

      assert isOptionValid();
      assert s_Head != null;
      assert s_Tail != null;
   }

   private boolean isOptionValid()
   {
      boolean result = true;

      for (int i = 0; i < s_OptionIdentifierChecks.size(); ++i)
      {
         OptionIdentifierCheck check = (OptionIdentifierCheck)s_OptionIdentifierChecks.get(i);
         check.setCurrentOption(this);
         result = result && check.checkOptionIdentifier(this);
         check.setCurrentOption(null);
      }

      return result;
   }

   boolean isMandatory()
   {
      return m_Mandatory;
   }

   static void init()
   {
      s_OptionIdentifier.clear();
      s_OptionIdentifierChecks.clear();
      s_Head = null;
      s_Tail = null;
      s_OptionToArgument.clear();
   }

   final String getDescription()
   {
      return m_Description;
   }

   protected final String getOptionIdentifier()
   {
      return m_OptionIdentifier;
   }

   protected final void setProcessed()
   {
      m_Processed = true;
      boolean success = s_ProcessedOptions.add(m_OptionIdentifier);
      assert success;
   }

   protected final boolean alreadyProcessed()
   {
      return m_Processed;
   }

   protected final void setDuplicateOption()
   {
      m_DuplicateOption = true;
   }

   protected String getArgument()
   {
      return null;
   }

   protected static void addCheck(OptionIdentifierCheck check)
   {
      assert check != null;
      assert !s_OptionIdentifierChecks.contains(check);
      s_OptionIdentifierChecks.add(check);
   }

   private void linkOption(Option option)
   {
      assert option != null;

      if (s_Head == null)
      {
         s_Head = option;
         s_Tail = option;
      }
      else
      {
         s_Tail.m_Successor = option;
         s_Tail = option;
      }
   }

   static int processNextArgument(String[] args, int index)
   {
      assert s_Head != null;

      int result = -1;
      Option currentOption = s_Head;

      while (result == -1 && currentOption != null)
      {
         result = currentOption.process(args, index);
         currentOption = currentOption.m_Successor;
      }

      return result;
   }

   protected int process(String[] args, int index)
   {
      assert args != null;
      assert args.length > 0;
      assert index >= 0;
      assert index < args.length;

      int resultingIndex = -1;

      if (args[index].equals(getOptionIdentifier()))
      {
         if (alreadyProcessed())
         {
            setDuplicateOption();
         }
         else
         {
            setProcessed();
         }

         resultingIndex = index;
      }

      return resultingIndex;
   }

   static boolean areStatesValid(CallbackIf cb)
   {
      if (s_Head != null)
      {
         boolean wasSyntaxCorrect = true;
         Option currentOption = s_Head;

         while (currentOption != null)
         {
            if (!currentOption.isStateValid(cb))
            {
               wasSyntaxCorrect = false;
            }

            currentOption = currentOption.m_Successor;
         }

         s_WasSyntaxCorrect = wasSyntaxCorrect;

         if (s_WasSyntaxCorrect)
         {
            collectOptionData();
         }

         return s_WasSyntaxCorrect;
      }
      else
      {
         return true;
      }
   }

   private static void collectOptionData()
   {
      assert s_Head != null;

      Option currentOption = s_Head;

      while (currentOption != null)
      {
         String optionIdentifier = currentOption.getOptionIdentifier();
         String argument = currentOption.getArgument();

         assert !s_OptionToArgument.containsKey(optionIdentifier);
         assert optionIdentifier != null;
         assert optionIdentifier.length() > 0;
         assert argument != null ? argument.length() > 0 : true;

         if (argument != null)
         {
            s_OptionToArgument.put(optionIdentifier, argument);
         }

         currentOption = currentOption.m_Successor;
      }
   }

   protected boolean isStateValid(CallbackIf cb)
   {
      boolean result = true;

      if (m_Mandatory && !m_Processed)
      {
         cb.mandatoryOptionMissing(m_OptionIdentifier);
         result = false;
      }

      if (m_DuplicateOption)
      {
         cb.duplicateOption(m_OptionIdentifier);
         result = false;
      }

      return result;
   }

   static boolean wasOptionProvided(String option)
   {
      assert s_WasSyntaxCorrect;
      assert option != null;
      assert option.length() > 0;

      boolean provided = s_ProcessedOptions.contains(option);
      return provided;
   }

   static boolean hasOptionAnArgument(String option)
   {
      assert s_WasSyntaxCorrect;
      assert wasOptionProvided(option);
      assert option != null;
      assert option.length() > 0;

      return s_OptionToArgument.get(option) != null;
   }

   static String getArgumentForOption(String option)
   {
      assert s_WasSyntaxCorrect;
      assert hasOptionAnArgument(option);
      assert option != null;
      assert option.length() > 0;

      return (String)s_OptionToArgument.get(option);
   }

   static String[] getOptionDescriptions()
   {
      assert s_Head != null;
      Option currentOption = s_Head;
      ArrayList<String> descriptions = new ArrayList<String>();

      while (currentOption != null)
      {
         descriptions.add(currentOption.toString());
         currentOption = currentOption.m_Successor;
      }

      return (String[])descriptions.toArray(new String[0]);
   }

   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append(getOptionIdentifier());
      buffer.append(" (");
      buffer.append(getDescription());
      buffer.append(m_Mandatory ? ", mandatory" : "");
      buffer.append(")");

      return buffer.toString();
   }
}