package org.unidal.maven.plugin.uml;

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

import org.unidal.helper.Files;
import org.unidal.helper.Scanners;
import org.unidal.helper.Scanners.FileMatcher;
import org.unidal.web.jsp.function.CodecFunction;

public class UmlServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private UmlManager m_manager = new UmlManager();

	private void createUml(HttpServletRequest req, UmlViewModel model) throws IOException {
		String uml = req.getParameter("uml");
		String umlFile = req.getParameter("newfile");

		if (!umlFile.endsWith(".uml")) {
			model.setError(true);
			model.setMessage(String.format("Target UML file(%s) must be ending with '.uml'!", umlFile));
		} else if (m_manager.tryCreateFile(umlFile)) {
			StringBuilder message = new StringBuilder();
			boolean success = m_manager.updateUml(umlFile, uml, message);

			model.setError(!success);
			model.setUmlFile(umlFile);
			model.setMessage(message.toString());
		} else {
			model.setError(true);
			model.setMessage(String.format("UML File(%s) is already existed! Please use another one.", umlFile));
		}
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		try {
			byte[] content = m_manager.generateImage("testdot", "atxt");
			String result = new String(content, "utf-8");

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

		String update = req.getParameter("update");
		String saveAs = req.getParameter("saveAs");
		UmlViewModel model = new UmlViewModel(req);

		try {
			if (saveAs != null) {
				createUml(req, model);
			}

			if (update != null) {
				updateUml(req, model);
			}

			String pathInfo = req.getPathInfo();
			String type = req.getParameter("type");

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
			res.setContentType(m_manager.getContextType(type));
			type = m_manager.getImageType(type);

			byte[] image = m_manager.generateImage(uml, type);

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

	private void showPage(HttpServletRequest req, HttpServletResponse res, UmlViewModel model) throws ServletException,
	      IOException {
		String uml = req.getParameter("uml");
		String editStyle = req.getParameter("es");
		String umlFile = req.getParameter("file");

		if (model.getUmlFile() == null) {
			model.setUmlFile(umlFile);
		}

		model.setUmlFiles(scanUmlFiles());

		if (editStyle == null) {
			model.setEditStyle("height: 500px; width: 320px");
		} else {
			model.setEditStyle(editStyle);
		}

		if (!isEmpty(uml)) {
			model.setUml(uml);
		} else if (umlFile != null && umlFile.length() > 0) {
			uml = Files.forIO().readFrom(new File(umlFile), "utf-8");

			model.setUml(uml);
		}

		if (uml != null) {
			byte[] image = m_manager.generateImage(uml, "svg");

			if (image != null) {
				model.setSvg(new String(image, "utf-8"));
			}
		}

		req.setAttribute("model", model);
		req.getRequestDispatcher("/jsp/home.jsp").forward(req, res);
	}

	private void updateUml(HttpServletRequest req, UmlViewModel model) throws IOException {
		String uml = req.getParameter("uml");
		String umlFile = req.getParameter("file");
		StringBuilder message = new StringBuilder();
		boolean success = m_manager.updateUml(umlFile, uml, message);

		model.setError(!success);
		model.setMessage(message.toString());
	}
}
