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
	if(request.getParameter("updateable")!=null){
		Site.setUpdateable(request.getParameter("updateable").equals("yes"));
		Site.setBook_able(request.getParameter("book_able").equals("yes"));
		Site.setCoach_edit_able(request.getParameter("Coach_edit_able").equals("yes"));
		Site.setVolunteer_reg_able(request.getParameter("volunteer_reg_able").equals("yes"));
		Site.setOpenTime(Long.parseLong(request.getParameter("opentime")));
		Site.setIndexNotice(request.getParameter("indexnotice"));
		response.sendRedirect("admin_system_settings.jsp");				
		s.message = "System Settings Updated.";
		s.msgType = MSG.notice;
		return;
	}
	
%>
<%@ include file="include/head_html.jspf"%>
<title>系统设置</title>
<%@ include file="include/body_html_java.jspf"%>
<div id="main">
	<h1 style="text-align:center">系统设置</h1>
	<p class="pull-right"><button class="btn btn-danger"  id="system_reset">重置系统</button></p>
<form action="" method="post">
	<p>
		<label for="updateable">领队能否更新:</label>
		<select id="updateable" name="updateable">
			<option value="yes">Yes</option>
			<option value="no">No</option>
		</select>
	</p>
	<p>
		<label for="book_able">领队能否预订酒店:</label>
		<select id="book_able" name="book_able">
			<option value="yes">Yes</option>
			<option value="no">No</option>
		</select>
	</p>
	<p>
		<label for="Coach_edit_able">领队能否教练（队伍）信息能否增加:</label>
		<select id="Coach_edit_able" name="Coach_edit_able">
			<option value="yes">Yes</option>
			<option value="no">No</option>
		</select>
	</p>
	<p>
		<label for="volunteer_reg_able">接受志愿者注册:</label>
		<select id="volunteer_reg_able" name="volunteer_reg_able">
			<option value="yes">Yes</option>
			<option value="no">No</option>
		</select>
	</p>
	<p>
		<label for="_opentime">领队开放时间:</label>			
		<input type="hidden" id="opentime" name="opentime" value="<%=Site.getOpenTime()%>" />	
		<input  type="datetime-local" id="_opentime" onchange="document.getElementById('opentime').value=getDateFromStr(this.value)" />
	</p>
	<p>
		<label for="indexnotice">首页公告:</label>
		<textarea name="indexnotice" id="indexnotice" style="width: 920px;" rows="20" cols="80"></textarea>
	</p>
	<p><input type="submit" /></p>
</form>
<script>
	document.getElementById("updateable").value='<%=Site.isUpdateable()?"yes":"no"%>';
	document.getElementById("book_able").value='<%=Site.isBook_able()?"yes":"no"%>';
	document.getElementById("Coach_edit_able").value='<%=Site.isCoach_edit_able()?"yes":"no"%>';
	document.getElementById("volunteer_reg_able").value='<%=Site.isVolunteer_reg_able()?"yes":"no"%>';
	document.getElementById("indexnotice").innerText="<%=Site.getIndexNotice().replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("/", "\\/")%>";
	var opentime=new Date(<%=Site.getOpenTime()%>);
	document.getElementById("_opentime").value=setDateStr(opentime);
	function setDateStr(myDate){
	        var year,month,date,hour,minute,second;
	        year=myDate.getFullYear();       
	        month=myDate.getMonth()+1;
	        if(parseInt(month,10)<10)
	            month="0"+month;
	        date=myDate.getDate();
	        if(parseInt(date,10)<10)
	            date="0"+date; 
	        hour=myDate.getHours();
	        if(parseInt(hour,10)<10)
	            hour="0"+hour;
	        minute=myDate.getMinutes();
	        if(parseInt(minute,10)<10)
	            minute="0"+minute;
	        second=myDate.getSeconds();
	        if(parseInt(second,10)<10)
	            second="0"+second;
	        DateStr=year+"-"+month+"-"+date+"T"+hour+":"+minute;//+":"+second;
	        return DateStr;
	}
	function getDateFromStr(str){
		if(!/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}$/.test(str)){//:\d{2}
			alert("Your haved enter a wrong date. Check your format, please.");
			return -1;
		}
        var year,month,date,hour,minute,second;
        year=getInt(str.slice(0,4));
        month=getInt(str.slice(5,7))-1;
        date=getInt(str.slice(8,10));
        hour=getInt(str.slice(11,13));
        minute=getInt(str.slice(14,16));
        //second=parseInt(str.slice(17,19));
        second=0;
        //alert(year+" "+month+" "+date+" "+hour+" "+minute+" "+second);
        d=new Date();
        d.setFullYear(year,month,date);
        d.setHours(hour,minute,second,0);
        return d.getTime();
	}
	function getInt(str){
		if(str.slice(0,1)=='0')
			return parseInt(str.slice(1));
		return parseInt(str);
	}
	$('input[type="datetime-local"]').will_pickdate({
		  timePicker: true,
		  format: 'Y-m-dTH:i',
		  inputOutputFormat: 'Y-m-dTH:i'
		});
	$("#system_reset").click(function(){
		if(confirm("你确定要重置数据库？操作不可逆，请保证数据已经备份！")){
			window.location="admin_system_reset.jsp";
		}
	});
</script>
<%--
try{

--%>

<%--
}catch(SQLException e){
	e.printStackTrace();
	s.message="Error Occour While Perform Your Request. Technical Detial: " +e.getMessage();
	s.msgType=MSG.error;
	response.sendRedirect(".");
}
--%>
</div>
<%@ include file="include/bottom_html_java.jspf"%>

