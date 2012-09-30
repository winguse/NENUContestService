<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="cn.edu.nenu.acm.contestservice.*,cn.edu.nenu.acm.contestservice.modeling.objects.*,
	java.sql.SQLException"%>
<%@ include file="include/head_java.jspf" %>
<%

if (s==null||s.user == null){
	response.sendRedirect(".");
	return;
}
final String myURL="person_edit.jsp";
String action=request.getParameter("action");
if(action==null){
	action="";
}
String actionToken=request.getParameter("actiontoken");
String id_str=request.getParameter("id");
String idToken=request.getParameter("idtoken");
String pageTitle="";
boolean updateable=Site.isUpdateable()||s.user.getPermission()==User.USER_ADMINISTRATOR;
if(s.user.isConfirm()){
	updateable=false;
}
Person p=new Person();
if(action.equals("addteammember")){
	String team=request.getParameter("team");
	if(Site.isEmpty(request.getParameter("reserve"))){
		if(!s.verifyToken(team+myURL, actionToken)&&s.user.getPermission()!=User.USER_ADMINISTRATOR){
			s.message="Illegal add team member request ...";
			s.msgType=MSG.error;
			response.sendRedirect(".");
			return;
		}
		p.setTitle(Person.PERSON_TEAM_MEMBER);
		pageTitle="添加队员";
	}else{
		if(!s.verifyToken(team+myURL+"reserve", actionToken)&&s.user.getPermission()!=User.USER_ADMINISTRATOR){
			s.message="Illegal add team member request ...";
			s.msgType=MSG.error;
			response.sendRedirect(".");
			return;
		}
		p.setTitle(Person.PERSON_TEAM_MEMBER_RESERVE);
		pageTitle="添加替补队员";
	}
	p.setTeam(Integer.parseInt(team));
}else if(s.verifyToken(action+myURL, actionToken)){
	if(action.equals("addcoach")){
		pageTitle="添加教练";
		p.setTitle(Person.PERSON_COACH);
	}
	if(action.equals("addretinue")){
		pageTitle="添加随行人员";
		p.setTitle(Person.PERSON_RETINUE);
	}
}else if(s.verifyToken(id_str+myURL, idToken)||s.user.getPermission()==User.USER_ADMINISTRATOR){
	if(p.load(Integer.parseInt(id_str))==false){
		s.message="The Person You Are Looking Is Not Found...";
		s.msgType=MSG.error;
		response.sendRedirect(".");
		return;
	}
	if(action.equals("delete")){
		try{
			Person.delete(Integer.parseInt(id_str));
			s.message = "Person Deleted.";
			s.msgType=MSG.notice;
		}catch(SQLException e){
			e.printStackTrace();
			s.message = "Person Deleted Fail. Please Check Constrain. You May Not Delete A Coach If he has teams.";
			s.msgType=MSG.error;
		}
		if(s.user.getPermission()==User.USER_ADMINISTRATOR){
				response.sendRedirect(s.lastPage);
		}else
			response.sendRedirect(".");
		return;
	}
}else{
	s.message="Illegal request ...";
	s.msgType=MSG.error;
	response.sendRedirect(".");
	return;
}


