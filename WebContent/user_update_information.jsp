<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" 
	import="cn.edu.nenu.acm.contestservice.*,cn.edu.nenu.acm.contestservice.modeling.roles.*,cn.edu.nenu.acm.contestservice.modeling.objects.*"%>
<%@ include file="include/head_java.jspf" %>
<%
if (s==null||s.user == null){
	response.sendRedirect(".");
	return;
}
%>
<%@ include file="include/head_html.jspf" %>
<title>密码修改(Password)</title>
<%@ include file="include/body_html_java.jspf" %>
	<div id="main">
		<h1 style="text-align:center">密码修改(Password)</h1>
			<form action="user_update" method="post" class="form-horizontal">
			<div class="control-group">
			<label class="control-label"  for="username">Username:</label>
				<div class="controls"><input id="username" name="username" type="text" maxlength="40" 
				size="20" disabled="disabled" value="<%=Site.htmlEncode(s.user.getUsername())%>" /></div></div>
			<div class="control-group">
			<label class="control-label"   for="oldpassword">Old Password:</label>
				<div class="controls"><input id="oldpassword" name="oldpassword" 	type="password" maxlength="40" size="20" /></div></div>
			<div class="control-group">
			<label class="control-label"   for="password">Password:</label>
				<div class="controls"><input id="password" name="password" 	type="password" maxlength="40" size="20" /></div></div>
			<div class="control-group">
			<label class="control-label"   for="password2">Password Again:</label>
				<div class="controls"><input id="password2" name="password2" 	type="password" maxlength="40" size="20" /> </div></div>
		<div class="form-actions"><input type="submit"  class="btn btn-primary"/></div>
		</form>
	</div>
<%@ include file="include/bottom_html_java.jspf" %>
