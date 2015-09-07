package com.valtech.source.dependometer.app.core.metrics;

import java.util.Arrays;
import java.util.Comparator;

import com.valtech.source.dependometer.app.core.provider.QueryInfoIf;

public enum ProjectMetricsEnum implements QueryInfoIf {

   ACD("Project.ACD", "Average component dependency - includes all project internal compilation units"),

   NumberOfProjectExternalLayers("Project.NumberOfProjectExternalLayers", "Number of project external layers"),

   NumberOfProjectInternalLayers("Project.NumberOfProjectInternalLayers", "Number of project internal layers"),

   NumberOfDefinedLayerDependencies("Project.NumberOfDefinedLayerDependencies",
      "Number of all defined dependencies between project internal and external layers"),

   NumberOfProjectInternalSubsystems("Project.NumberOfProjectInternalSubsystems",
      "Number of project internal subsystems"),

   NumberOfProjectExternalSubsystems("Project.NumberOfProjectExternalSubsystems",
      "Number of project external subsystems"),

   NumberOfNotImplementedSubsystems("Project.NumberOfNotImplementedSubsystems",
      "Number of project internal and external subsystems that have no associated packages"),

   NumberOfDefinedSubsystemDependencies("Project.NumberOfDefinedSubsystemDependencies",
      "Number of all defined dependencies between project internal and external subsystems"),

   LayerCyclesExist("Project.LayerCyclesExist", "Indicates that layer cycles exist with a value greater than '0'"),

   VerticalSliceCyclesExist("Project.VerticalSliceCyclesExist",
      "Indicates that vertical slice cycles exist with a value greater than '0'"),

   SubsystemCyclesExist("Project.SubsystemCyclesExist",
      "Indicates that subsystem cycles exist with a value greater than '0'"),

   PercentageOfLayersWithRcNotLessThanOne("Project.PercentageOfLayersWithRcNotLessThanOne",
      "Percentage of layers with a relational cohesion not less than '1.0'"),

   PercentageOfVerticalSlicesWithRcNotLessThanOne("Project.PercentageOfVerticalSlicesWithRcNotLessThanOne",
      "Percentage of vertical slices with a relational cohesion not less than '1.0'"),

   PercentageOfSubsystemsWithRcNotLessThanOne("Project.PercentageOfSubsystemsWithRcNotLessThanOne",
      "Percentage of subsystems with a relational cohesion not less than '1.0'"),

   PercentageOfPackagesWithRcNotLessThanOne("Project.PercentageOfPackagesWithRcNotLessThanOne",
      "Percentage of packages with a relational cohesion not less than '1.0'"),

   PackageCyclesExist("Project.PackageCyclesExist", "Indicates that package cycles exist with a value greater than '0'"),

   NumberOfDefinedPackageDependencies("Project.NumberOfDefinedPackageDependencies",
      "Number of all defined dependencies between project internal and external packages"),

   NumberOfProjectExternalPackages("Project.NumberOfProjectExternalPackages", "Number of project external packages"),

   MaxDepthOfPackageHierarchy("Project.MaxDepthOfPackageHierarchy",
      "Maximal encountered depth of the package hierarchy"),

   NumberOfProjectInternalTypes("Project.NumberOfProjectInternalTypes",
      "Number of project internal types - indicates potential problems with the input filter if its value is '0'"),

   CompilationUnitCyclesExist("Project.CompilationUnitCyclesExist",
      "Indicates that compilation unit cycles exist with a value greater than '0'"),

   TypeCyclesExist("Project.TypeCyclesExist", "Indicates that type cycles exist with a value greater than '0'"),

   NumberOfForbiddenEfferentPackageDependencies("Project.NumberOfForbiddenEfferentPackageDependencies",
      "Total number of forbidden outgoing dependencies between packages - dependencies that break the defined ones"),

   NumberOfNotAssignedPackages("Project.NumberOfNotAssignedPackages",
      "Number of packages that are not assigned to subsystems"),

   MaxDepthOfInheritance("Project.MaxDepthOfInheritance", "Maximal encountered depth of inheritance"),

   AverageNumberOfAssertionsPerProjectInternalClass("Project.AverageNumberOfAssertionsPerProjectInternalClass",
      "Average number of assertions per project internal class - only classes have implementations");

   private final String id;

   private final String description;

   private boolean valueSet;

   private double value;

   private ProjectMetricsEnum(String id, String description)
   {
      assert id != null;
      assert id.length() > 0;
      assert description != null;
      assert description.length() > 0;

      this.id = id;
      this.description = description;
   }

   public static ProjectMetricsEnum getById(String id)
   {
      assert id != null;
      assert id.length() > 0;
      for (ProjectMetricsEnum m : values())
      {
         if (m.id.equals(id))
         {
            return m;
         }
      }
      return null;
   }

   public static boolean exists(String id)
   {
      return getById(id) != null;
   }

   public String getId()
   {
      return id;
   }

   public void setValue(double value)
   {
      assert !valueSet;
      this.value = value;
      this.valueSet = true;
   }

   public double getValue()
   {
      assert valueSet;
      return value;
   }

   public boolean wasValueSet()
   {
      return valueSet;
   }

   public String getDescription()
   {
      return description;
   }

   public static void resetValues()
   {
      for (ProjectMetricsEnum m : values())
      {
         m.valueSet = false;
         m.value = 0.0;
      }
   }

   public static String asString()
   {
      String s = "Project Metrics:\n";

      for (ProjectMetricsEnum m : sortedValues())
      {
         s += "\t" + m.id + ": " + m.value + "\n";
      }
      return s;
   }

   public static ProjectMetricsEnum[] sortedValues()
   {
      ProjectMetricsEnum[] values = values();
      Arrays.sort(values, new Comparator<ProjectMetricsEnum>()
      {
         public int compare(ProjectMetricsEnum o1, ProjectMetricsEnum o2)
         {
            return o1.getId().compareTo(o2.getId());
         }
      });
      return values;
   }
}
