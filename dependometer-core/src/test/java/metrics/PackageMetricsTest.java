package metrics;

import org.junit.Assume;

public class PackageMetricsTest extends AbstractMetricsTest
{
   public void testSimplePackage() throws Exception
   {
      runDependometerWithTestProject("simple_package");

      assertEquals(4, project.getNumberOfProjectInternalCompilationUnits());
      assertEquals(3, project.getNumberOfProjectInternalPackages());
      assertEquals(3, project.getNumberOfProjectInternalNotAssignedPackages());

      assertEquals(false, project.existTypeCycles());
      assertEquals(false, project.existCompilationUnitCycles());
      assertEquals(false, project.existPackageCycles());

   }

   public void testPackageHierarchy()
   {
      runDependometerWithTestProject("package_hierarchy");

      assertEquals("3", project.getMetricByName("max depth of package hierarchy").getValueAsString());

      com.valtech.source.dependometer.app.core.elements.Package pack1 = com.valtech.source.dependometer.app.core.elements.Package
         .getPackage("sample1");
      Assume.assumeNotNull(pack1);
      assertEquals("1", pack1.getMetricByName("depth of package hierarchy").getValueAsString());

      com.valtech.source.dependometer.app.core.elements.Package pack2 = com.valtech.source.dependometer.app.core.elements.Package
         .getPackage("sample2.sub2.subsub1");
      Assume.assumeNotNull(pack2);
      assertEquals("3", pack2.getMetricByName("depth of package hierarchy").getValueAsString());

      com.valtech.source.dependometer.app.core.elements.Package pack3 = com.valtech.source.dependometer.app.core.elements.Package
         .getPackage("sample2.sub1");
      Assume.assumeNotNull(pack3);
      assertEquals("2", pack3.getMetricByName("depth of package hierarchy").getValueAsString());
   }
}
