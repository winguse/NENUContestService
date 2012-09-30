<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="java.util.Date,cn.edu.nenu.acm.contestservice.*,cn.edu.nenu.acm.contestservice.modeling.objects.*,java.sql.SQLException,java.sql.Connection,java.sql.PreparedStatement,java.sql.ResultSet,java.util.List"%>
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
<title>领队列表</title>
<%@ include file="include/body_html_java.jspf"%>
<div id="main">
	<h1 style="text-align:center">领队列表</h1>
	<p><a href="admin_user_edit.jsp?action=addleader">添加一个领队</a></p>
	<%
		try {
				Connection conn = Site.getDataBaseConnection();
				PreparedStatement pstat = conn
						.prepareStatement(
								"SELECT User.id,ChineseName,EnglishName,School,Username,Password,Salt,Permission,Confirm FROM User,School WHERE School.id=School and Permission=?",
								ResultSet.TYPE_FORWARD_ONLY,
								ResultSet.CONCUR_UPDATABLE,
								ResultSet.CLOSE_CURSORS_AT_COMMIT);
				pstat.setInt(1, User.USER_LEADER);
				pstat.execute();
				ResultSet rs = pstat.getResultSet();
	%>
	<table class="table table-striped table-bordered table-condensed"><thead>
		<tr>
			<th>ID</th>
			<th>学校</th>
			<th>用户名</th><%--
			<th>密码</th>
			<th>Salt</th>
			<th>Permission</th>--%>
			<th>是否确认</th>
			<th>发票抬头</th>
			<th>管理选项</th>
		</tr></thead><tbody>
		<%
		List<Message> msglst=null;
		Message msg=null;
		int cnt=0;
			while (rs.next()) {
				cnt++;
		%>
		<tr>
			<td><%=rs.getInt("id")%></td>
			<td><%if(rs.getInt("School")!=1){ %><a href="school_edit.jsp?id=<%=rs.getInt("School")%>"
			rel="popover" data-placement="top" data-content="<%=Site.htmlEncode(rs.getString("EnglishName"))%>" data-original-title="<%=Site.htmlEncode(rs.getString("ChineseName"))%>"><%} %>
			<%=Site.htmlEncode(rs.getString("ChineseName"))%>
			<%if(rs.getInt("School")!=1){ %></a><%} %></td>
			<td><a href="admin_user_edit.jsp?username=<%=Site.htmlEncode(rs.getString("Username"))%>"><%=Site.htmlEncode(rs.getString("Username"))%></a></td>
<%--			<td><%=rs.getString("Password")%></td>
			<td><%=rs.getString("Salt")%></td>
			<td><%=rs.getInt("Permission")%></td>--%>
			<td><%=rs.getBoolean("Confirm")?"Yes":"No" %></td>
			<td><%
			msglst=Message.getSpecificMessage(Message.MESSAGE_SPEC_OF_TICKET_NICK, rs.getInt("id"));
			if(msglst.size()>0){
				msg=msglst.get(0);%><%=msg.getContent()%><%
			}else{
				%>暂无信息<%
			}
					%></td>
			<td>
				<a href="admin_user_edit.jsp?username=<%=Site.htmlEncode(rs.getString("Username"))%>">编辑用户</a>
			<%--	<a href="admin_leader_main.jsp?id=<%=rs.getInt("id")%>">Act As Him</a> --%>
				<a href="login?action=otl&username=<%=Site.htmlEncode(rs.getString("Username"))%>&password=<%=Site.hash(otl+rs.getString("Username"), "otl")%>&otl=<%=otl%>&otl_permission=1">角色扮演</a>
				<a href="admin_user_edit.jsp?id=<%=rs.getInt("id")%>&action=delete">删除</a>
			</td>
		</tr>
		<%
			}
		%>
	</tbody></table>
	<p>共计：<%=cnt %></p>
	<%
		pstat.close();
		conn.close();
	%>

	<%
		} catch (SQLException e) {
				e.printStackTrace();
				s.message = "Error Occour While Perform Your Request. Technical Detial: "
						+ e.getMessage();
				s.msgType = MSG.error;
				response.sendRedirect("admin_hotel_room.jsp");
			}
	%>
</div>
<%@ include file="include/bottom_html_java.jspf"%>

