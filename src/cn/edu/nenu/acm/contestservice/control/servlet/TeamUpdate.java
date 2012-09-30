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
import cn.edu.nenu.acm.contestservice.modeling.objects.Log;
import cn.edu.nenu.acm.contestservice.modeling.objects.Team;
import cn.edu.nenu.acm.contestservice.modeling.objects.User;

/**
 * Servlet implementation class TeamUpdate
 */
@WebServlet("/team_update")
public class TeamUpdate extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public TeamUpdate() {
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
		final String actionURL = "team_update";
		CSSession s = (CSSession) request.getSession()
				.getAttribute("CSSession");
		if (s == null || s.user == null) {
			response.sendRedirect(".");
			return;
		}
		try {
			if (s.user.getPermission() != User.USER_LEADER
					&& s.user.getPermission() != User.USER_ADMINISTRATOR) {
				new Log(s.user.getId(), request.getRemoteAddr() + " / "
						+ request.getHeader("x-forwarded-for"),
						new Date().getTime(),
						"To update team information. Permission deny.").add();
				response.sendRedirect(".");
				return;
			}
			String id = request.getParameter("id");
			String chineseName = request.getParameter("chinesename");
			String englishName = request.getParameter("englishname");
			String statusDescription = "";
			statusDescription = request.getParameter("statusdescription");
			String seat = "";
			int coach = 0;
			int hotel = 0;
			int school = 0;
			int type = Team.TEAM_NOT_VERIFIED;
			boolean arrivaled = false;
			boolean leaved = false;
			if (s.user.getPermission() != User.USER_ADMINISTRATOR) {
				// t.getId()+t.getCoach()+t.getType()+actionURL
				if (!s.verifyToken(
						id + request.getParameter("coach")
								+ request.getParameter("type") + actionURL,
						request.getParameter("token"))) {
					new Log(s.user.getId(), request.getRemoteAddr() + " / "
							+ request.getHeader("x-forwarded-for"),
							new Date().getTime(),
							"Updateing illegal person information (id)").add();
					s.message = "Illegal Team ID!!";
					s.msgType = MSG.error;
					return;
				}
			}
			int i_id = Integer.parseInt(id);
			if (s.user.getPermission() == User.USER_LEADER)
				school = s.user.getSchool();
			if (s.user.getPermission() == User.USER_ADMINISTRATOR) {
				school = Integer.parseInt(request.getParameter("school"));
				type = Integer.parseInt(request.getParameter("type"));
				seat = request.getParameter("seat");
				hotel = Integer.parseInt(request.getParameter("hotel"));
				if (request.getParameter("arrivaled").equals("yes"))
					arrivaled = true;
				if (request.getParameter("leaved").equals("yes"))
					leaved = true;
			}
			s.message = "";
			coach = Integer.parseInt(request.getParameter("coach"));
			Team team = null;
			if (i_id > 0) {
				team = new Team();
				team.load(i_id);
				team.setCoach(coach);
				if (s.user.getPermission() == User.USER_ADMINISTRATOR) {
					team.setHotel(hotel);
					team.setSeat(seat);
					team.setArrivaled(arrivaled);
					team.setLeaved(leaved);
					team.setType(type);
				}
				team.setChineseName(chineseName);
				if (s.user.getPermission() == User.USER_ADMINISTRATOR
						|| team.getType() == Team.TEAM_TOURISM
						|| team.getType() == Team.TEAM_TOURISM_VERIFIED) {
					team.setEnglishName(englishName);
				}
				if (s.user.getPermission() == User.USER_ADMINISTRATOR) {
					team.setStatusDescription(statusDescription);
				} else if (s.user.getPermission() == User.USER_LEADER) {
					statusDescription = "等待审核，请稍等...";
					String[] orgDesc = team.getStatusDescription().split("\\|");
					String icpcDesc = "没有ICPC数据。";
					if (orgDesc.length >= 2)
						icpcDesc = orgDesc[1];
					team.setStatusDescription(statusDescription + "|"
							+ icpcDesc);
				}
				team.setSchool(school);
			} else {
				team = new Team(i_id, coach, hotel, type, seat, chineseName,
						englishName, arrivaled, leaved, statusDescription,
						school);
			}
			if (s.user.getPermission() == User.USER_ADMINISTRATOR) {
				// type = Integer.parseInt(request.getParameter("type"));
			} else if (team.getId() != -1) {
				if (team.getType() > 0)
					team.setType(-team.getType());
				s.message += "As you have change the team information, your team will become UNDER VERIFIED again even it was not before.";
			}
			if (team.getId() != -1) {
				team.update();
				s.message += "The team information has updated.";
			} else {
				team.add();
				s.message += "A team has added.";
			}
			s.msgType = MSG.notice;
			return;
		} catch (SQLException e) {
			e.printStackTrace();
			s.message = "Sorry, An SQL Error Occurs. Please Send Us The Technical Information: "
					+ e.getMessage();
			s.msgType = MSG.error;
			try {
				new Log(s.user.getId(), request.getRemoteAddr() + " / "
						+ request.getHeader("x-forwarded-for"),
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
				new Log(s.user.getId(), request.getRemoteAddr() + " / "
						+ request.getHeader("x-forwarded-for"),
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

}
