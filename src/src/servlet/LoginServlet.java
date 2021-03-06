package servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.UsersDao;
import model.Login;
import model.LoginCount;
import model.Result;
import model.Users;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// ログインページにフォワードする
				RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/login.jsp");
				dispatcher.forward(request, response);
			}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// リクエストパラメータを取得する
			request.setCharacterEncoding("UTF-8");
			String id = request.getParameter("ID");
			String pw = request.getParameter("PW");

		// ログイン処理を行う
			UsersDao iDao = new UsersDao();

			if (iDao.isLoginOK(new Users(id, pw))) {	// ログイン成功
				Object id1 = (Object)id;
		// セッションスコープにIDを格納する
				HttpSession session = request.getSession();
				session.setAttribute("id", new Login(id));
				session.setAttribute("memo",id1);
				UsersDao count = new UsersDao();
				count.LoginDate(new Users(id));
				LoginCount count1 = count.LoginCount(new Users(id));
				session.setAttribute("loginCount",count1.getCount() );
		// メニューサーブレットにリダイレクトする
				response.sendRedirect("/Forza/TopServlet");
			}
			else {									// ログイン失敗
		// リクエストスコープに、タイトル、メッセージ、戻り先を格納する
				request.setAttribute("result",
				new Result("ログイン失敗！", "ID(メールアドレス)またはPWに間違いがあります。", "/Forza/LoginServlet"));

		// 結果ページにフォワードする
				RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/result.jsp");
				dispatcher.forward(request, response);
			}
    }
}