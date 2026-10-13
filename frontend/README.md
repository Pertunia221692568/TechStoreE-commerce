# Nexora – React Frontend for TechStore E-commerce

Premium React frontend for the Spring Boot TechStore backend (ADP372S).

**Brand name:** TechVault

## Features

- Modern UI  layout (search bar, product grid, sticky cart) with a unique indigo + cyan colour palette
- Product catalogue with search & category filters
- Product detail pages with quantity selector
- Persistent shopping cart (localStorage)
- Customer registration & simple email-based login
- Checkout → places order via POST /api/orders
- Order history with cancel support
- Fully responsive (mobile-first)
- Loading states, empty states, and clear error messages when backend is offline

## Tech stack

- React 19 + Vite
- React Router v7
- Axios
- Tailwind CSS 3
- Context API (Cart + Auth)

---



### 1. Backend must be running first

```bash
# In your Spring Boot project (IntelliJ or terminal)
./mvnw spring-boot:run
# → http://localhost:8080
```

MySQL must be up (ecommerce_db, user/password as in application.properties).

### 2. Frontend

```bash
cd tech-frontend          # or wherever you extracted the frontend
npm install
npm run dev
# → http://localhost:3000
```

Open http://localhost:3000 in your browser.

Vite proxies /api/* to http://localhost:8080, so CORS works with your existing CorsConfig.

---

## Put frontend inside the same IntelliJ project

Recommended structure:

```
TechStoreE-commerce/          ← your existing Spring Boot root
├── pom.xml
├── src/
│   └── main/java/...
├── frontend/                 ← put the React app HERE
│   ├── package.json
│   ├── vite.config.js
│   ├── index.html
│   ├── public/
│   └── src/
│       ├── components/
│       ├── context/
│       ├── pages/
│       ├── services/
│       └── ...
└── ...
```

### Steps in IntelliJ

1. Open your TechStoreE-commerce project in IntelliJ as usual.
2. Right-click the project root → New → Directory → name it frontend.
3. Copy all contents of tech-frontend into that frontend folder
   (or unzip Nexora-Frontend.zip into frontend).
4. Open a terminal in IntelliJ (View → Tool Windows → Terminal) and run:

```bash
cd frontend
npm install
npm run dev
```

5. You do NOT need to add the frontend to pom.xml.
   Keep building/running Spring Boot with Maven as normal.
   Run the React app separately with npm run dev.

### Run both from IntelliJ

- Spring Boot: green Run button on TechStoreApplication.
- Frontend: Terminal → cd frontend && npm run dev.

You will have:
- Backend  → http://localhost:8080
- Frontend → http://localhost:3000

---

## Colour system

- Brand: Indigo (#4c6ef5 → #364fc7)
- Accent: Cyan (#06b6d4)
- Surfaces: Slate neutrals

Distinct from Amazon (orange) and Takealot (blue).

## Authors

Built for CPUT ADP372S – TechStore E-commerce project.
