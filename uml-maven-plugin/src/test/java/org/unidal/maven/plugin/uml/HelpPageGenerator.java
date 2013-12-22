package org.unidal.maven.plugin.uml;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.unidal.helper.Files;
import org.unidal.helper.Scanners;
import org.unidal.helper.Scanners.FileMatcher;
import org.unidal.web.jsp.function.CodecFunction;

public class HelpPageGenerator {
   @Test
   public void generateJsp() throws Exception {
      File base = new File(getClass().getResource("/webapp/help").getPath());
      String[] diagrams = base.list();

      if (diagrams != null) {
         for (String diagram : diagrams) {
            if (diagram.endsWith(".jsp") || diagram.startsWith(".")) {
               continue;
            }

            File dir = new File(base, diagram);
            File index = new File("src/main/resources/webapp/jsp/help/" + diagram + ".jsp");
            final List<String> paths = new ArrayList<String>();

            Scanners.forDir().scan(dir, new FileMatcher() {
               @Override
               public Direction matches(File base, String path) {
                  if (path.endsWith(".uml")) {
                     paths.add(path);
                  }

                  return Direction.DOWN;
               }
            });

            StringBuilder sb = new StringBuilder(4096);
            String template = Files.forIO().readFrom(getClass().getResourceAsStream("template.jsp"), "utf-8");

            Collections.sort(paths);
            
            for (String path : paths) {
               String id = path.substring(3, path.length() - 4);
               String uml = Files.forIO().readFrom(new File(dir, path), "utf-8");

               sb.append("<div>\r\n");
               sb.append("<h2>").append(id).append("</h2>\r\n");
               sb.append("<pre>").append(CodecFunction.htmlEncode(uml)).append("</pre>\r\n");
               sb.append("<img src='/uml/help/").append(diagram).append('/').append(path.replaceAll(" ", "+")).append("'>\r\n");
               sb.append("</div>\r\n\r\n");
            }

            System.out.println("Generating file " + index);

            index.getParentFile().mkdirs();
            Files.forIO().writeTo(index, template.replace("${help}", sb.toString()));
         }
      }
   }
}
