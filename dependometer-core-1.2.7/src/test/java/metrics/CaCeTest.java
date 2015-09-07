package metrics;

import com.valtech.source.dependometer.app.core.common.MetricEnum;
import com.valtech.source.dependometer.app.core.elements.Package;

public class CaCeTest extends AbstractMetricsTest
{
   public void testPackageDep_Out_InOut_In_Indep_NoCfg()
   {
      runDependometerWithTestProject("caCe_PackageDep_Out_InOut_In_Independent", false);
      // independent package
      Package package0 = Package.getPackage("p0");
      assertEquals("0", package0.getMetric(MetricEnum.AFFERENT_INCOMING_COUPLING ).getValueAsString());
      assertEquals("0", package0.getMetric(MetricEnum.EFFERENT_OUTGOING_COUPLING ).getValueAsString());
      // dependencies to p2, package internal dependencies
      Package package1 = Package.getPackage("p1");
      assertEquals("0", package1.getMetric(MetricEnum.AFFERENT_INCOMING_COUPLING ).getValueAsString());
      assertEquals("2", package1.getMetric(MetricEnum.EFFERENT_OUTGOING_COUPLING ).getValueAsString());
      // dependencies from p1 and dependencies to p3
      Package package2 = Package.getPackage("p2");
      assertEquals("1", package2.getMetric(MetricEnum.AFFERENT_INCOMING_COUPLING ).getValueAsString());
      assertEquals("2", package2.getMetric(MetricEnum.EFFERENT_OUTGOING_COUPLING ).getValueAsString());
      // dependencies from p2
      Package package3 = Package.getPackage("p3");
      assertEquals("3", package3.getMetric(MetricEnum.AFFERENT_INCOMING_COUPLING ).getValueAsString());
      assertEquals("0", package3.getMetric(MetricEnum.EFFERENT_OUTGOING_COUPLING ).getValueAsString());
   }
}
