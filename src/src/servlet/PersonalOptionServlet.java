package servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import dao.IconImagesDao;
import dao.UsersDao;
import model.Icon;

@MultipartConfig(location = "C:\\dojo6\\src\\WebContent\\icon_images") // アップロードファイルの一時的な保存先
@WebServlet("/PersonalOptionServlet")
public class PersonalOptionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public PersonalOptionServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// もしもログインしていなかったらログインサーブレットにリダイレクトする
		HttpSession session = request.getSession();

//		if (session.getAttribute("id") == null) {
//			response.sendRedirect("/Forza/LoginServlet");
//			return;
//		}

		request.setCharacterEncoding("UTF-8");

		// デフォルトのアイコンのデータをスコープに格納
		List<Icon> img_sample = new ArrayList<Icon>();
		Icon sample = new Icon("","/Forza/icon_images/icon_test_1.png");
		img_sample.add(sample);
		request.setAttribute("myIcon", img_sample.get(0).getIcon_image());

		// セッションスコープからUSER_IDを取得し、アイコンの選択
		if(session.getAttribute("id") != null) {
			String id = (String)session.getAttribute("memo");
				System.out.println("-----個人設定------");
				System.out.println(id);
			IconImagesDao iDao = new IconImagesDao();
			List<Icon> icon = iDao.select(new Icon(id));
			// 検索結果をリクエストスコープに上書きして格納する
			System.out.println(icon.get(0).getIcon_image());
			request.setAttribute("", icon.get(0).getIcon_image());
		}

		// 個人設定ページにフォワードする
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/personalOption.jsp");
		dispatcher.forward(request, response);

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		String id = (String)session.getAttribute("memo");

		request.setCharacterEncoding("UTF-8");

		Part part = request.getPart("IMAGE"); // getPartで取得

		String image = this.getFileName(part);
		request.setAttribute("image", image);
		// サーバの指定のファイルパスへファイルを保存
        //場所はクラス名↑の上に指定してある
		part.write(image);
		IconImagesDao newicon = new IconImagesDao();
		newicon.insert(new Icon(id,image));

        //ディスパッチ
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/result.jsp");
		dispatcher.forward(request, response);

		String password = request.getParameter("password");
		UsersDao user = new UsersDao();
		user.isChangePw(password);


	}

	//ファイルの名前を取得してくる
	private String getFileName(Part part) {
        String name = null;
        for (String dispotion : part.getHeader("Content-Disposition").split(";")) {
            if (dispotion.trim().startsWith("filename")) {
                name = dispotion.substring(dispotion.indexOf("=") + 1).replace("\"", "").trim();
                name = name.substring(name.lastIndexOf("\\") + 1);
                break;
            }
        }
        return name;
	}

}
