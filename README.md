# 🏆 Titan Manager: Java DSA Sports Engine

Titan Manager is a comprehensive tournament management system that integrates **Java Development**, **MySQL Database**, and **Data Structures & Algorithms (DSA)** to handle sports logistics for Basketball, Football, Table-tennis, and Badminton.



## 📌 Project Overview
This project transitions a traditional Web/Node.js stack into a high-performance Java Terminal Engine. It focuses on efficient data handling, bracket progression, and real-time "Running Time" analysis for competitive sports management.

## 🚀 Core Functionalities
1.  **Authentication:** Secure Login and Signup system using JDBC and SQL validation.
2.  **Sport Hubs:** Dedicated modules for 4 major games with nested action menus.
3.  **Live Updates:** Ability to create tournaments and update scores with automatic bracket progression.
4.  **Analytics:** In-depth module to test Search and Sort algorithm speeds.

---

## 🛠️ Data Structures & Algorithms (Applied)

The following DSA concepts are strictly implemented within the `TitanManagerDSA.java` file:

| DSA Topic | Implementation Detail | Complexity |
| :--- | :--- | :--- |
| **Circular Linked List** | Used to navigate between the 4 Sport Hubs (Infinite Carousel). | $O(1)$ |
| **Singly Linked List** | The core `List ADT` for storing all match events in memory. | $O(n)$ |
| **Stack ADT** | Tracks user navigation history (Breadcrumbs) for "Back" functionality. | $O(1)$ |
| **QuickSort** | Organizes match schedules based on their `matchLevel` (Priority). | $O(n \log n)$ |
| **Hashing** | Uses a Hash Table (Separate Chaining) for instant event lookups. | $O(1)$ |
| **Linear Search** | Used for filtering matches by specific sport names. | $O(n)$ |



---

## 📂 Database Configuration (MySQL)

Ensure your MySQL server is running and the following schema is applied to `TitanManagerDB`:

```sql
-- User Identity Table
CREATE TABLE Login (
    id INT AUTO_INCREMENT PRIMARY KEY,
    firstName VARCHAR(100),
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255)
);

-- Tournament Event Table
CREATE TABLE event (
    id INT AUTO_INCREMENT PRIMARY KEY,
    eventId VARCHAR(20),
    teamA VARCHAR(255),
    teamB VARCHAR(255),
    scoreA INT DEFAULT 0,
    scoreB INT DEFAULT 0,
    sport VARCHAR(100),
    status VARCHAR(20) DEFAULT 'Upcoming',
    matchLevel INT DEFAULT 4 -- 1:Finals, 2:Semis, 4:Quarters
);
