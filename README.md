# Support Desk System

A full-stack, production-quality customer support platform built with **Spring Boot 3**, **React 18 + Vite**, and **MySQL**. It enables support teams to manage incoming support tickets, collaborate via public replies and internal notes, track chronological audit trails, filter and sort tickets with deterministic logic, and monitor operational health through live, database-backed dashboard metrics.

---

## 1. Project Overview

Support Desk System provides support agents with a centralized, responsive workspace to:
- **Triage and track tickets** through distinct lifecycle stages (`Open`, `In Progress`, `Waiting on Customer`, `Resolved`).
- **Prioritize critical customer issues** using structured priority levels (`Low`, `Medium`, `High`, `Urgent`).
- **Communicate effectively** using both customer-facing **Public Replies** and staff-restricted **Internal Notes**.
- **Audit changes** automatically via chronological **Activity History** logs for all creations, reassignments, priority changes, status transitions, and conversation events.
- **Enforce SLA rules** by tracking **Overdue** tickets (> 24 hours old without resolution) and ensuring resolved tickets are immediately disqualified from open and overdue counts.
- **Search, filter, and sort** across subjects, customer identifiers, assignees, priorities, and timestamps.

---

## 2. Features

- **Ticket Lifecycle Management**:
  - Full CRUD lifecycle with server-side and client-side validation.
  - Required fields: Subject, Customer identifier (Name or Email), Description.
  - Sensible defaults: Status = `Open`, Priority = `Medium`, Assignee = `Unassigned`.
- **Chronological Conversation Feed**:
  - **Public Replies**: Distinct styling, visible to customers, timestamped with agent attribution.
  - **Internal Notes**: Distinct amber warning styling with lock icon and staff-only banner.
  - Live preview and validation preventing empty messages.
- **Automated Activity History**:
  - Every event is recorded to the audit log: ticket creation, assignment changes (with from/to attribution), status changes, priority shifts, and conversation updates.
- **Search, Multi-Filter & Deterministic Sorting**:
  - Global case-insensitive search across Subject, Customer Name, and Customer Email.
  - Multi-filtering: Status (`Open`, `In Progress`, `Waiting on Customer`, `Resolved`), Priority (`Low`, `Medium`, `High`, `Urgent`), and Assignee (specific agents or unassigned).
  - Deterministic sorting by `Most Recently Updated`, `Priority` (`Urgent` > `High` > `Medium` > `Low`), and `Newest Created`.
- **Live Database-Backed Dashboard**:
  - 6 Real-time KPI cards: **Total Tickets**, **Open Tickets**, **In Progress**, **Resolved**, **Urgent**, and **Overdue**.
  - Direct one-click drill-down from metric cards into pre-filtered ticket tables.
  - All figures calculated dynamically from MySQL queries—zero hardcoded mock statistics.
- **Overdue & Resolution Business Logic**:
  - Strict rule: A ticket is overdue if and only if `status != RESOLVED` and `createdAt > 24 hours ago`.
  - Resolved tickets are immediately excluded from Overdue and Open ticket totals.
- **Persistent Relational Database**:
  - Persisted in MySQL across restarts: Tickets, Agents, Ticket Entries, and Activity History.
- **Seeded Demo Data**:
  - Populates realistic demo support agents and diverse tickets covering all priority and status spectrums.
  - Includes pre-seeded overdue tickets and historical resolved tickets for immediate evaluation.

---

## 3. Tech Stack

### Frontend
- **React 18** & **Vite 5** (Fast ES Modules & HMR)
- **React Router 6** (Client-side routing)
- **Axios** (REST API communication with timeout handling)
- **Lucide React** (Consistent icon library)
- **Modern CSS** (Responsive layout, typography, cards, badges, modal forms)

### Backend
- **Java 21 / Java 24**
- **Spring Boot 3.3.5**
  - `spring-boot-starter-web` (RESTful Controllers & JSON handling)
  - `spring-boot-starter-data-jpa` (Hibernate 6 ORM & Repository layer)
  - `spring-boot-starter-validation` (Hibernate Validator & Bean Validation)
  - `mysql-connector-j` (MySQL JDBC Driver)
  - `spring-boot-starter-test` (JUnit 5 & Mockito)

### Database
- **MySQL 8.0 Community Server** (`support_desk_db`)

---

## 4. Architecture

```
+-------------------------------------------------------------+
|                     React Frontend (Vite)                   |
|  - Dashboard      - Tickets List      - Ticket Details     |
|  - Multi-Filter   - Quick Edit Panel  - Conversation Feed  |
+------------------------------+------------------------------+
                               |
                   HTTP / REST (JSON) on Port 5173 / 8080
                               |
+------------------------------v------------------------------+
|                   Spring Boot Backend (Port 8080)           |
|  Controllers  ->  Services  ->  Repositories (Spring Data)  |
|  - TicketController       - TicketService                   |
|  - AgentController        - AgentService                    |
|  - DashboardController    - GlobalExceptionHandler          |
+------------------------------+------------------------------+
                               |
                      JPA / Hibernate ORM
                               |
+------------------------------v------------------------------+
|                         MySQL Database                      |
|  - agents            - tickets                              |
|  - ticket_entries    - activity_history                     |
+-------------------------------------------------------------+
```

