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
<title>房间信息管理</title>
<%@ include file="include/body_html_java.jspf"%>
<%
try{
Room room = new Room();
int id;
if(request.getParameter("id")!=null){
	id = Integer.parseInt(request.getParameter("id"));
}else{
	id=-1;
}
if (request.getParameter("action")!=null&&request.getParameter("action").equals("post")) {
	if (id > 0)
		room.setId(id);
	room.setBooked(Integer.parseInt(request.getParameter("booked")));
	room.setBreakfast(request.getParameter("breakfast").equals("yes"));
	System.out.println(request.getParameter("breakfast").equals("yes"));
	System.out.println(request.getParameter("breakfast"));
	room.setInternet(request.getParameter("internet").equals("yes"));
	room.setHotel(Integer.parseInt(request.getParameter("hotel")));
	room.setPrice(Integer.parseInt(request.getParameter("price")));
	room.setTotal(Integer.parseInt(request.getParameter("total")));
	room.setTypeName(request.getParameter("typename"));
	room.setDescription(request.getParameter("description"));
	room.setVolume(Integer.parseInt(request.getParameter("volume")));
	if (id > 0)
		room.update();
	else
		room.add();
	response.sendRedirect("admin_hotel_room.jsp");
} else if(request.getParameter("action")!=null&&request.getParameter("action").equals("delete")) {
	Room.delete(id);
	response.sendRedirect("admin_hotel_room.jsp");
}else{
	if(id>0)
	room.load(id);
	else
		room.setHotel(Integer.parseInt(request.getParameter("hotel")));
%>
	<h1 id="h_title" style="text-align:center">房间信息管理</h1>
	<form action="admin_room_edit.jsp" method="post">
		<input type="hidden" name="id" value="<%=room.getId() %>" />
		<input type="hidden" name="action" value="post" />
		<p>
			<label for="hotel">酒店：</label>
			<select name="hotel" id="hotel">
				<option value="0">请选择酒店</option>
				<%for(Hotel h:Hotel.getAllHotels()){ %>
				<option value="<%=h.getId()%>"><%=h.getName() %></option>
				<%} %>
			</select>
			<script type="text/javascript">document.getElementById("hotel").value=<%=room.getHotel()%>;</script>
		</p>
		<p><label for="typename">类型名：</label><input type="text" name="typename" id="typename" maxlength="45" size="20"  value="<%=Site.htmlEncode(room.getTypeName())%>"  /></p>
		<p>
			<label for="breakfast">提供早餐：</label>
			<select name="breakfast" id="breakfast">
				<option value="yes">Yes</option>
				<option value="no">No</option>
			</select>
			<script type="text/javascript">document.getElementById("breakfast").value=<%=room.hasBreakfast()?"'yes'":"'no'"%>;</script>
		</p>
		<p>
			<label for="internet">互联网接入：</label>
			<select name="internet" id="internet">
				<option value="yes">Yes</option>
				<option value="no">No</option>
			</select>
			<script type="text/javascript">document.getElementById("internet").value=<%=room.hasInternet()?"'yes'":"'no'"%>;</script>
		</p>
		<p style=""><label for="volume">单间容量/人：</label><input type="number" name="volume" id="volume" size="20"  value="<%=room.getVolume()%>"  /></p>
		<p><label for="price">单价：</label><input type="number" name="price" id="price" size="20"  value="<%=room.getPrice()%>"  /></p>
		<p><label for="booked">已订数量：</label><input type="number" name="booked" id="booked" size="20"  value="<%=room.getBooked()%>"  /></p>
		<p><label for="total">总数量：</label><input type="number" name="total" id="total" size="20"  value="<%=room.getTotal()%>"  /></p>
		<p><label for="description">描述：</label>
			<textarea style="width:920px;" name="description" id="description" rows="10" cols="80"  ><%=Site.htmlEncode(room.getDescription())%></textarea></p>
		<p><input type="submit" value="提交" /></p>
	</form>
		<script type="text/javascript">
		var $h_title=$("#h_title");
		var v=$("#hotel").attr("value");
		$h_title.text($h_title.text()+" - "+$("#hotel>option[value="+v+"]").text()+" - <%=Site.htmlEncode(room.getTypeName())%>");
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
<%@ include file="include/bottom_html_java.jspf"%>

