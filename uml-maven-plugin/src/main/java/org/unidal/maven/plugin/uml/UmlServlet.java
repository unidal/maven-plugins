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

   private byte[] generateImage(String uml, String type) throws IOException {
      if (!uml.trim().startsWith("@startuml")) {
         uml = "@startuml\n" + uml;
      }

      if (!uml.trim().endsWith("@enduml")) {
         uml = uml + "\n@enduml";
      }

      SourceStringReader reader = new SourceStringReader(uml);
      ByteArrayOutputStream baos = new ByteArrayOutputStream(8192);
      FileFormat format = FileFormat.PNG;

      for (FileFormat e : FileFormat.values()) {
         if (e.name().equalsIgnoreCase(type)) {
            format = e;
            break;
         }
      }

      reader.generateImage(baos, new FileFormatOption(format));

      if (!hasError(reader.getBlocks())) {
         return baos.toByteArray();
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
         String result = new String(generateImage("testdot", "atxt"), "utf-8");

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
      if (req.getCharacterEncoding() == null) {
         req.setCharacterEncoding("utf-8");
      }

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

      try {
         if (uml != null && type != null && type.length() > 0) {
            if (type.equals("text")) {
               model.setImage(generateImage(uml, "svg"));
            } else {
               model.setImage(generateImage(uml, type));
            }

            showImage(req, res, model, type);
         } else {
            if (uml != null) {
               model.setSvg(new String(generateImage(uml, "svg"), "utf-8"));
            }

            showPage(req, res, model);
         }
      } catch (Exception e) {
         res.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
      }
   }

   private void showImage(HttpServletRequest req, HttpServletResponse res, UmlViewModel model, String type)
         throws UnsupportedEncodingException, IOException {
      byte[] image = model.getImage();

      if (image != null) {
         if ("text".equals(type)) {
            res.setContentType("text/plain; charset=utf-8");
         } else if ("svg".equals(type)) {
            res.setContentType("image/svg+xml; charset=utf-8");
         } else if ("png".equals(type)) {
            res.setContentType("image/png");
         }

         res.setContentLength(image.length);
         res.getOutputStream().write(image);
      } else {
         res.sendError(400, "Invalid uml!");
      }
   }

   private void showPage(HttpServletRequest req, HttpServletResponse res, UmlViewModel model) throws ServletException, IOException {
      req.setAttribute("model", model);
      req.getRequestDispatcher("/jsp/home.jsp").forward(req, res);
   }
}
