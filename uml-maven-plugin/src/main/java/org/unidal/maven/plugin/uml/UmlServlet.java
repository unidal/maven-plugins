package org.unidal.maven.plugin.uml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.plantuml.BlockUml;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.PSystemError;
import net.sourceforge.plantuml.SourceStringReader;

import org.unidal.helper.Files;
import org.unidal.web.jsp.function.CodecFunction;

public class UmlServlet extends HttpServlet {
   private static final long serialVersionUID = 1L;

   private String generateImage(String uml, String type) throws IOException {
      if (!uml.trim().startsWith("@startuml")) {
         uml = "@startuml\n" + uml;
      }

      if (!uml.trim().endsWith("@enduml")) {
         uml = uml + "\n@enduml";
      }

      SourceStringReader reader = new SourceStringReader(uml);
      ByteArrayOutputStream baos = new ByteArrayOutputStream(8192);
      FileFormat format = FileFormat.valueOf(type.toUpperCase());

      reader.generateImage(baos, new FileFormatOption(format));

      if (!hasError(reader.getBlocks())) {
         return baos.toString("utf-8");
      } else {
         return null;
      }
   }

   private boolean hasError(List<BlockUml> blocks) throws IOException {
      for (BlockUml b : blocks) {
         if (b.getDiagram() instanceof PSystemError) {
            return true;
         }
      }

      return false;
   }

   @Override
   public void init(ServletConfig config) throws ServletException {
      try {
         String result = generateImage("testdot", "atxt");

         System.out.println(result);

         if (result != null && result.contains("Error")) {
            System.err.println("WARNNING: Failed to testdot, the system will be run in downgraded mode, "
                  + "only sequence diagrams will be generated!\r\n"
                  + "Please make sure graphviz is installed, and mare sure file /usr/bin/dot exist!");
         }
      } catch (IOException e) {
         // ignore it
         e.printStackTrace();
         log("Error when initializing " + getClass().getName() + "!", e);
      }
   }

   @Override
   protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
      UmlViewModel model = new UmlViewModel(req);
      String pathInfo = req.getPathInfo();
      String uml = req.getParameter("uml");
      String type = req.getParameter("type");

      if (pathInfo != null && pathInfo.endsWith(".uml")) {
         String path = CodecFunction.urlDecode(pathInfo);
         InputStream in = req.getSession().getServletContext().getResourceAsStream(path);

         uml = Files.forIO().readFrom(in, "utf-8");
         type = "svg";
      }

      model.setUml(uml);

      if (uml != null) {
         model.setSvg(generateImage(uml, "svg"));
      }

      if (uml != null && ("text".equals(type) || "svg".equals(type))) {
         showImage(req, res, model, type);
      } else {
         showPage(req, res, model);
      }
   }

   private void showImage(HttpServletRequest req, HttpServletResponse res, UmlViewModel model, String type)
         throws UnsupportedEncodingException, IOException {
      String svg = model.getSvg();

      if (svg != null) {
         byte[] data = svg.getBytes("utf-8");

         if ("text".equals(type)) {
            res.setContentType("text/plain; charset=utf-8");
         } else if ("svg".equals(type)) {
            res.setContentType("image/svg+xml; charset=utf-8");
         }

         res.setContentLength(data.length);
         res.getOutputStream().write(data);
      } else {
         res.sendError(400, "Invalid uml!");
      }
   }

   private void showPage(HttpServletRequest req, HttpServletResponse res, UmlViewModel model) throws ServletException, IOException {
      req.setAttribute("model", model);
      req.getRequestDispatcher("/jsp/home.jsp").forward(req, res);
   }
}
