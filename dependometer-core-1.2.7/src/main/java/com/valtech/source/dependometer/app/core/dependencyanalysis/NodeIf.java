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
package com.valtech.source.dependometer.app.core.dependencyanalysis;

/**
 * Classes that implement this interface can be analyzed with a DependencyAnalysisIf implementation
 * 
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public interface NodeIf
{
   /**
    * Get the nodes that use the node
    * 
    * @return Array with nodes - must not be null
    */
   public NodeIf[] getAfferentNodes();

   /**
    * Get the nodes the node directly depends upon
    * 
    * @return Array with nodes - must not be null
    */
   public NodeIf[] getEfferentNodes();

   public int getNumberOfEfferentNodes();

   /**
    * Callback - If analyzed the component dependency will be set (Metric by John Lakos)
    * 
    * @param dependsUponNumberOfElements
    */
   public void setDependsUpon(int dependsUponNumberOfElements);

   public boolean wasDependsUponSet();

   public int getDependsUpon();
}
