package org.unidal.maven.plugin.wizard.model.transform;

import org.unidal.maven.plugin.wizard.model.entity.Datasource;
import org.unidal.maven.plugin.wizard.model.entity.Group;
import org.unidal.maven.plugin.wizard.model.entity.Jdbc;
import org.unidal.maven.plugin.wizard.model.entity.Model;
import org.unidal.maven.plugin.wizard.model.entity.Module;
import org.unidal.maven.plugin.wizard.model.entity.Page;
import org.unidal.maven.plugin.wizard.model.entity.Table;
import org.unidal.maven.plugin.wizard.model.entity.Webapp;
import org.unidal.maven.plugin.wizard.model.entity.Wizard;

public interface IParser<T> {
   public Wizard parse(IMaker<T> maker, ILinker linker, T node);

   public void parseForDatasource(IMaker<T> maker, ILinker linker, Datasource parent, T node);

   public void parseForGroup(IMaker<T> maker, ILinker linker, Group parent, T node);

   public void parseForJdbc(IMaker<T> maker, ILinker linker, Jdbc parent, T node);

   public void parseForModel(IMaker<T> maker, ILinker linker, Model parent, T node);

   public void parseForModule(IMaker<T> maker, ILinker linker, Module parent, T node);

   public void parseForPage(IMaker<T> maker, ILinker linker, Page parent, T node);

   public void parseForTable(IMaker<T> maker, ILinker linker, Table parent, T node);

   public void parseForWebapp(IMaker<T> maker, ILinker linker, Webapp parent, T node);
}
