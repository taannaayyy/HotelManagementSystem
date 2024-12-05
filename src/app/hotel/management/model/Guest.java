package app.hotel.management.model;

public class Guest extends User_Account {
    private int guestId; // Foreign key to UserAccount's userId
    private String guestName;
    private String guestSurname;
    private String guestContactInfo;

    // Constructors
    public Guest() {
        super();
    }

    public Guest(String username, String passwordHash, int guestId, String guestName, String guestSurname, String guestContactInfo) {
        super(guestId, username, passwordHash, "Guest"); // Call superclass constructor
        this.guestId = guestId;
        this.guestName = guestName;
        this.guestSurname = guestSurname;
        this.guestContactInfo = guestContactInfo;
    }

    // Getters and Setters
    public int getGuestId() {
        return guestId;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getGuestSurname() {
        return guestSurname;
    }

    public void setGuestSurname(String guestSurname) {
        this.guestSurname = guestSurname;
    }

    public String getGuestContactInfo() {
        return guestContactInfo;
    }

    public void setGuestContactInfo(String guestContactInfo) {
        this.guestContactInfo = guestContactInfo;
    }

    // Overriding abstract method
    @Override
    public String getDetails() {
        return "Guest Name: " + guestName + "Guest Surname: "+ guestSurname +", Contact Info: " + guestContactInfo;
    }

    // toString Method
    @Override
    public String toString() {
        return super.toString() + ", Guest{" +
                "guestId=" + guestId +
                ", guestName='" + guestName + '\'' +
                ", guestContactInfo='" + guestContactInfo + '\'' +
                '}';
    }
}
