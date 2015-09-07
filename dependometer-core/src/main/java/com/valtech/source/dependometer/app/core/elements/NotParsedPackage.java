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
package com.valtech.source.dependometer.app.core.elements;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public final class NotParsedPackage extends Package
{
   private static int s_NumberOfNotParsedJavaPackages;

   static int getNumberOfNotParsedPackages()
   {
      return s_NumberOfNotParsedJavaPackages;
   }

   public NotParsedPackage(String name)
   {
      super(name);
      ++s_NumberOfNotParsedJavaPackages;
   }

   public static void reset()
   {
      s_NumberOfNotParsedJavaPackages = 0;
   }
}
