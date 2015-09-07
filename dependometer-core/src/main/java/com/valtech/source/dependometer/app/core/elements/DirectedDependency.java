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
package com.valtech.source.dependometer.app.core.elements;

import com.valtech.source.dependometer.app.core.provider.DependencyElementIf;
import com.valtech.source.dependometer.app.core.provider.DirectedDependencyIf;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public final class DirectedDependency implements DirectedDependencyIf, Comparable<DirectedDependencyIf>
{
   private final DependencyElementIf from;

   private final DependencyElementIf to;

   private final int numberOfTypeRelations;

   private final boolean isViolation;

   private int cycleParticipation;

   public DirectedDependency(DependencyElementIf from, DependencyElementIf to, int typeRelations, boolean isViolation)
   {
      assert from != null;
      assert to != null;
      assert !from.equals(to);

      this.from = from;
      this.to = to;
      numberOfTypeRelations = typeRelations;
      this.isViolation = isViolation;
   }

   public DependencyElementIf getFrom()
   {
      return from;
   }

   public DependencyElementIf getTo()
   {
      return to;
   }

   public int compareTo(DirectedDependencyIf depIf)
   {
      assert depIf != null;

      int result = depIf.getNumberOfTypeRelations() - getNumberOfTypeRelations();
      if (result == 0)
      {
         DependencyElementIf from = getFrom();
         DependencyElementIf to = getTo();
         DependencyElementIf compareFrom = depIf.getFrom();
         DependencyElementIf compareTo = depIf.getTo();

         result = from.compareTo(compareFrom);
         if (result == 0)
         {
            result = to.compareTo(compareTo);
         }
      }
      return result;
   }

   public int getNumberOfTypeRelations()
   {
      return numberOfTypeRelations;
   }

   public boolean isViolation()
   {
      return isViolation;
   }

   public void incrementCycleParticipation()
   {
      cycleParticipation++;
   }

   public int getCycleParticipation()
   {
      return cycleParticipation;
   }

    @Override
    public boolean equals( Object o ) {
        if( this == o ) return true;
        if( !( o instanceof DirectedDependency ) ) return false;

        DirectedDependency that = (DirectedDependency) o;

        if( cycleParticipation != that.cycleParticipation ) return false;
        if( isViolation != that.isViolation ) return false;
        if( numberOfTypeRelations != that.numberOfTypeRelations ) return false;
        if( from != null ? !from.equals( that.from ) : that.from != null ) return false;
        if( to != null ? !to.equals( that.to ) : that.to != null ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = from != null ? from.hashCode() : 0;
        result = 31 * result + ( to != null ? to.hashCode() : 0 );
        result = 31 * result + numberOfTypeRelations;
        result = 31 * result + ( isViolation ? 1 : 0 );
        result = 31 * result + cycleParticipation;
        return result;
    }
}
