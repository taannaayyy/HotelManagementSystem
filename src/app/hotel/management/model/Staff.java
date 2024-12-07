package app.hotel.management.model;

public abstract class Staff extends User_Account {
    private int staffId;
    private String staffName;
    private String staffSurname;
    private String staffType; // Enum-like values: "Administrator", "Receptionist", "Housekeeper"
    private double staffSalary;
    private int hotelId;

    // Constructors
    public Staff() {
        super();
    }

    public Staff(int staffId, String username, String passwordHash, String staffType, String staffName, String staffSurname, double staffSalary, int hotelId) {
        super(staffId, username, passwordHash, ("Staff:" + staffType)); // Call superclass constructor
        this.staffId = staffId;
        this.staffName = staffName;
        this.staffSurname = staffSurname;
        this.staffType = staffType;
        this.staffSalary = staffSalary;
        this.hotelId = hotelId;
    }

    // Getters and Setters
    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getStaffSurname() {
        return staffSurname;
    }

    public void setStaffSurname(String staffSurname) {
        this.staffSurname = staffSurname;
    }

    public String getStaffType() {
        return staffType;
    }

    public void setStaffType(String staffType) {
        this.staffType = staffType;
    }

    public double getStaffSalary() {
        return staffSalary;
    }

    public void setStaffSalary(double staffSalary) {
        this.staffSalary = staffSalary;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    // Abstract method for subclass-specific details
    public abstract String getResponsibilities();

    // toString Method
    @Override
    public String toString() {
        return super.toString() + ", Staff{" +
                ", staffName='" + staffName + '\'' +
                ", staffSurname='" + staffSurname + '\'' +
                "staffType='" + staffType + '\'' +
                ", staffSalary=" + staffSalary +
                '}';
    }

}
