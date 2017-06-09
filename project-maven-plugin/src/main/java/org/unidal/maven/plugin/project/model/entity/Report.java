package org.unidal.maven.plugin.project.model.entity;

import java.util.ArrayList;
import java.util.List;

import org.unidal.maven.plugin.project.model.BaseEntity;
import org.unidal.maven.plugin.project.model.IVisitor;

public class Report extends BaseEntity<Report> {
   private String m_groupId;

   private String m_artifactId;

   private String m_version;

   private String m_baselineVersion;

   private String m_status;

   private String m_timestamp;

   private List<Failure> m_failures = new ArrayList<Failure>();

   public Report() {
   }

   @Override
   public void accept(IVisitor visitor) {
      visitor.visitReport(this);
   }

   public Report addFailure(Failure failure) {
      m_failures.add(failure);
      return this;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof Report) {
         Report _o = (Report) obj;

         if (!equals(getGroupId(), _o.getGroupId())) {
            return false;
         }

         if (!equals(getArtifactId(), _o.getArtifactId())) {
            return false;
         }

         if (!equals(getVersion(), _o.getVersion())) {
            return false;
         }

         if (!equals(getBaselineVersion(), _o.getBaselineVersion())) {
            return false;
         }

         if (!equals(getStatus(), _o.getStatus())) {
            return false;
         }

         if (!equals(getTimestamp(), _o.getTimestamp())) {
            return false;
         }

         if (!equals(getFailures(), _o.getFailures())) {
            return false;
         }


         return true;
      }

      return false;
   }

   public Failure findFailure(String type) {
      for (Failure failure : m_failures) {
         if (!equals(failure.getType(), type)) {
            continue;
         }

         return failure;
      }

      return null;
   }

   public String getArtifactId() {
      return m_artifactId;
   }

   public String getBaselineVersion() {
      return m_baselineVersion;
   }

   public List<Failure> getFailures() {
      return m_failures;
   }

   public String getGroupId() {
      return m_groupId;
   }

   public String getStatus() {
      return m_status;
   }

   public String getTimestamp() {
      return m_timestamp;
   }

   public String getVersion() {
      return m_version;
   }

   @Override
   public int hashCode() {
      int hash = 0;

      hash = hash * 31 + (m_groupId == null ? 0 : m_groupId.hashCode());
      hash = hash * 31 + (m_artifactId == null ? 0 : m_artifactId.hashCode());
      hash = hash * 31 + (m_version == null ? 0 : m_version.hashCode());
      hash = hash * 31 + (m_baselineVersion == null ? 0 : m_baselineVersion.hashCode());
      hash = hash * 31 + (m_status == null ? 0 : m_status.hashCode());
      hash = hash * 31 + (m_timestamp == null ? 0 : m_timestamp.hashCode());
      for (Failure e : m_failures) {
         hash = hash * 31 + (e == null ? 0 :e.hashCode());
      }


      return hash;
   }

   @Override
   public void mergeAttributes(Report other) {
      if (other.getGroupId() != null) {
         m_groupId = other.getGroupId();
      }

      if (other.getArtifactId() != null) {
         m_artifactId = other.getArtifactId();
      }

      if (other.getVersion() != null) {
         m_version = other.getVersion();
      }

      if (other.getBaselineVersion() != null) {
         m_baselineVersion = other.getBaselineVersion();
      }

      if (other.getStatus() != null) {
         m_status = other.getStatus();
      }

      if (other.getTimestamp() != null) {
         m_timestamp = other.getTimestamp();
      }
   }

   public Failure removeFailure(String type) {
      int len = m_failures.size();

      for (int i = 0; i < len; i++) {
         Failure failure = m_failures.get(i);

         if (!equals(failure.getType(), type)) {
            continue;
         }

         return m_failures.remove(i);
      }

      return null;
   }

   public Report setArtifactId(String artifactId) {
      m_artifactId = artifactId;
      return this;
   }

   public Report setBaselineVersion(String baselineVersion) {
      m_baselineVersion = baselineVersion;
      return this;
   }

   public Report setGroupId(String groupId) {
      m_groupId = groupId;
      return this;
   }

   public Report setStatus(String status) {
      m_status = status;
      return this;
   }

   public Report setTimestamp(String timestamp) {
      m_timestamp = timestamp;
      return this;
   }

   public Report setVersion(String version) {
      m_version = version;
      return this;
   }

}
