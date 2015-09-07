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
package com.valtech.source.dependometer.app.controller.layer;

import com.valtech.source.ag.util.AssertionUtility;
import com.valtech.source.dependometer.app.controller.project.DependencyAnalysisListener;
import com.valtech.source.dependometer.app.core.dependencyanalysis.DirectedNodeDependencyIf;
import com.valtech.source.dependometer.app.core.dependencyanalysis.NodeIf;
import com.valtech.source.dependometer.app.core.elements.DependencyElement;
import com.valtech.source.dependometer.app.core.elements.Layer;
import com.valtech.source.dependometer.app.core.provider.DirectedDependencyIf;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public final class LayerListener extends DependencyAnalysisListener
{
   private final LayerCycleCollectedEvent m_CycleEvent = new LayerCycleCollectedEvent();
   
   private final LayerTangleCollectedEvent m_TangleEvent = new LayerTangleCollectedEvent();
   
   private final LayerLevelCollectedEvent m_LevelEvent = new LayerLevelCollectedEvent();

   private final LayerCycleParticipationCollectedEvent m_CycleParticipationEvent = new LayerCycleParticipationCollectedEvent();

   private final LayerManager layerManager;

   public LayerListener(LayerManager layerManager)
   {
      this.layerManager = layerManager;
   }

   public NodeIf[] allocateNodes(int numberOfNodes)
   {
      assert numberOfNodes >= 0;
      return new Layer[numberOfNodes];
   }

   public void cycleCollected(NodeIf[] cycleNodes)
   {
      assert AssertionUtility.checkArray(cycleNodes);
      assert cycleNodes.length > 1;
      Layer[] elements = (Layer[])cycleNodes;
      m_CycleEvent.setCycle(elements);
      layerManager.dispatch(m_CycleEvent);
      cycleAdded();
   }

   public void tangleCollected(DependencyElement[] tangle)
   {
      assert tangle != null;
      m_TangleEvent.setTangle(tangle);
      layerManager.dispatch(m_TangleEvent);
   }

   public void cycleParticipationCollected(int count, DirectedNodeDependencyIf[] directedDependencies)
   {
      assert AssertionUtility.checkArray(directedDependencies);
      m_CycleParticipationEvent.setCount(count);
      m_CycleParticipationEvent.setDependencies((DirectedDependencyIf[])directedDependencies);
      layerManager.dispatch(m_CycleParticipationEvent);
   }

   public void levelCollected(int level, NodeIf[] nodes)
   {
      assert AssertionUtility.checkArray(nodes);
      int length = nodes.length;
      DependencyElement[] elements = new DependencyElement[length];
      System.arraycopy(nodes, 0, elements, 0, length);
      m_LevelEvent.setLevel(level);
      m_LevelEvent.setElements(elements);
      layerManager.dispatch(m_LevelEvent);
   }

   protected String getElementName()
   {
      return Layer.ELEMENT_NAME;
   }

   public void cycleParticipantsCollected(NodeIf[] participants, NodeIf[] notCompletelyAnalyzed)
   {
      assert AssertionUtility.checkArray(participants);
      int length = participants.length;
      DependencyElement[] elementParticipants = new DependencyElement[length];
      System.arraycopy(participants, 0, elementParticipants, 0, length);
      length = notCompletelyAnalyzed.length;
      DependencyElement[] elementsNotCompletelyAnalyzed = new DependencyElement[length];
      System.arraycopy(notCompletelyAnalyzed, 0, elementsNotCompletelyAnalyzed, 0, length);
      LayerCycleParticipantsCollectedEvent event = new LayerCycleParticipantsCollectedEvent(elementParticipants,
         elementsNotCompletelyAnalyzed);
      layerManager.dispatch(event);
   }

}
