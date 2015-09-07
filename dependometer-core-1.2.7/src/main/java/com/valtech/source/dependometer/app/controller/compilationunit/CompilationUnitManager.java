package com.valtech.source.dependometer.app.controller.compilationunit;

public class CompilationUnitManager
{
   private final DispatcherSinglePackageCompilationUnitCycleCollectedEvent dispatcherSinglePackageCompilationUnitCycleCollectedEvent = new DispatcherSinglePackageCompilationUnitCycleCollectedEvent();

   private final DispatcherMultiplePackageCompilationUnitCycleCollectedEvent dispatcherMultiplePackageCompilationUnitCycleCollectedEvent = new DispatcherMultiplePackageCompilationUnitCycleCollectedEvent();

   private final DispatcherCompilationUnitLevelCollectedEvent dispatcherCompilationUnitLevelCollectedEvent = new DispatcherCompilationUnitLevelCollectedEvent();

   private final DispatcherCompilationUnitCycleParticipationCollectedEvent dispatcherCompilationUnitCycleParticipationCollectedEvent = new DispatcherCompilationUnitCycleParticipationCollectedEvent();

   private final DispatcherCompilationUnitCycleParticipantsCollectedEvent dispatcherCompilationUnitCycleParticipantsCollectedEvent = new DispatcherCompilationUnitCycleParticipantsCollectedEvent();

   private final DispatcherCompilationUnitTangleCollectedEvent dispatcherCompilationUnitTangleCollectedEvent = new DispatcherCompilationUnitTangleCollectedEvent();
   
   
   // SinglePackageCompilationUnitCycleCollectedEvent
   public void attach(HandleSinglePackageCompilationUnitCycleCollectedEventIf handler)
   {
      dispatcherSinglePackageCompilationUnitCycleCollectedEvent.addEventHandler(handler);
   }

   public void detach(HandleSinglePackageCompilationUnitCycleCollectedEventIf handler)
   {
      dispatcherSinglePackageCompilationUnitCycleCollectedEvent.removeEventHandler(handler);
   }

   public void dispatch(SinglePackageCompilationUnitCycleCollectedEvent event)
   {
      dispatcherSinglePackageCompilationUnitCycleCollectedEvent.dispatch(event);
   }

   public boolean isAttached(HandleSinglePackageCompilationUnitCycleCollectedEventIf handler)
   {
      return dispatcherSinglePackageCompilationUnitCycleCollectedEvent.isAttached(handler);
   }

   public int numberOfSinglePackageCompilationUnitCycleCollectedEventHandler()
   {
      return dispatcherSinglePackageCompilationUnitCycleCollectedEvent.numberOfEventHandler();
   }

   // MultiplePackageCompilationUnitCycleCollectedEvent
   public void attach(HandleMultiplePackageCompilationUnitCycleCollectedEventIf handler)
   {
      dispatcherMultiplePackageCompilationUnitCycleCollectedEvent.addEventHandler(handler);
   }

   public void detach(HandleMultiplePackageCompilationUnitCycleCollectedEventIf handler)
   {
      dispatcherMultiplePackageCompilationUnitCycleCollectedEvent.removeEventHandler(handler);
   }

   public void dispatch(MultiplePackageCompilationUnitCycleCollectedEvent event)
   {
      dispatcherMultiplePackageCompilationUnitCycleCollectedEvent.dispatch(event);
   }

   public boolean isAttached(HandleMultiplePackageCompilationUnitCycleCollectedEventIf handler)
   {
      return dispatcherMultiplePackageCompilationUnitCycleCollectedEvent.isAttached(handler);
   }

   public int numberOfMultiplePackageCompilationUnitCycleCollectedEventHandler()
   {
      return dispatcherMultiplePackageCompilationUnitCycleCollectedEvent.numberOfEventHandler();
   }

   // CompilationUnitLevelCollectedEvent
   public void attach(HandleCompilationUnitLevelCollectedEventIf handler)
   {
      dispatcherCompilationUnitLevelCollectedEvent.addEventHandler(handler);
   }

   public void detach(HandleCompilationUnitLevelCollectedEventIf handler)
   {
      dispatcherCompilationUnitLevelCollectedEvent.removeEventHandler(handler);
   }

   public void dispatch(CompilationUnitLevelCollectedEvent event)
   {
      dispatcherCompilationUnitLevelCollectedEvent.dispatch(event);
   }

   public boolean isAttached(HandleCompilationUnitLevelCollectedEventIf handler)
   {
      return dispatcherCompilationUnitLevelCollectedEvent.isAttached(handler);
   }

   public int numberOfCompilationUnitLevelCollectedEventHandler()
   {
      return dispatcherCompilationUnitLevelCollectedEvent.numberOfEventHandler();
   }

   // CompilationUnitCycleParticipationCollectedEvent
   public void attach(HandleCompilationUnitCycleParticipationCollectedEventIf handler)
   {
      dispatcherCompilationUnitCycleParticipationCollectedEvent.addEventHandler(handler);
   }

   public void detach(HandleCompilationUnitCycleParticipationCollectedEventIf handler)
   {
      dispatcherCompilationUnitCycleParticipationCollectedEvent.removeEventHandler(handler);
   }

   public void dispatch(CompilationUnitCycleParticipationCollectedEvent event)
   {
      dispatcherCompilationUnitCycleParticipationCollectedEvent.dispatch(event);
   }

   public boolean isAttached(HandleCompilationUnitCycleParticipationCollectedEventIf handler)
   {
      return dispatcherCompilationUnitCycleParticipationCollectedEvent.isAttached(handler);
   }

   public int numberOfCompilationUnitCycleParticipationCollectedEventHandler()
   {
      return dispatcherCompilationUnitCycleParticipationCollectedEvent.numberOfEventHandler();
   }

   // CompilationUnitCycleParticipantsCollectedEvent
   public void attach(HandleCompilationUnitCycleParticipantsCollectedEventIf handler)
   {
      dispatcherCompilationUnitCycleParticipantsCollectedEvent.addEventHandler(handler);
   }

   public void detach(HandleCompilationUnitCycleParticipantsCollectedEventIf handler)
   {
      dispatcherCompilationUnitCycleParticipantsCollectedEvent.removeEventHandler(handler);
   }

   public void dispatch(CompilationUnitCycleParticipantsCollectedEvent event)
   {
      dispatcherCompilationUnitCycleParticipantsCollectedEvent.dispatch(event);
   }

   public boolean isAttached(HandleCompilationUnitCycleParticipantsCollectedEventIf handler)
   {
      return dispatcherCompilationUnitCycleParticipantsCollectedEvent.isAttached(handler);
   }

   public int numberOfCompilationUnitCycleParticipantsCollectedEventHandler()
   {
      return dispatcherCompilationUnitCycleParticipantsCollectedEvent.numberOfEventHandler();
   }

   
   // MultiplePackageCompilationUnitCycleCollectedEvent
   public void attach(HandleCompilationUnitTangleCollectedEventIf handler)
   {
      dispatcherCompilationUnitTangleCollectedEvent.addEventHandler(handler);
   }

   public void dispatch(CompilationUnitTangleCollectedEvent event)
   {
      dispatcherCompilationUnitTangleCollectedEvent.dispatch(event);
   }
}
