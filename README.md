# Clinic Appointment System

PROG2004 Object Oriented Programming - Assignment 1

This repository contains two versions of the same appointment system:

- `src/` - the Java console application used for the assignment.
- `web/` - a simple HTML/CSS/JavaScript version of the same system.

Both versions use the same clinic rules: seven health professionals, a
08:00-16:00 clinic day, 30-minute time slots, and no double booking.

## Java version

The Java program contains the required class design and a scripted
demonstration in `AssignmentOne.main`.

```text
cd src
javac *.java
java AssignmentOne
```

The demonstration creates the health professionals, books appointments of
different types, displays, filters, sorts and cancels them, and shows the error
cases. After the demonstration an optional console can be used to make
bookings with your own input.

Main classes:

- `HealthProfessional` - abstract base class with ID, name and available days.
- `GeneralPractitioner`, `Nurse`, `Surgeon`, `Dietitian` - the four
  professional types.
- `Patient`, `TimeSlot`, `Appointment` - the appointment model.
- `AppointmentManager` - booking rules, filtering, sorting and cancellation.
- `AssignmentOne` - the `main` method and demonstration.
- `AppointmentConsole` - the optional interactive console.

## Web version

Open `web/index.html` in a browser. No server, build step or internet connection
is needed.

The web page is a simple front end for the same system. It has four pages:

- Overview - today's statistics, the next appointment and the team.
- Book - choose a professional, a time and enter the patient details.
- Appointments - search, filter and cancel bookings.
- Team - the seven health professionals.

Bookings in the web version are saved in the browser with `localStorage`, so
they are still there after a refresh. The "Reset demo" button restores the
original demo day.

## Project structure

```text
.
├── README.md
├── .gitignore
├── src/
│   ├── AssignmentOne.java
│   ├── Appointment.java
│   ├── AppointmentConsole.java
│   ├── AppointmentManager.java
│   ├── Dietitian.java
│   ├── GeneralPractitioner.java
│   ├── HealthProfessional.java
│   ├── Nurse.java
│   ├── Patient.java
│   ├── Surgeon.java
│   └── TimeSlot.java
└── web/
    ├── index.html
    ├── styles.css
    ├── model.js
    └── app.js
```

## Notes

- The Java program is the assessed solution and keeps appointments in memory.
- The web version is a small demonstration of the same logic and is not part of
  the Java submission.
- No external libraries or frameworks are used in either version.
