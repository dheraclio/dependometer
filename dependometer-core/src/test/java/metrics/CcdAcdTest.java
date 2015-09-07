package metrics;

import com.valtech.source.dependometer.app.core.common.MetricEnum;
import com.valtech.source.dependometer.app.core.elements.Package;

public class CcdAcdTest extends AbstractMetricsTest
{
   public void testCcdAcdIndependentComponents()
   {
      runDependometerWithTestProject("ccdAcd1IndependentComponents", false);
      assertEquals("2", project.getMetricValue(MetricEnum.CUMULATIVE_COMPONENT_DEPENDENCY ));
      assertEquals("1.0", project.getMetricValue(MetricEnum.AVERAGE_COMPONENT_DEPENDENCY ));

      Package package1 = Package.getPackage("sample1");
      assertEquals("1", package1.getMetricByName(MetricEnum.CUMULATIVE_COMPONENT_DEPENDENCY.getDisplayName())
         .getValueAsString());
      assertEquals("1.0", package1.getMetricByName(MetricEnum.AVERAGE_COMPONENT_DEPENDENCY.getDisplayName())
         .getValueAsString());

      Package package2 = Package.getPackage("sample2");
      assertEquals("1", package2.getMetricByName(MetricEnum.CUMULATIVE_COMPONENT_DEPENDENCY.getDisplayName())
         .getValueAsString());
      assertEquals("1.0", package2.getMetricByName(MetricEnum.AVERAGE_COMPONENT_DEPENDENCY.getDisplayName())
         .getValueAsString());
   }

   public void testCcdAcdDirectlyAndIndirectlyDependentOnComponent()
   {
      runDependometerWithTestProject("ccdAcd2DirectlyAndIndirectlyDependentOnComponent", false);
      assertEquals("11", project.getMetricValue(MetricEnum.CUMULATIVE_COMPONENT_DEPENDENCY ));
      assertEquals("2.2", project.getMetricValue(MetricEnum.AVERAGE_COMPONENT_DEPENDENCY ));

      Package package1 = Package.getPackage("sample1");
      assertEquals("2", package1.getMetricByName(MetricEnum.CUMULATIVE_COMPONENT_DEPENDENCY.getDisplayName())
         .getValueAsString());
      assertEquals("2.0", package1.getMetricByName(MetricEnum.AVERAGE_COMPONENT_DEPENDENCY.getDisplayName())
         .getValueAsString());

      Package package2 = Package.getPackage("sample2");
      assertEquals("1", package2.getMetricByName(MetricEnum.CUMULATIVE_COMPONENT_DEPENDENCY.getDisplayName())
         .getValueAsString());
      assertEquals("1.0", package2.getMetricByName(MetricEnum.AVERAGE_COMPONENT_DEPENDENCY.getDisplayName())
         .getValueAsString());

      Package package3 = Package.getPackage("sample3");
      assertEquals("8", package3.getMetricByName(MetricEnum.CUMULATIVE_COMPONENT_DEPENDENCY.getDisplayName())
         .getValueAsString());
      assertEquals("2.66", package3.getMetricByName(MetricEnum.AVERAGE_COMPONENT_DEPENDENCY.getDisplayName())
         .getValueAsString());
   }

   public void testCcdAcdDependencyCycle3SequentiallyDependentComponents()
   {
      runDependometerWithTestProject("ccdAcd3DependencyCycle3SequentiallyDependentComponents", false);
      assertEquals("9", project.getMetricValue(MetricEnum.CUMULATIVE_COMPONENT_DEPENDENCY ));
      assertEquals("3.0", project.getMetricValue(MetricEnum.AVERAGE_COMPONENT_DEPENDENCY ));

      Package package1 = Package.getPackage("sample1");
      assertEquals("6", package1.getMetricByName(MetricEnum.CUMULATIVE_COMPONENT_DEPENDENCY.getDisplayName())
         .getValueAsString());
      assertEquals("3.0", package1.getMetricByName(MetricEnum.AVERAGE_COMPONENT_DEPENDENCY.getDisplayName())
         .getValueAsString());

      Package package2 = Package.getPackage("sample2");
      assertEquals("3", package2.getMetricByName(MetricEnum.CUMULATIVE_COMPONENT_DEPENDENCY.getDisplayName())
         .getValueAsString());
      assertEquals("3.0", package2.getMetricByName(MetricEnum.AVERAGE_COMPONENT_DEPENDENCY.getDisplayName())
         .getValueAsString());

   }

