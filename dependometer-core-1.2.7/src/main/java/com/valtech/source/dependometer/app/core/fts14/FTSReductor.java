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
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.valtech.source.dependometer.app.core.dependencyanalysis.DependencyAnalysisIf;
import com.valtech.source.dependometer.app.core.dependencyanalysis.DependencyAnalysisListenerIf;
import com.valtech.source.dependometer.app.core.dependencyanalysis.DirectedNodeDependencyIf;
import com.valtech.source.dependometer.app.core.dependencyanalysis.NodeIf;
import com.valtech.source.dependometer.app.core.elements.DependencyElement;

/**
 * Feed information about cycles to the output writers.
 * 
 * @author Bernhard Ruemenapp
 */
public class FTSReductor implements DependencyAnalysisIf
{
   private static Logger s_Logger = Logger.getLogger(FTSReductor.class.getName());

   private final DependencyAnalysisListenerIf m_Listener;

   private List m_Sorted;

   private Set m_CycleTellers = new LinkedHashSet();

   private List m_RestTellers = new ArrayList();

   private Set m_Participants = new HashSet();

   private int m_CollectedCycles = 0;

   private int m_CollectedTangles = 0;

   private int m_Size = 0; // for progress display

   private Map m_CycleParticipations = new HashMap();

   private boolean m_HasCycles;

   public FTSReductor(NodeIf[] nodes, DependencyAnalysisListenerIf listener)
   {
      s_Logger.debug("creating FTSReductor for " + nodes.length + " nodes");

      m_Listener = listener;

      Map superNodes = new HashMap(nodes.length);

      // create super graph

      for (int i = 0; i < nodes.length; i++)
      {
         NodeIf n = nodes[i];
         superNodes.put(n, new FTSNode(n));
      }

      addDependencies(nodes, superNodes);

      // topologically sort the super graph

      m_Sorted = superTSort(superNodes.values());

      // repair cut super node relationships

      for (Iterator it = m_Sorted.iterator(); it.hasNext();)
      {
         FTSNode n = (FTSNode)it.next();
         assert n == (superNodes.get(n.getOrigin()));
         for (Iterator itm = n.getMelted().iterator(); itm.hasNext();)
         {
            FTSNode m = (FTSNode)itm.next();
            superNodes.put(m.getOrigin(), n);
         }
      }

      addDependencies(nodes, superNodes);
   }

   private void addDependencies(NodeIf[] nodes, Map superNodes)
   {
      for (int i = 0; i < nodes.length; i++)
      {
         NodeIf n = nodes[i];
         NodeIf[] efferentNodes = n.getEfferentNodes();
         for (int j = 0; j < efferentNodes.length; j++)
         {
            NodeIf e = efferentNodes[j];
            ((FTSNode)superNodes.get(n)).dependUpon((FTSNode)superNodes.get(e));
         }
      }
   }

