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

import com.valtech.source.ag.evf.Event;
import com.valtech.source.ag.util.AssertionUtility;
import com.valtech.source.dependometer.app.core.elements.EntityTypeEnum;
import com.valtech.source.dependometer.app.core.provider.DependencyElementIf;

import java.util.Arrays;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public abstract class LevelCollectedEvent extends Event
{
   private int level = -2;

   private DependencyElementIf[] elements;

   public final DependencyElementIf[] getElements()
   {
      assert elements != null;
      return elements;
   }

   public final int getLevel()
   {
      assert level != -2;
      return level;
   }

   public final void setElements(DependencyElementIf[] elements)
   {
      assert AssertionUtility.checkArray(elements);
      this.elements = Arrays.copyOf( elements, elements.length );
   }

   public final void setLevel(int level)
   {
      assert level > -2;
      this.level = level;
   }
   
   public abstract EntityTypeEnum getEntityType();
}
