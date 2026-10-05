# Shopify Store – Java Full Stack E-Commerce Application

A full stack online fashion store. The customer-facing UI (HTML, CSS, JavaScript, Bootstrap) talks to a
**Spring Boot REST API** backed by **MySQL** through **Spring Data JPA / Hibernate**.
Authentication uses **JWT** with **Spring Security** and role-based access (USER / ADMIN).

## Features

**Customer**
- Browse products, filter by category, search by name (data comes from the database)
- Shopping cart (kept in the browser until checkout)
- Register / login (passwords hashed with BCrypt, JWT token auth)
- Place orders (server checks stock, reduces stock and calculates the total)
- "My Orders" page with order status
- Contact form (saved to the database)

**Admin** (`admin@shopify.com`)
- Dashboard: total products, orders, customers, revenue
- Add / edit / delete products
- View all orders and update their status (Placed → Shipped → Delivered / Cancelled)
- Read customer contact messages

## Tech stack

| Layer      | Technology |
|------------|------------|
| Frontend   | HTML5, CSS3, JavaScript (fetch API), Bootstrap 4 |
| Backend    | Java 17, Spring Boot 3, Spring MVC (REST), Spring Security, Bean Validation |
| Persistence| Spring Data JPA, Hibernate, MySQL (H2 profile for quick demo) |
| Security   | JWT (jjwt), BCrypt, role-based authorization |
| Build/Tools| Maven, Git/GitHub, Postman, Eclipse / STS / IntelliJ |

## Architecture

```
Browser (HTML/CSS/JS)
      │  fetch() + JSON + "Authorization: Bearer <jwt>"
      ▼
Controller  (REST endpoints, @Valid request validation)
      ▼
Service     (business logic, @Transactional)
      ▼
Repository  (Spring Data JPA)
      ▼
MySQL       users · categories · products · orders · order_items · contact_messages
```

Database relationships: `Category 1—* Product`, `User 1—* Order`, `Order 1—* OrderItem`, `Product 1—* OrderItem`.

## How to run

### Prerequisites
JDK 17 or newer, Maven (or use the one built into Eclipse/STS/IntelliJ), and MySQL (optional – see Option B).

### Option A – with MySQL (default)
1. Start MySQL. The database `shopify_store` is created automatically.
2. Open `src/main/resources/application.properties` and set your MySQL username and password
   (or set environment variables `DB_USERNAME` and `DB_PASSWORD`).
3. Run the app (see "Run" below) and open **http://localhost:8080**

### Option B – no MySQL (in-memory H2, good for a quick demo)
Run with the `h2` profile:
- Command line: `mvn spring-boot:run -Dspring-boot.run.profiles=h2`
- Eclipse/STS: Run Configurations → Arguments → VM arguments → `-Dspring.profiles.active=h2`

### Run
- **Eclipse / STS:** File → Import → *Existing Maven Projects* → select this folder →
  right-click `ShopifyStoreApplication.java` → Run As → *Java Application*
- **IntelliJ:** Open the folder as a Maven project → run `ShopifyStoreApplication`
- **Command line:** `mvn spring-boot:run`

Sample products and the admin account are created automatically on first start.

| Role  | Email               | Password   |
|-------|---------------------|------------|
| Admin | admin@shopify.com   | Admin@123  |

(Change these in `application.properties` and use a strong `app.jwt.secret` before deploying anywhere public.)

## REST API

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/api/auth/register` | Public | Create account, returns JWT |
| POST | `/api/auth/login` | Public | Login, returns JWT |
| GET | `/api/products?category=&q=` | Public | List / filter / search products |
| GET | `/api/products/{id}` | Public | Product details |
| GET | `/api/categories` | Public | Category names |
| POST | `/api/contact` | Public | Save a contact message |
| POST | `/api/orders` | Logged in | Place an order |
| GET | `/api/orders/my` | Logged in | My orders |
| POST / PUT / DELETE | `/api/products[/{id}]` | Admin | Manage products |
| GET | `/api/admin/orders` | Admin | All orders |
| PATCH | `/api/admin/orders/{id}/status` | Admin | Update order status |
| GET | `/api/admin/messages` | Admin | Contact messages |
| GET | `/api/admin/stats` | Admin | Dashboard numbers |

Try it in Postman:
```
POST http://localhost:8080/api/auth/login
{ "email": "admin@shopify.com", "password": "Admin@123" }
→ copy the "token" and send it as   Authorization: Bearer <token>   on protected endpoints
```

## Project structure

```
src/main/java/com/shopifystore
├── ShopifyStoreApplication.java
├── config/       SecurityConfig, DataSeeder
├── security/     JwtService, JwtAuthFilter
├── controller/   Auth, Product, Category, Order, Contact, Admin
├── service/      AuthService, ProductService, OrderService
├── repository/   Spring Data JPA repositories
├── entity/       User, Product, Category, Order, OrderItem, ContactMessage
├── dto/          request / response records
└── exception/    GlobalExceptionHandler + custom exceptions
src/main/resources
├── application.properties / application-h2.properties
└── static/       index.html, cart.html, login.html, orders.html, admin.html, ContactUs.html, css/, js/, images/
```

## Resume entry (copy / edit)

**Shopify Store – Java Full Stack E-Commerce Application**
*Java, Spring Boot, Spring Security (JWT), Spring Data JPA/Hibernate, MySQL, REST APIs, HTML, CSS, JavaScript, Bootstrap, Maven, Git*

- Built a full stack e-commerce web application with a layered Spring Boot backend (Controller → Service → Repository) and a responsive Bootstrap/JavaScript front end consuming REST APIs.
- Designed a relational MySQL schema (users, products, categories, orders, order items) with JPA/Hibernate entity mappings (one-to-many, many-to-one).
- Implemented JWT-based authentication and role-based authorization (USER/ADMIN) with Spring Security and BCrypt password hashing.
- Developed transactional order placement that validates stock, updates inventory and calculates totals on the server to prevent price tampering.
- Created an admin dashboard for product CRUD, order status management and sales statistics; added request validation and centralized exception handling.

## Interview talking points
- Why DTOs instead of returning entities directly (no lazy-loading/recursion issues, control over the JSON)?
- Why is the order total calculated on the server? (Never trust prices sent from the browser.)
- What does `@Transactional` do in `placeOrder`? (If one item is out of stock, nothing is saved.)
- How does the JWT filter work? (Reads the Bearer token → validates → sets the user in `SecurityContext`.)
- Why is `unitPrice` stored in `order_items`? (Old orders must keep the price at purchase time.)

## Ideas to extend it
Product image upload, pagination, payment gateway (Razorpay test mode), wishlist, order confirmation email,
Swagger/OpenAPI docs, unit tests with JUnit + Mockito, Docker, deployment (Render / AWS).
