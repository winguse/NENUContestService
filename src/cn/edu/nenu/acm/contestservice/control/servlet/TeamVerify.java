package cn.edu.nenu.acm.contestservice.control.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.edu.nenu.acm.contestservice.CSSession;
import cn.edu.nenu.acm.contestservice.Site;
import cn.edu.nenu.acm.contestservice.modeling.objects.Team;
import cn.edu.nenu.acm.contestservice.modeling.objects.User;

/**
 * Servlet implementation class TeamVerify
 */
@WebServlet("/teamVerify")
public class TeamVerify extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TeamVerify() {
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
		response.setCharacterEncoding("utf-8");
		CSSession s = (CSSession) request.getSession()
				.getAttribute("CSSession");
		 if (s == null || s.user == null) {
			 response.sendRedirect(".");
			 return;
		 }
		try {
			 if (s.user.getPermission() == User.USER_ADMINISTRATOR) {
					response.setContentType("application/json");
					int id=Integer.parseInt(request.getParameter("teamId"));
					int type=Integer.parseInt(request.getParameter("teamType"));
					String statusDescription=request.getParameter("statusdescription");
					if(statusDescription==null)
						statusDescription="";
					Team team=new Team();
					if(team.load(id)){
						String[] orgDesc=team.getStatusDescription().split("\\|");
						String icpcDesc="没有ICPC数据。";
						if(orgDesc.length>=2)
							icpcDesc=orgDesc[1];
						team.setStatusDescription(statusDescription+"|"+icpcDesc);
						team.setType(type);
						team.update();
						response.getWriter().print("{\"code\":0}");
					}else{
						response.getWriter().print("{\"code\":1}");
					}
			 }else{
					response.getWriter().print("{\"code\":2}");
			 }
		}catch(Exception e){
			response.getWriter().print("{\"code\":3}");
		}finally{
		}
	}

}
