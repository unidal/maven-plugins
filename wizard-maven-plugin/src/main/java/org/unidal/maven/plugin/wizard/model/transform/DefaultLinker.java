package org.unidal.maven.plugin.wizard.model.transform;

import java.util.ArrayList;
import java.util.List;
import org.unidal.maven.plugin.wizard.model.entity.Datasource;
import org.unidal.maven.plugin.wizard.model.entity.Group;
import org.unidal.maven.plugin.wizard.model.entity.Jdbc;
import org.unidal.maven.plugin.wizard.model.entity.Model;
import org.unidal.maven.plugin.wizard.model.entity.Module;
import org.unidal.maven.plugin.wizard.model.entity.Page;
import org.unidal.maven.plugin.wizard.model.entity.Table;
import org.unidal.maven.plugin.wizard.model.entity.Webapp;
import org.unidal.maven.plugin.wizard.model.entity.Wizard;

public class DefaultLinker implements ILinker {
   @SuppressWarnings("unused")
   private boolean m_deferrable;

   private List<Runnable> m_deferedJobs = new ArrayList<Runnable>();

   public DefaultLinker(boolean deferrable) {
      m_deferrable = deferrable;
   }

   public void finish() {
      for (Runnable job : m_deferedJobs) {
         job.run();
      }
   }

   @Override
   public boolean onDatasource(final Jdbc parent, final Datasource datasource) {
      parent.setDatasource(datasource);
      return true;
   }

   @Override
   public boolean onGroup(final Jdbc parent, final Group group) {
      parent.addGroup(group);
      return true;
   }

   @Override
   public boolean onJdbc(final Wizard parent, final Jdbc jdbc) {
      parent.addJdbc(jdbc);
      return true;
   }

   @Override
   public boolean onModel(final Wizard parent, final Model model) {
      parent.addModel(model);
      return true;
   }

   @Override
   public boolean onModule(final Webapp parent, final Module module) {
      parent.addModule(module);
      return true;
   }

   @Override
   public boolean onPage(final Module parent, final Page page) {
      parent.addPage(page);
      return true;
   }

   @Override
   public boolean onTable(final Group parent, final Table table) {
      parent.addTable(table);
      return true;
   }

   @Override
   public boolean onWebapp(final Wizard parent, final Webapp webapp) {
      parent.setWebapp(webapp);
      return true;
   }
}