   public void testCcdAcdDependencyCycle3InterdependentComponents()
   {
      runDependometerWithTestProject("ccdAcd3bDependencyCycle3InterdependentComponents", false);
      assertEquals("9", project.getMetricValue(MetricEnum.CUMULATIVE_COMPONENT_DEPENDENCY ));
      assertEquals("3.0", project.getMetricValue(MetricEnum.AVERAGE_COMPONENT_DEPENDENCY ));

      Package package1 = Package.getPackage("sample1");
      assertEquals("6", package1.getMetricByName(MetricEnum.CUMULATIVE_COMPONENT_DEPENDENCY.getDisplayName())
         .getValueAsString());
      assertEquals("3.0", package1.getMetricByName(MetricEnum.AVERAGE_COMPONENT_DEPENDENCY.getDisplayName())
         .getValueAsString());

      Package package2 = Package.getPackage("sample2");
      assertEquals("3", package2.getMetricByName(MetricEnum.CUMULATIVE_COMPONENT_DEPENDENCY.getDisplayName())
         .getValueAsString());
      assertEquals("3.0", package2.getMetricByName(MetricEnum.AVERAGE_COMPONENT_DEPENDENCY.getDisplayName())
         .getValueAsString());
   }

   public void testCcdAcdDependencyCycleAtLowerLevel()
   {
      runDependometerWithTestProject("ccdAcd4DependencyCycleAtLowerLevel", false);
      assertEquals("27", project.getMetricValue(MetricEnum.CUMULATIVE_COMPONENT_DEPENDENCY ));
      assertEquals("4.5", project.getMetricValue(MetricEnum.AVERAGE_COMPONENT_DEPENDENCY ));

      Package package1 = Package.getPackage("sample1");
      assertEquals("16", package1.getMetricByName(MetricEnum.CUMULATIVE_COMPONENT_DEPENDENCY.getDisplayName())
         .getValueAsString());
      assertEquals("4.0", package1.getMetricByName(MetricEnum.AVERAGE_COMPONENT_DEPENDENCY.getDisplayName())
         .getValueAsString());

      Package package2 = Package.getPackage("sample2");
      assertEquals("11", package2.getMetricByName(MetricEnum.CUMULATIVE_COMPONENT_DEPENDENCY.getDisplayName())
         .getValueAsString());
      assertEquals("5.5", package2.getMetricByName(MetricEnum.AVERAGE_COMPONENT_DEPENDENCY.getDisplayName())
         .getValueAsString());

   }

   public void testCcdAcdDependencyCycleAtHigherLevel()
   {
      runDependometerWithTestProject("ccdAcd4bDependencyCycleAtHigherLevel", false);
      assertEquals("31", project.getMetricValue(MetricEnum.CUMULATIVE_COMPONENT_DEPENDENCY ));
      // rounding for ACD wrong should be 5.17
      assertEquals("5.16", project.getMetricValue(MetricEnum.AVERAGE_COMPONENT_DEPENDENCY ));

      Package package1 = Package.getPackage("sample1");
      assertEquals("19", package1.getMetricByName(MetricEnum.CUMULATIVE_COMPONENT_DEPENDENCY.getDisplayName())
         .getValueAsString());
      assertEquals("4.75", package1.getMetricByName(MetricEnum.AVERAGE_COMPONENT_DEPENDENCY.getDisplayName())
         .getValueAsString());

      Package package2 = Package.getPackage("sample2");
      assertEquals("12", package2.getMetricByName(MetricEnum.CUMULATIVE_COMPONENT_DEPENDENCY.getDisplayName())
         .getValueAsString());
      assertEquals("6.0", package2.getMetricByName(MetricEnum.AVERAGE_COMPONENT_DEPENDENCY.getDisplayName())
         .getValueAsString());
   }

   public void testCcdAcdBinaryTree()
   {
      runDependometerWithTestProject("ccdAcd5BinaryTree", false);
      assertEquals("17", project.getMetricValue(MetricEnum.CUMULATIVE_COMPONENT_DEPENDENCY ));
      // rounding for ACD wrong should be 2.43
      assertEquals("2.42", project.getMetricValue(MetricEnum.AVERAGE_COMPONENT_DEPENDENCY ));

      Package package1 = Package.getPackage("sample1");
      assertEquals("7", package1.getMetricByName(MetricEnum.CUMULATIVE_COMPONENT_DEPENDENCY.getDisplayName())
         .getValueAsString());
      assertEquals("7.0", package1.getMetricByName(MetricEnum.AVERAGE_COMPONENT_DEPENDENCY.getDisplayName())
         .getValueAsString());

      Package package2 = Package.getPackage("sample2");
      assertEquals("6", package2.getMetricByName(MetricEnum.CUMULATIVE_COMPONENT_DEPENDENCY.getDisplayName())
         .getValueAsString());
      assertEquals("3.0", package2.getMetricByName(MetricEnum.AVERAGE_COMPONENT_DEPENDENCY.getDisplayName())
         .getValueAsString());

      Package package3 = Package.getPackage("sample3");
      assertEquals("4", package3.getMetricByName(MetricEnum.CUMULATIVE_COMPONENT_DEPENDENCY.getDisplayName())
         .getValueAsString());
      assertEquals("1.0", package3.getMetricByName(MetricEnum.AVERAGE_COMPONENT_DEPENDENCY.getDisplayName())
         .getValueAsString());
   }
}
