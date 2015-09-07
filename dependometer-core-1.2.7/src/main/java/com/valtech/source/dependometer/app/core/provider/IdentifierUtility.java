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
package com.valtech.source.dependometer.app.core.provider;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public final class IdentifierUtility
{

   private static IIdentifierResolver identifierResolver;

   private IdentifierUtility()
   {
      // Just to make the ctor unaccessible
   }

   static
   {
      identifierResolver = ProviderFactory.getInstance().getTypeDefinitionProvider().getIdentifierResolver();
   }

   public static String getVerticalSliceName(String fullyQualifiedSubsystem)
   {
      return identifierResolver.getVerticalSliceName(fullyQualifiedSubsystem);
   }

   public static String getLayerName(String fullyQualifiedSubsystem)
   {
      return identifierResolver.getLayerName(fullyQualifiedSubsystem);
   }

   public static String getFullyQualifiedTypeName(String fullyQualifiedTypeName)
   {
      return identifierResolver.getFullyQualifiedTypeName(fullyQualifiedTypeName);
   }

   public static String getTypeName(String fullyQualifiedTypeName)
   {
      return identifierResolver.getTypeName(fullyQualifiedTypeName);
   }

   public static String getPackageName(String fullyQualifiedTypeName)
   {
      return identifierResolver.getPackageName(fullyQualifiedTypeName);
   }

   public static String getRelativeCompilationUnitPath(String fullyQualifiedTypeName)
   {
      return identifierResolver.getRelativeCompilationUnitPath(fullyQualifiedTypeName);
   }

   public static String getCompilationUnitName(String fullyQualifiedTypeName)
   {
      return identifierResolver.getCompilationUnitName(fullyQualifiedTypeName);
   }
}