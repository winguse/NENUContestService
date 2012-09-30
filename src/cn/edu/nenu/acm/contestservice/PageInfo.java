package cn.edu.nenu.acm.contestservice;

public class PageInfo {
	public int page;
	public int totalCount;
	public int pages;
	public int pagesize;
	public PageInfo(int page,  int pagesize) {
		super();
		this.page = page;
		this.pagesize = pagesize;
	}
	
}
