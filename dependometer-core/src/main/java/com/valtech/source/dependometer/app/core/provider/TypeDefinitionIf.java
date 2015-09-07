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

import java.io.File;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public interface TypeDefinitionIf
{
   /**
    * Returns the object from which the type definition was read
    */
   public Object getSource();

   /**
    * Returns the absolute path of the corresponding source file
    */
   public File getAbsoluteSourcePath();

   /**
    * Indicates if the original type defintion was refactored
    */
   public boolean wasRefactored();

   /**
    * Precondition wasRefactored() == true
    * 
    * @return the unrefactored fully qualified name
    */
   public String getUnrefactoredFullyQualifiedTypeName();

   /**
    * Fully qualified name consists of: Package + TypeName + [$ + NestedTypeName] Example:
    * com.valtech.source.ag.cla.Option Example: com.valtech.source.ag.cla.Option$Check
    * 
    * @return Fully qualified type name
    */
   public String getFullyQualifiedTypeName();

   /**
    * Example: com/valtech/source/ag/cla/Option.java
    * 
    * @return relative path
    */
   public String getRelativeCompilationUnitPath();

   public boolean isClass();

   public boolean isInterface();

   public boolean isAbstract();

   /**
    * Answers the question if the type is extendable. Returns false if it is not possible to create subtypes
    */
   public boolean isExtendable();

   /**
    * Answers the question if the type is accessible from outside its package. Returns false if the visibility has
    * package scope
    */
   public boolean isAccessible();

   /**
    * Returns the number of used assertions
    * 
    * @return -1 if no assertion analysis was possible or 0 .. n number of encountered assertions
    */
   public int getNumberOfAssertions();

   /**
    * Returns all direct super class names
    */
   public String[] getFullyQualifiedSuperClassNames();

   /**
    * Returns all direct super interface names
    */
   public String[] getFullyQualifiedSuperInterfaceNames();

   /**
    * Returns all used type names
    */
   public String[] getFullyQualifiedImportedTypeNames();

   /**
    * Returns all type names that were ignored
    */
   public String[] getIgnoredFullyQualifiedImportedTypeNames();

   /**
    * Returns all type names that were skipped
    */
   public String[] getSkippedFullyQualifiedImportedTypeNames();

   /**
    * Returns true if the following information is provided - the source - the fully qualified type name - the
    * attributes (class or interface, abstractness, accessibility) - the relative compilation unit path
    */
   public boolean isValid();
}