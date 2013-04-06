package org.unidal.maven.plugin.wizard.webapp;

import java.util.List;

import org.unidal.maven.plugin.wizard.template.entity.Field;
import org.unidal.maven.plugin.wizard.template.entity.Group;
import org.unidal.maven.plugin.wizard.template.entity.Model;
import org.unidal.maven.plugin.wizard.template.entity.Page;
import org.unidal.maven.plugin.wizard.template.entity.Single;
import org.unidal.maven.plugin.wizard.template.entity.Template;
import org.unidal.maven.plugin.wizard.template.entity.Text;
import org.unidal.maven.plugin.wizard.template.entity.View;
import org.unidal.maven.plugin.wizard.template.transform.BaseVisitor;

public class TemplateBuilder extends BaseVisitor {
   private String m_moduleName;

   private String m_pageName;

   private String m_packageName;

   private String m_entityName;

   private List<Field> m_fields;

   public TemplateBuilder(String moduleName, String pageName, String packageName, String entityName, List<Field> fields) {
      m_moduleName = moduleName;
      m_pageName = pageName;
      m_packageName = packageName;
      m_entityName = entityName;
      m_fields = fields;
   }

   private String getTitle(String name) {
      int len = name.length();
      StringBuilder sb = new StringBuilder(len);
      boolean capital = true;

      for (int i = 0; i < len; i++) {
         char ch = name.charAt(i);

         switch (ch) {
         case '-':
         case '_':
            sb.append(' ');
            capital = true;
            break;
         default:
            if (capital) {
               capital = false;
               sb.append(Character.toUpperCase(ch));
            } else {
               sb.append(ch);
            }

            break;
         }
      }

      return sb.toString();
   }

   @Override
   public void visitField(Field field) {
      field.setTitle(getTitle(field.getName()));

      super.visitField(field);
   }

   @Override
   public void visitModel(Model model) {
      model.setPackage(m_packageName);
      model.setName(m_entityName);

      model.getFields().clear();

      for (Field field : m_fields) {
         model.addField(field);

         visitField(field);
      }
   }

   @Override
   public void visitPage(Page page) {
      Single single = page.getSingle();

      if (single == null) {
         single = new Single();
         page.setSingle(single);
      }

      visitSingle(single);
   }

   @Override
   public void visitSingle(Single single) {
      Model model = single.getModel();

      if (model == null) {
         model = new Model();
         single.setModel(model);
      }

      visitModel(model);

      View view = single.getView();

      if (view == null) {
         view = new View();
         single.setView(view);
      }

      visitView(view);
   }

   @Override
   public void visitView(View view) {
      view.getGroups().clear();

      for (Field field : m_fields) {
         Group group = new Group();
         Text text = new Text();

         text.setName(field.getName());
         group.setTitle(field.getTitle());
         group.setText(text);

         view.getGroups().add(group);
      }
   }

   @Override
   public void visitTemplate(Template template) {
      Page page = null;

      for (Page p : template.getPages()) {
         if (m_pageName.equals(p.getName()) && m_moduleName.equals(p.getModule())) {
            page = p;
            break;
         }
      }

      if (page == null) {
         page = new Page();

         page.setModule(m_moduleName);
         page.setName(m_pageName);
         template.addPage(page);
      }

      visitPage(page);
   }
}
