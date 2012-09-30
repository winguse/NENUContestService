package cn.edu.nenu.acm.contestservice.modeling.objects;

public class BookedRoomInfo extends Room {
	protected int count;
	protected String hotelName;

	public String getHotelName() {
		return hotelName;
	}

	public void setHotelName(String hotelName) {
		this.hotelName = hotelName;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public BookedRoomInfo(int id, int hotel, boolean breakfast,
			boolean internet, String typeName, int volume, int total,
			int booked, int price, int myBooked,String hotelName,String description) {
		super(id, hotel, breakfast, internet, typeName, volume, total, booked,
				price,description);
		this.count = myBooked;
		this.hotelName=hotelName;
	}

}
