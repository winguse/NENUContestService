<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="cn.edu.nenu.acm.contestservice.*,cn.edu.nenu.acm.contestservice.modeling.objects.*,
	java.sql.SQLException,
	java.util.Date
	"%> 
<%@ include file="include/head_java.jspf"%>
<%
if (s==null||s.user == null){
	response.sendRedirect(".");
	return;
}
if (s.user.getPermission() != User.USER_ADMINISTRATOR){
	s.message="Permisson Deny!";
	s.msgType=MSG.warning;
	response.sendRedirect(".");
	return;
}
	s.lastPage=request.getRequestURI()+"?"+request.getQueryString();
%>
<%@ include file="include/head_html.jspf"%>
<title>系统日志</title>
<%@ include file="include/body_html_java.jspf"%>
<div id="main">
	<h1 style="text-align:center">系统日志</h1>
<%
try{
	int _page=1,pagesize=20;
	try{
		_page=Integer.parseInt(request.getParameter("page"));
		pagesize=Integer.parseInt(request.getParameter("pagesize"));
	}catch(Exception e){
		
	}
	if(_page<1)_page=1;
	%>
	<div align="center">
		<a href="admin_log.jsp?page=<%=_page-1 %>">Previous</a>
		<a href="admin_log.jsp?page=<%=_page+1 %>">Next</a>
	</div>
	<table class="table table-striped table-bordered table-condensed"><thead>
		<tr>
			<th>ID</th>
			<th>User Name</th>
			<th>Time</th>
			<th>IP</th>
			<th>Action</th>
		</tr></thead><tbody>
	<%
	for(Log l:Log.getLogs(_page,pagesize)){
%>
		<tr>
			<td><a href="#"><%=l.getId() %></a></td>
			<td><%=l.getUser() %></td>
			<td><%=new Date(l.getTime()).toLocaleString() %></td>
			<td><%=l.getIp() %></td>
			<td><%=Site.htmlEncode(l.getAction()).replace("&nbsp;", " ") %></td>
		</tr>
<%
	}%>
	</tbody></table>
<%
}catch(SQLException e){
	e.printStackTrace();
	s.message="Error Occour While Perform Your Request. Technical Detial: " +e.getMessage();
	s.msgType=MSG.error;
	response.sendRedirect("admin_hotel_room.jsp");
}
%>
</div>
<%@ include file="include/bottom_html_java.jspf"%>

