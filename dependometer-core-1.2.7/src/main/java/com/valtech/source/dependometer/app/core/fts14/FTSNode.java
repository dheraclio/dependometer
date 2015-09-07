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
package com.valtech.source.dependometer.app.core.fts14;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.valtech.source.dependometer.app.core.dependencyanalysis.NodeIf;

/**
 * Super graph node.
 * 
 * @author Berhnard Ruemenapp
 */
class FTSNode
{
   private static final HashSet emptySet = new HashSet();

   FTSNode(NodeIf node)
   {
      origin = node;
   }

   private NodeIf origin;

   private Set efferent = new HashSet(); // outgoing link

   private Set afferent = new HashSet(); // incoming link

   private Set melted = emptySet; // cycle partners

   private FTSNode lastVisited; // already counted cumulated dependency on

   private int ccd; // cumulated dependencies counter

   public void dependUpon(FTSNode other)
   {
      if (this == other)
      {
         return;
      }
      assert efferent != emptySet;
      assert other.afferent != emptySet;
      efferent.add(other);
      other.afferent.add(this);
   }

   /**
    * Melt node a into this node
    * 
    * @param a disappearing node
    */
   void melt(FTSNode a)
   {
      if (a.melted.size() > melted.size())
      {
         Set t = melted;
         melted = a.melted;
         a.melted = t;
      }
      else if (melted == emptySet)
      {
         melted = new HashSet(a.melted.size() + 1);
      }

      melted.add(a);
      melted.addAll(a.melted);
      a.melted = emptySet;

      assert emptySet.size() == 0;

      for (Iterator iterator = a.afferent.iterator(); iterator.hasNext();)
      {
         FTSNode n = (FTSNode)iterator.next();
         n.efferent.remove(a);
         if (n != this)
            n.dependUpon(this);
      }
      a.afferent = emptySet;

      for (Iterator iterator = a.efferent.iterator(); iterator.hasNext();)
      {
         FTSNode n = (FTSNode)iterator.next();
         n.afferent.remove(a);
         if (this != n)
            dependUpon(n);
      }
      a.efferent = emptySet;
   }

   public NodeIf getOrigin()
   {
      return origin;
   }

   public Set getEfferent()
   {
      return efferent;
   }

   public Set getAfferent()
   {
      return afferent;
   }

   public Set getMelted()
   {
      return melted;
   }

   public int getSize()
   {
      return melted.size() + 1;
   }

   public int getAfferentSize()
   {
      return afferent.size();
   }


    @Override
    public boolean equals( Object o ) {
        if( this == o ) return true;
        if( !( o instanceof FTSNode ) ) return false;

        FTSNode ftsNode = (FTSNode) o;

        if( origin != null ? !origin.equals( ftsNode.origin ) : ftsNode.origin != null ) return false;

        return true;
    }

    public final int hashCode()
   {
      return origin.hashCode();
   }

   public FTSNode getLastVisited()
   {
      return lastVisited;
   }

   public void setLastVisited(FTSNode lastVisited)
   {
      this.lastVisited = lastVisited;
   }

   public void addToDependenciesCounter(int i)
   {
      ccd += i;
   }

   public int getCcd()
   {
      return ccd;
   }
}
