
-- DDL Statements for Hotel Management System

-- Hotel Table
CREATE TABLE Hotel (
    Hotel_ID INT PRIMARY KEY,
    Hotel_Name VARCHAR(100) NOT NULL,
    Hotel_Address VARCHAR(200) NOT NULL
);

-- User Account Table
CREATE TABLE User_Account (
    User_ID INT PRIMARY KEY,
    Username VARCHAR(100) UNIQUE NOT NULL,
    Password_Hash VARCHAR(255) NOT NULL,
    Hotel_ID INT,
    Role ENUM('Guest', 'Administrator', 'Receptionist', 'Housekeeper') NOT NULL,
    FOREIGN KEY (Hotel_ID) REFERENCES Hotel(Hotel_ID) ON DELETE CASCADE
);

-- Guest Table
CREATE TABLE Guest (
    Guest_ID INT PRIMARY KEY,
    User_ID INT UNIQUE,
    Guest_Name VARCHAR(100) NOT NULL,
    Guest_ContactInfo VARCHAR(150),
    FOREIGN KEY (User_ID) REFERENCES User_Account(User_ID) ON DELETE CASCADE
);

-- Staff Table
CREATE TABLE Staff (
    Staff_ID INT PRIMARY KEY,
    User_ID INT UNIQUE,
    Staff_Name VARCHAR(100) NOT NULL,
    Staff_Type ENUM('Administrator', 'Receptionist', 'Housekeeper') NOT NULL,
    Staff_Salary DECIMAL(10, 2),
    Hotel_ID INT,
    FOREIGN KEY (User_ID) REFERENCES User_Account(User_ID) ON DELETE CASCADE,
    FOREIGN KEY (Hotel_ID) REFERENCES Hotel(Hotel_ID) ON DELETE CASCADE
);

-- Administrator Table
CREATE TABLE Administrator (
    Staff_ID INT PRIMARY KEY,
    FOREIGN KEY (Staff_ID) REFERENCES Staff(Staff_ID) ON DELETE CASCADE
);

-- Receptionist Table
CREATE TABLE Receptionist (
    Staff_ID INT PRIMARY KEY,
    Reception_Shift VARCHAR(100),
    FOREIGN KEY (Staff_ID) REFERENCES Staff(Staff_ID) ON DELETE CASCADE
);

-- Housekeeper Table
CREATE TABLE Housekeeper (
    Staff_ID INT PRIMARY KEY,
    Shift VARCHAR(100),
    FOREIGN KEY (Staff_ID) REFERENCES Staff(Staff_ID) ON DELETE CASCADE
);

-- Room Table
CREATE TABLE Room (
    Room_ID INT PRIMARY KEY,
    Room_Type VARCHAR(50),
    Room_Price DECIMAL(10, 2) NOT NULL,
    Room_Status ENUM('Available', 'Occupied', 'Cleaning', 'Maintenance') NOT NULL,
    Hotel_ID INT,
    Staff_ID INT,
    FOREIGN KEY (Hotel_ID) REFERENCES Hotel(Hotel_ID) ON DELETE CASCADE,
    FOREIGN KEY (Staff_ID) REFERENCES Staff(Staff_ID) ON DELETE SET NULL
);

-- Reservation Table
CREATE TABLE Reservation (
    Reservation_ID INT PRIMARY KEY,
    Guest_ID INT,
    Room_ID INT,
    CheckInDate DATE NOT NULL,
    CheckOutDate DATE NOT NULL,
    Booking_Status ENUM('Pending', 'Confirmed', 'Cancelled') NOT NULL,
    FOREIGN KEY (Guest_ID) REFERENCES Guest(Guest_ID) ON DELETE CASCADE,
    FOREIGN KEY (Room_ID) REFERENCES Room(Room_ID) ON DELETE CASCADE
);

-- Payment Table
CREATE TABLE Payment (
    Payment_ID INT PRIMARY KEY,
    Reservation_ID INT,
    Amount DECIMAL(10, 2) NOT NULL,
    Payment_Date DATE NOT NULL,
    Payment_Status ENUM('Paid', 'Pending') NOT NULL,
    FOREIGN KEY (Reservation_ID) REFERENCES Reservation(Reservation_ID) ON DELETE CASCADE
);

-- Housekeeping Table
CREATE TABLE Housekeeping (
    Task_ID INT PRIMARY KEY,
    Housekeeper_Staff_ID INT,
    Room_ID INT,
    Schedule DATE NOT NULL,
    Task_Status ENUM('Pending', 'Completed') NOT NULL,
    FOREIGN KEY (Housekeeper_Staff_ID) REFERENCES Housekeeper(Staff_ID) ON DELETE SET NULL,
    FOREIGN KEY (Room_ID) REFERENCES Room(Room_ID) ON DELETE CASCADE
);