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
import cn.edu.nenu.acm.contestservice.modeling.objects.LedPlan;
import cn.edu.nenu.acm.contestservice.modeling.objects.Log;
import cn.edu.nenu.acm.contestservice.modeling.objects.User;

/**
 * Servlet implementation class PlanUpdate
 */
@WebServlet("/plan_update")
public class PlanUpdate extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PlanUpdate() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		final String actionURL = "plan_update";
		CSSession s = (CSSession) request.getSession()
				.getAttribute("CSSession");
		if (s == null || s.user == null) {
			response.sendRedirect(".");
			return;
		}
		String id = request.getParameter("id");
		String arrival=request.getParameter("arrival");
		String arrivalTraffic=request.getParameter("arrivaltraffic");
		String leave=request.getParameter("leave");
		String leaveTraffic=request.getParameter("leavetraffic");
		String bookTicket=request.getParameter("bookticket");
		try{
			if (s.user.getPermission() != User.USER_ADMINISTRATOR) {
				if (!s.verifyToken(id + actionURL,
						request.getParameter("idtoken"))) {
					new Log(s.user.getId(), request.getRemoteAddr()+" / "+request.getHeader("x-forwarded-for"),
							new Date().getTime(),
							"Updateing illegal Plan information (id)").add();
					s.message = "Illegal Plan ID!!";
					s.msgType = MSG.error;
					return;
				}
			}
			LedPlan plan=new LedPlan();
			if(s.user.getPermission()==User.USER_ADMINISTRATOR){
				plan.setLeader(Integer.parseInt(request.getParameter("leaderid")));
			}else{
				plan.setLeader(s.user.getId());
			}
			plan.setArrival(Long.parseLong(arrival));
			plan.setArrivalTraffic(arrivalTraffic);
			plan.setLeave(Long.parseLong(leave));
			plan.setLeaveTraffic(leaveTraffic);
			plan.setBookTicket(bookTicket);
			if(id.equals("-1")){
				plan.add();
				s.message="Your Plan has Added.";
				s.renewToken();
			}else{
				plan.setId(Integer.parseInt(id));
				plan.update();
				s.message="Plan Information Updated.";
			}
			s.msgType=MSG.notice;
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

}
