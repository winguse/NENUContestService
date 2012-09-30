package cn.edu.nenu.acm.contestservice.control.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.edu.nenu.acm.contestservice.CSSession;
import cn.edu.nenu.acm.contestservice.MSG;
import cn.edu.nenu.acm.contestservice.Site;
import cn.edu.nenu.acm.contestservice.modeling.objects.User;
import cn.edu.nenu.acm.contestservice.modeling.roles.Volunteer;

/**
 * Servlet implementation class SchoolVsVolunteer
 */
@WebServlet("/schoolvsvolunteer")
public class SchoolVsVolunteer extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SchoolVsVolunteer() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");
		CSSession s = (CSSession) request.getSession()
				.getAttribute("CSSession");
		try {
			if (s.user == null) {
				response.sendRedirect(".");
				return;
			}
			if (s.user.getPermission() != User.USER_ADMINISTRATOR) {
				response.sendRedirect(".");
				return;
			}
			int volunteerId = 0, schoolId = 0;
			try {
				volunteerId = Integer.parseInt(request
						.getParameter("volunteerid"));
				schoolId = Integer.parseInt(request.getParameter("schoolid"));
			} catch (Exception e) {

			}
			if (Volunteer.volunteerSchool(volunteerId, schoolId))
				response.getWriter().print("{\"code\":0}");
			else
				response.getWriter().print("{\"code\":1}");
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
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
