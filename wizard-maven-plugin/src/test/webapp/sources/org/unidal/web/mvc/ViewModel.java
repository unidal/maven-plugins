package org.unidal.web.mvc;

public abstract class ViewModel<P extends Page, A extends Action, M extends ActionContext<?>> {
   public ViewModel(M actionContext) {
   }

   public abstract A getDefaultAction();

   public void setAction(A action) {
   }

   public void setPage(P action) {
   }
}
