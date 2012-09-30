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
<title>导入ICPC数据</title>
<%@ include file="include/body_html_java.jspf"%>
<div id="main">
<h1 style="text-align:center">导入ICPC数据</h1>
<div class="alert alert-info"><a class="close" data-dismiss="alert">×</a><b>请注意：</b><br/>
导入数据的时候，任何格式错误、相关性缺失都是致命的。请严格按照导出截图做，行分割符是TAB，列分隔符是英文分号。特别注意一些教练在地址里面使用了相关的符号。这个是ICPC的一个BUG，没有用转义字符来处理。<a style="color:red;" href="admin_imported.jsp">已导入数据</a>。</div>
<form action="importData" method="post" class="form-horizontal">
	<div class="control-group">
	<label class="control-label" for="teamData">队伍导出信息：<br/><a href="img/teamData.png" target="top"><img alt="导出队伍信息的图" title="导出队伍信息的图，点击看大图" src="img/teamData.png" style="width:120px;height:auto;" /></a></label>
	<div class="controls">
	<textarea rows="10" cols="80" name="teamData" id="teamData" class="span9"></textarea>
	</div>
	</div>
	<div class="alert alert-info">改版之后ICPC不能够一次性导出所有都人物，所以你得分四次导出成为四个文本文件，将这四个文件的内容依次粘贴在这里，但要注意的是：两个文件内容之间插入一个“<code>	</code>”(TAB字符，之间打是不行的，选择然后复制粘贴吧)符号。</div>
	<div class="control-group">
	<label class="control-label" for="personData">人物导出信息：<br/><a href="img/personData.png" target="top"><img alt="导出所有人物信息的图" title="导出所有人物信息的图，点击看大图"  src="img/personData.png" style="width:120px;height:auto;" /></a></label>
	<div class="controls">
	<textarea rows="10" cols="80" name="personData" id="personData" class="span9"></textarea>
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

