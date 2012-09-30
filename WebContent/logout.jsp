<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="cn.edu.nenu.acm.contestservice.*,cn.edu.nenu.acm.contestservice.modeling.objects.Log,java.util.Date"%>
<%
	CSSession s = (CSSession) request.getSession().getAttribute(
			"CSSession");
	if(s!=null&&s.user!=null){
		new Log(s.user.getId(),request.getRemoteAddr(),new Date().getTime(),"LogOut").add();
		request.getSession().invalidate();
		s.user=null;
		s.msgType=MSG.notice;
		s.message="See you next time!";
		request.getSession().setAttribute("CSSession", s);
	}
	response.sendRedirect(".");
%>