package org.unidal.maven.plugin.project.plugin.transform;

import java.util.ArrayList;
import java.util.List;
import org.unidal.maven.plugin.project.plugin.entity.PluginMetadata;
import org.unidal.maven.plugin.project.plugin.entity.Versioning;
import org.unidal.maven.plugin.project.plugin.entity.Versions;

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
   public boolean onVersioning(final PluginMetadata parent, final Versioning versioning) {
      parent.setVersioning(versioning);
      return true;
   }

   @Override
   public boolean onVersions(final Versioning parent, final Versions versions) {
      parent.setVersions(versions);
      return true;
   }
}
