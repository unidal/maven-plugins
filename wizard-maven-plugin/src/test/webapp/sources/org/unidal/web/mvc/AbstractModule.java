package org.unidal.web.mvc;

import org.unidal.web.mvc.annotation.ErrorActionMeta;
import org.unidal.web.mvc.annotation.TransitionMeta;

public abstract class AbstractModule implements Module {
   public Class<? extends PageHandler<?>>[] getPageHandlers() {
      return null;
   }

   @TransitionMeta(name = "default")
   public void handleTransition(ActionContext<?> ctx) {
      // simple cases, nothing here
   }

   @ErrorActionMeta(name = "default")
   public void onError(ActionContext<?> ctx) {
      // ignore error, leave MVC to handle it
   }
}