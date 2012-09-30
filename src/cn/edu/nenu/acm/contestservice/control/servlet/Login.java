package cn.edu.nenu.acm.contestservice.control.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.edu.nenu.acm.contestservice.CSSession;
import cn.edu.nenu.acm.contestservice.MSG;
import cn.edu.nenu.acm.contestservice.Site;
import cn.edu.nenu.acm.contestservice.modeling.objects.Log;
import cn.edu.nenu.acm.contestservice.modeling.objects.User;

/**
 * Servlet implementation class Login
 */
@WebServlet("/login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Login() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		CSSession s = (CSSession) request.getSession()
				.getAttribute("CSSession");
		if (s == null) {
			s = new CSSession();// 通常来说，这个是在重启服务器后，上一次Session被注销，用户又提交一个SessionID的时候发生的，不然的话，会触发我的SessionManager的。
		}
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String action = request.getParameter("action");
		String otl = request.getParameter("otl");
		String otl_permission = request.getParameter("otl_permission");
		try {
			User user = new User();
			int loginStatus = User.USER_NOT_EXIST;
			if (action != null) {
				System.out.println(username+" - "+otl);
				if (action.equals("otl")
						&& Site.hash(otl + username, "otl").equals(password)) {
					long ts = 0;
					ts = Long.parseLong(otl);
					if (new Date().getTime() - ts < 1000 * 60 * 5) {
						user.load(username);
						if (otl_permission != null) {// TODO
							/*
							 * 这里有个不重要的安全问题，这个otl_permission
							 * 是没hash校验的，目前的功能是，可以增加教练信息不过，
							 * 后面关于酒店预订什么的，这个是否还要存在，校验是必然的要求.
							 * 
							 * 
							 */
							s.otl_permission = Integer.parseInt(otl_permission);
						}
						loginStatus = User.LOGIN_SUCCESS;
					} else {
						s.message = "Sorry, One Time Login Token Time Out!";
						s.msgType = MSG.error;
						s.user = null;
						response.sendRedirect(".");
						return;
					}
				} else {
					s.message = "Sorry, One Time Login Token Is Not Valid!";
					s.msgType = MSG.error;
					s.user = null;
					response.sendRedirect(".");
					return;
				}
			} else {
				loginStatus = user.login(username, password);
			}
			if (loginStatus == User.LOGIN_SUCCESS) {
				if (user.getPermission() ==User.USER_LEADER	&& Site.getOpenTime() > new Date().getTime()&&!"otl".equals(action)) {
					s.message = "Sorry, Our Service System Is NOT Open Yet!";
					s.msgType = MSG.error;
					s.user = null;
					response.sendRedirect(".");
					return;
				}
				s.user = user;
				response.getWriter().print("Welcome!");
				new Log(user.getId(), request.getRemoteAddr() + " / "
						+ request.getHeader("x-forwarded-for"),
						new Date().getTime(), "Login - "
								+ request.getHeader("REFERER") + " - "
								+ request.getHeader("User-Agent") + " #otl="
								+ otl).add();
				// TODO 角色处理 志愿者/带队 生成对应的实体对象
				switch (s.user.getPermission()) {
				case User.USER_NOT_EXAMED:
					s.user = null;
					s.message = "Sorry, your account was not examed, please wait...";
					s.msgType = MSG.notice;
					response.sendRedirect(".");
					break;
				case User.USER_ADMINISTRATOR:
					response.sendRedirect("admin.jsp");
					break;
				case User.USER_LEADER:
					response.sendRedirect("leader.jsp");
					break;
				case User.USER_VOLUNTEER:
				case User.USER_VOLUNTEER_PEDDING:
					response.sendRedirect("volunteer.jsp");
					break;
				default:
					s.user = null;
					s.message = "Sorry, your identity was not valid.";
					s.msgType = MSG.error;
					response.sendRedirect(".");
					break;
				}
			} else if (loginStatus == User.WRONG_PASSWORD) {
				s.message = "Your password is incorrect!";
				s.msgType = MSG.error;
				s.user = null;
				response.sendRedirect(".");
			} else if (loginStatus == User.USER_NOT_EXIST) {
				s.message = "Username does not exist!";
				s.msgType = MSG.error;
				s.user = null;
				response.sendRedirect(".");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			response.getWriter().print(
					"Sorry, our server encountered some troubles.\n\n"
							+ e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			request.getSession().setAttribute("CSSession", s);
		}

	}

}
