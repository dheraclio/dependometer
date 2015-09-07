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
package com.valtech.source.dependometer.app.controller.project;

import org.apache.log4j.Logger;

import com.valtech.source.dependometer.app.core.dependencyanalysis.DependencyAnalysisListenerIf;
import com.valtech.source.dependometer.app.core.dependencyanalysis.DirectedNodeDependencyIf;
import com.valtech.source.dependometer.app.core.dependencyanalysis.NodeIf;
import com.valtech.source.dependometer.app.core.elements.DirectedDependency;
import com.valtech.source.dependometer.app.core.provider.DependencyElementIf;
import com.valtech.source.dependometer.app.core.provider.DirectedDependencyIf;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public abstract class DependencyAnalysisListener implements DependencyAnalysisListenerIf
{
   private static Logger s_Logger = Logger.getLogger(DependencyAnalysisListener.class.getName());

   private static int s_CycleFeedback = 1;

   private static int s_ProgressFeedback = 1;

   private boolean m_FirstProgressInfo = true;

   private int m_NumberOfCycles = 0;

   public final void progressInfo(int totalNodes, int remainingNodes)
   {
      assert totalNodes >= 0;
      assert remainingNodes >= 0;

      if (s_ProgressFeedback > 0
         && (m_FirstProgressInfo || (remainingNodes % s_ProgressFeedback == 0) || remainingNodes == 0))
      {
         String state = "";
         if (remainingNodes == 0)
         {
            state = " [finished]";
         }
         s_Logger.info("cycle analysis progress for '" + getElementName() + "s' (remaining/total nodes) = "
            + remainingNodes + "/" + totalNodes + state);
         m_FirstProgressInfo = false;
      }
   }

   public static void setProgressFeedback(int progressFeedback)
   {
      assert progressFeedback >= 0;
      s_ProgressFeedback = progressFeedback;
   }

   public static void setCycleFeedback(int cycleFeedback)
   {
      assert cycleFeedback >= 0;
      s_CycleFeedback = cycleFeedback;
   }

   protected abstract String getElementName();

   protected final void cycleAdded()
   {
      ++m_NumberOfCycles;

      if (s_CycleFeedback > 0 && m_NumberOfCycles % s_CycleFeedback == 0)
      {
         s_Logger.warn("current number of detected '" + getElementName() + "' cycles = " + m_NumberOfCycles);
      }
   }

   public final DirectedNodeDependencyIf createDirectedDependency(NodeIf from, NodeIf to)
   {
      assert from != null;
      assert to != null;
      assert !from.equals(to);
      DependencyElementIf fromElement = (DependencyElementIf)from;
      DependencyElementIf toElement = (DependencyElementIf)to;
      int typeRelations = fromElement.getNumberOfTypeRelationsForEfferent(toElement);
      boolean isForbidden = fromElement.isForbiddenEfferent(toElement);
      return new DirectedDependency(fromElement, toElement, typeRelations, isForbidden);
   }

   public final DirectedNodeDependencyIf[] allocateDirectedDependencies(int number)
   {
      assert number >= 0;
      return new DirectedDependencyIf[number];
   }
}