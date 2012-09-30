package cn.edu.nenu.acm.contestservice.control.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.edu.nenu.acm.contestservice.CSSession;
import cn.edu.nenu.acm.contestservice.EmailSender;
import cn.edu.nenu.acm.contestservice.MSG;
import cn.edu.nenu.acm.contestservice.Site;
import cn.edu.nenu.acm.contestservice.modeling.objects.Person;
import cn.edu.nenu.acm.contestservice.modeling.objects.School;
import cn.edu.nenu.acm.contestservice.modeling.objects.User;

/**
 * Servlet implementation class NoticeLeader
 */
@WebServlet("/NoticeLeader")
public class NoticeLeader extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public NoticeLeader() {
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
		response.setContentType("text/html");
		CSSession s = (CSSession) request.getSession()
				.getAttribute("CSSession");
		if (s == null || s.user == null) {
			response.sendRedirect(".");
			return;
		}
		s.message = "";
		PrintWriter out = response.getWriter();
		out.print("<!DOCTYPE HTML><html><head><title>Working...DO NOT CLOSE.</title></head><body><script type='text/javascript'>setInterval(function(){document.body.scrollTop=document.body.offsetHeight;},1000);</script>");
		out.flush();
		try {
			if (s.user.getPermission() != User.USER_ADMINISTRATOR) {
				s.msgType = MSG.error;
				s.message = "权限不足！";
				return;
			}
			boolean isTest = !Site.isEmpty(request.getParameter("test"));
			ArrayList<String> arrLeader = new ArrayList<String>();
			if ("send".equals(request.getParameter("action"))) {
				String mpw = request.getParameter("password");
				if (Site.isEmpty(mpw)) {
					s.message = "空邮箱密码！";
				} else {
					List<Person> coachList = Person
							.loadSpecifyPersons(Person.PERSON_COACH);
					HashMap<String, String> extraCoach = new HashMap<String, String>();
					HashMap<Integer, List<String>> uid2email = new HashMap<Integer, List<String>>();
					String extraCoachStr = request.getParameter("extracoach");
					String excludeEmail = request.getParameter("excludeemail");
					String[] exEmail = excludeEmail.split("\\n");
					Set<String> exEmailSet = new HashSet<String>();
					String tttt = null;
					for (int i = 0; i < exEmail.length; i++) {
						exEmailSet.add(exEmail[i].toLowerCase().trim());
						out.print(exEmail[i].toLowerCase() + "<br/>");
					}
					if (!Site.isEmpty(extraCoachStr)) {
						String[] exCoachArr = extraCoachStr.split("\\n");
						for (int i = 0; i < exCoachArr.length; i++) {
							String[] sg = exCoachArr[i].split("\\t");
							if (sg.length < 3) {
								s.message += "Error single extra coach information : "
										+ sg + "<br/>";
								continue;
							}
							extraCoach.put(sg[0].toLowerCase(), exCoachArr[i]);
						}
					}
					out.print("Pedding dealing the coach information.<br/>");
					out.flush();
					for (Person p : coachList) {
						List<String> emailList = uid2email.get(p
								.getUserBelongs());
						if (emailList == null)
							emailList = new ArrayList<String>();
						emailList.add(p.getEmail() + "|" + p.getEnglishName());
						String[] en = p.getEnglishName().toLowerCase()
								.split(" ");
						if (en.length >= 2) {
							String findstr = en[0] + " " + en[1];
							String coachStr = extraCoach.get(findstr);
							if (coachStr == null) {
								findstr = en[1] + " " + en[0];
								coachStr = extraCoach.get(findstr);
							}
							if (coachStr != null) {
								String[] ch = coachStr.split("\\t");
								emailList.add(ch[1] + "|" + ch[0]);
								extraCoach.remove(findstr);
							}
						}
						uid2email.put(p.getUserBelongs(), emailList);
					}
					Iterator<Entry<String, String>> itc = extraCoach.entrySet()
							.iterator();
					while (itc.hasNext()) {
						Entry<String, String> e = itc.next();
						s.message += "This Extra Coach was not added: "
								+ e.getValue() + "<br/>";
					}
					Iterator<Entry<Integer, List<String>>> it = uid2email
							.entrySet().iterator();
					// -------------send mail init
					Date d = new Date();
					EmailSender sender = new EmailSender();
					String contentTemplate = "<!DOCTYPE html><html><head><title>东师赛事管理系统 - 系统通知</title><style>#main p{text-indent:2em;}</style></head><body><p>COACH_NAME教练：</p><div id='main'><p>您好！邮件请以最后一封为准。</p><p>我们已经把ICPC官网上的数据全部导入到我们的系统中。我们的注册系统将于2012年9月18日7：00正式开放，请你使用<b style='color:blue;'>USERNAME</b>，密码为：<b style='color:blue;'>PASSWORD</b>，登录我们的系统：<a href='http://acm.nenu.edu.cn/ContestService/'>http://acm.nenu.edu.cn/ContestService/</a>，协助我们完成以下四件事情：</p><p><ol><li>确认你们的学校名（中英文，包括简称），教练名、队伍名、队员名，如有错误，请更正。并请同时告诉我们，这将关系到证书上的姓名。</li><li>补充教练、队员的中文信息（姓名、性别和衣服大小最为关键）。</li><li>添加打星队伍的信息。</li><li>添加随从人员的信息。</li></ol></p><p style='color:red;'>请登录后注意修改密码！如果贵校有多个教练，我们会同时向教练的邮箱发送邮件，所以如果您的密码修改请通知贵校的其他教练。</p><p>此外，我们的系统中还提供了宾馆预订功能，该功能将于2012年9月23日上午8：00正式开放。请及时登录预订房间。在该系统里可以浏览到你们对应的志愿者信息，如有什么问题也可以直接和志愿者联系。另外，请大家在买好往返车票或机票后，登录我们的系统补充上你们来长和离长的具体日期和时间，以方便我们的接待工作，谢谢！</p></div><p style='text-align:right;'>长春站赛事组委会<br/>"
							+ d.toLocaleString() + "</p></body></html>";
					sender.setFrom("acm@nenu.edu.cn");
					sender.setUser("acm@nenu.edu.cn");
					sender.setPassword(mpw);
					sender.setHost("smtp.nenu.edu.cn");
					sender.setSubject("2012 ACM-ICPC Asia Regional Contest (Changchun Site) Contest Service System Notice");
					sender.connect();
					// ------------send mail init end
					int sendCnt = 0;
					while (it.hasNext()) {
						Entry<Integer, List<String>> tmp = it.next();
						User u = new User(tmp.getKey());
						List<String> coachs = tmp.getValue();
						School sch = new School();
						sch.load(u.getSchool());
						u.setUsername(sch.getChineseName().split("\\(")[0]);
						String password = Site.generateSalt().substring(0, 6);
						for (int i = password.length(); i < 6; i++) {
							password += "0";
						}
						u.setOrginalPassword(password);

						String[] cot = coachs.get(0).split("\\|");
						String to = cot[0];
						String coachName = cot[1];
						HashSet<String> coachMailSet = new HashSet<String>();
						coachMailSet.add(cot[0].toLowerCase());
						boolean notTosend = false;
						for (int i = 1; i < coachs.size(); i++) {
							cot = coachs.get(i).split("\\|");
							// out.print("Try："+exEmailSet.toString()+" ￥ "+cot[0].toLowerCase()+"<br/>");
							if (exEmailSet.contains(cot[0].toLowerCase())) {
								notTosend = true;
								// out.print("found:" + cot[0].toLowerCase());
								break;
							}
							if (coachMailSet.contains(cot[0].toLowerCase()))
								continue;
							coachMailSet.add(cot[0].toLowerCase());
							to += "," + cot[0];
							coachName += "， " + cot[1];
						}
						if (notTosend) {
							out.print(to + "Ignored.<br/>");
							s.message += to + "Ignored.<br/>";
							continue;
						}
						sender.setTo(to);
						sender.setContent(contentTemplate
								.replaceAll("USERNAME", u.getUsername())
								.replaceAll("PASSWORD", password)
								.replaceAll("COACH_NAME", coachName));
						s.message += "Sendding to: [" + to + "], [" + coachName
								+ "], " + u.getUsername() + "<br/>";
						if (isTest) {
							out.print("mail sended to: " + to + "<br/>");
							out.flush();
							sendCnt++;
						} else {
							boolean sended = false;
							int failCount = 0;
							while (true) {
								try {
									sender.send();
									u.updateUser();
									sended = true;
								} catch (MessagingException e) {
									e.printStackTrace();
									failCount++;
									if (failCount == 5) {
										s.message += "<b style='color:red'>------fail begin-----</b>"
												+ e.getMessage()
												+ "<b style='color:red'>------fail end-----</b><br/>";
										break;
									} else {
										sended = false;
										try {
											out.print("[Fail:"
													+ failCount
													+ "/5] Due to the Exception Of limitation, Wait 20 minutes for time out.<br/>");
											out.flush();
											sender.close();
											for (int i = 60 * 20; i > 0; i--) {
												out.print(i + "s..<br/>");
												out.flush();
												Thread.sleep(1000);
											}
											sender.connect();
											// sender.send();
										} catch (InterruptedException e1) {
											e1.printStackTrace();
										}
									}
								}
								if (sended) {
									out.print("mail sended to: " + to + "<br/>");
									out.flush();
									sendCnt++;
									s.message += "mail sended to: " + to
											+ "<br/>";
									break;
								}
								if (failCount == 5) {
									out.print("mail fail sended to: " + to
											+ " , GIVING UP!<br/>");
									out.flush();
									s.message += "mail fail sended to: " + to
											+ " , GIVING UP!<br/>";
									break;
								}
							}
						}
						if (sendCnt % 2 == 0) {
							sender.close();
							try {
								out.print("Pause every 2 emails...<br/>");
								out.flush();
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							sender.connect();
						}
					}
					sender.close();// ----------send mail destoryed
				}
				s.msgType = MSG.notice;
			}
		} catch (SQLException e) {
			s.message += e.getMessage();
			s.msgType = MSG.error;
			e.printStackTrace();
		} catch (MessagingException e) {
			s.message += e.getMessage();
			s.msgType = MSG.error;
			e.printStackTrace();
		} finally {
			request.getSession().setAttribute("CSSession", s);
			out.print("<script type='text/javascript'>alert('You Will Be Redirect After 60s.');setTimeout(function(){window.location='"
					+ s.lastPage + "';},60000);</script></body></html>");
		}
	}

}
