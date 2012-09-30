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
import cn.edu.nenu.acm.contestservice.modeling.objects.School;
import cn.edu.nenu.acm.contestservice.modeling.objects.User;

/**
 * Servlet implementation class SchoolUpdate
 */
@WebServlet("/school_update")
public class SchoolUpdate extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SchoolUpdate() {
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
		final String actionURL = "school_update";
		CSSession s = (CSSession) request.getSession()
				.getAttribute("CSSession");
		if (s == null || s.user == null) {
			response.sendRedirect(".");
			return;
		}
		String id = request.getParameter("id");
		String chineseName=request.getParameter("chinesename");
		String englishName=request.getParameter("englishname");
		String postCode=request.getParameter("postcode");
		String postAddress=request.getParameter("postaddress");
		try{
			if (s.user.getPermission() != User.USER_ADMINISTRATOR) {
				if (!s.verifyToken(id + actionURL,
						request.getParameter("idtoken"))) {
					new Log(s.user.getId(), request.getRemoteAddr()+" / "+request.getHeader("x-forwarded-for"),
							new Date().getTime(),
							"Updateing illegal school information (id)").add();
					s.message = "Illegal School ID!!";
					s.msgType = MSG.error;
					return;
				}
			}
			School school=new School();
			school.setChineseName(chineseName);
			school.setEnglishName(englishName);
			school.setPostAddress(postAddress);
			school.setPostCode(postCode);
			if(id.equals("-1")){
				school.add();
				s.message="New School Added.";
				s.renewToken();
				Connection conn = Site.getDataBaseConnection();
				if(s.user.getPermission()==User.USER_LEADER){
					conn.createStatement().execute("UPDATE User SET School=LAST_INSERT_ID() WHERE id=" + s.user.getId());
					s.user.load(s.user.getId());
				}else if(s.user.getPermission()==User.USER_ADMINISTRATOR){
					conn.createStatement().execute("UPDATE User SET School=LAST_INSERT_ID() WHERE id=" + Integer.parseInt(request.getParameter("leaderid")));
				}
				conn.close();
			}else{
				school.setId(Integer.parseInt(id));
				school.update();
				s.message="School Information Updated.";
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
