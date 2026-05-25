# 🏗️ Architecture & Design Documentation

**Toll Management System — Technical Reference**

---

## Table of Contents

- [Design Philosophy](#design-philosophy)
- [Layer Breakdown](#layer-breakdown)
- [Class Responsibilities](#class-responsibilities)
- [Data Flow Walkthroughs](#data-flow-walkthroughs)
- [Socket Communication Protocol](#socket-communication-protocol)
- [Database Design Decisions](#database-design-decisions)
- [Design Patterns Used](#design-patterns-used)

---

## Design Philosophy

The system was designed around three core principles:

1. **Separation of Concerns** — the View never queries the database directly; only Controllers do.
2. **Role-Based Access** — each user role gets its own dedicated View class with only the operations it needs.
3. **Simplicity over Abstraction** — no framework overhead; plain Java, JDBC, and Swing keep the codebase readable for academic review.

---

## Layer Breakdown

### View Layer (`src/view/`)

Each class is a `JFrame` that is instantiated after a successful login, based on the authenticated user's role.

| Class | Role it Serves | Main Tabs / Panels |
|---|---|---|
| `LoginView` | All (entry point) | Username + password form |
| `AdminView` | `admin` | Manage Users, Manage Booths |
| `SupervisorView` | `supervisor` | Transaction Report, Revenue Summary |
| `OperatorView` | `operator` | Process Payment, View History |
| `OwnerView` | `owner` | My Vehicles, My Payment History |

The login flow in `LoginView` uses `AuthController.login()` and dispatches to the correct View:

```java
switch (user.getRole()) {
    case "admin"      -> new AdminView(user);
    case "supervisor" -> new SupervisorView(user);
    case "operator"   -> new OperatorView(user);
    case "owner"      -> new OwnerView(user);
}
```

---

### Controller Layer (`src/controller/`)

Controllers contain all business logic and SQL. They accept plain Java model objects and return results — they have no knowledge of Swing.

| Controller | Responsibilities |
|---|---|
| `AuthController` | Authenticate users by username + password |
| `UserController` | CRUD operations on the `users` table |
| `BoothController` | CRUD operations on the `toll_booths` table |
| `VehicleController` | CRUD + plate lookup + owner-filtered queries |
| `TransactionController` | Record payments, fetch history, calculate revenue, look up rates |

---

### Model Layer (`src/model/`)

Plain Java objects (POJOs) that mirror database table rows. Each has a no-arg constructor, a convenience constructor, getters/setters, and a `toString()` for display in Swing tables and combo boxes.

| Model | Maps To Table |
|---|---|
| `User` | `users` |
| `TollBooth` | `toll_booths` |
| `TollRate` | `toll_rates` |
| `Vehicle` | `vehicles` |
| `Transaction` | `transactions` (with joined fields for display) |

`Transaction` carries extra display fields (`plateNumber`, `boothName`, `operatorName`) that are populated by JOIN queries — they have no columns of their own in the database.

---

### Database Layer (`src/database/`)

`DBConnection` is a **Singleton** that holds one static `Connection` to MySQL.

```java
// First call creates the connection
Connection conn = DBConnection.getConnection();

// Subsequent calls return the same connection if it's still open
```

> ⚠️ **Known Limitation:** A single shared connection is not thread-safe. When the socket server runs multiple `ClientHandler` threads that trigger controller calls simultaneously, race conditions on the connection are possible. A production fix would be a connection pool (e.g., HikariCP).

---

### Socket Layer (`src/socket/`)

A lightweight TCP broadcast server for real-time event notification.

| Class | Responsibility |
|---|---|
| `TollServer` | Listens on port 9999, accepts clients, manages a list of `PrintWriter` writers |
| `ClientHandler` | Runnable per-client thread; reads incoming messages and calls `broadcast()` |
| `TollClient` | Used inside `OperatorView`; sends messages and fires a `MessageListener` callback on receipt |

The `TollServer` must be running **before** any client view is launched if real-time features are needed. If the server is unavailable, `TollClient.connect()` catches the `IOException` and the app continues without the socket feature.

---

## Data Flow Walkthroughs

### 1. User Login

```
User types credentials in LoginView
  │
  └─► AuthController.login(username, password)
            │
            └─► SQL: SELECT * FROM users WHERE username=? AND password=?
                        │
                        ├── No match  → returns null → "Invalid credentials" dialog
                        │
                        └── Match     → returns User object
                                          │
                                          └─► LoginView dispatches to role-specific View
```

---

### 2. Operator Processes a Toll Payment

```
Operator enters plate number → clicks "Search Vehicle"
  │
  └─► VehicleController.getVehicleByPlate(plate)
            │
            ├── Not found → "Vehicle not registered" message
            │
            └── Found → Vehicle object returned
                          │
                          └─► TransactionController.getRateByVehicleType(vehicle.getType())
                                    │
                                    └─► SQL: SELECT amount FROM toll_rates WHERE vehicle_type=?
                                                │
                                                └─► Amount displayed in UI

Operator confirms → clicks "Process Payment"
  │
  └─► TransactionController.addTransaction(new Transaction(...))
            │
            └─► SQL: INSERT INTO transactions (vehicle_id, booth_id, amount_paid, processed_by)
                        │
                        └─► TollClient.sendMessage("TOLL_PAID: plate | booth | amount RWF")
                                  │
                                  └─► TollServer broadcasts to all connected clients
```

---

### 3. Admin Adds a New User

```
Admin fills form in AdminView → clicks "Add"
  │
  └─► UserController.addUser(new User(username, password, fullName, role))
            │
            └─► SQL: INSERT INTO users (username, password, full_name, role) VALUES (?,?,?,?)
                        │
                        └─► Table refreshed via loadUserTable()
```

---

## Socket Communication Protocol

Messages are plain-text strings sent over TCP. There is no formal protocol beyond newline-delimited messages.

**Example messages:**

```
TOLL_PAID: KAA 123X | Booth A | 500 RWF
TOLL_PAID: KBB 456Y | Booth B | 200 RWF
```

All connected `TollClient` instances receive these via their `MessageListener.onMessageReceived(String)` callback. In the current implementation, `OperatorView` logs them to the console — a future version could show a live notification panel.

---

## Database Design Decisions

| Decision | Rationale |
|---|---|
| `ENUM` for `users.role` | Enforces valid role values at the DB level |
| `DECIMAL(10,2)` for amounts | Avoids floating-point rounding errors for currency |
| `ON DELETE CASCADE` on `vehicles.owner_id` | Deleting a user removes their vehicles automatically |
| `ON DELETE RESTRICT` on `transactions` | Prevents orphaned payment records |
| `ON DELETE SET NULL` on `toll_booths.assigned_operator` | Booths remain if their operator is deleted |
| Separate `toll_rates` table | Rates can be updated centrally without touching vehicle records |

---

## Design Patterns Used

| Pattern | Where | How |
|---|---|---|
| **Singleton** | `DBConnection` | One static connection instance reused across all controllers |
| **MVC** | Entire application | View → Controller → Model/DB separation |
| **Observer (simplified)** | `TollClient.MessageListener` | Interface callback fires when server pushes a message |
| **DAO (implicit)** | All controllers | Each controller encapsulates all SQL for its entity |
