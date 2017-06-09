package org.unidal.maven.plugin.project.model.transform;

import static org.unidal.maven.plugin.project.model.Constants.ATTR_ARTIFACTID;
import static org.unidal.maven.plugin.project.model.Constants.ATTR_BASELINE_SIGNATURE;
import static org.unidal.maven.plugin.project.model.Constants.ATTR_BASELINE_VERSION;
import static org.unidal.maven.plugin.project.model.Constants.ATTR_GROUPID;
import static org.unidal.maven.plugin.project.model.Constants.ATTR_NAME;
import static org.unidal.maven.plugin.project.model.Constants.ATTR_SIGNATURE;
import static org.unidal.maven.plugin.project.model.Constants.ATTR_STATUS;
import static org.unidal.maven.plugin.project.model.Constants.ATTR_TIMESTAMP;
import static org.unidal.maven.plugin.project.model.Constants.ATTR_TYPE;
import static org.unidal.maven.plugin.project.model.Constants.ATTR_VERSION;
import static org.unidal.maven.plugin.project.model.Constants.ENTITY_CONSTRUCTOR;
import static org.unidal.maven.plugin.project.model.Constants.ENTITY_FAILURE;
import static org.unidal.maven.plugin.project.model.Constants.ENTITY_FIELD;
import static org.unidal.maven.plugin.project.model.Constants.ENTITY_METHOD;
import static org.unidal.maven.plugin.project.model.Constants.ENTITY_REPORT;
import static org.unidal.maven.plugin.project.model.Constants.ENTITY_TYPE;

import java.lang.reflect.Array;
import java.util.Collection;

import org.unidal.maven.plugin.project.model.IEntity;
import org.unidal.maven.plugin.project.model.IVisitor;
import org.unidal.maven.plugin.project.model.entity.ConstructorModel;
import org.unidal.maven.plugin.project.model.entity.Failure;
import org.unidal.maven.plugin.project.model.entity.FieldModel;
import org.unidal.maven.plugin.project.model.entity.MethodModel;
import org.unidal.maven.plugin.project.model.entity.Report;
import org.unidal.maven.plugin.project.model.entity.TypeModel;

public class DefaultXmlBuilder implements IVisitor {

   private IVisitor m_visitor = this;

   private int m_level;

   private StringBuilder m_sb;

   private boolean m_compact;

   public DefaultXmlBuilder() {
      this(false);
   }

   public DefaultXmlBuilder(boolean compact) {
      this(compact, new StringBuilder(4096));
   }

   public DefaultXmlBuilder(boolean compact, StringBuilder sb) {
      m_compact = compact;
      m_sb = sb;
      m_sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n");
   }

   public String buildXml(IEntity<?> entity) {
      entity.accept(m_visitor);
      return m_sb.toString();
   }

   protected void endTag(String name) {
      m_level--;

      indent();
      m_sb.append("</").append(name).append(">\r\n");
   }

   protected String escape(Object value) {
      return escape(value, false);
   }
   
   protected String escape(Object value, boolean text) {
      if (value == null) {
         return null;
      }

      String str = toString(value);
      int len = str.length();
      StringBuilder sb = new StringBuilder(len + 16);

      for (int i = 0; i < len; i++) {
         final char ch = str.charAt(i);

         switch (ch) {
         case '<':
            sb.append("&lt;");
            break;
         case '>':
            sb.append("&gt;");
            break;
         case '&':
            sb.append("&amp;");
            break;
         case '"':
            if (!text) {
               sb.append("&quot;");
               break;
            }
         default:
            sb.append(ch);
            break;
         }
      }

      return sb.toString();
   }
   
   protected void indent() {
      if (!m_compact) {
         for (int i = m_level - 1; i >= 0; i--) {
            m_sb.append("   ");
         }
      }
   }

   protected void startTag(String name) {
      startTag(name, false, null);
   }
   
   protected void startTag(String name, boolean closed, java.util.Map<String, String> dynamicAttributes, Object... nameValues) {
      startTag(name, null, closed, dynamicAttributes, nameValues);
   }

   protected void startTag(String name, java.util.Map<String, String> dynamicAttributes, Object... nameValues) {
      startTag(name, null, false, dynamicAttributes, nameValues);
   }