---

## 5. Database Schema & Entities

### 1. `agents`
- `id` (BIGINT, Primary Key, AUTO_INCREMENT)
- `name` (VARCHAR(100), NOT NULL)
- `email` (VARCHAR(150), NOT NULL, UNIQUE)

### 2. `tickets`
- `id` (BIGINT, Primary Key, AUTO_INCREMENT)
- `subject` (VARCHAR(255), NOT NULL)
- `customer_name` (VARCHAR(150))
- `customer_email` (VARCHAR(150))
- `description` (TEXT, NOT NULL)
- `status` (VARCHAR(30), NOT NULL: `OPEN`, `IN_PROGRESS`, `WAITING_ON_CUSTOMER`, `RESOLVED`)
- `priority` (VARCHAR(30), NOT NULL: `LOW`, `MEDIUM`, `HIGH`, `URGENT`)
- `assigned_agent_id` (BIGINT, Foreign Key referencing `agents(id)`, NULLable)
- `created_at` (DATETIME, NOT NULL, non-updatable)
- `updated_at` (DATETIME, NOT NULL)

### 3. `ticket_entries`
- `id` (BIGINT, Primary Key, AUTO_INCREMENT)
- `ticket_id` (BIGINT, Foreign Key referencing `tickets(id)`, NOT NULL)
- `type` (VARCHAR(30), NOT NULL: `PUBLIC_REPLY`, `INTERNAL_NOTE`)
- `content` (TEXT, NOT NULL)
- `author` (VARCHAR(100), NOT NULL)
- `created_at` (DATETIME, NOT NULL)

### 4. `activity_history`
- `id` (BIGINT, Primary Key, AUTO_INCREMENT)
- `ticket_id` (BIGINT, Foreign Key referencing `tickets(id)`, NOT NULL)
- `event_type` (VARCHAR(50), NOT NULL: `TICKET_CREATED`, `ASSIGNMENT_CHANGED`, `STATUS_CHANGED`, `PRIORITY_CHANGED`, `PUBLIC_REPLY_ADDED`, `INTERNAL_NOTE_ADDED`, `TICKET_UPDATED`)
- `description` (VARCHAR(500), NOT NULL)
- `author` (VARCHAR(100), NOT NULL)
- `created_at` (DATETIME, NOT NULL)

---

## 6. REST API Endpoints

| HTTP Method | Endpoint | Description |
|:---|:---|:---|
| `GET` | `/api/dashboard` | Get real-time database counts (Total, Open, In Progress, Resolved, Urgent, Overdue) |
| `GET` | `/api/agents` | List all support agents |
| `POST` | `/api/agents` | Create a new agent |
| `GET` | `/api/tickets` | Query tickets (`search`, `status`, `priority`, `assigneeId`, `sortBy`, `sortDir`) |
| `POST` | `/api/tickets` | Create a new ticket (validates subject, customer, description) |
| `GET` | `/api/tickets/{id}` | Get ticket detail with conversation entries and activity history |
| `PUT` | `/api/tickets/{id}` | Update ticket (status, priority, assignee, details) and log changes |
| `POST` | `/api/tickets/{id}/replies` | Add a public customer-visible reply |
| `POST` | `/api/tickets/{id}/notes` | Add an internal staff-only note |
| `GET` | `/api/tickets/{id}/entries` | Get conversation thread entries |
| `GET` | `/api/tickets/{id}/history` | Get ticket activity history audit trail |

---

## 7. Setup & Run Instructions

### Prerequisites
- **Java 21 or Java 24** installed (`java -version`)
- **Maven 3.8+** installed (`mvn -v`)
- **Node.js 18+** & **npm** installed (`node -v`, `npm -v`)
- **MySQL Server 8.0** running on `localhost:3306`

### 1. Database Setup
Create the MySQL database if not already created:
```sql
CREATE DATABASE IF NOT EXISTS support_desk_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. Configure Environment Variables
You can specify credentials in your environment or rely on the defaults in `application.properties`:
```bash
# Example environment variables
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=support_desk_db
export DB_USERNAME=root
export DB_PASSWORD=your_password_here
```
Refer to `.env.example` for details.

### 3. Run Backend (Spring Boot)
From the project root:
```bash
cd backend
mvn clean compile
mvn spring-boot:run
```
The backend starts on `http://localhost:8080`. On first run, it seeds initial agents and tickets automatically.

