## Project Description

**TechStore E-commerce** is a full-stack online store for buying technology products such as laptops, phones and accessories. Customers can browse the catalogue, add items to a cart, check out and track their orders, while the backend keeps product stock, orders, payments and deliveries organised and consistent.

The system has two parts that work together:

- **Backend:** a Spring Boot REST API that holds the business logic and stores data in a MySQL database (`ecommerce_db`).
- **Frontend:** a React web app (branded **TechVault**) that customers use in the browser and that talks to the backend through the `/api` endpoints.

### What the system does

- **Product catalogue:** view all products, search by name, filter by category, see product details and check which items are in stock.
- **Customer accounts:** register as a customer and log in, with each customer's details stored securely in their own records.
- **Shopping cart:** add products, change quantities and keep the cart saved between visits.
- **Checkout and orders:** place an order with a shipping address. The system checks the customer and the products, calculates the order total and reduces the stock automatically.
- **Order tracking:** every order moves through the statuses `PENDING`, `CONFIRMED`, `SHIPPED`, `DELIVERED` or `CANCELLED`, and customers can view their order history.
- **Order cancellation:** an order that is still cancellable can be cancelled, and the stock for its items is returned to the shop.
- **Payments and delivery:** each order can have payment details (method and transaction reference) and delivery details (carrier and tracking number).
- **Ratings:** customers can rate and review products.
- **Pricing rules:** prices include 15% South African VAT, and orders of R500 or more qualify for free delivery.

### Main entities

The system is built around 11 domain classes: `Customer`, `Registration`, `Login`, `ShippingAddress`, `ShoppingCart`, `Products`, `Order`, `OrderItem`, `PaymentDetails`, `Delivery` and `Rating`. A customer places many orders, and each order is made up of one or more order items that refer to products.

### Technologies used

| Layer | Technology |
|-------|------------|
| Backend | Java 21, Spring Boot 3.3, Spring Data JPA, Bean Validation |
| Database | MySQL |
| Frontend | React 19, Vite, React Router, Axios, Tailwind CSS |
| Testing | Spring Boot Test (JUnit) for factories, repositories, services and controllers |

### Architecture

The backend follows a layered design: **Controller** (REST endpoints) → **Service** (business rules) → **Repository** (database access) → **Domain** (entities). Objects are created through **factory** classes, and shared logic such as pricing and order formatting lives in **util** classes.

### Running the project

1. Start MySQL. The `ecommerce_db` database is created automatically on first run.
2. Start the backend (runs at `http://localhost:8080`):
   ```bash
   ./mvnw spring-boot:run
   ```
3. Start the frontend (runs at `http://localhost:3000`):
   ```bash
   cd frontend
   npm install
   npm run dev
   ```
