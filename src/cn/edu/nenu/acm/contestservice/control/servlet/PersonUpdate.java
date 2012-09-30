package cn.edu.nenu.acm.contestservice.control.servlet;

import java.io.IOException;
import java.sql.Connection;
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
import cn.edu.nenu.acm.contestservice.modeling.objects.Person;
import cn.edu.nenu.acm.contestservice.modeling.objects.User;

/**
 * Servlet implementation class Main
 */
@WebServlet("/person_update")
public class PersonUpdate extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PersonUpdate() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		final String actionURL = "person_update";
		String refURL=request.getHeader("Referer");
		CSSession s = (CSSession) request.getSession()
				.getAttribute("CSSession");
		if (s == null || s.user == null) {
			response.sendRedirect(".");
			return;
		}
		try {
			if (s.user.getPermission() != User.USER_LEADER
					&& s.user.getPermission() != User.USER_ADMINISTRATOR&&s.user.getPermission()!=User.USER_VOLUNTEER&&s.user.getPermission()!=User.USER_VOLUNTEER_PEDDING) {
				new Log(s.user.getId(), request.getRemoteAddr()+" / "+request.getHeader("x-forwarded-for"),
						new Date().getTime(),
						"To update person information. Permission deny.").add();// 是不是有点像入侵检测系统
				response.sendRedirect(".");
				return;
			}
			String id = request.getParameter("id");
			String team = request.getParameter("team");
			String title = request.getParameter("title");
			if (s.user.getPermission() != User.USER_ADMINISTRATOR) {
				if (!s.verifyToken(id + actionURL,
						request.getParameter("idtoken"))) {
					new Log(s.user.getId(), request.getRemoteAddr()+" / "+request.getHeader("x-forwarded-for"),
							new Date().getTime(),
							"Updateing illegal person information (id)").add();
					s.message = "Illegal Person ID!!";
					s.msgType = MSG.error;
					return;
				}
				if (!s.verifyToken(team + actionURL,
						request.getParameter("teamtoken"))) {
					new Log(s.user.getId(), request.getRemoteAddr()+" / "+request.getHeader("x-forwarded-for"),
							new Date().getTime(),
							"Updateing person information, illegal team.")
							.add();
					s.message = "Illegal Team ID!!";
					s.msgType = MSG.error;
					return;
				}
				if (!s.verifyToken(title + actionURL,
						request.getParameter("titletoken"))) {
					System.out.println(title + " "
							+ request.getParameter("titletoken") + " "
							+ s.getToken(title));
					new Log(s.user.getId(), request.getRemoteAddr()+" / "+request.getHeader("x-forwarded-for"),
							new Date().getTime(),
							"Updateing person information, illegal title.")
							.add();
					s.message = "Illegal Title!!";
					s.msgType = MSG.error;
					return;
				}
			}
			
			int userBelongs;
			if(s.user.getPermission()==User.USER_ADMINISTRATOR){
				userBelongs=Integer.parseInt(request.getParameter("userbelongs"));
			}else{
				userBelongs=s.user.getId();
			}
			String chineseName = request.getParameter("chinesename");
			System.out.println(chineseName);
			String englishName = request.getParameter("englishname");
			String idNumber = request.getParameter("idnumber");
			boolean gender = true;
			if ("false".equals(request.getParameter("gender"))) {
				gender = false;
			}
			String mobile = request.getParameter("mobile");
			int welcomeParty = 0;
			int awardsCeremony = 0;
			try{
				welcomeParty=Integer.parseInt(request.getParameter("welcomeparty"));
				awardsCeremony=Integer.parseInt(request.getParameter("awardceremony"));
			}catch(Exception e){
				
			}
			String major = request.getParameter("major");
			String photo = request.getParameter("photo");
			String email = request.getParameter("email");
			String clothes = request.getParameter("clothes");
			String description = request.getParameter("description");

			Person person = new Person(Integer.parseInt(id),
					Integer.parseInt(team), userBelongs,
					Integer.parseInt(title), chineseName, englishName,
					idNumber, gender, mobile, welcomeParty, awardsCeremony,
					major, photo, email, clothes, description);
			if (person.getId() != -1) {
				person.update();
				s.message = "Your submited person's information has updated.";
			} else {
				person.add();
				s.renewToken();
				s.message = "Your submited person has added to the database.";
			}

			if((Integer.parseInt(title)==Person.PERSON_TEAM_MEMBER||Integer.parseInt(title)==Person.PERSON_TEAM_MEMBER_RESERVE)
					&& s.user.getPermission() != User.USER_ADMINISTRATOR){
				Connection conn = Site.getDataBaseConnection();
				conn.createStatement().execute("UPDATE Team SET Type=-Type WHERE Type>0 AND id=" + person.getTeam());
				conn.close();
				s.message+="Please note that as you have changed the team member information, " +
						"the specify team will become under verfying status again even it was not before.";
			}
			s.msgType = MSG.notice;
			return;
		} catch (SQLException e) {
			e.printStackTrace();
			s.message = "Sorry, An SQL Error Occurs. Please Send Us The Technical Information: "
					+ e.getMessage();
			s.msgType = MSG.error;
			try {
				new Log(s.user.getId(), request.getRemoteAddr()+" / "+request.getHeader("x-forwarded-for"),
						new Date().getTime(), "SQL Error. " + e.getMessage())
						.add();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
			s.message = "Sorry, An Unknow Error Occurs. Please Send Us The Technical Information: "
					+ e.getMessage();
			s.msgType = MSG.error;
			try {
				new Log(s.user.getId(), request.getRemoteAddr()+" / "+request.getHeader("x-forwarded-for"),
						new Date().getTime(), "Unknow Error. " + e.getMessage())
						.add();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			request.getSession().setAttribute("CSSession", s);
			response.sendRedirect(s.lastPage);
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

}
