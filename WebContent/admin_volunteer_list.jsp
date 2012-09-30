<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="java.util.Date,cn.edu.nenu.acm.contestservice.*,cn.edu.nenu.acm.contestservice.modeling.objects.*,java.sql.SQLException,java.sql.Connection,java.sql.PreparedStatement,java.sql.ResultSet"%>
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
s.lastPage = request.getRequestURI() + "?"
				+ request.getQueryString();
		String schools = "", volunteer_school = "", volunteer = "";
		long otl = new Date().getTime();
%>
<%@ include file="include/head_html.jspf"%>
<title>志愿者分配</title>
<style>
#schools_container {
	display: none;
	position: absolute;
	top: 0px;
	left: 0px;
	width: 400px;
	height: 28px;
	padding: 3px 5px;
	z-index: 99;
	background-color: #fff;
}

.icon-trash {
	cursor: pointer;
}
</style>
<%@ include file="include/body_html_java.jspf"%>
	<h1 style="text-align:center">志愿者分配</h1>
<%
	try {
		//TODO ** 这里面学校志愿者的管理，应该是School Left Joint.
			boolean f0 = true;
			schools += "{0:[]";
			for (School sc : School.getAllSchools()) {
				schools += "," + sc.getId() + ":[\""
						+ Site.htmlEncode(sc.getChineseName())
						+ "\",\""
						+ Site.htmlEncode(sc.getEnglishName())
						+ "\",0," + sc.getTeamCount() + "]";
			}
			schools += "}";
			Connection conn = Site.getDataBaseConnection();
			PreparedStatement pstat = conn
					.prepareStatement(
							"SELECT User_id,School_id FROM School_Volunteer ORDER by User_id ASC",
							ResultSet.TYPE_FORWARD_ONLY,
							ResultSet.CONCUR_UPDATABLE,
							ResultSet.CLOSE_CURSORS_AT_COMMIT);
			pstat.execute();
			ResultSet rs = pstat.getResultSet();
			volunteer_school += "{0:[";
			int l = 0;
			while (rs.next()) {
				if (l == rs.getInt("User_id")) {
					volunteer_school += "," + rs.getInt("School_id");
				} else {
					f0 = true;
					volunteer_school += "]," + rs.getInt("User_id")
							+ ":[" + rs.getInt("School_id");
					l = rs.getInt("User_id");
				}
			}
			volunteer_school += "]}";
			rs.close();
			pstat.close();
			pstat = conn
					.prepareStatement(
							"SELECT User.id id,Username,Password,Salt,Permission,Person.id pid,ChineseName,EnglishName,School,Major,Gender,Mobile,Email,Description FROM User,Person WHERE Permission=? and Person.UserBelongs=User.id",
							ResultSet.TYPE_FORWARD_ONLY,
							ResultSet.CONCUR_UPDATABLE,
							ResultSet.CLOSE_CURSORS_AT_COMMIT);
			pstat.setInt(1, User.USER_VOLUNTEER);
			pstat.execute();
			rs = pstat.getResultSet();
%>
<table class="table table-striped table-bordered table-condensed" id="volunteer_table">
	<thead>
		<tr>
			<th>ID</th>
			<th>用户名</th>
			<th>姓名</th>
			<th>电话</th>
			<th>邮件</th>
			<th>性别</th>
			<th>专业</th>
			<th>服务学校</th>
			<th>自我描述</th>
			<%--
			<th>密码</th>
			<th>Salt</th>
			<th>权限值</th>--%>
			<th>管理选项</th>
		</tr>
	</thead>
	<tbody>
		<%
			volunteer += "[0";
			int cnt=0;
					while (rs.next()) {
						if(rs.getInt("id")==87&&!"x".equals(request.getParameter("x")))continue;//hide舒啸
						cnt++;
						volunteer += "," + rs.getInt("id");
		%>
		<tr>
			<td><%=rs.getInt("id")%></td>
			<td><a
				href="admin_user_edit.jsp?username=<%=Site.htmlEncode(rs.getString("Username"))%>"><%=Site.htmlEncode(rs.getString("Username"))%></a></td>
			<td><%=Site.htmlEncode(rs.getString("ChineseName"))%><br />(<%=Site.htmlEncode(rs.getString("EnglishName"))%>)</td>
			<td><%=Site.htmlEncode(rs.getString("Mobile"))%></td>
			<td><%=Site.htmlEncode(rs.getString("Email"))%></td>
			<td><%=rs.getBoolean("Gender") ? "男" : "女"%></td>
			<td><%=Site.htmlEncode(rs.getString("Major"))%></td>
			<td id="v_ctrl_<%=rs.getInt("id")%>"
				onmouseover="v_show(<%=rs.getInt("id")%>)"></td>
			<td><%=Site.htmlEncode(rs.getString("Description"))%></td>
			<%--
			<td><%=rs.getString("Password")%></td>
			<td><%=rs.getString("Salt")%></td>
			<td><%=rs.getInt("Permission")%></td>--%>
			<td><a
				href="admin_user_edit.jsp?username=<%=Site.htmlEncode(rs.getString("Username"))%>">编辑用户</a>
				<a href="person_edit.jsp?id=<%=rs.getInt("pid")%>">编辑信息</a> <a
				href="login?action=otl&username=<%=Site.htmlEncode(rs.getString("Username"))%>&password=<%=Site.hash(otl + rs.getString("Username"),
								"otl")%>&otl=<%=otl%>">角色扮演</a>
				<%--	<a href="admin_volunteer_main.jsp?id=<%=rs.getInt("id")%>">角色扮演</a>--%>
			</td>
		</tr>
		<%
			}
					volunteer += "]";
		%>
	</tbody>
