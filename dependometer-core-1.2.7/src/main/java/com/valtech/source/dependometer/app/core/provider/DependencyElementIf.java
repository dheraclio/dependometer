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

import com.valtech.source.dependometer.app.core.elements.EntityTypeEnum;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public interface DependencyElementIf extends MetricsProviderIf
{
   public final String USES = "uses";

   public final String IMPLEMENTS = "implements";

   public final String EXTENDS = "extends";

   public interface CouplingIf
   {
      public DependencyElementIf getContained();

      public DependencyElementIf[] getNotContainedRelatedElements();

      public String getRelationQualifier(DependencyElementIf element);
   }

   public String getElementName();

   public String getContainedElementName();

   public String getName();

   public String getFullyQualifiedContainmentName();

   public boolean hasDescription();

   public String getDescription();

   public boolean wasRefactored();

   public boolean hasViewableSourceFile();

   public File getAbsoluteSourcePath();

   public boolean belongsToProject();

   public DependencyElementIf belongsToDependencyElement();

   public DependencyElementIf[] containingDependencyElements();

   public DependencyElementIf[] containsDependencyElements();

   public boolean contains(DependencyElementIf element);

   public boolean wasDependsUponSet();

   public int getDependsUpon();

   public boolean hasProjectInternalTypes();

   public boolean hasAccessibleTypes();

   public boolean hasConcreteTypes();

   public DependencyElementIf[] getAfferents();

   public int getNumberOfAfferents();

   public boolean isAfferent(DependencyElementIf dependencyElement);

   public CouplingIf[] getAfferentCouplings(DependencyElementIf afferent);

   public String getAfferentRelationQualifier(DependencyElementIf relationFrom);

   public int getNumberOfTypeRelationsForAfferent(DependencyElementIf afferent);

   public DependencyElementIf[] getEfferents();

   public int getNumberOfEfferents();

   public boolean isEfferent(DependencyElementIf dependencyElement);

   public boolean isAllowedEfferent(DependencyElementIf allowed);

   public boolean isForbiddenEfferent(DependencyElementIf efferent);

   public DependencyElementIf[] getUsedAllowedEfferents();

   public DependencyElementIf[] getUnusedAllowedEfferents();

   public CouplingIf[] getEfferentCouplings(DependencyElementIf efferent);

   public String getEfferentRelationQualifier(DependencyElementIf relationTo);

   public int getNumberOfForbiddenEfferentDependencies();

   public int getNumberOfTypeRelationsForEfferent(DependencyElementIf efferent);
   
   EntityTypeEnum getEntityType();
}