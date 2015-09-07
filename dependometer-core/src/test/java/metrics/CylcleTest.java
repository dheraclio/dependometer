package metrics;

import com.valtech.source.dependometer.app.core.common.MetricEnum;

public class CylcleTest extends AbstractMetricsTest
{

   public void testSimpleCycle() throws Exception
   {
      runDependometerWithTestProject("simple_cycle");

      assertEquals(3, project.getNumberOfProjectInternalTypes());
      assertEquals(3, project.getNumberOfProjectInternalCompilationUnits());
      assertEquals(1, project.getNumberOfProjectInternalPackages());

      assertEquals(true, project.existTypeCycles());
      assertEquals(true, project.existCompilationUnitCycles());
      assertEquals(false, project.existPackageCycles());
      assertEquals(false, project.existSubsystemCycles());
      assertEquals(false, project.existLayerCycles());
      assertEquals(false, project.existVerticalSliceCycles());

      assertEquals("1", project.getMetricValue(MetricEnum.NUMBER_OF_TYPE_CYCLES ));
      assertEquals("1", project.getMetricValue(MetricEnum.NUMBER_OF_COMPILATION_UNIT_CYCLES ));
      assertEquals("n/a", project.getMetricValue(MetricEnum.NUMBER_OF_PACKAGE_CYCLES ));
      assertEquals("n/a", project.getMetricValue(MetricEnum.NUMBER_OF_SUBSYSTEM_CYCLES ));
      assertEquals("n/a", project.getMetricValue(MetricEnum.NUMBER_OF_LAYER_CYCLES ));
   }

   public void testNoCycle() throws Exception
   {
      runDependometerWithTestProject("no_cycle");

      assertEquals(3, project.getNumberOfProjectInternalTypes());

      assertEquals(false, project.existTypeCycles());
      assertEquals(false, project.existCompilationUnitCycles());
      assertEquals(false, project.existPackageCycles());
      assertEquals(false, project.existSubsystemCycles());
      assertEquals(false, project.existLayerCycles());
      assertEquals(false, project.existVerticalSliceCycles());

      assertEquals("n/a", project.getMetricValue(MetricEnum.NUMBER_OF_TYPE_CYCLES ));
      assertEquals("n/a", project.getMetricValue(MetricEnum.NUMBER_OF_COMPILATION_UNIT_CYCLES ));
      assertEquals("n/a", project.getMetricValue(MetricEnum.NUMBER_OF_PACKAGE_CYCLES ));
      assertEquals("n/a", project.getMetricValue(MetricEnum.NUMBER_OF_SUBSYSTEM_CYCLES ));
      assertEquals("n/a", project.getMetricValue(MetricEnum.NUMBER_OF_LAYER_CYCLES ));
   }

   public void testCycleGroups() throws Exception
   {
      runDependometerWithTestProject("cycle_groups");

      assertEquals(6, project.getNumberOfProjectInternalTypes());

      assertEquals("4", project.getMetricValue(MetricEnum.NUMBER_OF_TYPE_CYCLES ));
      assertEquals("4", project.getMetricValue(MetricEnum.NUMBER_OF_COMPILATION_UNIT_CYCLES ));
      assertEquals("n/a", project.getMetricValue(MetricEnum.NUMBER_OF_PACKAGE_CYCLES ));
      assertEquals("n/a", project.getMetricValue(MetricEnum.NUMBER_OF_SUBSYSTEM_CYCLES ));
      assertEquals("n/a", project.getMetricValue(MetricEnum.NUMBER_OF_LAYER_CYCLES ));
   }

   public void testPackageCylces() throws Exception
   {
      runDependometerWithTestProject("package_cycles");

      assertEquals(9, project.getNumberOfProjectInternalTypes());

      assertEquals("1", project.getMetricValue(MetricEnum.NUMBER_OF_TYPE_CYCLES ));
      assertEquals("1", project.getMetricValue(MetricEnum.NUMBER_OF_COMPILATION_UNIT_CYCLES ));

      assertEquals("2", project.getMetricValue(MetricEnum.NUMBER_OF_PACKAGE_CYCLES ));
      assertEquals("n/a", project.getMetricValue(MetricEnum.NUMBER_OF_SUBSYSTEM_CYCLES ));
      assertEquals("n/a", project.getMetricValue(MetricEnum.NUMBER_OF_LAYER_CYCLES ));
   }

   // TODO
   // layer, subsystem, vertical slices
   // check concrete cycles with particpants
}
