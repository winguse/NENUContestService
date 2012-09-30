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
 * Servlet implementation class Update
 */
@WebServlet("/user_update")
public class UserUpdate extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserUpdate() {
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
		try {
			if (s.user == null){
				response.sendRedirect(".");
				return;
			}
			String action=request.getParameter("action");
			if(action!=null&&action.equals("updateschool")){
				int id=-1,school=0;
				try{
					id=Integer.parseInt(request.getParameter("id"));
					school=Integer.parseInt(request.getParameter("school"));
				}catch(Exception e){
				}
				if(s.user.getPermission()!=User.USER_ADMINISTRATOR&&s.user.getId()!=id)
					return;
				User u=new User(id);
				u.setSchool(school);
				u.updateUser();
				s.msgType=MSG.notice;
				s.message="School Information of Specific User Updated.";
				return;
			}
			String password = request.getParameter("password");
			String password2 = request.getParameter("password2");
			if (!password.equals(password2)) {
				s.msgType = MSG.error;
				s.message = "The two new passwords was not match!";
				return;
			}
			String oldPassword = request.getParameter("oldpassword");
			System.out.println("[" + password + "]" + "[" + password2 + "]"
					+ "[" + oldPassword + "]");
			oldPassword = Site.hash(oldPassword, s.user.getSalt());
			if (!oldPassword.equals(s.user.getPassword())) {
				s.msgType = MSG.error;
				s.message = "The old passwords was not match!";
				return;
			}
			if (password.length()<6) {
				s.msgType = MSG.error;
				s.message = "Password Too Short !";
				return;
			}
			s.user.setOrginalPassword(password);
			s.user.updateUser();
			s.msgType = MSG.notice;
			s.message = "Your information has updated successfully!";

			new Log(s.user.getId(), request.getRemoteAddr() + " / "
					+ request.getHeader("x-forwarded-for"),
					new Date().getTime(), "##User Info Updated - "
							+ request.getHeader("REFERER") + " - "
							+ request.getHeader("User-Agent") ).add();
			return;
		} catch (SQLException e) {
			e.printStackTrace();
			response.getWriter().print(
					"Sorry, our server encountered some troubles.\n\n"
							+ e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			request.getSession().setAttribute("CSSession", s);
			response.sendRedirect(s.lastPage);
		}
	}
}
