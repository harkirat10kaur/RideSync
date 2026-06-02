# 🚖 RideSync - Full Stack Cab Booking Platform

RideSync is a full-stack cab booking application developed using Java, MySQL, HTML, CSS, and JavaScript. The platform allows users to book rides, estimate fares, visualize routes, schedule future bookings, and track ride status through an interactive and user-friendly interface.

---

# 📖 Project Overview

RideSync aims to simplify the cab booking process by providing users with a seamless experience for booking rides, calculating fares, selecting vehicles, and managing ride history.

The project demonstrates full-stack development concepts including frontend design, backend development, database integration, authentication, and API communication.

---

# ✨ Key Features

## 👤 User Authentication
- User Registration
- Secure Login System
- User Session Management

## 🚖 Cab Booking
- Enter Pickup Location
- Enter Destination
- Select Vehicle Type
- Schedule Future Rides
- Confirm Booking

## 📍 Route & Fare Management
- Route Visualization using OpenStreetMap
- Distance Calculation
- Dynamic Fare Estimation
- Vehicle-Based Pricing

## 💳 Payment Options
- Cash Payment
- UPI Payment
- Credit Card Payment

## 🚗 Ride Experience
- Driver Assignment Simulation
- Cab Number Generation
- Live Ride Status Tracking
- Booking Confirmation

## 📊 Booking Management
- Booking History
- Ride Status Monitoring
- Booking Cancellation

---

# 🏗️ System Architecture

```text
Frontend (HTML, CSS, JavaScript)
                ↓
         Java Backend
                ↓
         MySQL Database
```

---

# 🛠️ Technology Stack

## Frontend
- HTML5
- CSS3
- JavaScript

## Backend
- Java
- JDBC

## Database
- MySQL

## Maps & Location
- OpenStreetMap
- Leaflet.js

## Version Control
- Git
- GitHub

---

# 🗄️ Database

The application uses MySQL to manage:

- User Accounts
- Ride Bookings
- Driver Details
- Booking History
- Fare Information

---

# ⚙️ Installation & Setup

## 1. Clone Repository

```bash
git clone https://github.com/harkirat10kaur/RideSync.git
```

## 2. Open Project

Open the project in IntelliJ IDEA.

## 3. Create Database

```sql
CREATE DATABASE cab_booking;
```

## 4. Create Required Tables

Import or execute the SQL scripts used in the project.

## 5. Configure Database Connection

Update MySQL credentials in:

```java
DBConnection.java
```

Example:

```java
String url = "jdbc:mysql://localhost:3306/cab_booking";
String username = "root";
String password = "your_password";
```

## 6. Run Backend Server

Run:

```java
CabBookingServer.java
```

## 7. Launch Application

Open:

```text
http://localhost:8080
```

---

# 🚀 Future Enhancements

- Real-Time GPS Tracking
- Google Maps API Integration
- Online Payment Gateway
- Driver Dashboard
- Admin Panel
- Email Notifications
- Ride Reviews & Ratings
- Mobile Application Version

---
