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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.valtech.source.dependometer.app.core.dependencyanalysis.NodeIf;
import com.valtech.source.dependometer.app.core.elements.DependencyElement;

/**
 * Tell cycles on by one. Heavy computation done here.
 * 
 * @author Bernhard Ruemenapp
 */
public class FTSCycleTeller
{
   private static Logger s_Logger = Logger.getLogger(FTSCycleTeller.class.getName());

   /** aborting limit. */
   private int m_max = -1;

   /** Recently collected cycles. */
   private List m_cycles = new ArrayList(0);

   /** Current tangle. */
   private Set m_subSet;

   /** Partly analyzed rest. */
   private List m_surrendered = new ArrayList();

   /** progress memory */
   private int m_lastSize = -1;
   
   /** global timeout */
   private Date m_timeStop;

   /**
    * Constructor.
    * 
    * @param subSet current graph partition (the tangle)
    * @param maxCycles aborting limit
    */
   FTSCycleTeller(Collection subSet, int maxCycles, Date timeStop)
   {
      m_subSet = new HashSet(subSet);
      m_max = maxCycles > 0 ? maxCycles : 0;
      
      m_tangle = (DependencyElement[])subSet.toArray(new DependencyElement[subSet.size()]);
      Arrays.sort(m_tangle, new Comparator<DependencyElement>() {
         public final int compare(DependencyElement a, DependencyElement b)
         {
            return a.getFullyQualifiedName().compareTo(b.getFullyQualifiedName());
         }
      });
   }

   /**
    * Pick next cycle.
    * 
    * @return null if finished
    */
   Collection getCycle()
   {
      if (m_cycles.size() == 0)
      {
         doCollectCycles();
      }

      if (m_cycles.size() == 0)
      {
         return null;
      }
      else
      {
         return (Collection)m_cycles.remove(m_cycles.size() - 1);
      }
   }

   /**
    * 8-Queens algorithm Rules of the game: Place 8 queens of 8 different colors on a chess board. They must not attack
    * each other. So no two queens share the same row, line or diagonal.
    * 
    * How does this relate to the problem at hand? We walk the graph following the directed edges. At each node, we have
    * to decide which way to go. A queen stands for a each decision. We will not repeat the same decision. This is the
    * "row"-rule. We will not visit a node twice. (Whenever we could reach the starting node again report a cycle.) This
    * is the "line"-rule. We must follow the directed edges. This is a strange variation of the "diagonal"-rule.
    * 
    * When done backtracking remove the starting node from the now smaller subset.
    * 
    * @author Bernhard Ruemenapp
    * 
    */
   private class ChessBoard
   {
      int[][] adjacencyList = new int[m_subSet.size()][];

      NodeIf[] nodes = (NodeIf[])m_subSet.toArray(new NodeIf[m_subSet.size()]);

      Map mapping = new HashMap(nodes.length);

      int cursor[] = new int[nodes.length];

      Set visited = new HashSet();

      final int start = 0;

      int pos = 0;

      int[] n = new int[nodes.length];

      int[] mininalStepsToReachStart;

      public ChessBoard()
      {

         for (int i = 0; i < nodes.length; i++)
         {
            NodeIf n = nodes[i];
            mapping.put(n, new Integer(i));
         }

         assert m_lastSize != m_subSet.size();
         m_lastSize = m_subSet.size();

         for (int i = 0; i < nodes.length; i++)
         {
            NodeIf n = nodes[i];

            ArrayList efferents = new ArrayList();

            NodeIf[] efferentNodes = n.getEfferentNodes();
            for (int j = 0; j < efferentNodes.length; j++)
            {
               NodeIf e = efferentNodes[j];

               if (m_subSet.contains(e))
               {
                  efferents.add(e);
               }
            }

            int row = ((Integer)mapping.get(n)).intValue();
            adjacencyList[row] = new int[efferents.size()];
            int col = 0;
            for (Iterator iterator = efferents.iterator(); iterator.hasNext();)
            {
               NodeIf e = (NodeIf)iterator.next();

               adjacencyList[row][col++] = ((Integer)mapping.get(e)).intValue();
            }
         }

         assert nodes.length >= 2;

         cursor[0] = 0;
         n[0] = start;

         visited.clear();
         visited.add(nodes[start]);

         assert visited.size() == pos + 1;

         mininalStepsToReachStart = computeMinimalStepsToReach(nodes[start]);
      }