   /**
    * Create a topological sorted super graph. Each node of the super graph refers to either a single node or to a
    * tangle (largest set of cyclically depended nodes) in the unchanged original graph.
    * 
    * @param graph
    * @return super graph
    */
   private List superTSort(Collection graph)
   {
      Set rest = new HashSet(graph);

      // Collect nodes with no predecessor
      Set roots = new HashSet();
      for (Iterator iterator = rest.iterator(); iterator.hasNext();)
      {
         FTSNode node = (FTSNode)iterator.next();
         if (node.getAfferentSize() == 0)
         {
            roots.add(node);
         }
      }

      List topologicalSorted = new ArrayList();

      while (true)
      {
         while (roots.size() > 0)
         {
            FTSNode[] currentRoots = (FTSNode[])roots.toArray(new FTSNode[roots.size()]);
            roots.clear();

            for (int i = 0; i < currentRoots.length; i++)
            {
               FTSNode n = currentRoots[i];

               assert n.getAfferentSize() == 0;

               for (Iterator iterator = n.getEfferent().iterator(); iterator.hasNext();)
               {
                  FTSNode en = (FTSNode)iterator.next();
                  en.getAfferent().remove(n);
                  if (en.getAfferentSize() == 0)
                  {
                     roots.add(en);
                  }
               }
               rest.remove(n);

               topologicalSorted.add(n);
            }
         }

         if (rest.size() == 0)
            return topologicalSorted; // finished

         m_HasCycles = true;

         Set removed = new HashSet();

         // melt nodes of cycles
         while (roots.size() == 0)
         {
            FTSNode quickPointer = (FTSNode)rest.iterator().next();

            List list = new LinkedList();
            boolean listFull = false;
            for (int i = 0; roots.size() == 0; i++)
            {
               list.add(quickPointer);
               if (i % 3 == 2 || listFull)
               {
                  list.remove(0);
               }
               else
               {
                  // linear walk backwards until a cycle is detected
                  quickPointer = (FTSNode)quickPointer.getAfferent().iterator().next();
                  listFull = list.size() > rest.size();
               }

               if (list.size() > 1 && list.get(0).equals(quickPointer))
               {
                  // the slow moving end of the list is the same
                  // as the fast moving end, so:
                  // the list is a cycle!
                  for (Iterator iterator = list.iterator(); iterator.hasNext();)
                  {
                     FTSNode n = (FTSNode)iterator.next();
                     if (n != quickPointer && n.getAfferentSize() > 0)
                     {
                        quickPointer.melt(n);

                        removed.add(n);
                        roots.remove(n);

                        if (quickPointer.getAfferentSize() == 0)
                        {
                           // when we reach here,
                           // the whole cycle has been melted
                           roots.add(quickPointer);
                        }
                     }
                  }
                  list.clear();
                  listFull = false;
               }
            }
         }

         assert removed.size() > 0;
         assert roots.size() > 0;

         for (Iterator iterator = removed.iterator(); iterator.hasNext();)
         {
            FTSNode n = (FTSNode)iterator.next();
            assert rest.contains(n);
            rest.remove(n);
         }
      }
   }

   /**
    * Collect some cycles and report progress.
    * 
    * @return true if there a still cycles to collect.
    */
   private boolean collectCycles(int maxCycles)
   {
      int cycles = 0;
      Collection removed = new ArrayList();

      int oldSize = m_Participants.size();
      for (Iterator iterator = m_CycleTellers.iterator(); iterator.hasNext();)
      {
         FTSCycleTeller teller = (FTSCycleTeller)iterator.next();
         DependencyElement[] tangle = teller.getTangle();

         m_Listener.tangleCollected(tangle);
         m_CollectedTangles++;

         while (cycles < maxCycles)
         {
            Collection cycle = teller.getCycle();

            if (cycle == null)
            {
               removed.add(teller);
               if (teller.getRest().size() > 0)
               {
                  m_RestTellers.add(teller);
               }
               break;
            }

            m_Listener.cycleCollected((NodeIf[])cycle.toArray(m_Listener.allocateNodes(cycle.size())));

            for (Iterator iterator2 = cycle.iterator(); iterator2.hasNext();)
            {
               NodeIf participant = (NodeIf)iterator2.next();
               m_Participants.add(participant);
            }

            cycles++;
            m_CollectedCycles++;

            NodeIf[] nodes = (NodeIf[])cycle.toArray(m_Listener.allocateNodes(cycle.size()));
            for (int i = 0; i < nodes.length; i++)
            {
               incrementCycleParticipation(nodes[i], nodes[(i + 1) % nodes.length]);
            }
         }
      }

      m_CycleTellers.removeAll(removed);

      if (m_Participants.size() > oldSize)
      {
         m_Listener.progressInfo(m_Size, m_Size - m_Participants.size());
      }

      return m_CycleTellers.size() > 0;
   }

