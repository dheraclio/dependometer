package metrics;

import com.valtech.source.dependometer.app.core.elements.Layer;

public class LayerMetricsTest extends AbstractMetricsTest
{
   public void testLayerBasics()
   {
      runDependometerWithTestProject("subsystems_and_layers", true);

      for (Layer l : Layer.getLayers())
      {
         System.out.println(l.getFullyQualifiedName());
      }
      assertEquals(3, project.getNumberOfProjectInternalLayers());
      assertEquals(0, project.getNumberOfProjectExternalLayers());

      Layer layer = findLayer("One");

      assertEquals("Layer One", layer.getDescription());
      assertEquals(1, layer.getDependsUpon());
   }

   public void testNumberOfSubsystems() throws Exception
   {
      runDependometerWithTestProject("subsystems_and_layers", true);

      Layer layer = findLayer("One");
      assertEquals("1", layer.getMetricByName("number of contained subsystems").getValueAsString());

      layer = findLayer("Two");
      assertEquals("3", layer.getMetricByName("number of contained subsystems").getValueAsString());
   }

   private Layer findLayer(String fqName)
   {
      Layer layer = Layer.getLayer(fqName);
      assertNotNull(layer);
      return layer;
   }
}
