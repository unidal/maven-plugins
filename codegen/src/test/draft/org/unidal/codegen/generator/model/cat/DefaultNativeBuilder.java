package org.unidal.codegen.generator.model.cat;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.dianping.cat.consumer.transaction.model.IEntity;
import com.dianping.cat.consumer.transaction.model.IVisitor;
import com.dianping.cat.consumer.transaction.model.IVisitorEnabled;
import com.dianping.cat.consumer.transaction.model.entity.AllDuration;
import com.dianping.cat.consumer.transaction.model.entity.Duration;
import com.dianping.cat.consumer.transaction.model.entity.Machine;
import com.dianping.cat.consumer.transaction.model.entity.Range;
import com.dianping.cat.consumer.transaction.model.entity.Range2;
import com.dianping.cat.consumer.transaction.model.entity.TransactionName;
import com.dianping.cat.consumer.transaction.model.entity.TransactionReport;
import com.dianping.cat.consumer.transaction.model.entity.TransactionType;

public class DefaultNativeBuilder implements IVisitor, IVisitorEnabled {

   private IVisitor m_visitor;

   private DataOutputStream m_out;

   public DefaultNativeBuilder(OutputStream out) {
      m_out = new DataOutputStream(out);
      m_visitor = this;
   }

   public static byte[] build(IEntity<?> entity) throws IOException {
      ByteArrayOutputStream out = new ByteArrayOutputStream(8192);

      build(entity, out);
      return out.toByteArray();
   }

   public static void build(IEntity<?> entity, OutputStream out) throws IOException {
      DefaultNativeBuilder builder = new DefaultNativeBuilder(out);

      builder.buildDescriptor();
      entity.accept(builder);
   }

   @Override
   public void enableVisitor(IVisitor visitor) {
      m_visitor = visitor;
   }

   @Override
   public void visitAllDuration(AllDuration allDuration) {
      if (allDuration.getValue() != null) {
         writeTag(1, 1);
         writeInt(allDuration.getValue());
      }

      writeTag(2, 0);
      writeInt(allDuration.getCount());

      writeTag(63, 3);
   }

   @Override
   public void visitDuration(Duration duration) {
      if (duration.getValue() != null) {
         writeTag(1, 1);
         writeInt(duration.getValue());
      }

      writeTag(2, 0);
      writeInt(duration.getCount());

      writeTag(63, 3);
   }

   @Override
   public void visitMachine(Machine machine) {
      if (machine.getIp() != null) {
         writeTag(1, 1);
         writeString(machine.getIp());
      }

      if (!machine.getTypes().isEmpty()) {
         writeTag(33, 2);
         writeInt(machine.getTypes().size());

         for (TransactionType type : machine.getTypes().values()) {
            type.accept(m_visitor);
         }
      }

      writeTag(63, 3);
   }

   @Override
   public void visitName(TransactionName name) {
      if (name.getId() != null) {
         writeTag(1, 1);
         writeString(name.getId());
      }

      writeTag(2, 0);
      writeLong(name.getTotalCount());

      writeTag(3, 0);
      writeLong(name.getFailCount());

      writeTag(4, 0);
      writeDouble(name.getFailPercent());

      writeTag(5, 0);
      writeDouble(name.getMin());

      writeTag(6, 0);
      writeDouble(name.getMax());

      writeTag(7, 0);
      writeDouble(name.getAvg());

      writeTag(8, 0);
      writeDouble(name.getSum());

      writeTag(9, 0);
      writeDouble(name.getSum2());

      writeTag(10, 0);
      writeDouble(name.getStd());

      if (name.getSuccessMessageUrl() != null) {
         writeTag(11, 1);
         writeString(name.getSuccessMessageUrl());
      }

      if (name.getFailMessageUrl() != null) {
         writeTag(12, 1);
         writeString(name.getFailMessageUrl());
      }

      writeTag(13, 0);
      writeDouble(name.getTps());

      writeTag(14, 0);
      writeDouble(name.getLine95Value());

      if (!name.getRanges().isEmpty()) {
         writeTag(33, 2);
         writeInt(name.getRanges().size());

         for (Range range : name.getRanges().values()) {
            range.accept(m_visitor);
         }
      }

      if (!name.getDurations().isEmpty()) {
         writeTag(34, 2);
         writeInt(name.getDurations().size());

         for (Duration duration : name.getDurations().values()) {
            duration.accept(m_visitor);
         }
      }

      writeTag(63, 3);
   }

