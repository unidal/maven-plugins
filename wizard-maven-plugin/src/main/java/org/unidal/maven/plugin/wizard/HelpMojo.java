package org.unidal.maven.plugin.wizard;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.plugin.descriptor.Parameter;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.unidal.helper.Splitters;

/**
 * Describe all mojos in the plugin.
 * 
 * @goal help
 * @requiresProject false
 * @author Frankie Wu
 */
public class HelpMojo extends AbstractMojo {
   /**
    * Specify one or many goals separated by comma(',').
    * 
    * @parameter expression="${goal}" default-value=""
    */
   private String goal;

   private PluginDescriptor m_pd;

   private void describeGoal(String goal) {
      MojoDescriptor md = m_pd.getMojo(goal);

      info("%s:%s", m_pd.getGoalPrefix(), md.getGoal());

      List<Parameter> parameters = md.getParameters();

      Collections.sort(parameters, new Comparator<Parameter>() {
         @Override
         public int compare(Parameter o1, Parameter o2) {
            return o1.getName().compareTo(o2.getName());
         }
      });

      for (Parameter p : parameters) {
         if (!p.isEditable()) {
            continue;
         }

         info("   -D%-20s   - %s", p.getName(), p.getDescription());

         String d = p.getDefaultValue();

         if (d != null) {
            info("     %-20s     Default: [%s]", "", d);
         }
      }

      info("");
   }

   @Override
   public void execute() throws MojoExecutionException, MojoFailureException {
      m_pd = (PluginDescriptor) super.getPluginContext().get("pluginDescriptor");

      if (goal != null && goal.length() > 0) {
         List<String> list = Splitters.by(',').noEmptyItem().trim().split(goal);

         for (String item : list) {
            describeGoal(item);
         }
      } else {
         showMojoList();
      }
   }

   private void showMojoList() {
      List<MojoDescriptor> mds = m_pd.getMojos();

      Collections.sort(mds, new Comparator<MojoDescriptor>() {
         @Override
         public int compare(MojoDescriptor o1, MojoDescriptor o2) {
            String g1 = o1.getGoal();
            String g2 = o2.getGoal();

            if (g1.equals("help")) {
               return -1;
            } else if (g2.equals("help")) {
               return 1;
            }

            return g1.compareTo(g2);
         }
      });

      info("Usage:");

      for (MojoDescriptor md : mds) {
         info("   %s:%-20s   - %s", m_pd.getGoalPrefix(), md.getGoal(), md.getDescription());
      }

      info("");
   }

   private void info(String pattern, Object... args) {
      getLog().info(String.format(pattern, args));
   }
}
