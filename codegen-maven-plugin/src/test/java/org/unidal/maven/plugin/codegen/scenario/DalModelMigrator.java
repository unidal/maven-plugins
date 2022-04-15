package org.unidal.maven.plugin.codegen.scenario;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.junit.Test;
import org.unidal.helper.Files;
import org.unidal.helper.Scanners;
import org.unidal.helper.Scanners.DirMatcher;

public class DalModelMigrator {
   @Test
   public void migrate() throws IOException {
      File base = getTestResourcesBase();
      List<File> dirs = Scanners.forDir().scan(base, new DirMatcher() {
         @Override
         public Direction matches(File base, String path) {
            File file = new File(base, path);

            if (file.isDirectory() && !path.equals("cat")) {
               return Direction.MATCHED;
            } else {
               return Direction.DOWN;
            }
         }
      });

      for (File dir : dirs) {
         migrate(dir);
      }
   }

   private File getTestResourcesBase() {
      return new File("src/test/resources/org/unidal/codegen/generator/model");
   }

   private File getScenarioBase(String scenario) {
      return new File("src/test/dal-model/" + scenario);
   }

   private void migrate(File dir) throws IOException {
      File[] manifests = dir.listFiles(new FileFilter() {
         @Override
         public boolean accept(File path) {
            return path.getName().endsWith("manifest.xml");
         }
      });

      File migrated = new File(dir, "migrarted.txt");
      String content = Files.forIO().readFrom(migrated, "utf-8");

      if (content == null || content.startsWith(":")) {
         BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
         StringBuilder sb = new StringBuilder(1024);

         for (File manifest : manifests) {
            System.out.println(String.format("Please input scenario: [%s]", manifest));
            String scenario = reader.readLine();

            migrate(dir, manifest, scenario);
            sb.append(scenario).append(":").append(manifest).append("\r\n");
         }

         Files.forIO().writeTo(migrated, sb.toString());
      }
   }

   private void migrate(File dir, File manifest, String scenario) throws IOException {
      // determine file prefix
      String name = manifest.getName();
      final String prefix;

      if (name.equals("manifest.xml")) {
         prefix = null;
      } else {
         prefix = name.substring(0, name.length() - "-manifest.xml".length());
      }

      // find the matched files
      File[] files = dir.listFiles(new FilenameFilter() {
         @Override
         public boolean accept(File dir, String name) {
            if (prefix == null) {
               return "model.xml".equals(name) || "codegen.xml".equals(name) || "manifest.xml".equals(name);
            } else {
               return name.startsWith(prefix + "-");
            }
         }
      });

      // copy them to "model" folder
      File base = getScenarioBase(scenario);
      File model = new File(base, "model");

      if (!model.exists()) {
         model.mkdirs();
      }

      for (File file : files) {
         System.out.println("Creating: " + new File(model, file.getName()));

         Files.forDir().copyFile(file, new File(model, file.getName()));
      }
   }
}
