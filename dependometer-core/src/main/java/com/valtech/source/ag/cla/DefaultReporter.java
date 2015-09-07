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

import org.apache.log4j.Logger;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
final class DefaultReporter implements CallbackIf
{
   private static Logger s_Logger = Logger.getLogger(DefaultReporter.class.getName());

   public void wrongArgumentForOrCombinedOption(String option)
   {
      assert option != null;
      assert option.length() > 0;
      s_Logger.warn("Wrong argument for or option '" + option + "'");
   }

   public void argumentForArgumentOptionMissing(String option)
   {
      assert option != null;
      assert option.length() > 0;
      s_Logger.warn("Missing argument for argument option '" + option + "'");
   }

   public void unrecognizedOption(String option)
   {
      assert option != null;
      assert option.length() > 0;
      s_Logger.warn("Unrecognized option '" + option + "'");
   }

   public void missingArguments(String[] descriptions, String[] providedArguments)
   {
      assert descriptions != null;
      assert providedArguments != null;
      s_Logger.warn(descriptions.length - providedArguments.length + " argument(s) missing");
   }

   public void toManyArguments(String[] descriptions, String[] providedArguments)
   {
      assert descriptions != null;
      assert providedArguments != null;
      s_Logger.warn(providedArguments.length - descriptions.length + " argument(s) could not be processed - only "
         + descriptions.length + " allowed but " + providedArguments.length + " provided");
   }

   public void mandatoryOptionMissing(String option)
   {
      assert option != null;
      assert option.length() > 0;
      s_Logger.warn("Missing mandatory option '" + option + "'");
   }

   public void duplicateOption(String option)
   {
      assert option != null;
      assert option.length() > 0;
      s_Logger.warn("Duplicate option '" + option + "'");
   }

   public void misplacedOption(String option)
   {
      assert option != null;
      assert option.length() > 0;
      s_Logger.warn("Options must precede arguments - wrong placement of option '" + option + "'");
   }

   public void commandLine(String[] args)
   {
      assert args != null;

      StringBuffer buffer = new StringBuffer("Command line: ");
      for (int i = 0; i < args.length; ++i)
      {
         buffer.append(args[i] + " ");
      }

      s_Logger.debug(buffer.toString());
   }

   public void valueForPropertyOptionMissing(String option)
   {
      assert option != null;
      assert option.length() > 0;
      s_Logger.warn("Missing value for property option '" + option + "'");
   }
}
