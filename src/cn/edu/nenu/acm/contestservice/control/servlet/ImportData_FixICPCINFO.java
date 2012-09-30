package cn.edu.nenu.acm.contestservice.control.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.edu.nenu.acm.contestservice.CSSession;
import cn.edu.nenu.acm.contestservice.MSG;
import cn.edu.nenu.acm.contestservice.Site;
import cn.edu.nenu.acm.contestservice.modeling.objects.Team;
import cn.edu.nenu.acm.contestservice.modeling.objects.User;

/**
 * Servlet implementation class ImportData
 */
@WebServlet("/ImportData_FixICPCINFO")
public class ImportData_FixICPCINFO extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ImportData_FixICPCINFO() {
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
		response.setCharacterEncoding("utf-8");
		CSSession s = (CSSession) request.getSession()
				.getAttribute("CSSession");
		if (s == null || s.user == null) {
			response.sendRedirect(".");
			return;
		}
		Date now=new Date();
		try {
			if (s.user.getPermission() != User.USER_ADMINISTRATOR) {
				s.msgType = MSG.error;
				s.message = "权限不足！";
				return;
			}
			s.msgType = MSG.error;
			s.message = "不知道什么奇葩的错误……检查数据是否有问题，例如教练提供的地址里面有分割用的字符。很多数据已经导入，重置系统，调试，重新来吧。";
			String teamData;
			teamData = request.getParameter("teamData");
			if (Site.isEmpty(teamData)) {
				s.msgType = MSG.error;
				s.message = "空表单项目！";
				return;
			}
			s.message = "";
			String[] arrTeam = teamData.split("\\t");
			String[] colTeam = arrTeam[0].split(";");
			HashMap<String, Integer> mapColTeam = new HashMap<String, Integer>();
			// 计算字段映射
			for (int i = 0; i < colTeam.length; i++) {
				mapColTeam.put(colTeam[i], i);
			}

			int icTeamName = mapColTeam.get("Team name");
			int icStatus = mapColTeam.get("Status");
			int icMember = mapColTeam.get("entry.fullTeamMembers");
			for (int i = 1; i < arrTeam.length; i++) {
				arrTeam[i] += ";END";// 否则空数据就被split扔掉了
				String[] single = arrTeam[i].split(";");
				System.out.println("{" + arrTeam[i] + "}" + single.length);
				if (single[icStatus].equals("CANCELED"))
					continue;// TODO 这些非法队伍和信息的，要注意
				String teamName = single[icTeamName];
				String icpcDesc = single[icMember];

				Team team = new Team();
				if (team.load(teamName)) {
					String[] orgDesc = team.getStatusDescription().split("\\|");
					// if(orgDesc.length>=2)
					// icpcDesc=orgDesc[1];
					team.setStatusDescription(orgDesc[0] + "|" + teamName
							+ " # " + icpcDesc + " @"+now);
					team.update();
				} else {
					s.message += "Team Not Found: " + teamName + "<br/>";
				}
			}
			System.out.println("Import Successfully!");
			s.message += "Updated.";
			s.msgType = MSG.notice;
		} catch (SQLException e) {
			s.message += e.getMessage();
			e.printStackTrace();
		} finally {
			request.getSession().setAttribute("CSSession", s);
			response.sendRedirect("admin_imported.jsp");
		}
	}

}
