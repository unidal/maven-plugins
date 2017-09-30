package org.unidal.maven.plugin.pom;

import java.util.List;
import java.util.Map;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.unidal.lookup.LookupException;
import org.unidal.lookup.annotation.Named;

@Named
public class MavenContainer implements Contextualizable {
   private PlexusContainer m_container;

   public void contextualize(Context context) throws ContextException {
      m_container = (PlexusContainer) context.get("plexus");
   }

   public PlexusContainer getContainer() {
      return m_container;
   }

   public <T> boolean hasComponent(Class<T> role) {
      return hasComponent(role, null);
   }

   public <T> boolean hasComponent(Class<T> role, Object roleHint) {
      return m_container.hasComponent(role.getName(), roleHint == null ? "default" : roleHint.toString());
   }

   public <T> T lookup(Class<T> role) throws LookupException {
      return lookup(role, null);
   }

   public <T> T lookup(Class<T> role, String roleHint) throws LookupException {
      try {
         return (T) m_container.lookup(role, roleHint == null ? "default" : roleHint.toString());
      } catch (ComponentLookupException e) {
         String key = role.getName() + ":" + (roleHint == null ? "default" : roleHint.toString());

         throw new LookupException("Component(" + key + ") lookup failure. Details: " + e.getMessage(), e);
      }
   }

   public <T> List<T> lookupList(Class<T> role) throws LookupException {
      try {
         return (List<T>) m_container.lookupList(role);
      } catch (ComponentLookupException e) {
         String key = role.getName();

         throw new LookupException("Component list(" + key + ") lookup failure. Details: " + e.getMessage(), e);
      }
   }

   public <T> Map<String, T> lookupMap(Class<T> role) throws LookupException {
      try {
         return (Map<String, T>) m_container.lookupMap(role);
      } catch (ComponentLookupException e) {
         String key = role.getName();

         throw new LookupException("Component map(" + key + ") lookup failure. Details: " + e.getMessage(), e);
      }
   }

   public void release(Object component) throws LookupException {
      if (component != null) {
         try {
            m_container.release(component);
         } catch (ComponentLifecycleException e) {
            throw new LookupException("Can't release component: " + component, e);
         }
      }
   }

   public void releaseAll(List<Object> components) throws LookupException {
      if (components != null) {
         try {
            m_container.releaseAll(components);
         } catch (ComponentLifecycleException e) {
            throw new LookupException("Can't release components: " + components, e);
         }
      }
   }

   public void releaseAll(Map<String, Object> components) throws LookupException {
      if (components != null) {
         try {
            m_container.releaseAll(components);
         } catch (ComponentLifecycleException e) {
            throw new LookupException("Can't release components: " + components, e);
         }
      }
   }

   public void setContainer(PlexusContainer container) {
      m_container = container;
   }
}
