package org.unidal.maven.plugin.project.group.transform;

import java.util.ArrayList;
import java.util.List;
import org.unidal.maven.plugin.project.group.entity.GroupMetadata;
import org.unidal.maven.plugin.project.group.entity.Plugin;

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
   public boolean onPlugin(final GroupMetadata parent, final Plugin plugin) {
      parent.addPlugin(plugin);
      return true;
   }
}
