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

public interface IMaker<T> {

   public Datasource buildDatasource(T node);

   public Group buildGroup(T node);

   public Jdbc buildJdbc(T node);

   public Model buildModel(T node);

   public Module buildModule(T node);

   public Page buildPage(T node);

   public Table buildTable(T node);

   public Webapp buildWebapp(T node);

   public Wizard buildWizard(T node);
}
