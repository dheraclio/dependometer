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

import java.util.Date;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public interface DependencyAnalysisIf
{
   /**
    * Cycle existance check
    */
   boolean hasCycles();

   /**
    * Start the detection of cycles - there must exist cycles
    */
   void analyzeCycles(int maxCycles, Date timeStop);

   /**
    * Terminate cycle detection
    */
   void finishCycleAnalysis();

   /**
    * Perform the cycle participation analysis - the cycle analysis must not be running
    */
   void analyzeCyclePartizipation();

   /**
    * Perform the cumulation of node dependencies - the cycle analysis must not be running
    */
   void cumulateNodeDependencies();

   /**
    * Perform the levelization analysis - the cycle analysis must not be running
    */
   void analyzeLevels();

   /**
    * @return number of detected cycles
    */
   int getNumberOfDetectedTangles();
   
   /**
    * @return number of detected tangles
    */
   int getNumberOfDetectedCycles();
}