final String actionURL="person_update";
if(s.user.getPermission()==User.USER_ADMINISTRATOR){
	if(request.getParameter("leaderid")!=null)
		p.setUserBelongs(Integer.parseInt(request.getParameter("leaderid")));
}
pageTitle+=p.getChineseName();
%>
<%@ include file="include/head_html.jspf" %>
<title>人物信息编辑 - <%=pageTitle %></title>
<%@ include file="include/body_html_java.jspf" %>
		<h1 style="text-align:center"><%=pageTitle %></h1>
		<h3><%=Site.htmlEncode(p.getChineseName()) %> 的个人信息<br/><small>Personal Information Of <%=Site.htmlEncode(p.getEnglishName()) %></small></h3>
		<form action="<%=actionURL %>" method="post" id="person_form" onsubmit="return form_verify();" class="form-horizontal">
		<input type="hidden" name="id" value="<%=p.getId()%>" />
		<input type="hidden" name="team" value="<%=p.getTeam()%>" />
		<%if(s.user.getPermission()!=User.USER_ADMINISTRATOR){ %>
		<%if(updateable){ %>
		<input type="hidden" name="title" value="<%=p.getTitle()%>" />
		<input type="hidden" name="idtoken" value="<%=s.getToken(p.getId()+actionURL)%>" />
		<input type="hidden" name="titletoken" value="<%=s.getToken(p.getTitle()+actionURL)%>" />
		<input type="hidden" name="teamtoken" value="<%=s.getToken(p.getTeam()+actionURL)%>" />
		<%} %>
		<%}else{ %>
		<div id="not_recommanded_edit" style="background-color:#f7f7f7;">
		<div class="control-group">
			<label class="control-label"  for="title">称谓：<br/>(Title) </label>
			<div class="controls">
			<select name="title" id="title">
				<option value="1" >志愿者(Volunteer)</option>
				<option value="2" >随从人员(Retine)</option>
				<option value="4" >教练(Coach)</option>
				<option value="8" >队员(Team member)</option>
				<option value="16" >替补队员(Reserve team member)</option>
			</select>
			</div>
			<script>document.getElementById("title").value=<%=p.getTitle()%>;</script>
		</div>
		<div class="control-group" style="display)none"><label class="control-label"  for="userbelongs">账户所有者：<br/>(User Belongs)</label>
		<div class="controls"><input type="number" size="20" value="<%=p.getUserBelongs()%>"  name="userbelongs" id="userbelongs" /></div></div>
		</div>
		<script type="text/javascript">var $nce=$("#title,#userbelongs");$nce.attr("title","不建议修改，除非你很了解后台数据结构，如要修改，可以双击此区域。");$("#not_recommanded_edit").dblclick(function(){$nce.removeAttr("disabled");});</script>
		<%} %>
		<div class="control-group"><label class="control-label"  for="chinesename">中文名：<br/>(Chinese Name)</label>
		<div class="controls"><input type="text" id="chinesename" name="chinesename" maxlength="45" size="20"  value="<%=Site.htmlEncode(p.getChineseName())%>"  /></div></div>
		<div class="control-group"><label class="control-label"  for="englishname">英文名：<br>(English Name)</label>
		<div class="controls"><input type="text" id="englishname" name="englishname" maxlength="45" size="20" value="<%=Site.htmlEncode(p.getEnglishName())%>" /></div></div>
