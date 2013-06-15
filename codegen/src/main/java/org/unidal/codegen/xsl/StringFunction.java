package org.unidal.codegen.xsl;

import java.io.IOException;
import java.util.List;

import org.unidal.helper.Splitters;

public class StringFunction {
   /**
    * Used by model/entity/entity.xsl
    */
   public static String trimMethod(String str, int indent) throws IOException {
      List<String> lines = Splitters.by('\n').split(str);
      StringBuilder sb = new StringBuilder(str.length());

      while (lines.size() > 0) {
         if (lines.get(0).trim().length() == 0) {
            lines.remove(0);
         } else {
            break;
         }
      }

      while (lines.size() > 0) {
         if (lines.get(lines.size() - 1).trim().length() == 0) {
            lines.remove(lines.size() - 1);
         } else {
            break;
         }
      }

      int spacesToTrim = 0;

      if (lines.size() > 0) {
         String first = lines.get(0);
         int len = first.length();
         int spaces = 0;

         for (int i = 0; i < len; i++) {
            char ch = first.charAt(i);

            if (ch == ' ') {
               spaces++;
            } else if (ch == '\t') {
               spaces += 3;
            } else {
               break;
            }
         }

         spacesToTrim = spaces - indent;
      }

      for (String line : lines) {
         int len = line.length();
         int spaces = 0;

         for (int i = 0; i < len; i++) {
            char ch = line.charAt(i);

            if (spaces < spacesToTrim) {
               if (ch == ' ') {
                  spaces++;
               } else if (ch == '\t') {
                  spaces += 3;
               }
            } else {
               while (spaces > spacesToTrim) {
                  sb.append(' ');
                  spaces--;
               }

               sb.append(ch);
            }
         }

         sb.append("\r\n");
      }

      return sb.toString();
   }
}
