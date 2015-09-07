/*
 * Copyright 2009 Valtech GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.valtech.source.dependometer.ui.console.output;

import java.io.IOException;
import java.io.PrintWriter;

import com.valtech.source.dependometer.app.controller.project.HandleProjectInfoCollectedEventIf;
import com.valtech.source.dependometer.app.controller.project.ProjectInfoCollectedEvent;
import com.valtech.source.dependometer.app.core.provider.ConfigurationProviderIf;
import com.valtech.source.dependometer.app.core.provider.ProjectIf;
import com.valtech.source.dependometer.app.core.provider.ProviderFactory;
import com.valtech.source.dependometer.app.core.provider.ThresholdIf;
import com.valtech.source.dependometer.ui.console.Dependometer;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public class CsvWriter extends SingleFileWriter implements HandleProjectInfoCollectedEventIf
{
   public CsvWriter(String[] arguments) throws IOException
   {
      super(arguments);
      Dependometer.getContext().getProjectManager().attach(this);
   }

   public void handleEvent(ProjectInfoCollectedEvent event)
   {
      assert event != null;

      ProjectIf project = event.getProject();
      PrintWriter writer = getWriter();

      writer.println("### " + getClass().getName() + " - " + getTimestamp() + " ###");
      writer.println();

      ConfigurationProviderIf provider = ProviderFactory.getInstance().getConfigurationProvider();

      writer.println("# dependency check settings #");
      writer.println();

      if (provider.analyzeVerticalSlices())
      {
         writer.print(project.getVerticalSliceElementName());
         writer.print(" = ");
         writer.println(provider.checkVerticalSliceDependencies() ? "enabled" : "disabled");
      }

      writer.print(project.getLayerElementName());
      writer.print(" = ");
      writer.println(provider.checkLayerDependencies() ? "enabled" : "disabled");

      writer.print(project.getSubsystemElementName());
      writer.print(" = ");
      writer.println(provider.checkSubsystemDependencies() ? "enabled" : "disabled");

      writer.print(project.getPackageElementName());
      writer.print(" = ");
      writer.println(provider.checkPackageDependencies() ? "enabled" : "disabled");

      writer.println();

      writer.println("# dependency cumulation settings #");
      writer.println();

      if (provider.analyzeVerticalSlices())
      {
         writer.print(project.getVerticalSliceElementName());
         writer.print(" = ");
         writer.println(provider.cumulateVerticalSliceDependencies() ? "enabled" : "disabled");
      }

      writer.print(project.getLayerElementName());
      writer.print(" = ");
      writer.println(provider.cumulateLayerDependencies() ? "enabled" : "disabled");

      writer.print(project.getSubsystemElementName());
      writer.print(" = ");
      writer.println(provider.cumulateSubsystemDependencies() ? "enabled" : "disabled");

      writer.print(project.getPackageElementName());
      writer.print(" = ");
      writer.println(provider.cumulatePackageDependencies() ? "enabled" : "disabled");

      writer.print(project.getCompilationUnitElementName());
      writer.print(" = ");
      writer.println(provider.cumulateCompilationUnitDependencies() ? "enabled" : "disabled");

      writer.print(project.getTypeElementName());
      writer.print(" = ");
      writer.println(provider.cumulateTypeDependencies() ? "enabled" : "disabled");
      writer.println();

      writer.println();

      writer.println("# cycle analysis settings #");
      writer.println();

      if (provider.analyzeVerticalSlices())
      {
         writer.print(project.getVerticalSliceElementName());
         writer.print(" = ");
         writer.println(provider.getMaxVerticalSliceCycles());
      }

      writer.print(project.getLayerElementName());
      writer.print(" = ");
      writer.println(provider.getMaxLayerCycles());

      writer.print(project.getSubsystemElementName());
      writer.print(" = ");
      writer.println(provider.getMaxSubsystemCycles());

      writer.print(project.getPackageElementName());
      writer.print(" = ");
      writer.println(provider.getMaxPackageCycles());

      writer.print(project.getCompilationUnitElementName());
      writer.print(" = ");
      writer.println(provider.getMaxCompilationUnitCycles());

      writer.print(project.getTypeElementName());
      writer.print(" = ");
      writer.println(provider.getMaxTypeCycles());
      writer.println();

      writer.println("# thresholds #");
      writer.println();

      writer.print("name");
      writer.print(',');
      writer.print("type");
      writer.print(',');
      writer.print("threshold");
      writer.print(',');
      writer.print("supported");
      writer.print(',');
      writer.print("analyzed");
      writer.print(',');
      writer.print("value");
      writer.print(',');
      writer.println("violation");

      ThresholdIf[] thresholds = project.getThresholds();
      for (int i = 0; i < thresholds.length; i++)
      {
         ThresholdIf next = thresholds[i];
         if (next.isSupported())
         {
            writer.print(next.getQueryId());
            writer.print(',');
            writer.print(next.isLowerThreshold() ? "lower" : "upper");
            writer.print(',');
            writer.print(next.getThreshold());
            writer.print(',');
            writer.print("yes");
            writer.print(',');
            if (next.wasAnalyzed())
            {
               writer.print("yes");
               writer.print(',');
               writer.print(next.getValue());
               writer.print(',');
               writer.println(next.wasViolated() ? "yes" : "no");
            }
            else
            {
               writer.print("no");
               writer.print(',');
               writer.print("--");
               writer.print(',');
               writer.println("--");
            }
         }
         else
         {
            writer.print(next.getQueryId());
            writer.print(',');
            writer.print(next.isLowerThreshold() ? "lower" : "upper");
            writer.print(',');
            writer.print(next.getThreshold());
            writer.print(',');
            writer.print("no");
            writer.print(',');
            writer.print("--");
            writer.print(',');
            writer.print("--");
            writer.print(',');
            writer.println("--");
         }
      }

      finish();
   }

   void finish()
   {
      getLogger().info("writing csv file ...");
      close();
   }
}