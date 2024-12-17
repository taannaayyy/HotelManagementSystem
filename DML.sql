-- Insert data into Hotel
INSERT INTO Hotel (Hotel_ID, Hotel_Name, Hotel_Address)
SELECT 1, 'Ozu Grand Hotel', '123 University Blvd, Cityville'
WHERE NOT EXISTS (
    SELECT 1 FROM Hotel WHERE Hotel_ID = 1
);

INSERT INTO Hotel (Hotel_ID, Hotel_Name, Hotel_Address)
SELECT 2, 'Ozu Downtown Suites', '456 Main Street, Metropolis'
WHERE NOT EXISTS (
    SELECT 1 FROM Hotel WHERE Hotel_ID = 2
);

-- Insert data into User_Account
INSERT INTO User_Account (User_ID, Username, Password_Hash, Role,Hotel_ID)
SELECT 1, 'guest_alice', 'hashed_password_1', 'Guest',1
WHERE NOT EXISTS (
    SELECT 1 FROM User_Account WHERE User_ID = 1
);

INSERT INTO User_Account (User_ID, Username, Password_Hash, Role,Hotel_ID)
SELECT 2, 'admin_john', 'hashed_password_2', 'Administrator',1
WHERE NOT EXISTS (
    SELECT 1 FROM User_Account WHERE User_ID = 2
);

INSERT INTO User_Account (User_ID, Username, Password_Hash, Role,Hotel_ID)
SELECT 3, 'reception_jane', 'hashed_password_3', 'Receptionist',1
WHERE NOT EXISTS (
    SELECT 1 FROM User_Account WHERE User_ID = 3
);

INSERT INTO User_Account (User_ID, Username, Password_Hash, Role,Hotel_ID)
SELECT 4, 'housekeeper_emily', 'hashed_password_4', 'Housekeeper',1
WHERE NOT EXISTS (
    SELECT 1 FROM User_Account WHERE User_ID = 4
);

INSERT INTO User_Account (User_ID, Username, Password_Hash, Role,Hotel_ID)
SELECT 5, 'admin', 'password', 'Administrator',1
WHERE NOT EXISTS (
    SELECT 1 FROM User_Account WHERE User_ID = 5
);

-- Insert data into Guest
INSERT INTO Guest (Guest_ID, User_ID, Guest_Name, Guest_ContactInfo)
SELECT 1, 1, 'Alice Johnson', 'alice.johnson@example.com'
WHERE NOT EXISTS (
    SELECT 1 FROM Guest WHERE Guest_ID = 1
);

-- Insert data into Staff
INSERT INTO Staff (Staff_ID, User_ID, Staff_Name, Staff_Type, Staff_Salary, Hotel_ID)
SELECT 2, 2, 'John Doe', 'Administrator', 5000.00, 1
WHERE NOT EXISTS (
    SELECT 1 FROM Staff WHERE Staff_ID = 2
);

INSERT INTO Staff (Staff_ID, User_ID, Staff_Name, Staff_Type, Staff_Salary, Hotel_ID)
SELECT 3, 3, 'Jane Smith', 'Receptionist', 3500.00, 1
WHERE NOT EXISTS (
    SELECT 1 FROM Staff WHERE Staff_ID = 3
);

INSERT INTO Staff (Staff_ID, User_ID, Staff_Name, Staff_Type, Staff_Salary, Hotel_ID)
SELECT 4, 4, 'Emily Davis', 'Housekeeper', 3000.00, 2
WHERE NOT EXISTS (
    SELECT 1 FROM Staff WHERE Staff_ID = 4
);

-- Insert data into Administrator
INSERT INTO Administrator (Staff_ID)
SELECT 2
WHERE NOT EXISTS (
    SELECT 1 FROM Administrator WHERE Staff_ID = 2
);

-- Insert data into Receptionist
INSERT INTO Receptionist (Staff_ID, Reception_Shift)
SELECT 3, 'Morning Shift'
WHERE NOT EXISTS (
    SELECT 1 FROM Receptionist WHERE Staff_ID = 3
);

-- Insert data into Housekeeper
INSERT INTO Housekeeper (Staff_ID, Shift)
SELECT 4, 'Day Shift'
WHERE NOT EXISTS (
    SELECT 1 FROM Housekeeper WHERE Staff_ID = 4
);

-- Insert data into Room
INSERT INTO Room (Room_ID, Room_Type, Room_Price, Room_Status, Hotel_ID, Staff_ID)
SELECT 101, 'Single', 100.00, 'Available', 1, 4
WHERE NOT EXISTS (
    SELECT 1 FROM Room WHERE Room_ID = 101
);

INSERT INTO Room (Room_ID, Room_Type, Room_Price, Room_Status, Hotel_ID, Staff_ID)
SELECT 102, 'Double', 150.00, 'Occupied', 1, 4
WHERE NOT EXISTS (
    SELECT 1 FROM Room WHERE Room_ID = 102
);

INSERT INTO Room (Room_ID, Room_Type, Room_Price, Room_Status, Hotel_ID, Staff_ID)
SELECT 201, 'Suite', 300.00, 'Cleaning', 2, NULL
WHERE NOT EXISTS (
    SELECT 1 FROM Room WHERE Room_ID = 201
);
 
-- Insert data into Reservation
INSERT INTO Reservation (Reservation_ID, Guest_ID, Room_ID, CheckInDate, CheckOutDate, Booking_Status)
SELECT 1, 1, 101, '2024-12-01', '2024-12-05', 'Confirmed'
WHERE NOT EXISTS (
    SELECT 1 FROM Reservation WHERE Reservation_ID = 1
);

INSERT INTO Reservation (Reservation_ID, Guest_ID, Room_ID, CheckInDate, CheckOutDate, Booking_Status)
SELECT 2, 1, 102, '2024-12-10', '2024-12-15', 'Pending'
WHERE NOT EXISTS (
    SELECT 1 FROM Reservation WHERE Reservation_ID = 2
);

-- Insert data into Payment
INSERT INTO Payment (Payment_ID, Reservation_ID, Amount, Payment_Date, Payment_Status)
SELECT 1, 1, 400.00, '2024-12-01', 'Paid'
WHERE NOT EXISTS (
    SELECT 1 FROM Payment WHERE Payment_ID = 1
);

INSERT INTO Payment (Payment_ID, Reservation_ID, Amount, Payment_Date, Payment_Status)
SELECT 2, 2, 600.00, '1900-01-01', 'Pending'
WHERE NOT EXISTS (
    SELECT 1 FROM Payment WHERE Payment_ID = 2
);

-- Insert data into Housekeeping
INSERT INTO Housekeeping (Task_ID, Housekeeper_Staff_ID, Room_ID, Schedule, Task_Status)
SELECT 1, 4, 101, '2024-12-06', 'Pending'
WHERE NOT EXISTS (
    SELECT 1 FROM Housekeeping WHERE Task_ID = 1
);

INSERT INTO Housekeeping (Task_ID, Housekeeper_Staff_ID, Room_ID, Schedule, Task_Status)
SELECT 2, 4, 102, '2024-12-07', 'Completed'
WHERE NOT EXISTS (
    SELECT 1 FROM Housekeeping WHERE Task_ID = 2
);
