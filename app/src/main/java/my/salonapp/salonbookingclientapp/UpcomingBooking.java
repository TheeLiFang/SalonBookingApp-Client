package my.salonapp.salonbookingclientapp;

public class UpcomingBooking {

    private int bookingId;
    private String bookingTime;
    private String staffName;
    private String companyName;

    public UpcomingBooking() {
    }

    public UpcomingBooking(int bookingId, String bookingTime, String companyName, String staffName) {
        this.bookingId = bookingId;
        this.bookingTime = bookingTime;
        this.staffName = staffName;
        this.companyName = companyName;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public String getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(String bookingTime) {
        this.bookingTime = bookingTime;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }
}