   protected void startTag(String name, Object text, boolean closed, java.util.Map<String, String> dynamicAttributes, Object... nameValues) {
      indent();

      m_sb.append('<').append(name);

      int len = nameValues.length;

      for (int i = 0; i + 1 < len; i += 2) {
         Object attrName = nameValues[i];
         Object attrValue = nameValues[i + 1];

         if (attrValue != null) {
            m_sb.append(' ').append(attrName).append("=\"").append(escape(attrValue)).append('"');
         }
      }

      if (dynamicAttributes != null) {
         for (java.util.Map.Entry<String, String> e : dynamicAttributes.entrySet()) {
            m_sb.append(' ').append(e.getKey()).append("=\"").append(escape(e.getValue())).append('"');
         }
      }

      if (text != null && closed) {
         m_sb.append('>');
         m_sb.append(escape(text, true));
         m_sb.append("</").append(name).append(">\r\n");
      } else {
         if (closed) {
            m_sb.append('/');
         } else {
            m_level++;
         }
   
         m_sb.append(">\r\n");
      }
   }

   @SuppressWarnings("unchecked")
   protected String toString(Object value) {
      if (value instanceof String) {
         return (String) value;
      } else if (value instanceof Collection) {
         Collection<Object> list = (Collection<Object>) value;
         StringBuilder sb = new StringBuilder(32);
         boolean first = true;

         for (Object item : list) {
            if (first) {
               first = false;
            } else {
               sb.append(',');
            }

            if (item != null) {
               sb.append(item);
            }
         }

         return sb.toString();
      } else if (value.getClass().isArray()) {
         int len = Array.getLength(value);
         StringBuilder sb = new StringBuilder(32);
         boolean first = true;

         for (int i = 0; i < len; i++) {
            Object item = Array.get(value, i);

            if (first) {
               first = false;
            } else {
               sb.append(',');
            }

            if (item != null) {
               sb.append(item);
            }
         }
		
         return sb.toString();
      }
 
      return String.valueOf(value);
   }

   @Override
   public void visitConstructor(ConstructorModel constructor) {
      startTag(ENTITY_CONSTRUCTOR, true, null, ATTR_NAME, constructor.getName(), ATTR_SIGNATURE, constructor.getSignature(), ATTR_BASELINE_SIGNATURE, constructor.getBaselineSignature());
   }

   @Override
   public void visitFailure(Failure failure) {
      startTag(ENTITY_FAILURE, null, ATTR_TYPE, failure.getType());

      if (!failure.getTypes().isEmpty()) {
         for (TypeModel type : failure.getTypes()) {
            type.accept(m_visitor);
         }
      }

      endTag(ENTITY_FAILURE);
   }

   @Override
   public void visitField(FieldModel field) {
      startTag(ENTITY_FIELD, true, null, ATTR_NAME, field.getName(), ATTR_SIGNATURE, field.getSignature(), ATTR_BASELINE_SIGNATURE, field.getBaselineSignature());
   }

   @Override
   public void visitMethod(MethodModel method) {
      startTag(ENTITY_METHOD, true, null, ATTR_NAME, method.getName(), ATTR_SIGNATURE, method.getSignature(), ATTR_BASELINE_SIGNATURE, method.getBaselineSignature());
   }

   @Override
   public void visitReport(Report report) {
      startTag(ENTITY_REPORT, null, ATTR_GROUPID, report.getGroupId(), ATTR_ARTIFACTID, report.getArtifactId(), ATTR_VERSION, report.getVersion(), ATTR_BASELINE_VERSION, report.getBaselineVersion(), ATTR_STATUS, report.getStatus(), ATTR_TIMESTAMP, report.getTimestamp());

      if (!report.getFailures().isEmpty()) {
         for (Failure failure : report.getFailures()) {
            failure.accept(m_visitor);
         }
      }

      endTag(ENTITY_REPORT);
   }

   @Override
   public void visitType(TypeModel type) {
      startTag(ENTITY_TYPE, null, ATTR_NAME, type.getName(), ATTR_SIGNATURE, type.getSignature(), ATTR_BASELINE_SIGNATURE, type.getBaselineSignature());

      if (!type.getFields().isEmpty()) {
         for (FieldModel field : type.getFields()) {
            field.accept(m_visitor);
         }
      }

      if (!type.getConstructors().isEmpty()) {
         for (ConstructorModel constructor : type.getConstructors()) {
            constructor.accept(m_visitor);
         }
      }

      if (!type.getMethods().isEmpty()) {
         for (MethodModel method : type.getMethods()) {
            method.accept(m_visitor);
         }
      }

      endTag(ENTITY_TYPE);
   }
}
