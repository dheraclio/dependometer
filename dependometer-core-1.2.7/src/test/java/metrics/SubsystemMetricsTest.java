package metrics;

import com.valtech.source.dependometer.app.core.elements.Subsystem;


public class SubsystemMetricsTest extends AbstractMetricsTest
{
   public void testSubsystemBasics()
   {
      runDependometerWithTestProject("subsystems_and_layers", true);

      assertEquals(5, project.getNumberOfProjectInternalSubsystems());
      assertEquals(0, project.getNumberOfProjectExternalSubsystems());
      for(Subsystem s:Subsystem.getSubsystems())
      {
         System.out.println(s.getFullyQualifiedName());
      }
      
      Subsystem sub=findSubsystem("One::system1");
      
      assertEquals("system 1", sub.getDescription());
      assertEquals(1, sub.getDependsUpon());
   }
   
   public void testNumberOfPackages() throws Exception
   {
      runDependometerWithTestProject("subsystems_and_layers", true);

      Subsystem sub=findSubsystem("One::system1");
      
      assertEquals("3", sub.getMetricByName("number of contained packages").getValueAsString());
   }
   
   private Subsystem findSubsystem(String fqName)
   {
      Subsystem sub= Subsystem.getSubsystem(fqName);
      assertNotNull(sub);
      return sub;
   }
}
