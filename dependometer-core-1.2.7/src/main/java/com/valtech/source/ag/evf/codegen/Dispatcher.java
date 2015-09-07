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

package com.valtech.source.ag.evf.codegen;

import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.log4j.Logger;

import com.sun.javadoc.ClassDoc;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
class Dispatcher extends CodeGenerator
{
   private static Logger s_Logger = Logger.getLogger(Dispatcher.class.getName());

   protected void generate(ClassDoc[] eventClassDoc, ClassDoc[] eventPerEmitterClassDoc) throws IOException
   {
      assert eventClassDoc != null || eventPerEmitterClassDoc != null;

      s_Logger.debug("Codegenerator 'Dispatcher' entered ...");

      if (eventClassDoc != null)
      {
         for (int i = 0; i < eventClassDoc.length; ++i)
         {
            OutputStreamWriter writer = open("Dispatcher" + eventClassDoc[i].name() + ".java");

            writer.write("// GENERATED FILE!" + getLineSeparator(1));
            writer.write("// " + createVersionInfo(this) + getLineSeparator(1));
            writer.write("// " + createDateInfo() + getLineSeparator(2));
            writer.write("package " + getPackageName() + ";" + getLineSeparator(2));
            writer.write("import de.valtech.ag.evf.EventIf;" + getLineSeparator(1));
            writer.write("import de.valtech.ag.evf.Dispatcher;" + getLineSeparator(2));
            writer.write("final class Dispatcher" + eventClassDoc[i].name() + " extends Dispatcher"
               + getLineSeparator(1));
            writer.write("{" + getLineSeparator(1));
            writer.write(indent(2) + "protected void dispatch(Object handler, EventIf event)" + getLineSeparator(1));
            writer.write(indent(2) + "{" + getLineSeparator(1));
            writer.write(indent(4) + "assert handler!=null;" + getLineSeparator(1));
            writer.write(indent(4) + "assert event!=null;" + getLineSeparator(1));
            writer.write(indent(4) + "((Handle" + eventClassDoc[i].name() + "If)handler).handleEvent(("
               + eventClassDoc[i].qualifiedName() + ")event);" + getLineSeparator(1));
            writer.write(indent(2) + "}" + getLineSeparator(1));
            writer.write("}" + getLineSeparator(1));

            writer.flush();
            close(writer);
         }
      }

      if (eventPerEmitterClassDoc != null)
      {
         for (int i = 0; i < eventPerEmitterClassDoc.length; ++i)
         {
            OutputStreamWriter writer = open("Dispatcher" + eventPerEmitterClassDoc[i].name() + "PerEmitter.java");

            writer.write("// GENERATED FILE!" + getLineSeparator(1));
            writer.write("// " + createVersionInfo(this) + getLineSeparator(1));
            writer.write("// " + createDateInfo() + getLineSeparator(2));
            writer.write("package " + getPackageName() + ";" + getLineSeparator(2));
            writer.write("import de.valtech.ag.evf.EventIf;" + getLineSeparator(1));
            writer.write("import de.valtech.ag.evf.DispatcherPerEmitter;" + getLineSeparator(2));
            writer.write("final class Dispatcher" + eventPerEmitterClassDoc[i].name()
               + "PerEmitter extends DispatcherPerEmitter" + getLineSeparator(1));
            writer.write("{" + getLineSeparator(1));
            writer.write(indent(2) + "protected void dispatch(Object handler, EventIf event, Object emitter)"
               + getLineSeparator(1));
            writer.write(indent(2) + "{" + getLineSeparator(1));
            writer.write(indent(4) + "assert handler!=null;" + getLineSeparator(1));
            writer.write(indent(4) + "assert event!=null;" + getLineSeparator(1));
            writer.write(indent(4) + "assert emitter!=null;" + getLineSeparator(1));
            writer.write(indent(4) + "((Handle" + eventPerEmitterClassDoc[i].name()
               + "PerEmitterIf)handler).handleEvent((" + eventPerEmitterClassDoc[i].qualifiedName()
               + ")event, emitter);" + getLineSeparator(1));
            writer.write(indent(2) + "}" + getLineSeparator(1));
            writer.write("}" + getLineSeparator(1));

            writer.flush();
            close(writer);
         }
      }
   }
}