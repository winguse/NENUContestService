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
import cn.edu.nenu.acm.contestservice.modeling.objects.User;
import cn.edu.nenu.acm.contestservice.modeling.roles.Leader;

/**
 * Servlet implementation class Confirm
 */
@WebServlet("/confirm")
public class Confirm extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Confirm() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		CSSession s = (CSSession) request.getSession()
				.getAttribute("CSSession");
		if (s == null || s.user == null) {
			response.sendRedirect(".");
			return;
		}
		try{
			s.user.setConfirm(true);
			s.user.updateUser();
			s.message="Infromation Confirmed.";
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

}
