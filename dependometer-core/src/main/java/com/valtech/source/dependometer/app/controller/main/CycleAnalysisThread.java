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
package com.valtech.source.dependometer.app.controller.main;


import java.util.Date;

import org.apache.log4j.Logger;

import com.valtech.source.dependometer.app.core.dependencyanalysis.DependencyAnalysisIf;
import com.valtech.source.dependometer.app.core.elements.EntityTypeEnum;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
final class CycleAnalysisThread extends Thread
{
   private Logger s_Logger = Logger.getLogger(DependencyManager.class);

   private final DependencyAnalysisIf m_DependencyAnalysis;

   private final int m_MaxCycles;
   
   private final Date m_TimeStop;

   private final EntityTypeEnum entity;

   CycleAnalysisThread(DependencyAnalysisIf dependencyAnalysis, int maxCycles, EntityTypeEnum entity, Date timeStop)
   {
      super("CycleAnalysisThread");
      this.entity = entity;
      assert dependencyAnalysis != null;
      assert dependencyAnalysis.hasCycles();
      m_DependencyAnalysis = dependencyAnalysis;
      m_MaxCycles = maxCycles;
      m_TimeStop = timeStop;
      setPriority(MIN_PRIORITY);
   }

   synchronized void finish()
   {
      m_DependencyAnalysis.finishCycleAnalysis();
   }

   public void run()
   {
      s_Logger.info("starting analysis of " + entity.getEntityName() + " cycles ...\n" + getMaxCycleMessage());
      m_DependencyAnalysis.analyzeCycles(m_MaxCycles, m_TimeStop);
   }

   private String getMaxCycleMessage()
   {
      String msg = "[collecting ";
      if (m_MaxCycles < 0)
      {
         msg += "all";
      }
      else if (m_MaxCycles == 0)
      {
         msg += "no";
      }
      else
      {
         msg += "up to " + m_MaxCycles;
      }
      msg += " cycles]";
      return msg;
   }
}
