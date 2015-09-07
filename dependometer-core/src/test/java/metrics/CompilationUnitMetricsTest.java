package metrics;

import com.valtech.source.dependometer.app.core.elements.CompilationUnit;

public class CompilationUnitMetricsTest extends AbstractMetricsTest
{
   public void testSimpleClass()
   {
      runDependometerWithTestProject("simple_class");

      assertEquals(1, project.getNumberOfProjectInternalCompilationUnits());

      CompilationUnit classWithNestedTypes = findCompilationUnit("sample/SimpleClass");

      assertEquals("1", classWithNestedTypes.getMetricByName("number of types (Nc)").getValueAsString());
      assertEquals("1", classWithNestedTypes.getMetricByName("number of accessible types").getValueAsString());
      assertEquals("1", classWithNestedTypes.getMetricByName("number of concrete types").getValueAsString());
      assertEquals("0", classWithNestedTypes.getMetricByName("number of abstract types (Na)").getValueAsString());
   }

   public void testMultipleTypesInOneCompilationUnit() throws Exception
   {
      runDependometerWithTestProject("multiple_types_in_one_cu");

      assertEquals(9, project.getNumberOfProjectInternalTypes());
      assertEquals(2, project.getNumberOfProjectInternalCompilationUnits());

      CompilationUnit classWithNestedTypes = findCompilationUnit("sample/ClassWithNestedTypes");

      assertEquals("5", classWithNestedTypes.getMetricByName("number of types (Nc)").getValueAsString());
      assertEquals("5", classWithNestedTypes.getMetricByName("number of accessible types").getValueAsString());
      assertEquals("3", classWithNestedTypes.getMetricByName("number of concrete types").getValueAsString());
      assertEquals("2", classWithNestedTypes.getMetricByName("number of abstract types (Na)").getValueAsString());

      CompilationUnit interfaceWithNestedTypes = findCompilationUnit("sample/InterfaceWithNestedTypes");

      assertEquals("4", interfaceWithNestedTypes.getMetricByName("number of types (Nc)").getValueAsString());
      assertEquals("4", interfaceWithNestedTypes.getMetricByName("number of accessible types").getValueAsString());
      assertEquals("1", interfaceWithNestedTypes.getMetricByName("number of concrete types").getValueAsString());
      assertEquals("3", interfaceWithNestedTypes.getMetricByName("number of abstract types (Na)").getValueAsString());
   }

   private CompilationUnit findCompilationUnit(String fqName)
   {
      CompilationUnit cu = CompilationUnit.getCompilationUnit(fqName);
      assertNotNull(cu);
      return cu;
   }

   public void testPackageInternalExternalDependencies()
   {
      runDependometerWithTestProject("package_external_internal_dependencies");

      assertEquals(6, project.getNumberOfProjectInternalCompilationUnits());

      CompilationUnit cu = findCompilationUnit("sample1/SimpleClass1");

      assertEquals("4", cu.getMetricByName("number of package external relations").getValueAsString());
      assertEquals("3", cu.getMetricByName("number of package internal relations").getValueAsString());
      assertEquals("true", cu.getMetricByName("more package external than internal relations exist").getValueAsString());
      assertNull(cu.getMetricByName("contains accessible types but no incoming outer package dependencies exist"));
      assertEquals("sample2 (4)", cu.getMetricByName("the most external relations exist with package")
         .getValueAsString());

      cu = findCompilationUnit("sample1/SimpleClass2");
      assertEquals("0", cu.getMetricByName("number of package external relations").getValueAsString());
      assertEquals("1", cu.getMetricByName("number of package internal relations").getValueAsString());
      assertEquals("true", cu.getMetricByName(
         "contains accessible types but no incoming outer package dependencies exist").getValueAsString());
      assertNull(cu.getMetricByName("more package external than internal relations exist"));

      cu = findCompilationUnit("sample1/SimpleClass4");
      assertEquals("1", cu.getMetricByName("number of package external relations").getValueAsString());
      assertEquals("1", cu.getMetricByName("number of package internal relations").getValueAsString());
      assertNull(cu.getMetricByName("contains accessible types but no incoming outer package dependencies exist"));
      assertNull(cu.getMetricByName("more package external than internal relations exist"));

   }

}
