<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="cn.edu.nenu.acm.contestservice.*,cn.edu.nenu.acm.contestservice.modeling.objects.*,
	java.sql.SQLException"%> 
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
<title>更新ICPC数据队伍信息</title>
<%@ include file="include/body_html_java.jspf"%>
<div id="main">
<h1 style="text-align:center">更新ICPC数据队伍信息</h1>
<div class="alert alert-info">总有这样的情况，ICPC上面都队伍信息修改了，你要更新这个系统上面的。这里就是这个功能。
把队伍信息粘贴在这里，可以更新队伍信息都备注。仅仅是备注，这样你可以在参赛队管理里面通过备注识别那些队伍信息和ICPC的是否一致。
请注意：只会更新那些英文名和ICPC保持一致的队伍备注，没有找到的，会列举出来，注意查证就好了。</div>
<form action="ImportData_FixICPCINFO" method="post" class="form-horizontal">
	<div class="control-group">
	<label class="control-label" for="teamData">队伍导出信息：<br/><a href="img/teamData.png" target="top"><img alt="导出队伍信息的图" title="导出队伍信息的图，点击看大图" src="img/teamData.png" style="width:120px;height:auto;" /></a></label>
	<div class="controls">
	<textarea rows="10" cols="80" name="teamData" id="teamData" class="span9"></textarea>
	</div>
	</div>
	<div class="form-actions"><input type="submit" class="btn btn-primary" /></div>
</form>
<%
//try{
%><%
/*
}catch(SQLException e){
	e.printStackTrace();
	s.message="Error Occour While Perform Your Request. Technical Detial: " +e.getMessage();
	s.msgType=MSG.error;
	response.sendRedirect("admin_hotel_room.jsp");
}*/
%>
</div>
<%@ include file="include/bottom_html_java.jspf"%>

