package org.unidal.codegen.framework.build;

import java.util.ArrayList;
import java.util.List;

import org.unidal.codegen.code.DefaultObfuscater;
import org.unidal.codegen.framework.DefaultFileStorage;
import org.unidal.codegen.framework.DefaultXslGenerator;
import org.unidal.codegen.framework.DefaultXmlAggregator;
import org.unidal.codegen.framework.DefaultXslTransformer;
import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;

class ComponentsConfigurator extends AbstractResourceConfigurator {
   public static void main(String[] args) {
      generatePlexusComponentsXmlFile(new ComponentsConfigurator());
   }

   @Override
   public List<Component> defineComponents() {
      List<Component> all = new ArrayList<Component>();

      all.add(A(DefaultXslGenerator.class));
      all.add(A(DefaultXmlAggregator.class));
      all.add(A(DefaultXslTransformer.class));
      all.add(A(DefaultFileStorage.class));

      all.add(A(DefaultObfuscater.class));

      return all;
   }

   @Override
   protected boolean isMavenPlugin() {
      return true;
   }
}
