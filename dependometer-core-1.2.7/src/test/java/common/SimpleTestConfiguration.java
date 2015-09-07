package common;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

import com.valtech.source.dependometer.app.configprovider.filebased.xml.ListenerNode;
import com.valtech.source.dependometer.app.configprovider.filebased.xml.RegexprRefactoring;
import com.valtech.source.dependometer.app.core.provider.CompilationUnitFilterIf;
import com.valtech.source.dependometer.app.core.provider.ConfigurationProviderIf;
import com.valtech.source.dependometer.app.core.provider.ListenerIf;
import com.valtech.source.dependometer.app.core.provider.PackageFilterIf;
import com.valtech.source.dependometer.app.core.provider.RefactoringIf;
import com.valtech.source.dependometer.app.core.provider.RegexprDirectedTypeDependencyIf;
import com.valtech.source.dependometer.app.core.provider.SkipExternalIf;
import com.valtech.source.dependometer.app.core.provider.SubsystemFilterIf;
import com.valtech.source.dependometer.app.core.provider.ThresholdDefinitionIf;

public class SimpleTestConfiguration implements ConfigurationProviderIf
{

   public boolean analyzeVerticalSlices()
   {
      return true;
   }

   public boolean checkLayerDependencies()
   {
      return true;
   }

   public boolean checkPackageDependencies()
   {
      return true;
   }

   public boolean checkSubsystemDependencies()
   {
      return true;
   }

   public boolean checkVerticalSliceDependencies()
   {
      return true;
   }

   public boolean cumulateCompilationUnitDependencies()
   {
      return true;
   }

   public boolean cumulateLayerDependencies()
   {
      return true;
   }

   public boolean cumulatePackageDependencies()
   {
      return true;
   }

   public boolean cumulateSubsystemDependencies()
   {
      return true;
   }

   public boolean cumulateTypeDependencies()
   {
      return true;
   }

   public boolean cumulateVerticalSliceDependencies()
   {
      return true;
   }

   public String getAssertionPattern()
   {
      return null;
   }

   public CompilationUnitFilterIf getCompilationUnitFilter()
   {
      return null;
   }

   public int getCycleAnalysisProgressFeedback()
   {
      return 1;
   }

   public int getCycleFeedback()
   {
      return 1;
   }

   public RegexprDirectedTypeDependencyIf[] getIgnore()
   {
      return new RegexprDirectedTypeDependencyIf[0];
   }

   public File[] getInputDirectories()
   {
      return null;
   }

   public String getLayerDescription(String layer)
   {
      return null;
   }

   public String[] getLayers()
   {
      return new String[0];
   }

   public ListenerIf[] getListeners()
   {
      return new ListenerNode[0];
   }

   public ThresholdDefinitionIf[] getLowerThresholds()
   {
      return new ThresholdDefinitionIf[0];
   }

   public int getMaxCompilationUnitCycles()
   {
      return 106;
   }

   public int getMaxLayerCycles()
   {
      return 105;
   }

   public int getMaxPackageCycles()
   {
      return 104;
   }

   public int getMaxSubsystemCycles()
   {
      return 103;
   }

   public int getMaxTypeCycles()
   {
      return 102;
   }

   public int getMaxVerticalSliceCycles()
   {
      return 101;
   }

   public String getPackageDescription(String packageName)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public PackageFilterIf getPackageFilter()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String[] getPackages()
   {
      return new String[0];
   }

   public String getProjectName()
   {
      return "Simple Test Project";
   }

   public RefactoringIf[] getRefactorings()
   {
      return new RegexprRefactoring[0];
   }

   public SkipExternalIf[] getSkipPatterns()
   {
      return new SkipExternalIf[0];
   }

   public Object getSource()
   {
      return "sample source";
   }

   public String getSubsystemDescription(String layer)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public SubsystemFilterIf getSubsystemFilter()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public PackageFilterIf getSubsystemPackageFilter(String subsystem)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String[] getSubsystems()
   {
      return new String[0];
   }

   public Map<String, String> getSystemDefines()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public File[] getSystemIncludeDirectories()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public ThresholdDefinitionIf[] getUpperThresholds()
   {
      return new ThresholdDefinitionIf[0];
   }

   public boolean ignorePhysicalStructure()
   {
      // ignore phys structure in this test config
      return true;
   }

   public String[] layerDependsUpon(String layer)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String[] packageDependsUpon(String packageName)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public boolean readConfiguration(File file) throws IOException
   {
      return true;
   }

   public File resolveRelativePath(String relativePath) throws IOException
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String[] subsystemDependsUpon(String subsystem)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String[] getAdditionalInfo()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public Charset getFileEncoding()
   {
      return null;
   }

   public boolean isCycleAnalysisEnabled()
   {
      return true;
   }

   public long getTimeoutMinutes() {
	   return 15;
   }
}
