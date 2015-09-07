package metrics;

import com.valtech.source.dependometer.app.core.common.MetricEnum;

public class TangleTest extends AbstractMetricsTest
{

   public void testSimpleCycle() throws Exception
   {
      runDependometerWithTestProject("simple_cycle");

      assertEquals(3, project.getNumberOfProjectInternalTypes());
      assertEquals(3, project.getNumberOfProjectInternalCompilationUnits());
      assertEquals(1, project.getNumberOfProjectInternalPackages());

      assertEquals("1", project.getMetricValue(MetricEnum.NUMBER_OF_TYPE_TANGLES ));
      assertEquals("1", project.getMetricValue(MetricEnum.NUMBER_OF_COMPILATION_UNIT_TANGLES ));
      assertEquals("n/a", project.getMetricValue(MetricEnum.NUMBER_OF_PACKAGE_TANGLES ));
      assertEquals("n/a", project.getMetricValue(MetricEnum.NUMBER_OF_SUBSYSTEM_TANGLES ));
      assertEquals("n/a", project.getMetricValue(MetricEnum.NUMBER_OF_LAYER_TANGLES ));
   }

   public void testNoCycle() throws Exception
   {
      runDependometerWithTestProject("no_cycle");

      assertEquals(3, project.getNumberOfProjectInternalTypes());

      assertEquals("n/a", project.getMetricValue(MetricEnum.NUMBER_OF_TYPE_TANGLES ));
      assertEquals("n/a", project.getMetricValue(MetricEnum.NUMBER_OF_COMPILATION_UNIT_TANGLES ));
      assertEquals("n/a", project.getMetricValue(MetricEnum.NUMBER_OF_PACKAGE_TANGLES ));
      assertEquals("n/a", project.getMetricValue(MetricEnum.NUMBER_OF_SUBSYSTEM_TANGLES ));
      assertEquals("n/a", project.getMetricValue(MetricEnum.NUMBER_OF_LAYER_TANGLES ));
   }

   public void testCycleGroups() throws Exception
   {
      runDependometerWithTestProject("cycle_groups");

      assertEquals(6, project.getNumberOfProjectInternalTypes());

      assertEquals("2", project.getMetricValue(MetricEnum.NUMBER_OF_TYPE_TANGLES ));
      assertEquals("2", project.getMetricValue(MetricEnum.NUMBER_OF_COMPILATION_UNIT_TANGLES ));
      assertEquals("n/a", project.getMetricValue(MetricEnum.NUMBER_OF_PACKAGE_TANGLES ));
      assertEquals("n/a", project.getMetricValue(MetricEnum.NUMBER_OF_SUBSYSTEM_TANGLES ));
      assertEquals("n/a", project.getMetricValue(MetricEnum.NUMBER_OF_LAYER_TANGLES ));
   }

   public void testPackageCylces() throws Exception
   {
      runDependometerWithTestProject("package_cycles");

      assertEquals(9, project.getNumberOfProjectInternalTypes());

      assertEquals("1", project.getMetricValue(MetricEnum.NUMBER_OF_TYPE_TANGLES ));
      assertEquals("1", project.getMetricValue(MetricEnum.NUMBER_OF_COMPILATION_UNIT_TANGLES ));

      assertEquals("1", project.getMetricValue(MetricEnum.NUMBER_OF_PACKAGE_TANGLES ));
      assertEquals("n/a", project.getMetricValue(MetricEnum.NUMBER_OF_SUBSYSTEM_TANGLES ));
      assertEquals("n/a", project.getMetricValue(MetricEnum.NUMBER_OF_LAYER_TANGLES ));
   }

   // TODO
   // layer, subsystem, vertical slices
   // check concrete tangle with particpants
}