   /**
    * @see com.valtech.source.dependometer.app.core.dependencyanalysis.DependencyAnalysisIf#analyzeCyclePartizipation()
    */
   public void analyzeCyclePartizipation()
   {

      List rest = new ArrayList();

      for (Iterator iterator = m_CycleTellers.iterator(); iterator.hasNext();)
      {
         FTSCycleTeller teller = (FTSCycleTeller)iterator.next();
         rest.addAll(teller.getRest());
      }

      for (Iterator iterator = m_RestTellers.iterator(); iterator.hasNext();)
      {
         FTSCycleTeller teller = (FTSCycleTeller)iterator.next();
         rest.addAll(teller.getRest());
      }

      m_Listener.cycleParticipantsCollected((NodeIf[])m_Participants.toArray(m_Listener.allocateNodes(m_Participants
         .size())), (NodeIf[])rest.toArray(m_Listener.allocateNodes(rest.size())));

      Map countToCycleElements = new TreeMap();

      for (Iterator iterator2 = m_CycleParticipations.values().iterator(); iterator2.hasNext();)
      {
         DirectedNodeDependencyIf nextDependency = (DirectedNodeDependencyIf)iterator2.next();

         Integer countAsInteger = new Integer(nextDependency.getCycleParticipation());
         List cycleElements = (List)countToCycleElements.get(countAsInteger);
         if (cycleElements == null)
         {
            cycleElements = new ArrayList();
            countToCycleElements.put(countAsInteger, cycleElements);
         }
         cycleElements.add(nextDependency);
      }

      Integer[] counters = (Integer[])countToCycleElements.keySet().toArray(new Integer[0]);
      for (int i = counters.length - 1; i >= 0; i--)
      {
         Integer nextCounter = counters[i];
         DirectedNodeDependencyIf[] cycleElements = (DirectedNodeDependencyIf[])((Collection)countToCycleElements
            .get(nextCounter)).toArray(m_Listener.allocateDirectedDependencies(0));
         m_Listener.cycleParticipationCollected(nextCounter.intValue(), cycleElements);
      }
   }

   /**
    * @see com.valtech.source.dependometer.app.core.dependencyanalysis.DependencyAnalysisIf#analyzeCycles(int)
    */
   public void analyzeCycles(int maxCycles, Date timeStop)
   {
      maxCycles = maxCycles > -1 ? maxCycles : Integer.MAX_VALUE;

      int all = 0;
      for (Iterator iterator = m_Sorted.iterator(); iterator.hasNext();)
      {
         FTSNode n = (FTSNode)iterator.next();
         all += n.getSize();
      }

      int done = 0;
      for (Iterator iterator = m_Sorted.iterator(); iterator.hasNext();)
      {
         FTSNode n = (FTSNode)iterator.next();
         done += n.getSize();

         if (n.getSize() > 1)
         {
            List tangle = new ArrayList(n.getSize());
            tangle.add(n.getOrigin());
            for (Iterator iterator2 = n.getMelted().iterator(); iterator2.hasNext();)
            {
               FTSNode m = (FTSNode)iterator2.next();
               tangle.add(m.getOrigin());
            }

            m_Size += tangle.size();
            m_CycleTellers.add(new FTSCycleTeller(tangle, maxCycles, timeStop));
         }
      }

      // Sort to print the largest cycle first

      FTSCycleTeller[] array = new FTSCycleTeller[m_CycleTellers.size()];
      array = (FTSCycleTeller[])m_CycleTellers.toArray(array);
      Arrays.sort(array, new Comparator()
      {
         public int compare(Object a, Object b)
         {
            FTSCycleTeller aa = (FTSCycleTeller)a, bb = (FTSCycleTeller)b;

            return bb.size() - aa.size(); // descending
         }
      });

      m_CycleTellers.clear();
      m_CycleTellers.addAll(Arrays.asList(array));

      while (m_CollectedCycles < maxCycles)
      {
         if (!collectCycles(maxCycles - m_CollectedCycles))
            break;
      }
   }

