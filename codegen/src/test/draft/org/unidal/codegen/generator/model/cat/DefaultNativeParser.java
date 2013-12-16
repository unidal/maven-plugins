package org.unidal.codegen.generator.model.cat;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.dianping.cat.consumer.transaction.model.IVisitor;
import com.dianping.cat.consumer.transaction.model.entity.AllDuration;
import com.dianping.cat.consumer.transaction.model.entity.Duration;
import com.dianping.cat.consumer.transaction.model.entity.Machine;
import com.dianping.cat.consumer.transaction.model.entity.Range;
import com.dianping.cat.consumer.transaction.model.entity.Range2;
import com.dianping.cat.consumer.transaction.model.entity.TransactionName;
import com.dianping.cat.consumer.transaction.model.entity.TransactionReport;
import com.dianping.cat.consumer.transaction.model.entity.TransactionType;
import com.dianping.cat.consumer.transaction.model.transform.DefaultLinker;

public class DefaultNativeParser implements IVisitor {

   private DefaultLinker m_linker = new DefaultLinker(true);

   private DataInputStream m_in;

   public DefaultNativeParser(InputStream in) {
      m_in = new DataInputStream(in);
   }

   public static TransactionReport parse(byte[] data) {
      return parse(new ByteArrayInputStream(data));
   }

   public static TransactionReport parse(InputStream in) {
      DefaultNativeParser parser = new DefaultNativeParser(in);
      TransactionReport transactionReport = new TransactionReport();

      try {
         transactionReport.accept(parser);
      } catch (RuntimeException e) {
         if (e.getCause() !=null && e.getCause() instanceof java.io.EOFException) {
            // ignore it
         } else {
            throw e;
         }
      }
      
      parser.m_linker.finish();
      return transactionReport;
   }

   @Override
   public void visitAllDuration(AllDuration allDuration) {
      byte tag;

      while ((tag = readTag()) != -1) {
         visitAllDurationChildren(allDuration, (tag & 0xFF) >> 2, tag & 0x3);
      }
   }

   protected void visitAllDurationChildren(AllDuration allDuration, int _field, int _type) {
      switch (_field) {
         case 1:
            allDuration.setValue(readInt());
            break;
         case 2:
            allDuration.setCount(readInt());
            break;
      }
   }

   @Override
   public void visitDuration(Duration duration) {
      byte tag;

      while ((tag = readTag()) != -1) {
         visitDurationChildren(duration, (tag & 0xFF) >> 2, tag & 0x3);
      }
   }

   protected void visitDurationChildren(Duration duration, int _field, int _type) {
      switch (_field) {
         case 1:
            duration.setValue(readInt());
            break;
         case 2:
            duration.setCount(readInt());
            break;
      }
   }

   @Override
   public void visitMachine(Machine machine) {
      byte tag;

      while ((tag = readTag()) != -1) {
         visitMachineChildren(machine, (tag & 0xFF) >> 2, tag & 0x3);
      }
   }

   protected void visitMachineChildren(Machine machine, int _field, int _type) {
      switch (_field) {
         case 1:
            machine.setIp(readString());
            break;
         case 33:
            if (_type == 1) {
              TransactionType types = new TransactionType();

              visitType(types);
              m_linker.onType(machine, types);
            } else if (_type == 2) {
               for (int i = readInt(); i > 0; i--) {
                 TransactionType types = new TransactionType();

                 visitType(types);
                 m_linker.onType(machine, types);
               }
            }
            break;
      }
   }

   @Override
   public void visitName(TransactionName name) {
      byte tag;

      while ((tag = readTag()) != -1) {
         visitNameChildren(name, (tag & 0xFF) >> 2, tag & 0x3);
      }
   }

   protected void visitNameChildren(TransactionName name, int _field, int _type) {
      switch (_field) {
         case 1:
            name.setId(readString());
            break;
         case 2:
            name.setTotalCount(readLong());
            break;
         case 3:
            name.setFailCount(readLong());
            break;
         case 4:
            name.setFailPercent(readDouble());
            break;
         case 5:
            name.setMin(readDouble());
            break;
         case 6:
            name.setMax(readDouble());
            break;
         case 7:
            name.setAvg(readDouble());
            break;
         case 8:
            name.setSum(readDouble());
            break;
         case 9:
            name.setSum2(readDouble());
            break;
         case 10:
            name.setStd(readDouble());
            break;
         case 11:
            name.setSuccessMessageUrl(readString());
            break;
         case 12:
            name.setFailMessageUrl(readString());
            break;
         case 13:
            name.setTps(readDouble());
            break;
         case 14:
            name.setLine95Value(readDouble());
            break;
         case 33:
            if (_type == 1) {
              Range ranges = new Range();

              visitRange(ranges);
              m_linker.onRange(name, ranges);
            } else if (_type == 2) {
               for (int i = readInt(); i > 0; i--) {
                 Range ranges = new Range();

                 visitRange(ranges);
                 m_linker.onRange(name, ranges);
               }
            }
            break;
         case 34:
            if (_type == 1) {
              Duration durations = new Duration();

              visitDuration(durations);
              m_linker.onDuration(name, durations);
            } else if (_type == 2) {
               for (int i = readInt(); i > 0; i--) {
                 Duration durations = new Duration();

                 visitDuration(durations);
                 m_linker.onDuration(name, durations);
               }
            }
            break;
      }
   }

