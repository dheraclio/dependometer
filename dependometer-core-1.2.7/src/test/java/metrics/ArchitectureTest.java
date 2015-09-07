package metrics;

import com.valtech.source.dependometer.app.core.elements.Layer;
import com.valtech.source.dependometer.app.core.elements.Subsystem;
import com.valtech.source.dependometer.app.core.metrics.ProjectMetricsEnum;

public class ArchitectureTest extends AbstractMetricsTest
{
   public void testNoArchitectureDefinition()
   {
      runDependometerWithTestProject("simple_class");

      assertEquals(0, project.getNumberOfForbiddenEfferentLayerDependencies());
      assertEquals(0, project.getNumberOfForbiddenEfferentSubsystemDependencies());
      assertEquals(0, project.getNumberOfForbiddenEfferentVerticalSliceDependencies());

      assertEquals(0, project.getNumberOfProjectExternalLayers());
      assertEquals(0, project.getNumberOfProjectExternalNotImplementedSubsystems());
      assertEquals(0, project.getNumberOfProjectExternalSubsystems());

      assertEquals(0, project.getNumberOfProjectInternalLayers());
      assertEquals(0, project.getNumberOfProjectInternalNotImplementedSubsystems());
      assertEquals(0, project.getNumberOfProjectInternalSubsystems());

      assertEquals(0, project.getNumberOfUnusedDefinedEfferentLayerDependencies());
      assertEquals(0, project.getNumberOfUnusedDefinedEfferentPackageDependencies());
      assertEquals(0, project.getNumberOfUnusedDefinedEfferentSubsystemDependencies());

      assertEquals(0, project.getNumberOfVerticalSlices());
   }

   public void testInternalExternalEntities()
   {
      runDependometerWithTestProject("simple_architecture", true);

      System.out.println(ProjectMetricsEnum.asString());

      assertEquals(2, project.getNumberOfProjectInternalLayers());
      assertEquals(4, project.getNumberOfProjectInternalSubsystems());
      assertEquals(7, project.getNumberOfProjectInternalPackages());
      assertEquals(7, project.getNumberOfProjectInternalCompilationUnits());
      assertEquals(7, project.getNumberOfProjectInternalTypes());

      assertEquals(1, project.getNumberOfProjectExternalLayers());
      assertEquals(1, project.getNumberOfProjectExternalSubsystems());
      assertEquals(1, project.getNumberOfProjectExternalPackages());
      assertEquals(1, project.getNumberOfProjectExternalCompilationUnits());
      assertEquals(1, project.getNumberOfProjectExternalTypes());
   }

   public void testDefinedDependencies()
   {
      runDependometerWithTestProject("architecture_dependencies", true);

      System.out.println(ProjectMetricsEnum.asString());

      assertEquals(1, Layer.getNumberOfAllowedEfferents());
      assertEquals(4, Subsystem.getNumberOfAllowedEfferents());

      assertEquals(0, project.getNumberOfUnusedDefinedEfferentLayerDependencies());
      assertEquals(1, project.getNumberOfUnusedDefinedEfferentSubsystemDependencies());
      assertEquals(0, project.getNumberOfUnusedDefinedEfferentPackageDependencies());
   }

   public void testForbiddenDependenciesNoCycles()
   {
      runDependometerWithTestProject("architecture_dependencies", true);

      System.out.println(ProjectMetricsEnum.asString());

      assertEquals(2, project.getNumberOfForbiddenEfferentLayerDependencies());

      Layer layer = getLayer("Application");
      assertEquals(1, layer.getForbiddenEfferents().length);
      assertEquals("External", layer.getForbiddenEfferents()[0].getFullyQualifiedName());

      assertEquals(3, project.getNumberOfForbiddenEfferentSubsystemDependencies());
      assertEquals(4, project.getNumberOfForbiddenEfferentPackageDependencies());
      assertEquals(5, project.getNumberOfForbiddenEfferentCompilationUnitDependencies());
      assertEquals(7, project.getNumberOfForbiddenEfferentTypeDependencies());
   }

   private Layer getLayer(String fqName)
   {
      Layer layer = Layer.getLayer(fqName);
      assertNotNull(layer);
      return layer;
   }
}
