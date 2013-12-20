package org.unidal.maven.plugin.uml;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.ISourceFileReader;
import net.sourceforge.plantuml.Option;
import net.sourceforge.plantuml.SourceFileReader2;

import org.unidal.helper.Files;

public class UmlServlet extends HttpServlet {
   private static final long serialVersionUID = 1L;

   @Override
   protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
      UmlViewModel model = new UmlViewModel(req);
      String uml = req.getParameter("uml");
      String op = req.getParameter("op");

      if (uml != null) {
         model.setUml(uml);

         try {
            model.setSvg(generateSvg(uml));
         } catch (InterruptedException e) {
            // ignore it
         }
      }

      if ("text".equals(op) || "svg".equals(op)) {
         String svg = model.getSvg();

         if (svg != null) {
            byte[] data = svg.getBytes("utf-8");

            if ("text".equals(op)) {
               res.setContentType("text/plain; charset=utf-8");
            } else if ("svg".equals(op)) {
               res.setContentType("image/svg+xml; charset=utf-8");
            }

            res.setContentLength(data.length);
            res.getOutputStream().write(data);
         } else {
            res.sendError(400, "Invalid uml!");
         }
      } else {
         showPage(req, res, model);
      }
   }

   private String generateSvg(String uml) throws IOException, InterruptedException {
      long timestamp = System.currentTimeMillis();
      String charset = "utf-8";
      File source = new File("target/tmp/" + timestamp + ".uml");
      File target = new File("target/tmp/" + timestamp + ".svg");
      Option option = new Option();

      option.setFileFormat(FileFormat.SVG);
      option.setCharset(charset);

      if (!uml.trim().startsWith("@startuml")) {
         uml = "@startuml\n" + uml;
      }

      if (!uml.trim().endsWith("@enduml")) {
         uml = uml + "\n@enduml";
      }

      source.getParentFile().mkdirs();
      Files.forIO().writeTo(source, uml.getBytes(charset));

      ISourceFileReader reader = new SourceFileReader2(option.getDefaultDefines(), source, target, option.getConfig(),
            option.getCharset(), option.getFileFormatOption());

      try {
         if (!reader.hasError() && !reader.getGeneratedImages().isEmpty()) {
            String result = Files.forIO().readFrom(target, charset);

            return result;
         } else {
            return null;
         }
      } finally {
         Files.forDir().delete(source);
         Files.forDir().delete(target);
      }
   }

   private void showPage(HttpServletRequest req, HttpServletResponse res, UmlViewModel model) throws ServletException, IOException {
      req.setAttribute("model", model);
      req.getRequestDispatcher("/jsp/home.jsp").forward(req, res);
   }
}
