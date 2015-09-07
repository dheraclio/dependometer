package com.valtech.source.dependometer.app.controller.project;

public class ProjectManager
{
   private final DispatcherProjectInfoCollectedEvent dispatcherProjectInfoCollectedEvent = new DispatcherProjectInfoCollectedEvent();

   private final DispatcherAnalysisFinishedEvent dispatcherAnalysisFinishedEvent = new DispatcherAnalysisFinishedEvent();

   // ProjectInfoCollectedEvent
   public void attach(HandleProjectInfoCollectedEventIf handler)
   {
      dispatcherProjectInfoCollectedEvent.addEventHandler(handler);
   }

   public void detach(HandleProjectInfoCollectedEventIf handler)
   {
      dispatcherProjectInfoCollectedEvent.removeEventHandler(handler);
   }

   public void dispatch(ProjectInfoCollectedEvent event)
   {
      dispatcherProjectInfoCollectedEvent.dispatch(event);
   }

   public boolean isAttached(HandleProjectInfoCollectedEventIf handler)
   {
      return dispatcherProjectInfoCollectedEvent.isAttached(handler);
   }

   public int numberOfProjectInfoCollectedEventHandler()
   {
      return dispatcherProjectInfoCollectedEvent.numberOfEventHandler();
   }

   // AnalysisFinishedEvent
   public void attach(HandleAnalysisFinishedEventIf handler)
   {
      dispatcherAnalysisFinishedEvent.addEventHandler(handler);
   }

   public void detach(HandleAnalysisFinishedEventIf handler)
   {
      dispatcherAnalysisFinishedEvent.removeEventHandler(handler);
   }

   public void dispatch(AnalysisFinishedEvent event)
   {
      dispatcherAnalysisFinishedEvent.dispatch(event);
   }

   public boolean isAttached(HandleAnalysisFinishedEventIf handler)
   {
      return dispatcherAnalysisFinishedEvent.isAttached(handler);
   }

   public int numberOfAnalysisFinishedEventHandler()
   {
      return dispatcherAnalysisFinishedEvent.numberOfEventHandler();
   }
}
