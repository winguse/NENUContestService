<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="cn.edu.nenu.acm.contestservice.*,cn.edu.nenu.acm.contestservice.modeling.objects.*,
	java.sql.SQLException"%>
<%@ include file="include/head_java.jspf" %>
<%
if (s==null||s.user == null){
	response.sendRedirect(".");
	return;
}
final String myURL="team_edit.jsp";
String id_str=request.getParameter("id");
String coach_str=request.getParameter("coach");
String school_str=request.getParameter("school");
String token=request.getParameter("token");
String idtoken=request.getParameter("idtoken");
boolean updateable=Site.isUpdateable()||s.user.getPermission()==User.USER_ADMINISTRATOR;
if(s.user.isConfirm()){
	updateable=false;
}
Team t=new Team();
if(s.verifyToken(id_str+myURL, idtoken)||s.verifyToken(id_str+coach_str+myURL, token)||s.user.getPermission()==User.USER_ADMINISTRATOR){
	if(id_str.equals("-1")){
		t.setCoach(Integer.parseInt(coach_str));
		if(school_str!=null)
			t.setSchool(Integer.parseInt(school_str));
		else
			t.setSchool(s.user.getSchool());
	}else if(t.load(Integer.parseInt(id_str))==false){
		s.message="The Person You Are Looking Is Not Found...";
		s.msgType=MSG.error;
		response.sendRedirect(".");
		return;
	}
	if(request.getParameter("action")!=null){
		if(request.getParameter("action").equals("delete")){
			try{
				Team.delete(Integer.parseInt(id_str));
				s.message = "Team Deleted.";
				s.msgType=MSG.notice;
			}catch(SQLException e){
				e.printStackTrace();
				s.message = "Team Deleted Fail. Please Check Constrain. You May Not Delete A Team If he has Team Member.";
				s.msgType=MSG.error;
			}
			response.sendRedirect(s.lastPage);
			return;
		}
	}
}else{
	s.message="Illegal request ...";
	s.msgType=MSG.error;
	response.sendRedirect(".");
	return;
}
final String actionURL="team_update";
%>
<%@ include file="include/head_html.jspf" %>
<title>编辑队伍信息(Edit Team Information)</title>
<%@ include file="include/body_html_java.jspf" %>
	<h3>编辑队伍信息<br/><small>(Edit Team Information)</small></h3>
		<form action="<%=actionURL %>" method="post" id="person_form" onsubmit="return form_verify()" class="form-horizontal">
		<input type="hidden" name="id" value="<%=t.getId()%>" />
		<%if(s.user.getPermission()==User.USER_ADMINISTRATOR){ %>
		<div id="team_more" style="display:none">
		<div class="control-group">
			<label class="control-label"   for="coach">Coach ID: </label>
			<div class="controls"><input type="text" id="coach" name="coach" maxlength="255" size="20"  value="<%=t.getCoach()%>"  />
		</div></div>
		<div class="control-group">
			<label class="control-label"  for="hotel">酒店：<br/>(Hotel)</label>
			<div class="controls"><select id="hotel" name="hotel" >
				<option value="0">暂无信息(No Information)</option>
				<%for(Hotel h:Hotel.getAllHotels()){ %>
				<option value="<%=h.getId()%>"><%=Site.htmlEncode(h.getName()) %></option>
				<%} %>
			</select>
			<script>document.getElementById("hotel").value=<%=t.getHotel()%>;</script>
		</div></div>
		<div class="control-group">
			<label class="control-label"  for="school">学校：<br/>(School)</label>
			<div class="controls"><select name="school" id="school">
				<option value="0">Please Select A School</option>
				<%for(School sc:School.getAllSchools()) {%>
				<option value="<%=sc.getId()%>"><%=Site.htmlEncode(sc.getChineseName()) %>(<%=Site.htmlEncode(sc.getEnglishName()) %>)</option>
				<%} %>
			</select>
			<script>document.getElementById("school").value=<%=t.getSchool()%>;</script>
		</div></div>
		<div class="control-group">
			<label class="control-label"   for="seat">座位：<br/>(Seat)</label>
			<div class="controls"><select name="seat" id="seat">
			</select>
			<script>document.getElementById("seat").value='<%=Site.htmlEncode(t.getSeat())%>';</script>
		</div></div>
		<div class="control-group">
			<label class="control-label"   for="arrivaled">已经达到：<br/>(Arrival)</label>
			<div class="controls"><select name="arrivaled" id="arrivaled">
				<option value="yes">Yes</option>
				<option value="no">No</option>
			</select>
			<script>document.getElementById("arrivaled").value='<%=t.isArrivaled()?"yes":"no"%>';</script>
		</div></div>
		<div class="control-group">
			<label class="control-label"   for="leaved">已经离开：<br/>(Leaved)</label>
			<div class="controls"><select name="leaved" id="leaved">
				<option value="yes">Yes</option>
				<option value="no">No</option>
			</select>
			<script>document.getElementById("leaved").value='<%=t.isLeaved()?"yes":"no"%>';</script>
		</div></div>
		</div>
		<p><input type="button" value="显示隐藏其他信息" onclick="$('#team_more').toggle()"/></p>
		<div class="control-group">
			<label class="control-label"   for="type">类型：<br/>(Type) </label>
			<div class="controls"><select name="type" id="type">
				<option value="0" >等待审核(Waiting For Verification)</option>
				<option value="1" >正式队(Offical)</option>
				<option value="2" >酱油队(Observe/Tourism)</option>
				<option value="-1" >待审正式队(Offical [Under Verifying])</option>
				<option value="-2" >待审酱油队(Observe/Tourism [Under Verifying])</option>
				<option value="4" >正式队（已审）</option>
				<option value="-4" >正式队（待再审）</option>
			</select>
			<script>document.getElementById("type").value=<%=t.getType()%>;</script>
		</div></div>
		<%}else{ %>
		<%if(updateable){ %>
		<input type="hidden" name="coach" value="<%=t.getCoach()%>" />
		<input type="hidden" name="type" value="<%=t.getType()%>" />
		<input type="hidden" name="token" value="<%=s.getToken(""+t.getId()+t.getCoach()+t.getType()+actionURL)%>" />
		<%} %>
		<%} %>
		<div class="control-group">
			<label class="control-label"  for="chinesename">中文名：<br/>(Chinese Name) </label>
			<div class="controls"><input type="text" id="chinesename" name="chinesename" maxlength="255" size="20"  value="<%=Site.htmlEncode(t.getChineseName())%>"  /></div></div>
		<div class="control-group">
			<label class="control-label"  for="englishname">英文名(ICPC注册队名)：<br/>(English Name,ICPC Registration Name)</label>
			<div class="controls"><input <%if(s.user.getPermission()!=User.USER_ADMINISTRATOR&&t.getType()!=Team.TEAM_TOURISM&&t.getType()!=Team.TEAM_TOURISM_VERIFIED){ %>disabled="disabled" title="官方注册队伍名不可修改，详细请联系我们。The ICPC Registration Team Name is not changeable, contract us for details."<%} %> type="text" id="englishname" name="englishname" maxlength="255" size="20" value="<%=Site.htmlEncode(t.getEnglishName())%>" /></div></div>
		<%if(s.user.getPermission()==User.USER_ADMINISTRATOR){ %>
		<div class="control-group" >
			<label class="control-label"  for="statusdescription">更多描述：<br/>(More Description)</label>
			<div class="controls"><textarea rows="5" cols="20" id="statusdescription" name="statusdescription"><%=Site.htmlEncode(t.getStatusDescription()) %></textarea><p>“|”分隔前面是正常描述，后面是来自ICPC的备注。</p></div>
		</div>
		<%} %>
		<div class="form-actions"><input type="submit"  class="btn btn-primary"/></div>
		</form>
		<%if(!updateable){ %>
	<script>
		$("input").attr("disabled","disabled");
		$("select").attr("disabled","disabled");
		$("textarea").attr("disabled","disabled");
	</script>
	<%} %><script type="text/javascript">
<!--
function form_verify(){
	var noEmptyId=new Array(
			"chinesename",
			"englishname"
	);
	for(var i=0;i<noEmptyId.length;i++){
		if($$(noEmptyId[i]).value==""){
			alert("You have an empty form to be fill.");
			$$(noEmptyId[i]).focus();
			return false;
		}
	}
	return true;
		
				
}
//-->
</script>
<%@ include file="include/bottom_html_java.jspf" %>