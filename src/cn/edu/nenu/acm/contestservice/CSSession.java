package cn.edu.nenu.acm.contestservice;

import cn.edu.nenu.acm.contestservice.modeling.objects.User;

public class CSSession {

	public User user = null;
	public String lang="en";
	public String lastPage=".";
	public MSG msgType = MSG.notice;
	public String message = "Welcome!";
	public int otl_permission=0;
	private String salt = Site.generateSalt();
	
	public CSSession() {

	}

	/**
	 * 更换所有令牌（Token）<br>
	 * 有些只能够操作一次的令牌使用完之后就要刷新，让其失效。 例如当用户第一次访问领队页面时，是需要插入新的学校的，而学校只能够插入一次
	 * ，其他时候都只能是更新，所以提交ID=-1的令牌要更新，否则用户（别有用心地
	 * ）就可以用这个令牌插入好多冗余的数据。当然，正常来说，那个令牌不会再展示给用户
	 * （第二次的时候，ID就变成生成的新ID，他随意改了），除非他保存下来。
	 */
	public void renewToken() {
		salt = Site.generateSalt();
	}

	public String getToken(int msg) {
		return Site.hash("" + msg, salt);
	}

	public String getToken(String msg) {
		return Site.hash(msg, salt);
	}

	public boolean verifyToken(String msg, String token) {
		if (msg == null || token == null)
			return false;
		if (Site.hash(msg, salt).equals(token)) {
			// salt = Site.generateSalt();//token要一次性的
			// 要一次性的时候，用户可能会遇到麻烦，比如它如果刷新了插入新纪录页面
			// （令牌用GET方式保存，那么就认为令牌过期了（刷新过了），然后出现非法请求）
			return true;
		}
		return false;
	}

	public boolean verifyToken(int msg, String token) {
		if (token == null)
			return false;
		if (Site.hash("" + msg, salt).equals(token)) {
			// salt = Site.generateSalt();
			return true;
		}
		return false;
	}
}
