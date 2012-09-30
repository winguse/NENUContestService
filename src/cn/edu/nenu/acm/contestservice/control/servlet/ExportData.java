package cn.edu.nenu.acm.contestservice.control.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.edu.nenu.acm.contestservice.CSSession;
import cn.edu.nenu.acm.contestservice.MSG;
import cn.edu.nenu.acm.contestservice.modeling.objects.Coach;
import cn.edu.nenu.acm.contestservice.modeling.objects.Pair;
import cn.edu.nenu.acm.contestservice.modeling.objects.Person;
import cn.edu.nenu.acm.contestservice.modeling.objects.School;
import cn.edu.nenu.acm.contestservice.modeling.objects.Team;
import cn.edu.nenu.acm.contestservice.modeling.objects.User;
import cn.edu.nenu.acm.contestservice.modeling.roles.Leader;

import com.csvreader.CsvWriter;

/**
 * Servlet implementation class ExportData
 */
@WebServlet("/exportData")
public class ExportData extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ExportData() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
//		request.setCharacterEncoding("utf-8");
//		response.setCharacterEncoding("utf-8");
		CSSession s = (CSSession) request.getSession()
				.getAttribute("CSSession");
		if (s == null || s.user == null) {
			response.sendRedirect(".");
			return;
		}
		try {
			if (s.user.getPermission() != User.USER_ADMINISTRATOR) {
				s.msgType = MSG.error;
				s.message = "权限不足！";
				return;
			}
			// 奇葩，数据库设计的奇葩。。
			HashMap<Integer, Pair<School, Leader>> schools = new HashMap<Integer, Pair<School, Leader>>();
			for (School sch : School.getAllSchools()) {
				schools.put(new Integer(sch.getId()), new Pair<School, Leader>(
						sch, null));
			}
			String path=request.getServletContext().getRealPath("")+"/";
			String filename="iloveshuxiao123we23wer24fdg657.csv";//防止任何人都可以下载
			CsvWriter csv = new CsvWriter(path+filename, ',',
					Charset.forName("UTF-8"));
			String[] a = {
				"中文校名",
				"英文校名",
				"通信地址",
				"邮编",
				"角色",
				"中文队名",
				"英文队名",
				"中文姓名",
				"英文姓名",
				"性别",
				"电话",
				"参加欢迎宴会",
				"参加颁奖礼",
				"专业",
				"邮件",
				"服装大小",
				"补充描述"
			};
			csv.writeRecord(a);
			String[] enterStatus = { "No", "Yes", "Not Sure" };
			for (User user : User.loadSpecifyUser(User.USER_LEADER, -1)) {
				Leader leader = new Leader(user);
				leader.loadRetinue();
				Pair<School, Leader> p = schools.get(user.getSchool());
				if(p==null){
					p=new Pair<School, Leader>();
					p.a=leader.getSchool();
				}else{
					leader.setSchool(p.a);
				}
				p.b = leader;
				for (Coach coach : leader.getCoaches()) {
					ArrayList<String> head = new ArrayList<String>(), rs = new ArrayList<String>();
					head.add(p.a.getChineseName());
					head.add(p.a.getEnglishName());
					head.add(p.a.getPostAddress());
					head.add(p.a.getPostCode());
					Person psn = coach;
					rs.clear();
					rs.addAll(head);
					rs.add("教练");
					rs.add("");
					rs.add("");
					rs.add(psn.getChineseName());
					rs.add(psn.getEnglishName());
					rs.add(psn.getGender() ? "男" : "女");
					rs.add(psn.getMobile());
					rs.add(enterStatus[psn.enterWelcomeParty()]);
					rs.add(enterStatus[psn.enterAwardsCeremony()]);
					rs.add(psn.getMajor());
					rs.add(psn.getEmail());
					rs.add(psn.getClothes());
					rs.add(psn.getDescription());
					csv.writeRecord(rs.toArray(a));
					for (Team team : coach.getTeams()) {
						for (Person ptm : Person.getTeamMembers(team.getId())) {
							rs.clear();
							rs.addAll(head);
							rs.add("队员");
							rs.add(team.getChineseName());
							rs.add(team.getEnglishName());
							rs.add(ptm.getChineseName());
							rs.add(ptm.getEnglishName());
							rs.add(ptm.getGender() ? "男" : "女");
							rs.add(ptm.getMobile());
							rs.add(enterStatus[ptm.enterWelcomeParty()]);
							rs.add(enterStatus[ptm.enterAwardsCeremony()]);
							rs.add(ptm.getMajor());
							rs.add(ptm.getEmail());
							rs.add(ptm.getClothes());
							rs.add(ptm.getDescription());
							csv.writeRecord(rs.toArray(a));
						}
					}
					for (Person ptm : leader.getRetinue()) {
						rs.clear();
						rs.addAll(head);
						rs.add("随行人员");
						rs.add("");
						rs.add("");
						rs.add(ptm.getChineseName());
						rs.add(ptm.getEnglishName());
						rs.add(ptm.getGender() ? "男" : "女");
						rs.add(ptm.getMobile());
						rs.add(enterStatus[ptm.enterWelcomeParty()]);
						rs.add(enterStatus[ptm.enterAwardsCeremony()]);
						rs.add(ptm.getMajor());
						rs.add(ptm.getEmail());
						rs.add(ptm.getClothes());
						rs.add(ptm.getDescription());
						csv.writeRecord(rs.toArray(a));
					}
				}
			}
			for (Person ptm : Person.loadSpecifyPersons(Person.PERSON_VOLUNTEER)) {
				if(ptm.getUserBelongs()==87&&!"x".equals(request.getParameter("x")))continue;
				ArrayList<String> rs = new ArrayList<String>();
				rs.add("");
				rs.add("");
				rs.add("");
				rs.add("");
				rs.add("志愿者");
				rs.add("");
				rs.add("");
				rs.add(ptm.getChineseName());
				rs.add(ptm.getEnglishName());
				rs.add(ptm.getGender() ? "男" : "女");
				rs.add(ptm.getMobile());
				rs.add(enterStatus[ptm.enterWelcomeParty()]);
				rs.add(enterStatus[ptm.enterAwardsCeremony()]);
				rs.add(ptm.getMajor());
				rs.add(ptm.getEmail());
				rs.add(ptm.getClothes());
				rs.add(ptm.getDescription());
				csv.writeRecord(rs.toArray(a));
			}
			csv.flush();
			csv.close();
			csv = null;
			response.addHeader("Content-Disposition", "attachment;filename=export.csv");
//			BufferedReader reader = new BufferedReader(new FileReader(filename));
//			reader.
			request.getRequestDispatcher(filename).forward(request, response);
			
			new File(path+filename).delete();
		} catch (SQLException e) {
			s.message += e.getMessage();
			e.printStackTrace();
			response.sendRedirect(s.lastPage);
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
		// TODO Auto-generated method stub
	}

}
