package cn.edu.nenu.acm.contestservice.control.servlet;

import java.io.IOException;
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
import cn.edu.nenu.acm.contestservice.Site;
import cn.edu.nenu.acm.contestservice.modeling.objects.Person;
import cn.edu.nenu.acm.contestservice.modeling.objects.School;
import cn.edu.nenu.acm.contestservice.modeling.objects.Team;
import cn.edu.nenu.acm.contestservice.modeling.objects.User;

/**
 * Servlet implementation class ImportData
 */
@WebServlet("/importData")
public class ImportData extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ImportData() {
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
		int schoolCount=0,coachCount=0,contestantCount=0,reserveCount=0,teamCount=0,officalTeamCount=0,routineCount=0;
		String specialLog="";
		 if (s == null || s.user == null) {
		 response.sendRedirect(".");
		 return;
		 }
		 ArrayList<String> loginInfo=(ArrayList<String>) request.getSession().getAttribute("loginInfo");
		 if(loginInfo==null){
			 loginInfo=new ArrayList<String>();
		 }
		 final String defaultPassword="1234567890";
		try {
			 if (s.user.getPermission() != User.USER_ADMINISTRATOR) {
			 s.msgType = MSG.error;
			 s.message = "权限不足！";
			 return;
			 }
			 s.msgType = MSG.error;
			 s.message = "不知道什么奇葩的错误……检查数据是否有问题，例如教练提供的地址里面有分割用的字符。很多数据已经导入，重置系统，调试，重新来吧。";
			String teamData, personData;
			teamData = request.getParameter("teamData");
			personData = request.getParameter("personData");
			if (Site.isEmpty(teamData) || Site.isEmpty(personData)) {
				s.msgType = MSG.error;
				s.message = "空表单项目！";
				return;
			}
			String[] arrTeam = teamData.split("\\t");
			String[] colTeam = arrTeam[0].split(";");
			String[] arrPerson = personData.split("\\t");
			String[] colPerson = arrPerson[0].split(";");
			HashMap<String, Integer> mapColTeam = new HashMap<String, Integer>();
			HashMap<String, Integer> mapColPerson = new HashMap<String, Integer>();
			// 计算字段映射
			for (int i = 0; i < colTeam.length; i++) {
				mapColTeam.put(colTeam[i], i);
			}
			for (int i = 0; i < colPerson.length; i++) {
				mapColPerson.put(colPerson[i], i);
			}
			// 字段下标映射
			int icLastname = mapColPerson.get("Last name"), icFirstname = mapColPerson
					.get("First name"), icGender = mapColPerson.get("Gender"), icMobile = mapColPerson
					.get("Phone"), icEmail = mapColPerson.get("Email"), icClothes = mapColPerson
					.get("Shirt size"), icTitle = mapColPerson.get("Team role"),
			/* 非必填字段 */
			icStatus = mapColPerson.get("Status"), icInstitution = mapColPerson
					.get("Institution name"), icShortInstitution = mapColPerson
					.get("Short name"),
			/* 非必须字段 */
			icCountry = mapColPerson.get("Country"), icTeamName = mapColPerson
					.get("Team name");
			/* 处理人物信息表，把所有任务信息识别出来 */
			HashMap<String, Person> mapPerson = new HashMap<String, Person>();
			HashMap<String, School> mapSchool = new HashMap<String, School>();
			HashMap<String, User> mapUser = new HashMap<String, User>();
			for (int i = 1; i < arrPerson.length; i++) {
				arrPerson[i] += ";END";// 否则空数据就被split扔掉了
				String[] single = arrPerson[i].split(";");
				if (single[icStatus].equals("CANCELED")) {// TODO
															// 其他状态呢？或者说，我们只要ACCEPTED
					continue;
				}
				Person p = new Person();
				if (single[icTitle].equals("COACH")) {// 角色设置
					p.setTitle(Person.PERSON_COACH);
				} else if (single[icTitle].equals("CONTESTANT")) {
					p.setTitle(Person.PERSON_TEAM_MEMBER);
				} else if (single[icTitle].equals("RESERVE")) {
					p.setTitle(Person.PERSON_TEAM_MEMBER_RESERVE);
				} else if (single[icTitle].equals("ATTENDEE")) {
					p.setTitle(Person.PERSON_RETINUE);
				}else{
					System.out.println("Unknow Role:"+single[icTitle]);
				}
				String pname;// 人名
				if (single[icLastname].charAt(0) <= 'z') {
					pname = single[icFirstname] + " " + single[icLastname];
					p.setEnglishName(pname);
				} else {
					pname = single[icLastname] + single[icFirstname];
					p.setChineseName(pname);
				}
				p.setGender(single[icGender].equals("MALE"));// 性别
				p.setMobile(single[icMobile]);// 电话
				p.setEmail(single[icEmail]);// 电邮
				// System.out.println(icClothes+" # "+single.length
				// +" # "+arrPerson[i]);
				p.setClothes(single[icClothes]);// 衣服
				p.setDescription(single[icTeamName] + " / " + single[icCountry]);// 描述，发现那个什么special
																					// need的不在这个文件导出的，不过大家都随便写的
				// 加上队伍名做Hash，你不能还有同名的那么坑爹了吧，这里不检查重复了，之间put了，你数据可不能给我错的，否则，覆盖了就不是我的责任了，教练是会覆盖的
				//Update:Orz北航buaa.gg，既当教练又做队员，赛事管理系统重新给你做数据导入……我加角色字段再hash一次……
				mapPerson.put(single[icFirstname] + " " + single[icLastname]
						+ single[icInstitution]+"#"+p.getTitle(), p);
				System.out.println("put: "+single[icFirstname] + " "
						+ single[icLastname] + single[icInstitution]);
				if (!mapSchool.containsKey(single[icInstitution])) {// 如果已经有这货了，就不put了
					schoolCount++;
					School school = new School();
					school.setEnglishName(single[icInstitution] + " ("+ single[icShortInstitution] + ")");// 顺手把学校信息先读了
					school.setChineseName("#中文# ("+ single[icShortInstitution] + ")");// 顺手把学校信息先读了
					school.add();
					User user = new User();
					user.setUsername(single[icShortInstitution]);
					user.setOrginalPassword(Site.hash(
							single[icShortInstitution], "lovely shuxiao")
							.substring(0, 8));
					user.setPermission(User.USER_LEADER);
					user.setSchool(school.getId());
					int uI = 0;
					while (user.add() != User.ADD_NEW_USER_SUCCESS) {
						user.setUsername(single[icShortInstitution] + "_" + uI);
						uI++;
					}
					mapUser.put(single[icInstitution], user);
					mapSchool.put(single[icInstitution], school);
				}
			}
			icTeamName = mapColTeam.get("Team name");
			icInstitution = mapColTeam.get("Institution name");
			icStatus = mapColTeam.get("Status");
			int icAddress = mapColTeam.get("shippingAddress.fullAddress");
			int icMember = mapColTeam.get("entry.fullTeamMembers");
			for (int i = 1; i < arrTeam.length; i++) {
				arrTeam[i] += ";END";// 否则空数据就被split扔掉了
				String[] single = arrTeam[i].split(";");
				System.out.println("{"+arrTeam[i]+"}"+single.length);
				if(single[icStatus].equals("CANCELED"))continue;//TODO 这些非法队伍和信息的，要注意
				String teamName = single[icTeamName];
				String schoolName = single[icInstitution];
				String icpcTeamDesc=single[icMember];
				System.out.println("Tobe Split: "+single[icMember].replaceAll("\\]","").replaceAll("\\[\\[", ""));
				String[] members = single[icMember].replaceAll("\\]", "").replaceAll("\\[\\[", "").split("\\[");
				System.out.println("split length : "+members.length);
				User user = mapUser.get(schoolName);
				School school = mapSchool.get(schoolName);
				Person coach = null;
				ArrayList<Person> contestant = new ArrayList<Person>();
				ArrayList<Person> reserve = new ArrayList<Person>();
				ArrayList<Person> routine = new ArrayList<Person>();
				for (String m : members) {
					String[] n = m.split("\\.");
					// single[icFirstname]+single[icLastname]+single[icTeamName]
					String findPersonStr = n[0] + schoolName;
					System.out.println("find: ["+findPersonStr+"]");
					if (n[1].equals("Coach")) {
						System.out.println("## "+n[0]);
						findPersonStr+="#"+Person.PERSON_COACH;
						coach = mapPerson.get(findPersonStr);
					} else if (n[1].equals("Contestant")) {
						findPersonStr+="#"+Person.PERSON_TEAM_MEMBER;
						contestant.add(mapPerson.get(findPersonStr));
					} else if (n[1].equals("Reserve")) {
						findPersonStr+="#"+Person.PERSON_TEAM_MEMBER_RESERVE;
						reserve.add(mapPerson.get(findPersonStr));
					} else  if (n[1].equals("Attendee")) {
						findPersonStr+="#"+Person.PERSON_RETINUE;
						routine.add(mapPerson.get(findPersonStr)); //随行人员，ICPC数据是和队伍相关的，不过系统是和领队相关的 TODO
					} else {
						System.out.println("UNKNOW TEAM ROLE : " + n[1]);
						specialLog+="UNKNOW TEAM ROLE : " + n[1]+"\n";
					}
				}
				if (coach.getId() == -1) {
					coachCount++;
					System.out.println("# " + user.getId() + " "
							+ user.getUsername());
					coach.setUserBelongs(user.getId());
					coach.add();
				}
				if (school.getPostAddress().equals("")) {//在第一次的时候，添加学校地址信息、修改用户名为第一个教练的邮箱，密码是该教练的手机号
					school.setPostAddress(single[icAddress]+"\n");//add \n to avoid the original post address is empty
					school.update();
					user.setUsername(coach.getEmail());					
					if(coach.getMobile().equals("")){
						user.setOrginalPassword(defaultPassword);
						loginInfo.add(coach.getEmail()+"\t"+defaultPassword);
					}else{
						user.setOrginalPassword(coach.getMobile());
						loginInfo.add(coach.getEmail()+"\t"+coach.getMobile());
					}
					user.updateUser();
				}
				Team team = new Team();
				team.setEnglishName(teamName);
				team.setSchool(school.getId());
				team.setCoach(coach.getId());
				if (single[icStatus].equals("ACCEPTED")){// TODO ACCEPTED 最终版本记得改
					team.setType(Team.TEAM_OFFICAL);
					officalTeamCount++;
				}else if (single[icStatus].equals("PENDING"))
					team.setType(Team.TEAM_NOT_VERIFIED);
				else if(false)//TODO 旅游队的话，ICPC有什么标志吗？
					team.setType(Team.TEAM_TOURISM);
				team.setStatusDescription("|"+icpcTeamDesc);
				team.add();
				teamCount++;
				for (Person p : contestant) {
					if(p.getId()>0){
						System.out.println("Error contestant:"+p.getEnglishName());
						specialLog+="Error contestant:"+p.getEnglishName()+"\n";
						continue;
					}
					p.setUserBelongs(user.getId());
					p.setTeam(team.getId());
					p.add();
					contestantCount++;
				}
				for (Person p : reserve) {
					if(p.getId()>0){
						System.out.println("Error reserve:"+p.getEnglishName());
						specialLog+="Error reserve:"+p.getEnglishName()+"\n";
						continue;
					}
					p.setUserBelongs(user.getId());
					p.setTeam(team.getId());
					p.add();
					reserveCount++;
				}
				for (Person p : routine) {
					if(p.getId()>0){
						System.out.println("Error routine:"+p.getEnglishName());
						specialLog+="Error routine:"+p.getEnglishName()+"\n";
						continue;
					}
					p.setUserBelongs(user.getId());
//					p.setTeam(team.getId());//随行人员没有队伍信息
					p.add();
					routineCount++;
				}
			}
			System.out.println("Import Successfully!");
			s.message="已经成功导入了 "+schoolCount+" 个学校 "+teamCount+" 支队伍，其中正式队 "+officalTeamCount+" 支，教练 "+coachCount+" 位，正式队员： "+contestantCount+" 人，替补队员 "+reserveCount+" 人，随行人员 "+routineCount+" 人。<br/><b style='color:red;'>如果没有问题，请记得清空本缓存！</b><pre>"+specialLog+"</pre>";
			s.msgType=MSG.notice;
		} catch (SQLException e) {
			 s.message+=e.getMessage();
			e.printStackTrace();
		} finally {
			request.getSession().setAttribute("CSSession", s);
			request.getSession().setAttribute("loginInfo", loginInfo);
			response.sendRedirect("admin_imported.jsp");
		}
	}

}
