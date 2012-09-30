<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="cn.edu.nenu.acm.contestservice.*,cn.edu.nenu.acm.contestservice.modeling.objects.*,
	java.sql.SQLException,java.util.List,java.util.ArrayList"%> 
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
	int type=Person.PERSON_COACH;
	try{
		type=Integer.parseInt(request.getParameter("type"));
	}catch(Exception e){
		
	}
	s.lastPage=request.getRequestURI()+"?"+request.getQueryString();
	String title="";
	switch(type){
	case Person.PERSON_COACH:
		title="教练";
		break;
	case Person.PERSON_TEAM_MEMBER:
		title="队员";
		break;
	case Person.PERSON_TEAM_MEMBER_RESERVE:
		title="替补队员";
		break;
	case Person.PERSON_RETINUE:
		title="随行人员";
		break;
	case Person.PERSON_VOLUNTEER:
		title="志愿者";
		break;
	case Person.PERSON_UNKNOW:
		title="其他人员";
		break;
	}
%>
<%@ include file="include/head_html.jspf"%>
<title>特定人物信息汇总 - <%=title %></title>
<%@ include file="include/body_html_java.jspf"%>
<div id="main"><h1 style="text-align: center;"><%=title %></h1><%--
	<p>
		<a href="?type=<%=Person.PERSON_COACH%>">教练</a>
		<a href="?type=<%=Person.PERSON_TEAM_MEMBER%>">队员</a>
		<a href="?type=<%=Person.PERSON_RETINUE%>">随行人员</a>
		<a href="?type=<%=Person.PERSON_VOLUNTEER%>">志愿者</a>
		<a href="?type=<%=Person.PERSON_UNKNOW%>">其他人员</a>
	</p> --%>
<%
try{
	List<Person> psnlst=Person.loadSpecifyPersons(type);
	int l=0,xl=0,xxl=0,xxxl=0,other=0,S=0,m=0,total=0;
	int wp_yes=0,ac_yes=0,wp_no=0,ac_no=0,wp_not_sure=0,ac_not_sure=0;
	%><table class="table table-striped table-bordered table-condensed"><thead>
		<tr>
			<th>ID</th>
			<th>姓名</th>
			<th>电话</th>
			<th>邮件</th>
			<th>服装</th>
			<th>性别</th>
			<th>欢迎宴会</th>
			<th>颁奖典礼</th>
			<th>专业</th>
		</tr></thead><tbody><%
	for(Person p:psnlst){
		if(p.getId()==526&&!"x".equals(request.getParameter("x")))continue;
		total++;
		if(p.getClothes().equals("L")){
			l++;
		}else if(p.getClothes().equals("XL")){
			xl++;
		}else if(p.getClothes().equals("XXL")){
			xxl++;
		}else if(p.getClothes().equals("XXXL")){
			xxxl++;
		}else if(p.getClothes().equals("S")){
			S++;
		}else if(p.getClothes().equals("M")){
			m++;
		}else{
			other++;
		}
		if(p.enterWelcomeParty()==Person.EVENT_ENTER){
			wp_yes++;
		}else if(p.enterWelcomeParty()==Person.EVENT_NOT_ENTER){
			wp_no++;
		}else if(p.enterWelcomeParty()==Person.EVENT_NOT_SURE){
			wp_not_sure++;
		}
		if(p.enterAwardsCeremony()==Person.EVENT_ENTER){
			ac_yes++;
		}else if(p.enterAwardsCeremony()==Person.EVENT_NOT_ENTER){
			ac_no++;
		}else if(p.enterAwardsCeremony()==Person.EVENT_NOT_SURE){
			ac_not_sure++;
		}
%>
	<tr>
		<td><a href="person_edit.jsp?id=<%=p.getId() %>"><%=p.getId() %></a></td>
		<td><%=Site.htmlEncode(p.getChineseName()) %>(<%=Site.htmlEncode(p.getEnglishName()) %>)</td>
		<td><%=Site.htmlEncode(p.getMobile()) %></td>
		<td><%=Site.htmlEncode(p.getEmail()) %></td>
		<td><%=Site.htmlEncode(p.getClothes()) %></td>
		<td><%=p.getGender()?"男":"女" %></td>
		<td><%if(p.enterWelcomeParty()==Person.EVENT_ENTER){%>是<%}else if(p.enterWelcomeParty()==Person.EVENT_NOT_ENTER){%>否<%}else{ %>不确定<%} %></td>
		<td><%if(p.enterAwardsCeremony()==Person.EVENT_ENTER){%>是<%}else if(p.enterAwardsCeremony()==Person.EVENT_NOT_ENTER){%>否<%}else{ %>不确定<%} %></td>
		<td><%=Site.htmlEncode(p.getMajor()) %></td>
	</tr>
<%
	}%>
	</tbody></table>
	<h3>信息统计汇总</h3>
	<table class="table table-striped table-bordered table-condensed">
		<tr><td>服装信息汇总</td><td colspan="8">S：<%=S %>，M：<%=m %>，L：<%=l %>，XL：<%=xl %>，XXL：<%=xxl %>，XXXL：<%=xxxl %>，其他：<%=other%>，共 <%=total %></td></tr>
		<tr><td>参与欢迎宴会</td><td colspan="8">参加：<%=wp_yes %>，不参加：<%=wp_no %>，不确定：<%=wp_not_sure %></td></tr>
		<tr><td>参与颁奖会</td><td colspan="8">参加：<%=ac_yes %>，不参加：<%=ac_no %>，不确定：<%=ac_not_sure %></td></tr>
	</table>
	<%
}catch(SQLException e){
	e.printStackTrace();
	s.message="Error Occour While Perform Your Request. Technical Detial: " +e.getMessage();
	s.msgType=MSG.error;
	response.sendRedirect(".");
}
%>
</div>
<%@ include file="include/bottom_html_java.jspf"%>

