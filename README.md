<div align="center">

# 🚗 Toll Management System

**A role-based desktop application for managing toll booth operations, payments, and reporting**

[![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)](LICENSE)
[![Eclipse IDE](https://img.shields.io/badge/IDE-Eclipse-2C2255?style=for-the-badge&logo=eclipseide&logoColor=white)](https://www.eclipse.org/)

</div>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [System Architecture](#-system-architecture)
- [Screenshots](#-screenshots)
- [Database Schema](#-database-schema)
- [Getting Started](#-getting-started)
- [User Roles](#-user-roles)
- [Socket Communication](#-socket-communication)
- [Project Structure](#-project-structure)
- [Known Issues & Future Work](#-known-issues--future-work)
- [Team](#-team)

---

## 🌍 Overview

The **Toll Management System** is a Java Swing desktop application designed to digitize and streamline toll booth operations. It provides a centralized platform for administrators, supervisors, operators, and vehicle owners — each with their own tailored dashboard and access level.

The system handles real-time toll payment processing, vehicle registration, booth management, and revenue reporting. A built-in socket server enables live event broadcasting across all connected clients.

> 💡 Currency is denominated in **RWF (Rwandan Francs)**, making this system well-suited for toll operations in East African urban transport networks.

---

## ✨ Features

| Feature | Description |
|---|---|
| 🔐 **Role-Based Login** | Separate dashboards for Admin, Supervisor, Operator, and Owner |
| 🚘 **Vehicle Registry** | Register, search by plate number, update, and delete vehicles |
| 🛣️ **Booth Management** | Create and assign operators to toll booths across locations |
| 💳 **Payment Processing** | Operators scan plate numbers, auto-fetch toll rates, and record payments |
| 📊 **Revenue Reporting** | Supervisors and admins view transaction history and total revenue |
| 🔴 **Live Notifications** | Socket server broadcasts toll events to all connected clients in real time |
| 👤 **Owner Portal** | Vehicle owners view their own registered vehicles and payment history |

---

## 🏗️ System Architecture

The project follows a clean **3-Layer MVC architecture**:

```
┌─────────────────────────────────────────────────────┐
│                    VIEW LAYER                        │
│   LoginView │ AdminView │ OperatorView │ OwnerView   │
│             SupervisorView                           │
└────────────────────┬────────────────────────────────┘
                     │ calls
┌────────────────────▼────────────────────────────────┐
│                CONTROLLER LAYER                      │
│  AuthController │ UserController │ BoothController   │
│  VehicleController │ TransactionController           │
└────────────────────┬────────────────────────────────┘
                     │ queries
┌────────────────────▼────────────────────────────────┐
│              DATABASE LAYER (MySQL)                  │
│  users │ vehicles │ toll_booths │ toll_rates         │
│  transactions                                        │
└─────────────────────────────────────────────────────┘

        ┌─────────────────────────┐
        │     SOCKET LAYER        │
        │  TollServer (port 9999) │
        │  ClientHandler          │
        │  TollClient             │
        └─────────────────────────┘
```

---

## 📸 Screenshots

> **Note to contributors:** Replace the placeholder images below with actual screenshots of your running application.

### Login Screen
> 📌 *Place screenshot here — capture `LoginView` showing the username/password form*

```
[ docs/screenshots/01_login.png ]
```

---

### Admin Dashboard — User Management
> 📌 *Place screenshot here — capture the "Manage Users" tab in `AdminView`, showing the user table and form fields*

```
[ docs/screenshots/02_admin_users.png ]
```

---

### Admin Dashboard — Booth Management
> 📌 *Place screenshot here — capture the "Manage Booths" tab in `AdminView`*

```
[ docs/screenshots/03_admin_booths.png ]
```

---

### Operator Panel — Process Payment
> 📌 *Place screenshot here — capture `OperatorView` with a plate number searched, vehicle info populated, and amount shown*

```
[ docs/screenshots/04_operator_payment.png ]
```

---

### Supervisor Dashboard — Transaction Report
> 📌 *Place screenshot here — capture the transaction table with total revenue displayed*

```
[ docs/screenshots/05_supervisor_report.png ]
```

---

### Owner Portal — My Vehicles & History
> 📌 *Place screenshot here — capture `OwnerView` showing vehicle list and payment history tabs*

```
[ docs/screenshots/06_owner_portal.png ]
```

---

## 🗄️ Database Schema

The system uses a MySQL database named `toll_management`. See the full setup script in [`docs/schema.sql`](docs/schema.sql).

### Entity Relationship Overview

```
users (user_id, username, password, full_name, role, created_at)
  │
  ├──< vehicles (vehicle_id, plate_number, vehicle_type, owner_id→users, registered_at)
  │         │
  │         └──< transactions (transaction_id, vehicle_id→vehicles,
  │                             booth_id→toll_booths, amount_paid,
  │                             payment_date, processed_by→users)
  │
  └──< toll_booths (booth_id, booth_name, location, assigned_operator→users)

toll_rates (rate_id, vehicle_type, amount)
```

### Roles

| Role | Access Level |
|---|---|
| `admin` | Full system access — manage users and booths |
| `supervisor` | View all transactions and revenue reports |
| `operator` | Process toll payments at assigned booths |
| `owner` | View own vehicles and payment history |

---

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher (JDK)
- MySQL 8.0
- Eclipse IDE (recommended) or any Java IDE
- MySQL JDBC Driver (`mysql-connector-java-8.x.x.jar`)

### 1. Clone the Repository

```bash
git clone https://github.com/Collomut/TollManagementSystem.git
cd TollManagementSystem
```

### 2. Set Up the Database

Open MySQL and run the schema script:

```bash
mysql -u root -p < docs/schema.sql
```

Or paste the contents of [`docs/schema.sql`](docs/schema.sql) into MySQL Workbench and execute.

### 3. Configure the Database Connection

Open `src/database/DBConnection.java` and update the credentials to match your local MySQL setup:

```java
private static final String URL      = "jdbc:mysql://localhost:3306/toll_management";
private static final String USER     = "root";
private static final String PASSWORD = "your_password_here";
```

### 4. Add the JDBC Driver

In Eclipse:
1. Right-click the project → **Build Path** → **Configure Build Path**
2. Under **Libraries**, click **Add External JARs**
3. Select your `mysql-connector-java-x.x.x.jar`

### 5. Run the Application

- To start the **Socket Server** first (optional, for real-time features):
  Right-click `TollServer.java` → **Run As** → **Java Application**

- To launch the **main app**:
  Right-click `LoginView.java` → **Run As** → **Java Application**

### Default Login Credentials

> ⚠️ Change these after first login.

| Role | Username | Password |
|---|---|---|
| Admin | `admin` | `admin123` |
| Operator | `operator1` | `op123` |
| Supervisor | `supervisor1` | `sup123` |
| Owner | `owner1` | `own123` |

*(Insert actual seed credentials from your `schema.sql` here)*

---

## 👥 User Roles

### 🔧 Admin
- Create, update, and delete system users
- Assign operators to toll booths
- Manage booth locations

### 📋 Supervisor
- View all toll transactions across all booths
- Monitor total revenue collected

### 🛂 Operator
- Search vehicles by plate number
- Auto-fetch toll rate based on vehicle type
- Record toll payment and broadcast event to server

### 🚗 Owner
- Register vehicles under their account
- View personal vehicle list
- Review toll payment history for their vehicles

---

## 🔌 Socket Communication

The system includes a basic TCP socket layer for real-time event broadcasting.

```
  Operator pays toll
       │
       ▼
  TollClient.sendMessage("TOLL_PAID: KAA 123X | Booth A | 500 RWF")
       │
       ▼
  TollServer (port 9999) receives message
       │
       ▼
  ClientHandler.broadcast() → all connected clients notified
```

**Starting the server:**
```bash
# Run TollServer.java before launching client views
# Server listens on port 9999 by default
```

---

## 📁 Project Structure

```
TollManagementSystem/
├── tollManagement_System/
│   └── src/
│       ├── controller/
│       │   ├── AuthController.java        # Login logic
│       │   ├── BoothController.java       # Booth CRUD
│       │   ├── TransactionController.java # Payments & revenue
│       │   ├── UserController.java        # User CRUD
│       │   └── VehicleController.java     # Vehicle CRUD
│       ├── database/
│       │   └── DBConnection.java          # Singleton DB connection
│       ├── model/
│       │   ├── TollBooth.java
│       │   ├── TollRate.java
│       │   ├── Transaction.java
│       │   ├── User.java
│       │   └── Vehicle.java
│       ├── socket/
│       │   ├── ClientHandler.java         # Per-client thread
│       │   ├── TollClient.java            # Client-side socket
│       │   └── TollServer.java            # Broadcast server
│       └── view/
│           ├── AdminView.java
│           ├── LoginView.java
│           ├── OperatorView.java
│           ├── OwnerView.java
│           └── SupervisorView.java
├── docs/
│   ├── schema.sql                         # Database setup script
│   ├── screenshots/                       # UI screenshots
│   └── ARCHITECTURE.md                   # Deep-dive design doc
└── README.md
```

---

## ⚠️ Known Issues & Future Work

### Known Issues

| Issue | Location | Severity |
|---|---|---|
| Passwords stored in plain text | `AuthController`, `UserController` | 🔴 High |
| Single shared DB connection (not thread-safe) | `DBConnection.java` | 🟠 Medium |
| No input validation before SQL execution | All controllers | 🟠 Medium |
| DB credentials hardcoded in source | `DBConnection.java` | 🟠 Medium |

### Suggested Improvements

- [ ] Hash passwords with BCrypt before storage
- [ ] Use a connection pool (e.g., HikariCP) instead of a single static connection
- [ ] Move DB credentials to a `.env` or `config.properties` file
- [ ] Add input validation and error dialogs in all views
- [ ] Generate printable PDF receipts for toll transactions
- [ ] Add date-range filtering to transaction reports

---

## 👨‍💻 Team

| Name | Role |
|---|---|
| *(Team Member 1)* | *(e.g., Backend / DB Design)* |
| *(Team Member 2)* | *(e.g., UI / Frontend)* |
| *(Team Member 3)* | *(e.g., Socket Layer / Testing)* |

> 📌 *Fill in your actual team names and roles above.*

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).

---

<div align="center">

*Built as part of a Software Project Management course assignment.*

</div>