<%-- 		<div class="control-group"><label class="control-label"  for="idnumber">Identification numbers) </label><input type="text" id="idnumber" name="idnumber" maxlength="45" size="20"  value="<%=Site.htmlEncode(p.getIdNumber())%>"  /></div>--%>
		<div class="control-group"><label class="control-label"  for="gender">性别：<br/>(Gender)</label>
		<div class="controls">
			<select name="gender" id="gender">
				<option value="true" >男(Male)</option>
				<option value="false" >女(Female)</option>
			</select>
			</div>
			<script>document.getElementById("gender").value=<%=p.getGender()%>;</script>
		</div>
		<div class="control-group"><label class="control-label"  for="mobile">手机：<br/>(Mobile)</label>
		<div class="controls"><input type="text" id="mobile" name="mobile" maxlength="45" size="20"  value="<%=Site.htmlEncode(p.getMobile())%>"  /></div>
		</div>
		<%if(p.getTitle()!=Person.PERSON_VOLUNTEER){ %>
		<div<%if(p.getTitle()!=Person.PERSON_COACH&&p.getTitle()!=Person.PERSON_RETINUE){ %> style="display:none"<%} %> class="control-group"><label class="control-label"  for="welcomeparty">参与宴会：<br/>(Enter Welcome Party)</label>
		<div class="controls">
			<select name="welcomeparty" id="welcomeparty" >
				<option value="1" >Yes</option>
				<option value="0" >No</option>
				<option value="2" >Not Sure</option>
			</select>
			</div>
			<script>document.getElementById("welcomeparty").value=<%=p.enterWelcomeParty()%>;</script>
		</div>
		<div class="control-group"><label class="control-label"  for="awardceremony">参与颁奖礼：<br/>(Enter Award Ceremony) </label>
		<div class="controls">
			<select name="awardceremony" id="awardceremony">
				<option value="1" >Yes</option>
				<option value="0" >No</option>
				<option value="2" >Not Sure</option>
			</select>
			</div>
			<script>document.getElementById("awardceremony").value=<%=p.enterAwardsCeremony()%>;</script>
		</div>
		<%} %>
		<div class="control-group"><label class="control-label"  for="major">专业(大学入学年份)：<br/>Major (Year Enter College) </label>
		<div class="controls">
		<input type="text" id="major" name="major" maxlength="255" size="20"  value="<%=Site.htmlEncode(p.getMajor())%>" />
		<span><%if(p.getTitle()==Person.PERSON_VOLUNTEER||p.getTitle()==Person.PERSON_VOLUNTEER_PEDDING){ %>志愿者请填写年级，<%} %>例如：计算机(2009)。Example: Computer Science (2009).</span>
		</div>
		</div>
		<div class="control-group"><label class="control-label"  for="email">电子邮箱：<br/>(Email) </label>
		<div class="controls"><input type="text" id="email" name="email" maxlength="255" size="20"  value="<%=Site.htmlEncode(p.getEmail())%>" /></div>
		</div>
		<div class="control-group"><label class="control-label"  for="clothes">服装大小：<br/>(Clothes Size) </label>
		<div class="controls">
			<select name="clothes" id="clothes" >
<%-- 				<option value="S" >S</option>
				<option value="M" >M</option>--%>
				<option value="L" >L</option>
				<option value="XL" >XL</option>
				<option value="XXL" >XXL</option>
				<option value="XXXL" >XXXL</option>
			</select>
			</div>
			<script>document.getElementById("clothes").value="<%=Site.htmlEncode(p.getClothes())%>";</script>
		</div>
		<%-- <div class="control-group"><label class="control-label"  for="photo">Photo) </label><img id="photo" src="<%=Site.htmlEncode(p.getPhoto())%>" onclick="" /><input type="hidden" name="photo" value=""/></div> --%>
		<div class="control-group">
			<label class="control-label"  for="description">更多描述补充：<br/>(More  Description) </label>
			<div class="controls">
			<textarea rows="5" cols="20" id="description" name="description"><%=Site.htmlEncode(p.getDescription()) %></textarea>
			<div><%if(p.getTitle()==Person.PERSON_VOLUNTEER){ %>
			你可以描述一下你自己，这部分对你的协作志愿者以及服务对象的带队老师可见。（请注意修改！）
			<%}else if(p.getTitle()==Person.PERSON_VOLUNTEER_PEDDING){ %>
			此处请注明你的身高（方便统计比赛服装大小，比较胖的同学请注明一下，免得发下去的衣服穿不下），是否当礼仪。另外如果想当技术志愿者，请特别注明。
			<%}else{ %>
			如果是少数民族，或者有饮食上的特别需要，或者有其他补充说明的地方，请务必注明，谢谢！<br/>
			If you have any special need of dietary habits or anything else, please tell us. 
			<%} %></div>
			</div>
		</div>
		<div class="form-actions"><input type="submit"  class="btn btn-primary"/></div>
		</form>
<%if(!updateable){ %>
	<script>
		$("input").attr("disabled","disabled");
		$("select").attr("disabled","disabled");
		$("textarea").attr("disabled","disabled");
	</script>
	<%} %>
	<script type="text/javascript">
<!--
function form_verify(){
	var noEmptyId=new Array(
			"chinesename",
			"englishname"
			//,"idnumber"
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