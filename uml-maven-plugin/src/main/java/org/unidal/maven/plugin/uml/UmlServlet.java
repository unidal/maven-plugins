package org.unidal.maven.plugin.uml;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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
import org.unidal.helper.Scanners;
import org.unidal.helper.Scanners.FileMatcher;
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

   private boolean isEmpty(String str) {
      return str == null || str.length() == 0;
   }

   private List<File> scanUmlFiles() {
      final List<File> files = new ArrayList<File>();

      FileMatcher matcher = new FileMatcher() {
         @Override
         public Direction matches(File base, String path) {
            if (path.endsWith(".uml")) {
               files.add(new File(base, path));
            }

            return Direction.DOWN;
         }
      };

      Scanners.forDir().scan(new File("src"), matcher);
      Scanners.forDir().scan(new File("doc"), matcher);

      return files;
   }

   @Override
   protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
      if (req.getCharacterEncoding() == null) {
         req.setCharacterEncoding("utf-8");
      }

      UmlViewModel model = new UmlViewModel(req);
      String pathInfo = req.getPathInfo();
      String type = req.getParameter("type");
      String update = req.getParameter("update");

      try {
         if (update != null) {
            updateUml(req, res, model);
         }

         if (pathInfo != null && pathInfo.endsWith(".uml")) {
            String path = CodecFunction.urlDecode(pathInfo);

            showImage(req, res, model, type, path);
         } else if (!isEmpty(type)) {
            showImage(req, res, model, type, null);
         } else {
            showPage(req, res, model);
         }
      } catch (Exception e) {
         e.printStackTrace();

         res.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
      }
   }

   private void showImage(HttpServletRequest req, HttpServletResponse res, UmlViewModel model, String type, String path)
         throws UnsupportedEncodingException, IOException {
      String uml = req.getParameter("uml");

      if (!isEmpty(path)) {
         InputStream in = req.getSession().getServletContext().getResourceAsStream(path);

         if (in == null) {
            File file = new File(path.substring(1));

            if (file.exists()) {
               in = new FileInputStream(file);
            }
         }

         if (in == null) {
            res.sendError(404, "File Not Found(" + path + ")!");
            return;
         }

         uml = Files.forIO().readFrom(in, "utf-8");
      }

      if (uml != null) {
         if (type == null || "png".equals(type)) {
            type = "png";

            res.setContentType("image/png");
         } else if ("text".equals(type)) {
            type = "svg";

            res.setContentType("text/plain; charset=utf-8");
         } else if ("svg".equals(type)) {
            res.setContentType("image/svg+xml; charset=utf-8");
         }

         byte[] image = generateImage(uml, type);

         if (image != null) {
            res.setContentLength(image.length);
            res.getOutputStream().write(image);
         } else {
            res.sendError(400, "UML Incompleted!");
         }
      } else {
         res.sendError(400, "Invalid Argument!");
      }
   }

   private void showPage(HttpServletRequest req, HttpServletResponse res, UmlViewModel model) throws ServletException, IOException {
      String uml = req.getParameter("uml");
      String umlFile = req.getParameter("file");
      String editStyle = req.getParameter("es");

      model.setUmlFiles(scanUmlFiles());
      model.setUmlFile(umlFile);

      if (editStyle == null) {
         model.setEditStyle("height: 500px; width: 320px");
      } else {
         model.setEditStyle(editStyle);
      }

      if (!isEmpty(uml)) {
         model.setUml(uml);
      } else if (umlFile != null) {
         uml = Files.forIO().readFrom(new File(umlFile), "utf-8");

         model.setUml(uml);
      }

      if (uml != null) {
         byte[] image = generateImage(uml, "svg");

         if (image != null) {
            model.setSvg(new String(image, "utf-8"));
         }
      }

      req.setAttribute("model", model);
      req.getRequestDispatcher("/jsp/home.jsp").forward(req, res);
   }

   private void updateUml(HttpServletRequest req, HttpServletResponse res, UmlViewModel model) throws IOException {
      String uml = req.getParameter("uml");
      String umlFile = req.getParameter("file");

      if (!isEmpty(umlFile) && !isEmpty(uml)) {
         File file = new File(umlFile);

         if (file.exists()) {
            byte[] image = generateImage(uml, null);

            if (image != null) {
               Files.forIO().writeTo(file, uml);

               model.setMessage("Update file(" + umlFile + ") successfully!");
            } else {
               model.setError(true);
               model.setMessage("UML is invalid, can't update file(" + umlFile + ")!");
            }
         } else {
            model.setError(true);
            model.setMessage("Failed to update file(" + umlFile + ")!");
         }
      }
   }
}
