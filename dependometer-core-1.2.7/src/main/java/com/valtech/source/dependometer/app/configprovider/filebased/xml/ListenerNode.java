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

import com.valtech.source.dependometer.app.core.provider.ListenerIf;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public final class ListenerNode extends Node implements ListenerIf
{
   private final String className;

   private final String listenerArguments;

   public ListenerNode(String className, String arguments)
   {
      assert className != null;
      assert className.length() > 0;
      assert arguments != null;

      if (className.length() == 0)
      {
         throw new IllegalArgumentException("No listener class specified - " + Node.getLocationInfoProvider().getInfo());
      }
      this.className = className;
      this.listenerArguments = arguments;
   }

   public String getArguments()
   {
      return listenerArguments;
   }

   public String getClassName()
   {
      return className;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (!(obj instanceof ListenerNode))
      {
         return false;
      }
      ListenerNode otherNode = (ListenerNode)obj;
      return hashCode() == otherNode.hashCode();
   }

   @Override
   public int hashCode()
   {
      return (className + listenerArguments).hashCode();
   }

}
