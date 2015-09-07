package com.valtech.source.dependometer.app.controller.main;

import com.valtech.source.dependometer.app.controller.compilationunit.CompilationUnitManager;
import com.valtech.source.dependometer.app.controller.layer.LayerManager;
import com.valtech.source.dependometer.app.controller.pack.PackageManager;
import com.valtech.source.dependometer.app.controller.project.ProjectManager;
import com.valtech.source.dependometer.app.controller.subsystem.SubsystemManager;
import com.valtech.source.dependometer.app.controller.type.TypeManager;
import com.valtech.source.dependometer.app.controller.verticalslice.VerticalSliceManager;

/**
 * @author oliver.rohr
 * 
 *         Fascade for controllers and other stuff.
 * 
 *         Singleton due to backward compatibily.
 * */
public class DependometerContext
{
   private TypeManager typeManager = new TypeManager();

   private CompilationUnitManager compilationUnitManager = new CompilationUnitManager();

   private PackageManager packageManager = new PackageManager();

   private SubsystemManager subsystemManager = new SubsystemManager();

   private LayerManager layerManager = new LayerManager();

   private VerticalSliceManager verticalSliceManager = new VerticalSliceManager();

   private ProjectManager projectManager = new ProjectManager();

   public TypeManager getTypeManager()
   {
      return typeManager;
   }

   public void setTypeManager(TypeManager typeManager)
   {
      this.typeManager = typeManager;
   }

   public CompilationUnitManager getCompilationUnitManager()
   {
      return compilationUnitManager;
   }

   public void setCompilationUnitManager(CompilationUnitManager compilationUnitManager)
   {
      this.compilationUnitManager = compilationUnitManager;
   }

   public PackageManager getPackageManager()
   {
      return packageManager;
   }

   public void setPackageManager(PackageManager packageManager)
   {
      this.packageManager = packageManager;
   }

   public SubsystemManager getSubsystemManager()
   {
      return subsystemManager;
   }

   public void setSubsystemManager(SubsystemManager subsystemManager)
   {
      this.subsystemManager = subsystemManager;
   }

   public LayerManager getLayerManager()
   {
      return layerManager;
   }

   public void setLayerManager(LayerManager layerManager)
   {
      this.layerManager = layerManager;
   }

   public VerticalSliceManager getVerticalSliceManager()
   {
      return verticalSliceManager;
   }

   public void setVerticalSliceManager(VerticalSliceManager verticalSliceManager)
   {
      this.verticalSliceManager = verticalSliceManager;
   }

   public ProjectManager getProjectManager()
   {
      return projectManager;
   }

   public void setProjectManager(ProjectManager projectManager)
   {
      this.projectManager = projectManager;
   }
}
