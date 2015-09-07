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
package com.valtech.source.dependometer.app.configprovider.filebased.xml;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.valtech.source.dependometer.app.core.provider.CompilationUnitFilterIf;
import com.valtech.source.dependometer.app.core.provider.ListenerIf;
import com.valtech.source.dependometer.app.core.provider.PackageFilterIf;
import com.valtech.source.dependometer.app.core.provider.RefactoringIf;
import com.valtech.source.dependometer.app.core.provider.RegexprDirectedTypeDependencyIf;
import com.valtech.source.dependometer.app.core.provider.SkipExternalIf;
import com.valtech.source.dependometer.app.core.provider.SubsystemFilterIf;
import com.valtech.source.dependometer.app.core.provider.ThresholdDefinitionIf;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public class ProjectNode extends Node
{
   private static final String DOT = ".";

   private static Logger s_Logger = Logger.getLogger(ProjectNode.class.getName());

   private File m_Source;

   private Charset fileEncoding;

   private final Set<File> m_InputDirectories = new HashSet<File>();

   private final Set<File> m_systemIncludeDirectories = new HashSet<File>();

   private final Map<String, String> m_systemDefines = new HashMap<String, String>();

   private final List<ListenerNode> m_ListenerNodes = new ArrayList<ListenerNode>();

   private final Set<RegexprSkipExternal> m_Skip = new HashSet<RegexprSkipExternal>();

   private final Set<RefactoringIf> m_Refactorings = new HashSet<RefactoringIf>();

   private final Set<RegexprDirectedTypeDependency> m_Ignore = new HashSet<RegexprDirectedTypeDependency>();

   private final RegexprPackageFilter m_PackageFilter = new RegexprPackageFilter();

   private final RegexprCompilationUnitFilter m_CompilationUnitFilter = new RegexprCompilationUnitFilter();

   private final RegexprSubsystemFilter m_SubsystemFilter = new RegexprSubsystemFilter();

   private final Set<ThresholdNode> m_UpperThresholds = new HashSet<ThresholdNode>();

   private final Set<ThresholdNode> m_LowerThresholds = new HashSet<ThresholdNode>();

   private String m_ProjectName;

   private boolean m_CheckLayerDependencies = true;

   private boolean m_CheckVerticalSliceDependencies = true;

   private boolean m_CheckSubsystemDependencies = true;

   private boolean m_CheckPackageDependencies = true;

   private boolean m_CumulateLayerDependencies = true;

   private boolean m_CumulateVerticalSliceDependencies = true;

   private boolean m_CumulateSubsystemDependencies = true;

   private boolean m_CumulatePackageDependencies = true;

   private boolean m_CumulateCompilationUnitDependencies = true;

   private boolean m_CumulateTypeDependencies = true;

   private boolean m_IgnorePhysicalStructure = false;

   private String m_AssertionPattern;

   private int m_CycleFeedback = 1;

   private int m_CycleAnalysisProgressFeedback = 1;

   private int m_MaxLayerCycles = 0;

   private int m_MaxVerticalSliceCycles = 0;

   private int m_MaxSubsystemCycles = 0;

   private int m_MaxPackageCycles = 0;

   private int m_MaxCompilationUnitCycles = 0;

   private int m_MaxTypeCycles = 0;

   private boolean m_VerticalSlicesNodeIsPresent;

   private long m_timeoutMinutes = 360;

   protected ProjectNode()
   {
      // Just to make the ctor protected
   }

   final void setSourceFile(File source)
   {
      assert source != null;
      assert source.length() > 0;
      assert m_Source == null;
      m_Source = source;
      getLogger().debug("ProjectNode uses xml source - " + m_Source);
   }

   public final File getSource()
   {
      assert m_Source != null;
      return m_Source;
   }

   final void setProjectName(String name)
   {
      assert name != null;
      assert name.length() > 0;
      m_ProjectName = name;
      getLogger().debug("Project name set - " + m_ProjectName);
   }

   final public String getProjectName()
   {
      return m_ProjectName;
   }

   final public boolean ignorePhysicalStructure()
   {
      return m_IgnorePhysicalStructure;
   }

   final public int getCycleFeedback()
   {
      return m_CycleFeedback;
   }

   final public int getMaxLayerCycles()
   {
      return m_MaxLayerCycles;
   }

   final public int getMaxVerticalSliceCycles()
   {
      return m_MaxVerticalSliceCycles;
   }

   final public int getMaxSubsystemCycles()
   {
      return m_MaxSubsystemCycles;
   }

   final public int getMaxPackageCycles()
   {
      return m_MaxPackageCycles;
   }

   final public int getMaxCompilationUnitCycles()
   {
      return m_MaxCompilationUnitCycles;
   }

   final public int getMaxTypeCycles()
   {
      return m_MaxTypeCycles;
   }

   final void processInputNode(String dir) throws IOException, IllegalArgumentException
   {
      File resolvedDir = Node.getPath(dir);
      if (resolvedDir.isDirectory())
      {
         if (!m_InputDirectories.add(resolvedDir))
         {
            throw new IllegalArgumentException("Directory '" + resolvedDir + "' already added - Location '"
               + Node.getLocationInfoProvider().getInfo() + "'");
         }
      }
      else
      {
         throw new IllegalArgumentException(resolvedDir + " is not a directory - Location '"
            + Node.getLocationInfoProvider().getInfo() + "'");
      }

      getLogger().debug("Input directory added - " + resolvedDir);
   }

   final void processSystemIncludePathNode(String dir) throws IOException, IllegalArgumentException
   {
      File resolvedDir = Node.getPath(dir);
      if (resolvedDir.isDirectory())
      {
         if (!m_systemIncludeDirectories.add(resolvedDir))
         {
            throw new IllegalArgumentException("Directory '" + resolvedDir + "' already added - Location '"
               + Node.getLocationInfoProvider().getInfo() + "'");
         }
      }
      else
      {
         throw new IllegalArgumentException(resolvedDir + " is not a directory - Location '"
            + Node.getLocationInfoProvider().getInfo() + "'");
      }

      getLogger().debug("System include directory added - " + resolvedDir);
   }

   final void processSystemDefineNode(String key, String value) throws IOException, IllegalArgumentException
   {
      assert key != null;
      assert key.length() > 0;
      assert value != null;

      if (m_systemDefines.containsKey(key))
         throw new IllegalArgumentException("System define '" + key + "' already added - Location '"
            + Node.getLocationInfoProvider().getInfo() + "'");

      m_systemDefines.put(key, value);

      getLogger().debug("System define - " + key + " -> " + value);
   }

   final public File[] getInputDirectories()
   {
      return m_InputDirectories.toArray(new File[0]);
   }

   final public File[] getSystemIncludeDirectories()
   {
      return m_systemIncludeDirectories.toArray(new File[0]);
   }

   final public Map<String, String> getSystemDefines()
   {
      return m_systemDefines;
   }

   final void processIncludePackageNode(String pattern)
   {
      assert pattern != null;
      assert pattern.length() > 0;

      if (m_PackageFilter.patternAlreadyAdded(pattern))
      {
         throw new IllegalArgumentException("Duplicate exclude package entry '" + pattern + "' - "
            + Node.getLocationInfoProvider().getInfo());
      }

      m_PackageFilter.include(pattern);
      getLogger().debug("Include package pattern for project added - " + pattern);
   }

   final void processExcludePackageNode(String pattern)
   {
      assert pattern != null;
      assert pattern.length() > 0;

      if (m_PackageFilter.patternAlreadyAdded(pattern))
      {
         throw new IllegalArgumentException("Duplicate exclude package entry '" + pattern + "' - "
            + Node.getLocationInfoProvider().getInfo());
      }

      m_PackageFilter.exclude(pattern);
      getLogger().debug("Exclude package pattern for project added - " + pattern);
   }

   final public void processExcludeCompilationUnitNode(String pattern)
   {
      assert pattern != null;
      assert pattern.length() > 0;

      if (m_CompilationUnitFilter.patternAlreadyAdded(pattern))
      {
         throw new IllegalArgumentException("Duplicate exclude compilation unit entry '" + pattern + "' - "
            + Node.getLocationInfoProvider().getInfo());
      }

      m_CompilationUnitFilter.addExcludePattern(pattern);
      getLogger().debug("Exclude compilation unit pattern for project added - " + pattern);
   }

   final public PackageFilterIf getPackageFilter()
   {
      return m_PackageFilter;
   }

   final public CompilationUnitFilterIf getCompilationUnitFilter()
   {
      return m_CompilationUnitFilter;
   }

   final void processListenerNode(String className, String argument)
   {
      ListenerNode listener = new ListenerNode(className, argument);
      if (m_ListenerNodes.contains(listener))
      {
         throw new IllegalArgumentException("Duplicate listener definition added - "
            + Node.getLocationInfoProvider().getInfo());
      }

      m_ListenerNodes.add(new ListenerNode(className, argument));
   }

   final LayerNode processLayerNode(String name)
   {
      LayerNode node = new LayerNode(name);
      getLogger().debug("Layer added - " + node);
      return node;
   }

   final void processRefactoringNode(String compilationUnit, String toPackage)
   {
      RefactoringIf refactoring = new RegexprRefactoring(compilationUnit, toPackage);
      if (toPackage.endsWith(DOT))
      {
         throw new IllegalArgumentException("toPackage value must not end with a dot"
            + Node.getLocationInfoProvider().getInfo());
      }
      if (!m_Refactorings.add(refactoring))
      {
         throw new IllegalArgumentException("Duplicate refactoring added - " + Node.getLocationInfoProvider().getInfo());
      }

      getLogger().debug("Refactoring added - " + refactoring);
   }

   final public RefactoringIf[] getRefactorings()
   {
      return m_Refactorings.toArray(new RefactoringIf[0]);
   }

   final void processSkipNode(String pattern)
   {
      RegexprSkipExternal skipExternal = new RegexprSkipExternal(pattern);
      if (!m_Skip.add(skipExternal))
      {
         throw new IllegalArgumentException("Duplicate skip entry '" + pattern + "' - "
            + Node.getLocationInfoProvider().getInfo());
      }
   }

   final public SkipExternalIf[] getSkipPatterns()
   {
      return m_Skip.toArray(new SkipExternalIf[0]);
   }

   final void processAssertionNode(String pattern)
   {
      assert pattern != null;
      assert pattern.length() > 0;
      m_AssertionPattern = pattern;
   }

   final public String getAssertionPattern()
   {
      return m_AssertionPattern;
   }

   final void processIgnoreNode(String from, String to)
   {
      RegexprDirectedTypeDependency ignore = new RegexprDirectedTypeDependency(from, to);
      if (!m_Ignore.add(ignore))
      {
         throw new IllegalArgumentException("Duplicate ignore dependency added - "
            + Node.getLocationInfoProvider().getInfo());
      }

      getLogger().debug("Ignore added - " + ignore);
   }

   final public RegexprDirectedTypeDependencyIf[] getIgnore()
   {
      return m_Ignore.toArray(new RegexprDirectedTypeDependencyIf[0]);
   }

   final void processLowerThresholdNode(String id, String value)
   {
      ThresholdNode node = new ThresholdNode(id, value);
      if (!m_LowerThresholds.add(node))
      {
         throw new IllegalArgumentException("Lower threshold for '" + id + "' already added - "
            + Node.getLocationInfoProvider().getInfo());
      }

      getLogger().debug("Lower threshold added - " + node);
   }

   final public ThresholdDefinitionIf[] getLowerThresholds()
   {
      return m_LowerThresholds.toArray(new ThresholdDefinitionIf[0]);
   }

   final void processUpperThresholdNode(String id, String value)
   {
      ThresholdNode node = new ThresholdNode(id, value);
      if (!m_UpperThresholds.add(node))
      {
         throw new IllegalArgumentException("Upper threshold for '" + id + "' already added - "
            + Node.getLocationInfoProvider().getInfo());
      }

      getLogger().debug("Upper threshold added - " + node);
   }

   final public ThresholdDefinitionIf[] getUpperThresholds()
   {
      return m_UpperThresholds.toArray(new ThresholdDefinitionIf[0]);
   }

   final public String[] getLayers()
   {
      return LayerNode.getLayerNames();
   }

   final public String[] layerDependsUpon(String layer)
   {
      return LogicalElementNode.getLogicalElementNode(layer).dependsUpon();
   }

   final public String getLayerDescription(String layer)
   {
      return LogicalElementNode.getLogicalElementNode(layer).getDescription();
   }

   public String getSubsystemDescription(String subsystem)
   {
      return LogicalElementNode.getLogicalElementNode(subsystem).getDescription();
   }

   public String[] getSubsystems()
   {
      return SubsystemNode.getSubsystemNames();
   }

   public String[] subsystemDependsUpon(String subsystem)
   {
      return LogicalElementNode.getLogicalElementNode(subsystem).dependsUpon();
   }

   public PackageFilterIf getSubsystemPackageFilter(String subsystem)
   {
      return ((SubsystemNode)LogicalElementNode.getLogicalElementNode(subsystem)).getPackageFilter();
   }

   final public ListenerIf[] getListenerNodes()
   {
      return m_ListenerNodes.toArray(new ListenerIf[0]);
   }

   final public void processVerticalSlicesExcludeNode(String exclude)
   {
      assert exclude != null;
      assert exclude.length() > 0;
      if (m_SubsystemFilter.patternAlreadyAdded(exclude))
      {
         throw new IllegalArgumentException("Duplicate exclude subsystem entry '" + exclude + "' - "
            + Node.getLocationInfoProvider().getInfo());
      }

      m_SubsystemFilter.exclude(exclude);
      getLogger().debug("Exclude subsystem pattern for vertical slice analysis added - " + exclude);
   }

   final void setVerticalSlicesNodePresent()
   {
      m_VerticalSlicesNodeIsPresent = true;
   }

   final public boolean isVerticalSlicesNodePresent()
   {
      return m_VerticalSlicesNodeIsPresent;
   }

   final public SubsystemFilterIf getSubsystemFilter()
   {
      return m_SubsystemFilter;
   }

   final void setMaxCompilationUnitCycles(String value)
   {
      assert value != null;
      assert value.length() > 0;
      m_MaxCompilationUnitCycles = Integer.parseInt(value);
      if (m_MaxCompilationUnitCycles < -2)
      {
         s_Logger.warn("Compilation unit cycle setting '" + m_MaxCompilationUnitCycles
            + "' not supported - using '-2' (no analysis)");
         m_MaxCompilationUnitCycles = -2;
      }
   }

   final void setMaxLayerCycles(String value)
   {
      assert value != null;
      assert value.length() > 0;
      m_MaxLayerCycles = Integer.parseInt(value);
      if (m_MaxLayerCycles < -2)
      {
         s_Logger.warn("Layer cycle setting '" + m_MaxLayerCycles + "' not supported - using '-2' (no analysis)");
         m_MaxLayerCycles = -2;
      }
   }

   final void setMaxPackageCycles(String value)
   {
      assert value != null;
      assert value.length() > 0;
      m_MaxPackageCycles = Integer.parseInt(value);
      if (m_MaxPackageCycles < -2)
      {
         s_Logger.warn("Package cycle setting '" + m_MaxPackageCycles + "' not supported - using '-2' (no analysis)");
         m_MaxPackageCycles = -2;
      }
   }

   final void setMaxSubsystemCycles(String value)
   {
      assert value != null;
      assert value.length() > 0;
      m_MaxSubsystemCycles = Integer.parseInt(value);
      if (m_MaxSubsystemCycles < -2)
      {
         s_Logger.warn("Subsystem cycle setting '" + m_MaxSubsystemCycles
            + "' not supported - using '-2' (no analysis)");
         m_MaxSubsystemCycles = -2;
      }
   }

   final void setMaxTypeCycles(String value)
   {
      assert value != null;
      assert value.length() > 0;
      m_MaxTypeCycles = Integer.parseInt(value);
      if (m_MaxTypeCycles < -2)
      {
         s_Logger.warn("Type cycle setting '" + m_MaxTypeCycles + "' not supported - using '-2' (no analysis)");
         m_MaxTypeCycles = -2;
      }
   }

   final void setMaxVerticalSliceCycles(String value)
   {
      assert value != null;
      m_MaxVerticalSliceCycles = Integer.parseInt(value);
      if (m_MaxVerticalSliceCycles < -2)
      {
         s_Logger.warn("Vertical slice cycle setting '" + m_MaxVerticalSliceCycles
            + "' not supported - using '-2' (no analysis)");
         m_MaxVerticalSliceCycles = -2;
      }
   }

   final void setCycleFeedback(String value)
   {
      assert value != null;
      m_CycleFeedback = Integer.parseInt(value);
      if (m_CycleFeedback < 0)
      {
         s_Logger.warn("Cycle feedback setting '" + m_CycleFeedback + "' not supported - using '0' (no feedback)");
         m_CycleFeedback = 0;
      }
   }

   public void setCycleAnalysisProgressFeedback(String value)
   {
      assert value != null;
      m_CycleAnalysisProgressFeedback = Integer.parseInt(value);
      if (m_CycleAnalysisProgressFeedback < 0)
      {
         s_Logger.warn("Cycle analysis progress feedback setting '" + m_CycleAnalysisProgressFeedback
            + "' not supported - using '0' (no feedback)");
         m_CycleAnalysisProgressFeedback = 0;
      }
   }

   public void setIgnorePhysicalStructure(String value)
   {
      assert value != null;
      m_IgnorePhysicalStructure = Boolean.valueOf(value).booleanValue();
   }

   final public boolean checkLayerDependencies()
   {
      return m_CheckLayerDependencies;
   }

   final public boolean checkPackageDependencies()
   {
      return m_CheckPackageDependencies;
   }

   final public boolean checkSubsystemDependencies()
   {
      return m_CheckSubsystemDependencies;
   }

   final public boolean checkVerticalSliceDependencies()
   {
      return m_CheckVerticalSliceDependencies;
   }

   final void setCheckLayerDependencies(String value)
   {
      assert value != null;
      m_CheckLayerDependencies = Boolean.valueOf(value).booleanValue();
   }

   final void setCheckPackageDependencies(String value)
   {
      assert value != null;
      m_CheckPackageDependencies = Boolean.valueOf(value).booleanValue();
   }

   final void setCheckSubsystemDependencies(String value)
   {
      assert value != null;
      m_CheckSubsystemDependencies = Boolean.valueOf(value).booleanValue();
   }

   final void setCheckVerticalSliceDependencies(String value)
   {
      assert value != null;
      m_CheckVerticalSliceDependencies = Boolean.valueOf(value).booleanValue();
   }

   final void setCumulateCompilationUnitDependencies(String value)
   {
      assert value != null;
      m_CumulateCompilationUnitDependencies = Boolean.valueOf(value).booleanValue();
   }

   final void setCumulateLayerDependencies(String value)
   {
      assert value != null;
      m_CumulateLayerDependencies = Boolean.valueOf(value).booleanValue();
   }

   final void setCumulatePackageDependencies(String value)
   {
      assert value != null;
      m_CumulatePackageDependencies = Boolean.valueOf(value).booleanValue();
   }

   final void setCumulateSubsystemDependencies(String value)
   {
      assert value != null;
      m_CumulateSubsystemDependencies = Boolean.valueOf(value).booleanValue();
   }

   final void setCumulateTypeDependencies(String value)
   {
      assert value != null;
      m_CumulateTypeDependencies = Boolean.valueOf(value).booleanValue();
   }

   final void setCumulateVerticalSliceDependencies(String value)
   {
      assert value != null;
      m_CumulateVerticalSliceDependencies = Boolean.valueOf(value).booleanValue();
   }

   final public boolean cumulateCompilationUnitDependencies()
   {
      return m_CumulateCompilationUnitDependencies;
   }

   final public boolean cumulateLayerDependencies()
   {
      return m_CumulateLayerDependencies;
   }

   final public boolean cumulatePackageDependencies()
   {
      return m_CumulatePackageDependencies;
   }

   final public boolean cumulateSubsystemDependencies()
   {
      return m_CumulateSubsystemDependencies;
   }

   final public boolean cumulateTypeDependencies()
   {
      return m_CumulateTypeDependencies;
   }

   final public boolean cumulateVerticalSliceDependencies()
   {
      return m_CumulateVerticalSliceDependencies;
   }

   public int getCycleAnalysisProgressFeedback()
   {
      return m_CycleAnalysisProgressFeedback;
   }

   public Charset getFileEncoding()
   {
      return fileEncoding;
   }

   public void setFileEncoding(Charset charset)
   {
      fileEncoding = charset;
   }

   public long getTimeoutMinutes() {
	   return m_timeoutMinutes ;
	}

 
   public void setTimeoutMinutes(String value) {
	   
	   assert value != null;
	   assert value.length() > 0;
	   m_timeoutMinutes = Long.parseLong(value);
	   if (m_timeoutMinutes < 1)
	   {
		  m_timeoutMinutes = 720L;
	      s_Logger.warn("Only posititve Values for Timeout Minutes supported, setting to " + m_timeoutMinutes);
	   }
   }
}