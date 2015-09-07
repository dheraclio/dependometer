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
package com.valtech.source.dependometer.app.controller.type;

import com.valtech.source.ag.util.AssertionUtility;
import com.valtech.source.dependometer.app.controller.project.DependencyAnalysisListener;
import com.valtech.source.dependometer.app.core.dependencyanalysis.DirectedNodeDependencyIf;
import com.valtech.source.dependometer.app.core.dependencyanalysis.NodeIf;
import com.valtech.source.dependometer.app.core.elements.DependencyElement;
import com.valtech.source.dependometer.app.core.elements.Type;
import com.valtech.source.dependometer.app.core.provider.DirectedDependencyIf;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public final class TypeListener extends DependencyAnalysisListener
{
   private final SingleCompilationUnitTypeCycleCollectedEvent m_SingleCompilationUnitCycleEvent = new SingleCompilationUnitTypeCycleCollectedEvent();

   private final SinglePackageTypeCycleCollectedEvent m_SinglePackageCycleEvent = new SinglePackageTypeCycleCollectedEvent();

   private final MultiplePackageTypeCycleCollectedEvent m_MultiplePackageCycleEvent = new MultiplePackageTypeCycleCollectedEvent();

   private final TypeTangleCollectedEvent m_TangleEvent = new TypeTangleCollectedEvent();
   
   private final TypeLevelCollectedEvent m_LevelEvent = new TypeLevelCollectedEvent();

   private final TypeCycleParticipationCollectedEvent m_CycleParticipationEvent = new TypeCycleParticipationCollectedEvent();

   private final TypeManager typeManager;

   public TypeListener(TypeManager typeManager)
   {
      this.typeManager = typeManager;
   }

   public NodeIf[] allocateNodes(int numberOfNodes)
   {
      assert numberOfNodes >= 0;
      return new Type[numberOfNodes];
   }

   public void cycleCollected(NodeIf[] cycleNodes)
   {
      assert cycleNodes != null;
      assert cycleNodes.length > 1;

      Type[] elements = (Type[])cycleNodes;

      boolean isSingleCompilationUnitCycle = true;
      boolean isSinglePackageCycle = true;

      Type next = elements[0];
      String packageNameOfFirstType = next.getPackageName();
      String compilationUnitNameOfFirstType = next.getCompilationUnitName();

      for (int i = 1; i < elements.length; ++i)
      {
         next = elements[i];
         if (!next.getPackageName().equals(packageNameOfFirstType))
         {
            isSinglePackageCycle = false;
            break;
         }
         else if (!next.getCompilationUnitName().equals(compilationUnitNameOfFirstType))
         {
            isSingleCompilationUnitCycle = false;
            break;
         }
      }
      
      if (isSinglePackageCycle)
      {
         if (isSingleCompilationUnitCycle)
         {
            m_SingleCompilationUnitCycleEvent.setCycle(elements);
            typeManager.dispatch(m_SingleCompilationUnitCycleEvent);
         }
         else
         {
            m_SinglePackageCycleEvent.setCycle(elements);
            typeManager.dispatch(m_SinglePackageCycleEvent);
         }
      }
      else
      {
         m_MultiplePackageCycleEvent.setCycle(elements);
         typeManager.dispatch(m_MultiplePackageCycleEvent);
      }

      cycleAdded();
   }

   public void tangleCollected(DependencyElement[] tangle)
   {
      assert tangle != null;
      m_TangleEvent.setTangle(tangle);
      typeManager.dispatch(m_TangleEvent);
   }

   public void cycleParticipationCollected(int count, DirectedNodeDependencyIf[] directedDependencies)
   {
      assert AssertionUtility.checkArray(directedDependencies);
      m_CycleParticipationEvent.setCount(count);
      m_CycleParticipationEvent.setDependencies((DirectedDependencyIf[])directedDependencies);
      typeManager.dispatch(m_CycleParticipationEvent);
   }

   public void levelCollected(int level, NodeIf[] nodes)
   {
      assert AssertionUtility.checkArray(nodes);
      int length = nodes.length;
      DependencyElement[] elements = new DependencyElement[length];
      System.arraycopy(nodes, 0, elements, 0, length);
      m_LevelEvent.setLevel(level);
      m_LevelEvent.setElements(elements);
      typeManager.dispatch(m_LevelEvent);
   }

   protected String getElementName()
   {
      return Type.ELEMENT_NAME;
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
      TypeCycleParticipantsCollectedEvent event = new TypeCycleParticipantsCollectedEvent(elementParticipants,
         elementsNotCompletelyAnalyzed);
      typeManager.dispatch(event);
   }
}
