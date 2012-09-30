<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="cn.edu.nenu.acm.contestservice.*,cn.edu.nenu.acm.contestservice.modeling.objects.*,
	java.sql.SQLException,
	java.util.HashMap"%> 
<%@ include file="include/head_java.jspf"%>
<%
	s.message="提示：点击表头可以排序。";
	s.msgType=MSG.notice;
%>
<%@ include file="include/head_html.jspf"%>
<title>参赛队列表</title>
<%@ include file="include/body_html_java.jspf"%>
<div id="main">
	<h1 style="text-align:center">参赛队列表</h1>
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
		</tr></thead><tbody>
<%
	int cnt=0;
	for(Team t:Team.getAllTeams()){
		cnt++;
%>
		<tr>
			<td><%School sc=schools.get(new Integer(t.getSchool())); if(sc!=null){%>
			<%=Site.htmlEncode(sc.getChineseName())%><%} %></td>
			<td><%=Site.htmlEncode(t.getChineseName()) %><br/>(<%=Site.htmlEncode(t.getEnglishName()) %>)</td>
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
				%>正式队<%
				break;
			case Team.TEAM_TOURISM_VERIFIED:
				%>酱油队<%
				break;
			case Team.TEAM_OFFICAL_VERIFIED:
				%>正式队<%
				break;
			case Team.TEAM_OFFICAL_UNDER_VERIFIED_AGAIN:
				%>正式队<%
				break;
			}
			%></td>
			<td><%
			Coach coach=new Coach();
			coach.load(t.getCoach());
			%><%=Site.htmlEncode(coach.getChineseName()) %><br/>(<%=Site.htmlEncode(coach.getEnglishName()) %>)</td>
			<td><%for(Person p:Person.getTeamMembers(t.getId())){ %>
				<p>
					<%=p.getGender()?"♂":"♀"%><%=Site.htmlEncode(p.getChineseName()) %>(<%=Site.htmlEncode(p.getEnglishName()) %>)
				</p><%} %>
			</td>
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

