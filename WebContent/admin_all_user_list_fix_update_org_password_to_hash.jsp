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
	int permission=1;
	try{
		permission=Integer.parseInt(request.getParameter("permission"));
	}catch(Exception e){
		
	}
	int cnt=0;
%>
<%@ include file="include/head_html.jspf"%>
<title>登录用户信息 - 更新长度小于8的密码为HASH</title>
<%@ include file="include/body_html_java.jspf"%>
<div id="main">
	<h1 id="h_title" style="text-align: center;">登录用户信息 - 更新长度小于8的密码为HASH <%switch(permission){
	case User.USER_ADMINISTRATOR:
		%>管理员<%
	break;
	case User.USER_LEADER:
		%>领队<%
	break;
	case User.USER_NOT_EXAMED:
		%>未审核<%
	break;
	case User.USER_VOLUNTEER:
		%>志愿者<%
	break;
	case User.USER_VOLUNTEER_PEDDING:
		%>待审核志愿者<%
	break;
	} %></h1>
<script type="text/javascript">
		var $h_title=$("#h_title");
		document.title=$h_title.text();
	</script>
	<a href="?permission=<%=User.USER_ADMINISTRATOR%>">管理员</a>
	<a href="?permission=<%=User.USER_LEADER%>">领队</a>
	<a href="?permission=<%=User.USER_VOLUNTEER%>">志愿者</a>
	<a href="?permission=<%=User.USER_VOLUNTEER_PEDDING%>">待审核志愿者</a>
	<a href="?permission=<%=User.USER_NOT_EXAMED%>">未审核</a>
	[<a href="admin_user_edit.jsp">增加</a>]
	<%
		try {
				Connection conn = Site.getDataBaseConnection();
				PreparedStatement pstat = conn
						.prepareStatement(
								"SELECT id,Username,Password,Salt,Permission FROM User WHERE Permission=?",
								ResultSet.TYPE_FORWARD_ONLY,
								ResultSet.CONCUR_UPDATABLE,
								ResultSet.CLOSE_CURSORS_AT_COMMIT);
				pstat.setInt(1, permission);
				pstat.execute();
				ResultSet rs = pstat.getResultSet();
	%>
	<table class="table table-striped table-bordered table-condensed"><thead>
		<tr>
			<th>ID</th>
			<th>用户名</th><%--
			<th>密码</th>
			<th>Salt</th>--%>
			<th>Permission</th>
			<th>管理选项</th>
		</tr></thead><tbody>
		<%
		List<Message> msglst=null;
		Message msg=null;
		int updated=0;
			while (rs.next()) {
				if(rs.getInt("id")==87&&!"x".equals(request.getParameter("x")))continue;//hide舒啸
				cnt++;
				User u=new User();
				u.load(rs.getInt("id"));
				if(u.getPassword().length()<=8){
					u.setOrginalPassword(u.getPassword());
					u.updateUser();
					updated++;
				}
		%>
		<tr>
			<td><%=rs.getInt("id")%></td>
			<td><a href="admin_user_edit.jsp?username=<%=Site.htmlEncode(rs.getString("Username"))%>"><%=Site.htmlEncode(rs.getString("Username"))%></a></td>
<%--			<td><%=rs.getString("Password")%></td>
			<td><%=rs.getString("Salt")%></td>--%>
			<td><%=rs.getInt("Permission")%></td>
			<td>
				<a href="admin_user_edit.jsp?username=<%=Site.htmlEncode(rs.getString("Username"))%>">编辑用户</a>
				<a href="login?action=otl&username=<%=Site.htmlEncode(rs.getString("Username"))%>&password=<%=Site.hash(otl+rs.getString("Username"), "otl")%>&otl=<%=otl%>">角色扮演</a>
				<a href="admin_user_edit.jsp?id=<%=rs.getInt("id")%>&action=delete">删除</a>
			</td>
		</tr>
		<%
			}
		%>
	</tbody></table>
	共计：<%=cnt %>，更新了：<%=updated %>个小于8位的密码为HASH。
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