To run the unit tests:
```bash
mvn test
```

### 4. Run Frontend (React + Vite)
From the project root:
```bash
cd frontend
npm install
npm run dev
```
Open `http://localhost:5173` in your browser.

---

## 8. Demo Seed Data

When the database is empty, `DataSeeder` automatically populates:
- **4 Support Agents**:
  - `Abhijat Sharma` (abhijat.sharma@supportdesk.internal)
  - `Amit Patel` (amit.patel@supportdesk.internal)
  - `Sarah Connor` (sarah.connor@supportdesk.internal)
  - `Priya Singh` (priya.singh@supportdesk.internal)
- **8 Diverse Tickets**:
  1. *Payment gateway checkout failing on card submit* — Urgent, Open, Overdue (created 36h ago, assigned to Abhijat Sharma, with replies & notes).
  2. *Cannot export monthly billing invoice to PDF* — High, In Progress, Overdue (created 28h ago, assigned to Amit Patel).
  3. *SSO login timeout when authenticating via Okta* — Urgent, In Progress (created 6h ago, not overdue, assigned to Sarah Connor).
  4. *Feature Request: Add dark mode theme option* — Low, Open, Unassigned (created 2h ago).
  5. *Need assistance updating registered company address* — Medium, Waiting on Customer (created 14h ago, assigned to Priya Singh).
  6. *Database connection spikes during midnight cron sync* — High, Resolved (created 48h ago; proves resolved tickets > 24h are **NOT** overdue).
  7. *Webhook notifications missing signature headers* — Medium, Resolved (created 10h ago).
  8. *Password reset link throws 404 error* — Urgent, Open, Overdue (created 30h ago, unassigned).

---

## 9. Business Rules Implementation

### Overdue Tickets Rule
- **Definition**: A ticket is overdue if `status != TicketStatus.RESOLVED` AND `createdAt < (now - 24 hours)`.
- **Exclusion**: Resolved tickets are **NEVER** overdue, regardless of how long ago they were created.
- **Dynamic Calculation**: Computed in SQL queries and Java entity logic (`ticket.isOverdue()`).

### Resolution Transition Rule
- When a ticket's status is changed to `RESOLVED`:
  - It immediately stops counting as `Open`, `In Progress`, or `Overdue`.
  - The dashboard counts immediately decrement the open and overdue numbers and increment resolved tickets.
  - An activity history event is recorded: `Status changed from <Previous> to Resolved`.

### Assignment & Audit Rule
- When an agent is assigned or changed:
  - If reassigned: `Assigned from <Old Agent> to <New Agent>`.
  - If newly assigned: `Assigned to <New Agent>`.
  - If unassigned: `Unassigned from <Old Agent>`.
  - Recorded with the actor's name and accurate UTC timestamps.

---

## 10. Verification & What Works

The complete solution has been tested and verified end-to-end:
- [x] Backend compiles and passes all JUnit 5 tests.
- [x] Frontend builds cleanly with Vite production bundler (`npm run build`).
- [x] Backend connects to real MySQL database with automatic table creation/updating.
- [x] Ticket creation with frontend and backend input validation (missing customer identifier, missing subject, missing description).
- [x] Ticket listing with real-time status and priority badges.
- [x] Global search by subject, customer name, and customer email.
- [x] Multi-filtering (Status + Priority + Assignee) working in combination.
- [x] Sorting by recently updated, priority rank (Urgent > High > Medium > Low), and creation date.
- [x] Ticket details page with description, customer metadata, and quick edit controls.
- [x] Public replies persisted and formatted as customer-facing messages.
- [x] Internal notes persisted and formatted with distinct staff warning styles and lock icons.
- [x] Chronological conversation feed combining replies and notes.
- [x] Chronological activity history audit log with actor attribution.
- [x] Live dashboard with 6 database-calculated metrics and drill-down links.
- [x] Overdue logic verified: unresolved > 24h is overdue; resolved > 24h is not overdue.
- [x] Resolving an overdue ticket immediately removes it from overdue metrics.
- [x] Restarting backend retains all tickets, entries, and activity history in MySQL.
- [x] Zero mock or hardcoded ticket counts.

---

## 11. Known Issues & Limitations

- The system currently assumes a local network deployment; if deploying across domains, configure the allowed origins in `WebConfig.java` to match your production domain.
- File attachment uploads were intentionally omitted to maintain focus on the core assessment scope within the time limit.

---

## 12. AI Usage Disclosure

AI tooling (Antigravity with Google DeepMind agentic capabilities) was utilized for pair programming, scaffolding project architecture, validating Spring Data JPA specifications, generating mock seed data, and authoring unit tests. All generated code was compiled, reviewed, verified against the assessment specification, manually tested via live HTTP REST requests, and validated against a running MySQL database instance.

