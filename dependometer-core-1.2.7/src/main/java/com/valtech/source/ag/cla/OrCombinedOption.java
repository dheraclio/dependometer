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

import java.util.Arrays;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
class OrCombinedOption extends ArgumentOption
{
   private String[] m_Or;

   // private String m_Choosen;
   private boolean m_WrongOrArgument = true;

   private boolean isOrOptionValid()
   {
      assert m_Or != null;
      assert m_Or.length > 0;
      for (int i = 0; i < m_Or.length; ++i)
      {
         String next = m_Or[i];
         assert next != null;
         assert next.length() > 0;
      }

      return true;
   }

   OrCombinedOption(String option, String[] or, String description, boolean mandatory)
   {
      super(option, description, mandatory);
      m_Or = Arrays.copyOf( or, or.length );
      assert isOrOptionValid();
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
            if (index + 1 >= args.length || args[index + 1].startsWith(HYPHEN))
            {
               resultingIndex = index;
            }
            else
            {
               resultingIndex = index + 1;
            }
         }
         else
         {
            if (index + 1 >= args.length || args[index + 1].startsWith(HYPHEN))
            {
               setMissingArgument();
               resultingIndex = index;
            }
            else
            {
               resultingIndex = index + 1;
               String arg = args[resultingIndex];
               for (int i = 0; i < m_Or.length; ++i)
               {
                  if (m_Or[i].equals(arg))
                  {
                     m_WrongOrArgument = false;
                     break;
                  }
               }

               if (!m_WrongOrArgument)
               {
                  setArgument(args[resultingIndex]);
               }
            }

            setProcessed();
         }
      }

      return resultingIndex;
   }

   protected boolean isStateValid(CallbackIf cb)
   {
      if (m_WrongOrArgument)
      {
         cb.wrongArgumentForOrCombinedOption(getOptionIdentifier());
      }

      return super.isStateValid(cb) && !m_WrongOrArgument;
   }
}
