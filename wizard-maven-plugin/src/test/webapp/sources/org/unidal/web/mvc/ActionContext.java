package org.unidal.web.mvc;

public abstract class ActionContext<T extends ActionPayload<? extends Page, ? extends Action>> {
   public void sendError(int code, String message) {
   }

   public boolean isProcessStopped() {
      return false;
   }
}
