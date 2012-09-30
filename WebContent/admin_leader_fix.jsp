<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="java.util.Map.Entry,java.util.Iterator,java.util.HashMap,java.util.ArrayList,java.util.Date,cn.edu.nenu.acm.contestservice.*,cn.edu.nenu.acm.contestservice.modeling.objects.*,java.sql.SQLException,java.sql.Connection,java.sql.PreparedStatement,java.sql.ResultSet,java.util.List"%>
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
<title>领队通知模块</title>
<%@ include file="include/body_html_java.jspf"%>
<div id="main">
	<h1 style="text-align:center">领队通知模块</h1>
	<div class="alert">
	请注意：这个模块是写得很糟糕的，因为长春站用的时候，就发生了意外，例如邮箱发信配额之类的，很容易造成重复发送的问题。
	如果你要使用，先检查我的代码，或者，让我给你发——但都难以保证不会发生意外。
	使用前，请保证导入的数据里面，所有学校的中文校名都已经填好，一个学校不能出现两次哦。
	建议你用刚刚导入完那个网页的连接发送邮件。
	</div>
	<form action="NoticeLeader" method="post">
		<input type="hidden" name="action" value="send" />
		<p>
		<label for="password">邮箱密码，邮箱配置需要改代码，前端不提供接口：</label>
		<input type="password" name="password" id="password" />
		</p>
		<p>
		<label for="test">测试模式(不会直接发信，仅仅测试一下现在的数据能不能跑起来)：</label>
		<input type="checkbox" name="test" id="test" checked="checked" />
		</p>
		<p>
		<label for="extracoach">额外要模糊查找通知的用户：<br/>如果教练有多个邮箱，然后你又之前整理了他的邮箱，想都给他发，这里可以填。这个是个很奇怪的需求——在长春站发生了。<br/>格式：教练名[TAB]邮箱地址</label>
		<textarea id="extracoach" name="extracoach" rows="20" cols="80" class="span10"></textarea>
		</p>
		<p>
		<label for="excludeemail">排除的邮箱(例如你之前发送失败了，现在要重发，一定要记得整理发送的那些日志，把已经发的粘贴在这里，一行一个)：</label>
		<textarea id="excludeemail" name="excludeemail" rows="20" cols="80" class="span10"></textarea>
		</p>
		<input type="submit"/>
	</form>
	<%

	%>
</div>
<%@ include file="include/bottom_html_java.jspf"%>

