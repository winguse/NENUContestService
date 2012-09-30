package cn.edu.nenu.acm.contestservice.modeling.objects;

import java.sql.SQLException;
import java.util.List;

public class Coach extends Person {
	private List<Team> teams = null;

	public Coach() {
	}

	public Coach(int id, int team, int userBelongs, int title,
			String chineseName, String englishName, String idNumber,
			boolean gender, String mobile, int welcomeParty,
			int awardsCeremony, String major, String photo, String email,
			String clothes, String description) {
		super(id, team, userBelongs, title, chineseName, englishName, idNumber, gender,
				mobile, welcomeParty, awardsCeremony, major, photo, email, clothes,
				description);
	}

	public Coach(int team, int userBelongs, int title, String chineseName,
			String englishName, String idNumber, boolean gender, String mobile,
			int welcomeParty, int awardsCeremony, String major, String photo,
			String email, String clothes, String description) {
		super(team, userBelongs, title, chineseName, englishName, idNumber, gender,
				mobile, welcomeParty, awardsCeremony, major, photo, email, clothes,
				description);
	}

	public void loadMyTeams() throws SQLException {
		this.teams = Team.getCoachTeams(super.getId());
	}

	public List<Team> getTeams() throws SQLException {
		if(teams==null)loadMyTeams();
		return teams;
	}

}
