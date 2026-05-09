# Design and Development of an Airline Online

# Check-In Mobile App

## Project context

An airline mobile app for online check-in enables passengers to complete the
check-in process remotely before arriving at the airport. Through the app,
travelers can confirm their identity by scanning their passport, choose their
preferred seat, declare their baggage, and obtain a digital boarding pass directly
on their smartphone

## App features

**1)** **_Registration_**
Users can create an account by providing basic information such as their name,
email address, phone number, and password. Alternatively, they can sign in
quickly using their Google account.

**2)** **_Flight Lookup & Booking Retrieval_**
Passengers can access their flight information by entering their booking
reference and last name. The app then displays their itinerary along with the
current check-in status..

**3)** **_Online Check-In_**
Available 24 hours before departure, the online check-in process includes the
following steps:
● Passport Scan: The passenger scans the first page of their passport. The
application uses Optical Character Recognition (OCR) technology to
extract the passenger’s identity information from the scanned page and
verify it against the booking details.
● Details Review: The passenger reviews and confirms their personal and
travel information.
● Seat Selection: : The passenger selects a seat using an interactive aircraft
seat map that shows available, occupied, and premium seats.
● Baggage Declaration: The passenger declares the number and type of
bags to check in.
● Special Requests: Dietary preferences, assistance needs and infant or pet
declarations.
A confirmation notification is sent to the passenger upon completion.

**4)** **_QR Code Boarding Pass_**
A digital boarding pass containing a unique QR code is generated. The QR code
can be scanned at baggage drop counters, security checkpoints, and boarding
gates.
● PDF Boarding Pass Generation: Passengers can download their boarding
pass as a PDF document, which also includes a summary of their flight
information.
● Offline Access: Boarding passes and flight information are stored locally
on the device, allowing passengers to access them without an internet
connection.

**5)** **_Offline Mode & Synchronization_**
Flight details and boarding passes are cached locally and remain accessible
offline. When the device reconnects to the internet, the application
automatically synchronizes with the server to retrieve the latest updates.

### Evaluation Criteria

```
● Design and UI/UX: Visual consistency, clarity, and intuitive user
experience.
● Performance: Smooth navigation and responsive interactions.
● Architecture: Clean, modular code with clear separation of concerns.
● Security: Safe handling of sensitive data and secure communication.
● Functionality: Correct and complete implementation of all features.
```


