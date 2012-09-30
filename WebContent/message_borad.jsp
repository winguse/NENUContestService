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
	String delete=request.getParameter("delete");
	if(!Site.isEmpty(delete)){
		Message dm=new Message();
		int id=-1;
		try{
			id=Integer.parseInt(delete);
		}catch(Exception e){
			
		}
		if(dm.load(id)){
			if(s.user.getPermission()!=User.USER_ADMINISTRATOR&&s.user.getId()!=dm.getUser()){
				s.message="Permission Deny, delete fail!";
				s.msgType=MSG.error;
			}else{
				Message.delete(id);
				s.message="Post was deleted.";
				s.msgType=MSG.notice;
			}
		}else{
			s.message="Post does not exist!";
			s.msgType=MSG.error;
		}
		response.sendRedirect(s.lastPage);
		return;
	}
	
	int type=Message.MESSAGE_TYPE_NORMAL;
	try{
		type=Integer.parseInt(request.getParameter("type"));
	}catch(Exception e){
		
	}
	String pageTitle;
	if(type==0){
		pageTitle="综合提问(Q&A)";
	}else{
		pageTitle="拼房信息(Sharing Room)";
	}
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
%>
<%@ include file="include/head_html.jspf"%>
<title>留言板 (Message Board) - <%=pageTitle %></title>
<%@ include file="include/body_html_java.jspf"%>
<h1 style="text-align:center"><%=pageTitle %></h1>
<h3>已有帖子<br/><small>Existing Posts</small></h3>
<div class="well">
<%
s.lastPage=request.getRequestURI()+"?"+request.getQueryString();
try{
	List<Message> msglst=Message.getSpecificMessage(Message.MESSAGE_SPEC_OF_TYPE, type);
	for(Message msg:msglst){
		List<Message> replymsglst=Message.getSpecificMessage(Message.MESSAGE_SPEC_GET_REPLY, msg.getId());
	%>
<div class="message thumbnail">
	<a href="message_content.jsp?id=<%=msg.getId()%>"><b><%=Site.htmlEncode(msg.getTitle()) %></b></a> (回复：<%=replymsglst.size() %><%if(replymsglst.size()>0){ %>，最后时间：<%=df.format(new Date(replymsglst.get(replymsglst.size()-1).getTime())) %>，by：<%=Site.htmlEncode(replymsglst.get(replymsglst.size()-1).getUsername())%><%} %>)
	<span class="pull-right"><span class="user"><%=Site.htmlEncode(msg.getUsername()) %></span> <i><%=df.format((new Date(msg.getTime()))) %></i><%
	if(s.user.getPermission()==User.USER_ADMINISTRATOR||s.user.getId()==msg.getUser()){
%><a href="?delete=<%=msg.getId()%>" class="icon-trash" title="删除"></a><%}%></span>
</div>
<%}%>
<%if(msglst.size()==0){ %>
<div class="message thumbnail"><b>暂无主题，您可以抢沙发了！(There is no existing post, be first to post!)</b></div>
<%}%>
</div>
	<h3>新帖子<br/><small>New Post</small></h3>
	<div id="message_poster" class="well">
		<form method="post" action="message_update" class="form-horizontal">
			<input type="hidden" name="type" value="<%=type %>" />
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