   @Override
   public void visitRange(Range range) {
      byte tag;

      while ((tag = readTag()) != -1) {
         visitRangeChildren(range, (tag & 0xFF) >> 2, tag & 0x3);
      }
   }

   protected void visitRangeChildren(Range range, int _field, int _type) {
      switch (_field) {
         case 1:
            range.setValue(readInt());
            break;
         case 2:
            range.setCount(readInt());
            break;
         case 3:
            range.setSum(readDouble());
            break;
         case 4:
            range.setAvg(readDouble());
            break;
         case 5:
            range.setFails(readInt());
            break;
      }
   }

   @Override
   public void visitRange2(Range2 range2) {
      byte tag;

      while ((tag = readTag()) != -1) {
         visitRange2Children(range2, (tag & 0xFF) >> 2, tag & 0x3);
      }
   }

   protected void visitRange2Children(Range2 range2, int _field, int _type) {
      switch (_field) {
         case 1:
            range2.setValue(readInt());
            break;
         case 2:
            range2.setCount(readInt());
            break;
         case 3:
            range2.setSum(readDouble());
            break;
         case 4:
            range2.setAvg(readDouble());
            break;
         case 5:
            range2.setFails(readInt());
            break;
      }
   }

   @Override
   public void visitTransactionReport(TransactionReport transactionReport) {
      byte tag;

      if ((tag = readTag()) != -4) {
         throw new RuntimeException(String.format("Malformed payload, expected: %s, but was: %s!", -4, tag));
      }

      while ((tag = readTag()) != -1) {
         visitTransactionReportChildren(transactionReport, (tag & 0xFF) >> 2, tag & 0x3);
      }
   }

   protected void visitTransactionReportChildren(TransactionReport transactionReport, int _field, int _type) {
      switch (_field) {
         case 1:
            transactionReport.setDomain(readString());
            break;
         case 2:
            transactionReport.setStartTime(readDate());
            break;
         case 3:
            transactionReport.setEndTime(readDate());
            break;
         case 4:
            if (_type == 1) {
                  transactionReport.addDomain(readString());
            } else if (_type == 2) {
               for (int i = readInt(); i > 0; i--) {
                  transactionReport.addDomain(readString());
               }
            }
            break;
         case 5:
            if (_type == 1) {
                  transactionReport.addIp(readString());
            } else if (_type == 2) {
               for (int i = readInt(); i > 0; i--) {
                  transactionReport.addIp(readString());
               }
            }
            break;
         case 33:
            if (_type == 1) {
              Machine machines = new Machine();

              visitMachine(machines);
              m_linker.onMachine(transactionReport, machines);
            } else if (_type == 2) {
               for (int i = readInt(); i > 0; i--) {
                 Machine machines = new Machine();

                 visitMachine(machines);
                 m_linker.onMachine(transactionReport, machines);
               }
            }
            break;
      }
   }

   @Override
   public void visitType(TransactionType type) {
      byte tag;

      while ((tag = readTag()) != -1) {
         visitTypeChildren(type, (tag & 0xFF) >> 2, tag & 0x3);
      }
   }

   protected void visitTypeChildren(TransactionType type, int _field, int _type) {
      switch (_field) {
         case 1:
            type.setId(readString());
            break;
         case 2:
            type.setTotalCount(readLong());
            break;
         case 3:
            type.setFailCount(readLong());
            break;
         case 4:
            type.setFailPercent(readDouble());
            break;
         case 5:
            type.setMin(readDouble());
            break;
         case 6:
            type.setMax(readDouble());
            break;
         case 7:
            type.setAvg(readDouble());
            break;
         case 8:
            type.setSum(readDouble());
            break;
         case 9:
            type.setSum2(readDouble());
            break;
         case 10:
            type.setStd(readDouble());
            break;
         case 11:
            type.setSuccessMessageUrl(readString());
            break;
         case 12:
            type.setFailMessageUrl(readString());
            break;
         case 13:
            type.setTps(readDouble());
            break;
         case 14:
            type.setLine95Value(readDouble());
            break;
         case 33:
            if (_type == 1) {
              TransactionName names = new TransactionName();

              visitName(names);
              m_linker.onName(type, names);
            } else if (_type == 2) {
               for (int i = readInt(); i > 0; i--) {
                 TransactionName names = new TransactionName();

                 visitName(names);
                 m_linker.onName(type, names);
               }
            }
            break;
      }
   }

   private java.util.Date readDate() {
      try {
         return new java.util.Date(readVarint(64));
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   private double readDouble() {
      try {
         return m_in.readDouble();
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   private int readInt() {
      try {
         return (int) readVarint(32);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   private long readLong() {
      try {
         return readVarint(64);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   private String readString() {
      try {
         return m_in.readUTF();
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   private byte readTag() {
      try {
         return m_in.readByte();
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   protected long readVarint(final int length) throws IOException {
      int shift = 0;
      long result = 0;

      while (shift < length) {
         final byte b = m_in.readByte();
         result |= (long) (b & 0x7F) << shift;
         if ((b & 0x80) == 0) {
            return result;
         }
         shift += 7;
      }

      throw new RuntimeException("Malformed variable int " + length + "!");
   }
}
