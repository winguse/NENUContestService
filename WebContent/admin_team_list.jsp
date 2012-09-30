<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="cn.edu.nenu.acm.contestservice.*,cn.edu.nenu.acm.contestservice.modeling.objects.*,
	java.sql.SQLException,
	java.util.HashMap"%> 
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
<title>参赛队管理</title>
<%@ include file="include/body_html_java.jspf"%>
<div id="main">
	<h1 style="text-align:center">参赛队管理</h1>
<%
try{
	//这个地方显然不是一个好做法，数据库以Leader为核心也显然不是一个好办法。2012-9-19。
	HashMap<Integer,School> schools=new HashMap<Integer,School>();
	HashMap<Integer,Hotel> hotels=new HashMap<Integer,Hotel>();
	for(Hotel h:Hotel.getAllHotels()){
		hotels.put(new Integer(h.getId()),h);
	}
	for(School sc:School.getAllSchools()){
		schools.put(new Integer(sc.getId()), sc);
	}
%>
	<table class="table table-striped table-bordered table-condensed"><thead>
		<tr>
			<th>学校</th>
			<th>队伍名称</th>
			<th>类型</th>
			<th>教练</th>
			<th>队员</th>
<%--			<th>座位</th> --%>
<%--			<th>酒店</th>--%>
			<th>状态描述</th>
			<th>管理选项</th>
		</tr></thead><tbody>
<%
	int cnt=0;
	for(Team t:Team.getAllTeams()){
		cnt++;
%>
		<tr>
			<td><%School sc=schools.get(new Integer(t.getSchool())); if(sc!=null){%>
			<a href="school_edit.jsp?id=<%=t.getSchool()%>"  rel="popover" data-placement="top" data-content="<%=Site.htmlEncode(sc.getEnglishName())%>"
			data-original-title="<%=Site.htmlEncode(sc.getChineseName())%>">
			<%=Site.htmlEncode(sc.getChineseName())%></a><%} %></td>
			<td><a href="team_edit.jsp?id=<%=t.getId() %>"><%=Site.htmlEncode(t.getChineseName()) %><br/>(<%=Site.htmlEncode(t.getEnglishName()) %>)</a></td>
			<td><% 
			switch(t.getType()){
			case Team.TEAM_NOT_VERIFIED:
				%>未审核<%
				break;
			case Team.TEAM_OFFICAL:
				%>正式队<%
				break;
			case Team.TEAM_TOURISM:
				%>酱油队<%
				break;
			case Team.TEAM_OFFICAL_UNDER_VERIFIED:
				%>待审正式队<%
				break;
			case Team.TEAM_TOURISM_VERIFIED:
				%>待审酱油队<%
				break;
			case Team.TEAM_OFFICAL_VERIFIED:
				%>#正式队（已审）<%
				break;
			case Team.TEAM_OFFICAL_UNDER_VERIFIED_AGAIN:
				%>#正式队（待再审）<%
				break;
			}
			%></td>
			<td><%
			Coach coach=new Coach();
			coach.load(t.getCoach());
			%><a href="person_edit.jsp?id=<%=coach.getId()%>"><%=Site.htmlEncode(coach.getChineseName()) %><br/>(<%=Site.htmlEncode(coach.getEnglishName()) %>)</a></td>
			<td><%for(Person p:Person.getTeamMembers(t.getId())){ %>
				<p>
					<%=p.getGender()?"♂":"♀"%><a<%if(p.getTitle()==Person.PERSON_TEAM_MEMBER_RESERVE){ %> style="color:#f70;" title="替补队员"<%}%> href="person_edit.jsp?id=<%=p.getId()%>"><%=Site.htmlEncode(p.getChineseName()) %>(<%=Site.htmlEncode(p.getEnglishName()) %>)</a>
					<a href="person_edit.jsp?id=<%=p.getId()%>&action=delete" title="删除(Delete)"><i class="icon-trash"></i></a>
				</p>
			<%} %></td>
<%--			<td><%=Site.htmlEncode(t.getSeat()) %></td>--%>
<%-- 			<td><%if(t.getHotel()!=0){ %><a href="admin_hotel_edit.jsp?id=<%=t.getHotel()%>"><%=Site.htmlEncode(hotels.get(new Integer(t.getHotel())).getName()) %><%}else{%>暂无信息<%} %></a></td>--%>
			<td><%=Site.htmlEncode(t.getStatusDescription()) %></td>
			<td><a href="team_edit.jsp?id=<%=t.getId() %>">编辑</a> <a href="team_edit.jsp?id=<%=t.getId() %>&action=delete">删除</a></td>
		</tr>
<%
	}%>
	</tbody></table>
	<p>共计：<%=cnt %></p><%
}catch(SQLException e){
	e.printStackTrace();
	s.message="Error Occour While Perform Your Request. Technical Detial: " +e.getMessage();
	s.msgType=MSG.error;
	response.sendRedirect(s.lastPage);
}
%>
</div>
<%@ include file="include/bottom_html_java.jspf"%>