   /**
    * @see com.valtech.source.dependometer.app.core.dependencyanalysis.DependencyAnalysisIf#analyzeLevels()
    */
   public void analyzeLevels()
   {
      FTSNode[] array = (FTSNode[])m_Sorted.toArray(new FTSNode[m_Sorted.size()]);

      int level = 1;
      Set currentLevel = new HashSet();
      backwards: for (int i = array.length - 1; i >= 0; i--)
      {
         FTSNode n = array[i];

         for (Iterator iterator = n.getEfferent().iterator(); iterator.hasNext();)
         {
            FTSNode e = (FTSNode)iterator.next();

            if (currentLevel.contains(e))
            {
               finishLevel(level, currentLevel);
               currentLevel.clear();
               level++;

               currentLevel.add(n);

               if (i == 0)
               {
                  finishLevel(level, currentLevel);
               }

               continue backwards;
            }
         }

         currentLevel.add(n);

         if (i == 0)
         {
            finishLevel(level, currentLevel);
         }
      }
   }

   /**
    * Report a level.
    * 
    * @param level current level number
    * @param currentLevel nodes on said level
    */
   private void finishLevel(int level, Set currentLevel)
   {
      List levelNodes = new ArrayList();
      // finish level

      for (Iterator iterator = currentLevel.iterator(); iterator.hasNext();)
      {
         FTSNode nn = (FTSNode)iterator.next();

         if (nn.getMelted().size() == 0)
         {
            levelNodes.add(nn.getOrigin());
         }
      }

      if (levelNodes.size() > 0)
      {
         m_Listener.levelCollected(level, (NodeIf[])levelNodes.toArray(new NodeIf[levelNodes.size()]));
      }
   }

   /**
    * @see com.valtech.source.dependometer.app.core.dependencyanalysis.DependencyAnalysisIf#cumulateNodeDependencies()
    */
   public void cumulateNodeDependencies()
   {
      FTSNode[] array = (FTSNode[])m_Sorted.toArray(new FTSNode[m_Sorted.size()]);

      int ccd_all = 0;

      for (int i = 0; i < array.length; i++)
      {
         FTSNode n = array[i];
         n.addToDependenciesCounter(n.getSize());

         Iterator it = n.getAfferent().iterator();
         while (it.hasNext())
         {
            countDependency((FTSNode)it.next(), n);
         }
      }

      for (int i = 0; i < array.length; i++)
      {
         FTSNode n = array[i];
         int ccd = n.getCcd();
         n.getOrigin().setDependsUpon(ccd);
         for (Iterator iterator = n.getMelted().iterator(); iterator.hasNext();)
         {
            FTSNode m = (FTSNode)iterator.next();
            m.getOrigin().setDependsUpon(ccd);
         }

         ccd_all += ccd;
      }

   }

   private void countDependency(FTSNode a, FTSNode n)
   {
      if (a.getLastVisited() == n)
         return;

      a.setLastVisited(n);
      a.addToDependenciesCounter(n.getSize());

      Iterator it = a.getAfferent().iterator();
      while (it.hasNext())
      {
         countDependency((FTSNode)it.next(), n);
      }
   }

   /**
    * @see com.valtech.source.dependometer.app.core.dependencyanalysis.DependencyAnalysisIf#finishCycleAnalysis()
    */
   public void finishCycleAnalysis()
   {
      // What was here, is now done earlier.
   }

   /**
    * @see com.valtech.source.dependometer.app.core.dependencyanalysis.DependencyAnalysisIf#hasCycles()
    */
   public boolean hasCycles()
   {
      return m_HasCycles;
   }

   /**
    * Count how many cycles share a certain edge.
    * 
    * @param from edge point
    * @param to edge point
    */
   private void incrementCycleParticipation(NodeIf from, NodeIf to)
   {
      FTSDependencyKey key = new FTSDependencyKey(from, to);
      DirectedNodeDependencyIf dependency = (DirectedNodeDependencyIf)m_CycleParticipations.get(key);
      if (dependency == null)
      {
         dependency = m_Listener.createDirectedDependency(from, to);
         m_CycleParticipations.put(key, dependency);
      }

      dependency.incrementCycleParticipation();
   }

   public int getNumberOfDetectedCycles()
   {
      return m_CollectedCycles;
   }

   public int getNumberOfDetectedTangles()
   {
      return m_CollectedTangles;
   }

}
