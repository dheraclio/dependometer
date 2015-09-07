package metrics;

import org.junit.Assume;

import com.valtech.source.dependometer.app.core.elements.ParsedClass;
import com.valtech.source.dependometer.app.core.elements.ParsedType;
import com.valtech.source.dependometer.app.core.elements.Type;

public class TypeMetricsTest extends AbstractMetricsTest
{
   public void testSimpleClass() throws Exception
   {
      runDependometerWithTestProject("simple_class");

      assertEquals(0, project.getNumberOfForbiddenEfferentCompilationUnitDependencies());
      assertEquals(0, project.getNumberOfForbiddenEfferentPackageDependencies());
      assertEquals(0, project.getNumberOfForbiddenEfferentTypeDependencies());

      assertEquals(0, project.getNumberOfProjectExternalCompilationUnits());
      assertEquals(0, project.getNumberOfProjectExternalNotAssignedPackages());
      assertEquals(0, project.getNumberOfProjectExternalTypes());

      assertEquals(1, project.getNumberOfProjectInternalCompilationUnits());
      assertEquals(1, project.getNumberOfProjectInternalNotAssignedPackages());
      assertEquals(1, project.getNumberOfProjectInternalPackages());
      assertEquals(1, project.getNumberOfProjectInternalTypes());

      assertEquals(false, project.existTypeCycles());
      assertEquals(false, project.existCompilationUnitCycles());
      assertEquals(false, project.existPackageCycles());
      assertEquals(false, project.existSubsystemCycles());
      assertEquals(false, project.existLayerCycles());
      assertEquals(false, project.existVerticalSliceCycles());

      Type simpleClass = Type.getType("sample.SimpleClass");
      assertNotNull(simpleClass);

      assertEquals(false, simpleClass.isInterface());
      assertEquals(true, simpleClass.isConcrete());
      assertEquals(true, simpleClass.isAccessible());

      assertEquals("false", simpleClass.getMetricByName("nested").getValueAsString());

      assertEquals("0", simpleClass.getMetricByName("number of children (NOC)").getValueAsString());
      assertEquals("0", simpleClass.getMetricByName("depth of class inheritance").getValueAsString());
      assertEquals("0", simpleClass.getMetricByName("depth of interface inheritance").getValueAsString());
   }

   public void testAbstractTypes()
   {
      runDependometerWithTestProject("abstract_types");

      Type abstractClass = Type.getType("sample.AbstractClass");
      assertNotNull(abstractClass);
      assertEquals(false, abstractClass.isConcrete());

      Type abstractClass2 = Type.getType("sample.AbstractClassWithInterface");
      assertNotNull(abstractClass2);
      assertEquals(false, abstractClass2.isConcrete());

      com.valtech.source.dependometer.app.core.elements.Package pack = com.valtech.source.dependometer.app.core.elements.Package
         .getPackage("sample");
      Assume.assumeNotNull(pack);

      // 2 abstract classes, 1 interface
      assertEquals("3", pack.getMetricByName("number of abstract types (Na)").getValueAsString());
   }

   public void testInterfaces()
   {
      runDependometerWithTestProject("interfaces");

      Type inter = Type.getType("sample.SimpleInterfaceA");
      assertEquals(true, inter.isInterface());
      assertEquals(false, inter.isConcrete());

      Type inter2 = Type.getType("sample.SimpleInterfaceB");
      assertEquals(true, inter2.isInterface());
      assertEquals(false, inter2.isConcrete());

      assertEquals("0", inter.getMetricByName("depth of class inheritance").getValueAsString());
      assertEquals("0", inter.getMetricByName("depth of interface inheritance").getValueAsString());
   }

   public void testClassHierarchy()
   {
      runDependometerWithTestProject("class_hierarchy");

      assertEquals("3", project.getMetricByName("max depth of type inheritance").getValueAsString());

      Type subClass = Type.getType("sample.SubClass1");
      assertNotNull(subClass);

      assertEquals("3", subClass.getMetricByName("depth of class inheritance").getValueAsString());
      assertEquals("0", subClass.getMetricByName("depth of interface inheritance").getValueAsString());

      Type superClassA = Type.getType("sample.SuperClassA");
      assertNotNull(superClassA);
      assertEquals("2", superClassA.getMetricByName("depth of class inheritance").getValueAsString());
      assertEquals("1", superClassA.getMetricByName("number of children (NOC)").getValueAsString());

      Type superClassD = Type.getType("sample.SuperClassD");
      assertNotNull(superClassD);
      assertEquals("0", superClassD.getMetricByName("depth of class inheritance").getValueAsString());
      assertEquals("2", superClassD.getMetricByName("number of children (NOC)").getValueAsString());
   }

   public void testInterfaceHierarchy()
   {
      runDependometerWithTestProject("interface_hierarchy");

      assertEquals("3", project.getMetricByName("max depth of type inheritance").getValueAsString());

      Type subInterface = Type.getType("sample.SubInterface1");
      assertNotNull(subInterface);

      assertEquals("0", subInterface.getMetricByName("depth of class inheritance").getValueAsString());
      assertEquals("3", subInterface.getMetricByName("depth of interface inheritance").getValueAsString());

      Type superInterfaceA = Type.getType("sample.SuperInterfaceB");
      assertNotNull(superInterfaceA);
      assertEquals("2", superInterfaceA.getMetricByName("depth of interface inheritance").getValueAsString());
      assertEquals("1", superInterfaceA.getMetricByName("number of children (NOC)").getValueAsString());

      Type superInterfaceD = Type.getType("sample.SuperInterfaceD");
      assertNotNull(superInterfaceD);
      assertEquals("0", superInterfaceD.getMetricByName("depth of interface inheritance").getValueAsString());
      assertEquals("2", superInterfaceD.getMetricByName("number of children (NOC)").getValueAsString());
   }

   public void testNestedType()
   {
      runDependometerWithTestProject("nested_types");

      Type nestedType = Type.getType("sample.ClassWithNestedTypes$Nested1");
      assertNotNull(nestedType);

      assertEquals("true", nestedType.getMetricByName("nested").getValueAsString());
   }

   public void testAccessible()
   {
      runDependometerWithTestProject("type_access");

      Type t = Type.getType("sample.PublicClass");
      assertNotNull(t);

      assertEquals("true", t.getMetricByName("accessible").getValueAsString());
      assertEquals(true, t.isAccessible());

      t = Type.getType("sample.ProtectedClass");
      assertNotNull(t);
      assertEquals(false, t.isAccessible());

      t = Type.getType("sample.PrivateClass");
      assertNotNull(t);
      assertEquals(false, t.isAccessible());
   }

   public void testExtendable()
   {
      runDependometerWithTestProject("type_access");

      ParsedType t = (ParsedClass)Type.getType("sample.PublicFinalClass");
      assertNotNull(t);

      assertEquals("false", t.getMetricByName("extendable").getValueAsString());
   }
}
