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
<title>酒店信息编辑</title>
<%@ include file="include/body_html_java.jspf"%>
<div id="main">
	<h1 id="h_title" style="text-align:center">酒店信息</h1>
<%
try{
	Hotel hotel = new Hotel();
	int id;
	if(request.getParameter("id")!=null){
		id = Integer.parseInt(request.getParameter("id"));
	}else{
		id=-1;
	}
	if (request.getParameter("action")!=null&&request.getParameter("action").equals("post")) {
		if (id > 0)
			hotel.setId(id);
		hotel.setAddress(request.getParameter("address"));
		hotel.setMapURL(request.getParameter("mapurl"));
		hotel.setName(request.getParameter("name"));
		hotel.setTelephone(request.getParameter("telephone"));
		hotel.setDescription(request.getParameter("description"));
		if (id > 0)
			hotel.update();
		else
			hotel.add();
		response.sendRedirect("admin_hotel_room.jsp");
	} else if(request.getParameter("action")!=null&&request.getParameter("action").equals("delete")) {
		try{
		Hotel.delete(id);
		response.sendRedirect("admin_hotel_room.jsp");
		}catch(SQLException e){
			s.message="Error Occour While Deleting A Hotel. You May Not Delete It If There Exist Constrain That It was booked Or Others";// +e.getMessage();
			s.msgType=MSG.error;
			response.sendRedirect("admin_hotel_room.jsp");
			return;
		}
	} else {
		if(id>0)
			hotel.load(id);
		%>
	<form action="admin_hotel_edit.jsp" method="post">
		<input type="hidden" name="id" value="<%=hotel.getId() %>" />
		<input type="hidden" name="action" value="post" />
		<p><label for="name">酒店名： </label><input style="width:420px;" type="text" name="name" id="name" maxlength="45" size="60"  value="<%=Site.htmlEncode(hotel.getName())%>"  /></p>
		<p><label for="address">地址： </label><input style="width:420px;" type="text" name="address" id="address" maxlength="255" size="60"  value="<%=Site.htmlEncode(hotel.getAddress())%>"  /></p>
		<p><label for="telephone">联系电话：</label><input style="width:420px;" type="text" name="telephone" id="telephone" maxlength="45" size="60"  value="<%=Site.htmlEncode(hotel.getTelephone()) %>"  /></p>
		<p><label for="mapurl">地图网址：</label><input style="width:420px;" type="text" name="mapurl" id="mapurl" maxlength="2048" size="60"  value="<%=Site.htmlEncode(hotel.getMapURL()) %>"  /></p>
		<p><label for="description">描述信息：</label>
			<textarea style="width:920px;" name="description" id="description" rows="10" cols="80"  ><%=Site.htmlEncode(hotel.getDescription())%></textarea></p>
		<p><input type="submit" value="Submit" /></p>
	</form>
	<script type="text/javascript">
		var $h_title=$("#h_title");
		$h_title.text($h_title.text()+" - "+"<%=Site.htmlEncode(hotel.getName())%>");
		document.title=$h_title.text();
	</script>
<%
	}
}catch(SQLException e){
	e.printStackTrace();
	s.message="Error Occour While Perform Your Request. Technical Detial: " +e.getMessage();
	s.msgType=MSG.error;
	response.sendRedirect("admin_hotel_room.jsp");
}
%>
</div>
<%@ include file="include/bottom_html_java.jspf"%>

