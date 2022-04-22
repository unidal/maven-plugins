package org.unidal.web.mvc.view;

import java.io.IOException;

import javax.servlet.ServletException;

import org.unidal.lookup.annotation.Named;
import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.ViewModel;

@Named
public class HtmlTemplate {

   public void render(String template, ActionContext<?> ctx, ViewModel<?, ?, ?> model)
         throws ServletException, IOException {

   }
}