</table>
	<p>共计：<%=cnt %></p>
<form class="form-horizontal">
	<div id="schools_container" class="control-group thumbnail">
		<label class="control-label" for="schools_seleter">添加服务学校：</label>
		<div class="controls">
			<select rel="tooltip" data-original-title="学校 ( 队伍数量 / 志愿者分配数量 )"
				id="schools_seleter" onchange="select_school(this.value)"></select><a
				href="#" class="close" onclick="$('#schools_container').fadeOut(500);return false;">×</a>
		</div>
	</div>
</form>
<p>
	<a href="admin_user_edit.jsp?action=addvolunteer">添加一个志愿者</a>
</p>
<div>
	<script type="text/javascript">
	var schools=<%=schools%>,volunteer_school=<%=volunteer_school%>,volunteer=<%=volunteer%>;
	var current_volunteer=0;
	var sid=1;
	var tmp='';
	for(var sid in schools){
		if(sid==0)continue;
		tmp+='<option id="scp_'+sid+'" value="'+sid+'" title="'+schools[sid][1]+'">'+schools[sid][0]+'('+schools[sid][3]+'/'+schools[sid][2]+')'+'</option>';
	}
	$("#schools_seleter").html(tmp);
	sid=1;
	$("#schools_seleter").html($("#schools_seleter option").sort(function (a, b) {
	    return a.text == b.text ? 0 : a.text < b.text ? -1 : 1;
	}));
	$("#schools_seleter").prepend('<option id="scp_'+sid+'" value="'+sid+
			'" title="Please Select A School">请选择学校(队伍数/志愿者数)'+'</option>');
	for(var i in volunteer){
		var v=volunteer[i];
		if(volunteer_school[v]!=undefined)
		for(var j in volunteer_school[v]){
			var s=volunteer_school[v][j];
	//		alert(s+" "+j+" * "+v);
			addSchool(v,s);
		}
	}
	function v_show(vid){
		current_volunteer=vid;
		var of=$("#v_ctrl_"+vid).offset();
		$("#schools_container").offset({
			top:of.top-$("#schools_container").outerHeight()+$("#v_ctrl_"+vid).outerHeight(),
			left:of.left-$("#schools_container").outerWidth()
		});
		$$("schools_seleter").value=1;
		$("#schools_container").fadeIn(800);
	}
	function select_school(sid){
		if(sid<=1||current_volunteer==0)return;
		$$("schools_seleter").value=1;
		$.get(
			"schoolvsvolunteer",
			{volunteerid:current_volunteer,schoolid:sid},
			function(d){
				if(d.code==0){
					addSchool(current_volunteer,sid);
				}
		},"json");
	}
	function addSchool(vid,sid){
		//alert(sid);
		if(schools[sid]==null)return;
		schools[sid][2]++;
		var c=255-schools[sid][2]*16;
		$("#scp_"+sid).css({background: "rgb("+c+","+c+","+c+")"});
		$$("scp_"+sid).innerHTML=schools[sid][0]+'('+schools[sid][3]+'/'+schools[sid][2]+')';
		$$("v_ctrl_"+vid).innerHTML+='<span id="vs_'+vid+'_'+sid+'" class="badge" title="英文名：'+schools[sid][1]+' 队伍数：('+schools[sid][3]+')">'+schools[sid][0]+'<i class="icon-trash" onclick="rmSchool('+vid+','+sid+')" title="删除"></i></span><br/>';
	}
	function rmSchool(vid,sid){
		$.get(
				"schoolvsvolunteer",
				{volunteerid:-vid,schoolid:sid},
				function(d){
					schools[sid][2]--;
					var c=255-schools[sid][2]*16;
					$("#scp_"+sid).css({background: "rgb("+c+","+c+","+c+")"});
					$$("scp_"+sid).innerHTML=schools[sid][0]+'('+schools[sid][3]+'/'+schools[sid][2]+')';
					$('#vs_'+vid+'_'+sid+"+br").remove();
					$('#vs_'+vid+'_'+sid).remove();
			},"json");
	}
	function add_volunteers(){
		var text=$$("add_list").value.split("\n"),tmp;
		for(i in text){
			var line=text[i];
			if(/[^ ]+ [^ ]+/.test(line)){
				tmp=line.split(" ");
			//	alert(tmp[0]+' x '+tmp[1]);
				//恶心啊，因为把逻辑和jsp这个UI写一块的，所以……无法判断是否成功
				$.ajax({
					async:false,
					url:"admin_user_edit.jsp",
					type:"POST",
					data:{
						id:-2,
						username:tmp[0],
						password:tmp[1],
						permission:2
					}
				});
			}else{
				alert("不符合规范的导入格式：\n\n"+line+"\n\n请注意记录，此志愿者没有被导入！");
			}
		}
		location.reload();
	}
	</script>
	<h3>批量增加志愿者</h3>
	<p>一行一个格式：用户名[空格]密码</p>
	<textarea rows="10" cols="80" style="width: 900px" id="add_list"></textarea>
	<input type="button" value="增加" onclick="add_volunteers()" />
</div>
<%
	pstat.close();
			conn.close();
%>
<%
	} catch (SQLException e) {
			e.printStackTrace();
			s.message = "Error Occour While Perform Your Request. Technical Detial: "
					+ e.getMessage();
			s.msgType = MSG.error;
			response.sendRedirect("admin_hotel_room.jsp");
		}
%>
<%@ include file="include/bottom_html_java.jspf"%>

