<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="cn.edu.nenu.acm.contestservice.*,cn.edu.nenu.acm.contestservice.modeling.objects.*,
	java.sql.SQLException,java.util.List,java.util.Date,java.text.SimpleDateFormat"%> 
<%@ include file="include/head_java.jspf"%>
<%

if (s==null||s.user == null){
	response.sendRedirect(".");
	return;
}
	//if (s.user.getPermission() == User.USER_ADMINISTRATOR)
		//response.sendRedirect(".");
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
%>
<%@ include file="include/head_html.jspf"%>
<title>Message Board</title>
<%@ include file="include/body_html_java.jspf"%>
<%
int id=-1;
try{
	id=Integer.parseInt(request.getParameter("id"));
}catch(Exception e){
	
}
try{
	Message message=new Message();
	if(id<1||message.load(id)==false){
		s.message="You are looking for a post that does not exist.";
		s.msgType=MSG.warning;
		response.sendRedirect(s.lastPage);
		return;
	}
	if(message.getType()!=Message.MESSAGE_TYPE_NORMAL&&message.getType()!=Message.MESSAGE_TYPE_ROOM_EXCHANGE){
		s.message="You are looking for a post that was not public content.";
		s.msgType=MSG.warning;
		response.sendRedirect(".");
		return;
	}
	%>
	<h3><%=Site.htmlEncode(message.getTitle()) %><br/><small>
<span class="user"><%=Site.htmlEncode(message.getUsername()) %></span>
<i><%=df.format((new Date(message.getTime()))) %></i></small></h3>
	<div class="message well">
	<%=Site.htmlEncode(message.getContent()) %>
	</div>
	<%
	List<Message> msglst=Message.getSpecificMessage(Message.MESSAGE_SPEC_GET_REPLY, id);
	s.lastPage=request.getRequestURI()+"?"+request.getQueryString();
	for(Message msg:msglst){%>
<div class="message_reply message thumbnail">
	<p><b><%=Site.htmlEncode(msg.getTitle()) %></b> <span class="user"><%=Site.htmlEncode(msg.getUsername()) %></span> <i><%=df.format((new Date(msg.getTime()))) %></i></p>
	<p><%=Site.htmlEncode(msg.getContent()) %></p>
</div>
<%}%>
	<h3>回复<br/><small>Reply This Post</small></h3>
	<div id="message_poster" class="well">
		<form method="post" action="message_update" class="form-horizontal">
			<input type="hidden" name="replyid" value="<%=id %>" />
			<div class="control-group">
			<label class="control-label"   for="title">标题(Title)：</label>
				<div class="controls"><input type="text" maxlength="255" size="50" id="title" name="title" />
			</div></div>
			<div class="control-group">
			<label class="control-label"  for="content">内容(Content)：</label>
			<div class="controls"><textarea name="content" id="content" rows="8" class="80"></textarea>
			</div></div>
		<div class="form-actions"><input type="submit"  class="btn btn-primary"/></div>
		</form>
	</div>
<%
}catch(SQLException e){
	e.printStackTrace();
	s.message="Error Occour While Perform Your Request. Technical Detial: " +e.getMessage();
	s.msgType=MSG.error;
	response.sendRedirect(".");
}
%>
<%@ include file="include/bottom_html_java.jspf"%>