      public void step()
      {
         if (m_cycles.size() >= m_max)
         {
            m_subSet.remove(nodes[start]);
            m_surrendered.add(nodes[start]);
            m_loop = null;
            s_Logger.info("Finishing analysis of type: " + nodes[start].toString()
               + " early, because the configured limit of cycles to output was reached.");
            return;
         }

         int current = n[pos]; // current node
         int[] a = adjacencyList[current];

         assert visited.size() == pos + 1;
         assert visited.contains(nodes[current]);

         if (cursor[pos] < a.length)
         {
            if (pos > 0 && start == a[cursor[pos]])
            {
               // cycle detected
               List cycle = new ArrayList(pos + 1);

               for (int j = 0; j <= pos; j++)
               {
                  cycle.add(nodes[n[j]]);
               }

               m_cycles.add(cycle);

               // advance current cursor
               cursor[pos]++;
               return;
            }

            if (cursor[pos] < a.length && pos + 1 < cursor.length)
            {
               int next = a[cursor[pos]];
               if (!visited.contains(nodes[next]) && mininalStepsToReachStart[next] < nodes.length - pos)
               {
                  // go to next row
                  visited.add(nodes[next]);
                  pos++;
                  cursor[pos] = 0;
                  n[pos] = next;
                  return;
               }
            }

            // advance current cursor
            cursor[pos]++;
         }
         else if (pos > 0)
         {
            // advance previous cursor
            visited.remove(nodes[current]);
            pos--;
            cursor[pos]++;
         }
         else
         {
            assert pos == 0;
            assert cursor[pos] >= a.length;

            // done searching a cycles to the start node
            m_loop = null;
            m_subSet.remove(nodes[start]);
            return;
         }
      }

      /**
       * For each node: Compute the minimum number of steps to reach the start node
       * 
       * @param node cycles are searched from and to
       * @return minimal steps array
       */
      private int[] computeMinimalStepsToReach(NodeIf start)
      {
         Set reachers = new HashSet();

         int[] result = new int[nodes.length];
         for (int i = 0; i < result.length; i++)
         {
            result[i] = Integer.MAX_VALUE;
         }

         Set added = new HashSet(1);
         added.add(start);

         int step = 0;
         while (added.size() > 0)
         {
            Set afferents = new HashSet();
            for (Iterator iterator = added.iterator(); iterator.hasNext();)
            {
               NodeIf n = (NodeIf)iterator.next();

               int index = ((Integer)mapping.get(n)).intValue();
               result[index] = step;

               for (int i = 0; i < n.getAfferentNodes().length; i++)
               {
                  NodeIf a = n.getAfferentNodes()[i];

                  if (m_subSet.contains(a) && !reachers.contains(a) && !added.contains(a))
                     afferents.add(a);
               }
            }

            assert step < nodes.length;

            reachers.addAll(added);
            added = afferents;
            step++;
         }

         return result;
      }
   }

   /**
    * Current backtracking state. Null when finished
    */
   private ChessBoard m_loop = null;

   private DependencyElement[] m_tangle;

   /**
    * Collect some cycles and return. Repeatedly call, until no more cycles are found.
    */
   private void doCollectCycles()
   {
      if (m_cycles.size() >= m_max) return;
      
      while (m_subSet.size() >= 2)
      {
         if (m_loop == null)
         {
            m_loop = new ChessBoard();
         }

         for (int i = 1; m_loop != null && i < 1000; i++)
         {
            m_loop.step();
         }

         if (m_cycles.size() > 0)
         {
            return;
         }

         if (m_timeStop != null && new Date().after(m_timeStop)) {
             s_Logger.warn("Finishing analysis early, because the configured timeout was reached.");             
             return;
         }
      }

      assert m_subSet.size() < 2;
      m_subSet.clear();
   }

   /**
    * For reporting partly finished work.
    * 
    * @return nodes not fully analyzed
    */
   Collection getRest()
   {
      Collection result = new ArrayList(m_subSet.size() + m_surrendered.size());
      result.addAll(m_subSet);
      result.addAll(m_surrendered);
      return result;
   }

   /**
    * Current job size.
    * 
    * @return number of nodes that still need analysis
    */
   int size()
   {
      return m_subSet.size();
   }

   DependencyElement[] getTangle()
   {
      return m_tangle;
   }
}
