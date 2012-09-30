<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="cn.edu.nenu.acm.contestservice.*,cn.edu.nenu.acm.contestservice.modeling.objects.*,java.sql.SQLException,java.sql.Connection,java.sql.PreparedStatement,java.sql.ResultSet,java.util.Date"%>

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
	long otl=new Date().getTime();
%>
<%@ include file="include/head_html.jspf"%>
<title>领队行程计划</title>
<%@ include file="include/body_html_java.jspf"%>
<div id="main">
	<h1 style="text-align:center">领队行程计划</h1>
<%
try{
	Connection conn = Site.getDataBaseConnection();
	PreparedStatement pstat = conn
			.prepareStatement(
					"SELECT User.id,EnglishName,School,ChineseName,Username,Leader,Arrival,ArrivalTraffic,`Leave`,LeaveTraffic,BookTicket From LedPlan,User,School WHERE School.id=School and Leader=User.id ",
					ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_UPDATABLE,
					ResultSet.CLOSE_CURSORS_AT_COMMIT);
	pstat.execute();
	ResultSet rs = pstat.getResultSet();

%>
	<table class="table table-striped table-bordered table-condensed"><thead>
		<tr>
			<th>学校</th>
			<th>用户名</th>
			<th>抵达时间</th>
			<th>抵达交通方式</th>
			<th>离开时间</th>
			<th>离开交通方式</th>
			<!-- <th>订票信息</th> -->
			<th>管理选项</th>
		</tr></thead><tbody>
		<%
			while (rs.next()) {
		%>
		<tr>
			<td><a href="school_edit.jsp?id=<%=rs.getInt("School")%>"   rel="popover" data-placement="top" data-content="<%=Site.htmlEncode(rs.getString("EnglishName"))%>"
			data-original-title="<%=Site.htmlEncode(rs.getString("ChineseName"))%>"><%=Site.htmlEncode(rs.getString("ChineseName"))%></a></td>
			<td><a href="admin_user_edit.jsp?username=<%=rs.getString("Username")%>"><%=rs.getString("Username")%></a></td>
			<td><%=new Date(rs.getLong("Arrival")).toLocaleString() %></td>
			<td><%=Site.htmlEncode(rs.getString("ArrivalTraffic"))%></td>
			<td><%=new Date(rs.getLong("Leave")).toLocaleString() %></td>
			<td><%=Site.htmlEncode(rs.getString("LeaveTraffic"))%></td>
			<!-- <td><%=Site.htmlEncode(rs.getString("BookTicket"))%></td> -->
			<td>
				<a href="admin_user_edit.jsp?username=<%=rs.getString("Username")%>">编辑用户</a>
				<%--<a href="admin_leader_main.jsp?id=<%=rs.getInt("User.id")%>">Act As Him</a> --%>
				<a href="login?action=otl&username=<%=Site.htmlEncode(rs.getString("Username"))%>&password=<%=Site.hash(otl+rs.getString("Username"), "otl")%>&otl=<%=otl%>">角色扮演</a>
				
			</td>
		</tr>
		<%
			}
		%>
	</tbody></table>

<%
	pstat.close();
	conn.close();
}catch(SQLException e){
	e.printStackTrace();
	s.message="Error Occour While Perform Your Request. Technical Detial: " +e.getMessage();
	s.msgType=MSG.error;
	response.sendRedirect("admin_hotel_room.jsp");
}
%>
</div>
<%@ include file="include/bottom_html_java.jspf"%>

