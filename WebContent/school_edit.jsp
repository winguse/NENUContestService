<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="cn.edu.nenu.acm.contestservice.*,cn.edu.nenu.acm.contestservice.modeling.objects.*"%>
<%@ include file="include/head_java.jspf" %>
<%
if (s==null||s.user == null){
	response.sendRedirect(".");
	return;
}
final String myURL="school_edit.jsp";
String id_str=request.getParameter("id");
String idtoken=request.getParameter("idtoken");
School school=new School();
if(s.verifyToken(id_str+myURL, idtoken)||s.user.getPermission()==User.USER_ADMINISTRATOR){
	if(school.load(Integer.parseInt(id_str))==false){
		s.message="The School You Are Looking Is Not Found...";
		s.msgType=MSG.error;
		response.sendRedirect(".");
		return;
	}
}else{
	s.message="Illegal request ...";
	s.msgType=MSG.error;
	response.sendRedirect(".");
	return;
}
final String actionURL="school_update";
%>
<%@ include file="include/head_html.jspf" %>
<title>编辑学校信息 (Edit School Information)</title>
<%@ include file="include/body_html_java.jspf" %>
	<div id="main"  class="main_mid">
		<h3>编辑学校信息<br/><small>(Edit School Information)</small></h3>
		<form action="<%=actionURL %>" method="post">
			<input name="id" type="hidden" value="<%=school.getId() %>" />
			<input name="idtoken" type="hidden" value="<%=s.getToken(school.getId()+actionURL) %>" />
			<p><label for="englishname">English Name:</label><input type="text" name="englishname" id="englishname" maxlength="255" size="20"  value="<%=Site.htmlEncode(school.getEnglishName())%>"  /></p>
			<p><label for="chinesename">Chinese Name: </label><input type="text" name="chinesename" id="chinesename" maxlength="255" size="20"  value="<%=Site.htmlEncode(school.getChineseName())%>"  /></p>
			<p><label for="postaddress">Post Address: </label><input type="text" name="postaddress" id="postaddress" maxlength="255" size="20"  value="<%=Site.htmlEncode(school.getPostAddress())%>"  /></p>
			<p><label for="postcode">Post Code: </label><input type="text" name="postcode" id="postcode" maxlength="255" size="20"  value="<%=Site.htmlEncode(school.getPostCode())%>"  /></p>
			<p><input type="submit" value="Submit" /></p>
		</form>
	</div>
<%@ include file="include/bottom_html_java.jspf" %>