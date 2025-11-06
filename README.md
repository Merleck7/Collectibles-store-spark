# 🛒 Collectibles Store

## 🚀 Overview

**Collectibles Store** is a complete Spark Java web application that allows users to view collectible items, add new offers through a web form, and receive **real-time price updates** using **WebSockets**.  
This Sprint introduces advanced features like **filtering by name and price range**, **dynamic Mustache templates**, and a fully functional **.jar executable build**.

---

## 🧩 Features

✅ Dynamic item listing with Mustache templates  
✅ Form to submit new collectible offers  
✅ Item filtering by name, minimum and maximum price  
✅ Real-time updates with WebSocket (`/price-updates`)  
✅ Custom error handling for exceptions and missing routes  
✅ Static file serving (CSS, JS)  
✅ Fully packaged executable `.jar`  
✅ Modular and scalable project structure  

---

## 🗂 Project Structure

```
collectibles-store-spark/
│
├── 📂 src/
│   └── 📂 main/
│       ├── 📂 java/
│       │   └── 📂 com/
│       │       └── 📂 collectibles/
│       │           ├── 📂 filters/
│       │           │   └── 📄 ItemFilter.java
│       │           │
│       │           ├── 📂 models/
│       │           │   └── 📄 Item.java
│       │           │
│       │           ├── 📂 websocket/
│       │           │   └── 📄 PriceWebSocket.java
│       │           │
│       │           ├── 📄 ApiException.java
│       │           ├── 📄 Item.java
│       │           ├── 📄 Main.java
│       │           └── 📄 User.java
│       │
│       └── 📂 resources/
│           ├── 📂 public/
│           │   ├── 📜 price-updates.js
│           │   ├── 📜 prices.html
│           │   └── 🎨 style.css
│           │
│           └── 📂 templates/
│               ├── 🧩 index.mustache
│               └── 🧩 offer_form.mustache
│
├── 📂 target/
│
├── ⚙ .gitignore
├── 📦 pom.xml
└── 📝 README.md

```

---

## ⚙️ Setup & Run

### 1️⃣ Compile the project and generate the `.jar`
```bash
mvn clean compile assembly:single
```

### 2️⃣ Run the app
```bash
java -jar target/collectibles-store-1.0.0-jar-with-dependencies.jar
```

### 3️⃣ Access the web app
- 🏠 [http://localhost:4567/](http://localhost:4567/) → View collectibles  
- ➕ [http://localhost:4567/offer](http://localhost:4567/offer) → Add new item  
- ⚙️ [http://localhost:4567/test](http://localhost:4567/test) → Server test route  

---

## 🧱 Default Data

| ID | Name | Price | Description |
|----|------|--------|-------------|
| 1 | Iron Man Figure | $49.99 | 6-inch collectible |
| 2 | Spider-Man Poster | $19.99 | Limited edition |

---

## 🔍 Filtering API

You can filter collectibles using query parameters:

**Endpoint:**  
```
GET /filter?name=man&min=10&max=50
```

**Example Response:**
```json
[
  {
    "name": "Iron Man Figure",
    "price": 49.99,
    "description": "6-inch collectible"
  }
]
```

---

## 🔄 WebSocket: Real-Time Price Updates

The app integrates a **WebSocket server** to broadcast price updates to all connected clients.

**Endpoint:**  
```
ws://localhost:4567/price-updates
```

Whenever a price change or new offer is registered, all active clients receive the update instantly.

---

## 💡 Exception Handling

Handled globally using a custom exception class:

```java
exception(ApiException.class, (ex, req, res) -> {
    res.status(ex.getStatusCode());
    res.type("application/json");
    res.body(gson.toJson(Map.of("error", ex.getMessage())));
});
```

If a route or resource is not found:
```json
{"error": "Route not found"}
```

---

## 🎨 Styling & UI

Includes a modern responsive design:

- Gradient header  
- Centered item cards  
- Animated buttons and inputs  
- Responsive layout for mobile  
- Visual alerts for WebSocket notifications  

---

## 🧠 Final Sprint 3 Checklist

✅ Dynamic templates working with Mustache  
✅ Form submission for new collectibles  
✅ Price and name filtering via query params  
✅ WebSocket updates for connected users  
✅ Exception handling for all routes  
✅ `.jar` build tested and running  
✅ Code reviewed and documented  

---

## 👨‍💻 Authors
**Luis Mendoza** & **Emilio Flores Licea**  
[GitHub](https://github.com/Merleck7) • [LinkedIn](https://www.linkedin.com/in/luismendoza2007/)
  

---

## 👨‍💻 Authors
**Luis Mendoza** & **Emilio Flores Licea**  
[GitHub](https://github.com/Merleck7) • [LinkedIn](https://www.linkedin.com/in/luismendoza2007/)
