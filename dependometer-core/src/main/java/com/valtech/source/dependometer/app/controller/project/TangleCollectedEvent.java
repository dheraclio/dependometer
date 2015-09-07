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
import com.valtech.source.dependometer.app.core.elements.EntityTypeEnum;
import com.valtech.source.dependometer.app.core.provider.DependencyElementIf;

import java.util.Arrays;

/**
 * @author Bernhard Ruemenapp
 */
public abstract class TangleCollectedEvent extends Event
{
   private DependencyElementIf[] tangle;

   public DependencyElementIf[] getTangle()
   {
      return tangle;
   }

   public void setTangle(DependencyElementIf[] tangle)
   {
      assert tangle != null && tangle.length >= 2;
      this.tangle = Arrays.copyOf( tangle, tangle.length );
   }

   public abstract EntityTypeEnum getEntityTypeEnum();
}
