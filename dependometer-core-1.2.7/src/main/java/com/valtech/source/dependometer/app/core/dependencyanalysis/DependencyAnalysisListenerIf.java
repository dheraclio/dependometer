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

import com.valtech.source.dependometer.app.core.elements.DependencyElement;

/**
 * Classes that implement this interface can fetch cycle detection related information
 * 
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public interface DependencyAnalysisListenerIf
{
   public void progressInfo(int totalNodes, int remainingNodes);

   /**
    * Request the storage for a detected cycle
    * 
    * @param number
    * @return allocated storage
    */
   public NodeIf[] allocateNodes(int number);

   /**
    * Create a directed dependency
    * 
    * @param from
    * @param to
    * @return allocated storage
    */
   public DirectedNodeDependencyIf createDirectedDependency(NodeIf from, NodeIf to);

   /**
    * Request the storage for directed dependencies
    * 
    * @param number
    * @return allocated storage
    */
   public DirectedNodeDependencyIf[] allocateDirectedDependencies(int number);

   
   /**
    * The nodes forming a tangle are reported
    * 
    * @param tangle an array containing the cyclic nodes
    */
   public void tangleCollected(DependencyElement[] tangle);
   
   
   /**
    * The nodes forming a cycle are reported
    * 
    * @param cycleNodes an array containing the cyclic nodes
    */
   public void cycleCollected(NodeIf[] cycleNodes);

   /**
    * The participation of dependencies in cycles is reported
    * 
    * @param count participation
    * @param cycleElements array with dependencies
    */
   public void cycleParticipationCollected(int count, DirectedNodeDependencyIf[] cycleElements);

   /**
    * The nodes directly participating in cycles and the nodes that have not been completely analyzed are reported
    * 
    * @param participants the nodes directly participating in cycles
    * @param notCompletelyAnalyzed the nodes that have not been completely analyzed
    */
   public void cycleParticipantsCollected(NodeIf[] participants, NodeIf[] notCompletelyAnalyzed);

   /**
    * The participation of dependencies in cycles is reported
    * 
    * @param level
    * @param nodes
    */
   public void levelCollected(int level, NodeIf[] nodes);

  
}