   @Override
   public void visitRange(Range range) {
      if (range.getValue() != null) {
         writeTag(1, 1);
         writeInt(range.getValue());
      }

      writeTag(2, 0);
      writeInt(range.getCount());

      writeTag(3, 0);
      writeDouble(range.getSum());

      writeTag(4, 0);
      writeDouble(range.getAvg());

      writeTag(5, 0);
      writeInt(range.getFails());

      writeTag(63, 3);
   }

   private void buildDescriptor() throws IOException {
      writeVarint(8); // entities
      writeString("transaction-report"); // name
      writeVarint(6); // fields
      writeString("domain");
      writeString("start-time");
      writeString("transaction-report"); // name
      writeVarint(0); // flag
      writeVarint(6); // fields
      writeString("domain");
      writeString("start-time");
   }

   @Override
   public void visitRange2(Range2 range2) {
      if (range2.getValue() != null) {
         writeTag(1, 1);
         writeInt(range2.getValue());
      }

      writeTag(2, 0);
      writeInt(range2.getCount());

      writeTag(3, 0);
      writeDouble(range2.getSum());

      writeTag(4, 0);
      writeDouble(range2.getAvg());

      writeTag(5, 0);
      writeInt(range2.getFails());

      writeTag(63, 3);
   }

   @Override
   public void visitTransactionReport(TransactionReport transactionReport) {
      writeTag(63, 0);

      if (transactionReport.getDomain() != null) {
         writeTag(1, 1);
         writeString(transactionReport.getDomain());
      }

      if (transactionReport.getStartTime() != null) {
         writeTag(2, 1);
         writeDate(transactionReport.getStartTime());
      }

      if (transactionReport.getEndTime() != null) {
         writeTag(3, 1);
         writeDate(transactionReport.getEndTime());
      }

      if (!transactionReport.getDomainNames().isEmpty()) {
         writeTag(4, 2);
         writeInt(transactionReport.getDomainNames().size());

         for (String domain : transactionReport.getDomainNames()) {
            writeString(domain);
         }
      }

      if (!transactionReport.getIps().isEmpty()) {
         writeTag(5, 2);
         writeInt(transactionReport.getIps().size());

         for (String ip : transactionReport.getIps()) {
            writeString(ip);
         }
      }

      if (!transactionReport.getMachines().isEmpty()) {
         writeTag(33, 2);
         writeInt(transactionReport.getMachines().size());

         for (Machine machine : transactionReport.getMachines().values()) {
            machine.accept(m_visitor);
         }
      }

      writeTag(63, 3);
   }

   @Override
   public void visitType(TransactionType type) {
      if (type.getId() != null) {
         writeTag(1, 1);
         writeString(type.getId());
      }

      writeTag(2, 0);
      writeLong(type.getTotalCount());

      writeTag(3, 0);
      writeLong(type.getFailCount());

      writeTag(4, 0);
      writeDouble(type.getFailPercent());

      writeTag(5, 0);
      writeDouble(type.getMin());

      writeTag(6, 0);
      writeDouble(type.getMax());

      writeTag(7, 0);
      writeDouble(type.getAvg());

      writeTag(8, 0);
      writeDouble(type.getSum());

      writeTag(9, 0);
      writeDouble(type.getSum2());

      writeTag(10, 0);
      writeDouble(type.getStd());

      if (type.getSuccessMessageUrl() != null) {
         writeTag(11, 1);
         writeString(type.getSuccessMessageUrl());
      }

      if (type.getFailMessageUrl() != null) {
         writeTag(12, 1);
         writeString(type.getFailMessageUrl());
      }

      writeTag(13, 0);
      writeDouble(type.getTps());

      writeTag(14, 0);
      writeDouble(type.getLine95Value());

      if (!type.getNames().isEmpty()) {
         writeTag(33, 2);
         writeInt(type.getNames().size());

         for (TransactionName name : type.getNames().values()) {
            name.accept(m_visitor);
         }
      }

      writeTag(63, 3);
   }

   private void writeDate(java.util.Date value) {
      try {
         writeVarint(value.getTime());
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   private void writeDouble(double value) {
      try {
         m_out.writeDouble(value);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   private void writeInt(int value) {
      try {
         writeVarint(value & 0xFFFFFFFFL);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   private void writeLong(long value) {
      try {
         writeVarint(value);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   private void writeString(String value) {
      try {
         m_out.writeUTF(value);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   private void writeTag(int field, int type) {
      try {
         m_out.writeByte((field << 2) + type);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   protected void writeVarint(long value) throws IOException {
      while (true) {
         if ((value & ~0x7FL) == 0) {
            m_out.writeByte((byte) value);
            return;
         } else {
            m_out.writeByte(((byte) value & 0x7F) | 0x80);
            value >>>= 7;
         }
      }
   }
}
