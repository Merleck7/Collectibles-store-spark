# 🛒 Collectibles Store (Sprint 2)

## 🚀 Overview

**Collectibles Store** is a Spark Java web application that allows users to view collectible items and submit new offers through a web form.  
This version implements **exception handling**, **dynamic Mustache templates**, and **modern styling** with CSS.

---

## 🧩 Features

✅ Custom exception handling (`ApiException.java`)  
✅ Dynamic views using Mustache templates  
✅ Web form for submitting new collectible offers  
✅ Static file serving (CSS)  
✅ Two default items displayed on startup  
✅ Modular and easy to extend

---

## 🗂 Project Structure

```
src/
│ └── main/
│     ├── java/
│     │   └── com/collectibles/
│     │       ├── Main.java
│     │       ├── User.java
│     │       ├── Item.java
│     │       └── ApiException.java
│     │
│     └── resources/
│        ├── templates/
│        │   ├── index.mustache
│        │   └── offer_form.mustache
│        └── public/
│            └── style.css
├──  README.md
├──  pom.xml
├──  sameple_requests.http          
└── .gitignore 
```

---

## ⚙️ Setup & Run

### 1️⃣ Compile the project
```bash
mvn clean package
```

### 2️⃣ Run the app
```bash
java -cp target/collectibles-store-1.0.0-jar-with-dependencies.jar com.collectibles.Main
```

### 3️⃣ Access the web app
- 🏠 [http://localhost:4567/](http://localhost:4567/) → View collectibles  
- ➕ [http://localhost:4567/offer](http://localhost:4567/offer) → Add new item  

---

## 🧱 Default Data

| ID | Name | Price | Description |
|----|------|--------|-------------|
| 1 | Iron Man Figure | $49.99 | 6-inch collectible |
| 2 | Spider-Man Poster | $19.99 | Limited edition |

---

## 💡 Exception Handling

Custom API exceptions are handled through the `ApiException` class:

```java
exception(ApiException.class, (ex, req, res) -> {
    res.status(ex.getStatusCode());
    res.type("application/json");
    res.body(gson.toJson(Map.of("error", ex.getMessage())));
});
```

If a route or resource is not found, a JSON error is returned:
```json
{"error": "Route not found"}
```

---

## 🎨 Styling Preview

Includes a responsive design and card layout with gradients and shadows:

- Modern header with gradient
- Centered cards for items
- Form with rounded inputs and hover animations

---

## 🧠 Partial Review Checklist

✅ Exception handling tested with invalid routes  
✅ Form submissions validated  
✅ CSS loaded from `/public`  
✅ Peer review completed for logic and integration consistency  

---

## 💾 Commit Message Suggestion

```
✨ Sprint 2 Complete: Added views, styling, and exception handling
- Added Mustache templates for index and offer form
- Implemented static file serving for CSS
- Introduced ApiException module
- Created Item model and integrated form logic
- Enhanced UI with modern styling
```

---

## 👨‍💻 Authors
**Luis Mendoza** & **Emilio Flores Licea**   
[GitHub](https://github.com/Merleck7) • [LinkedIn](https://www.linkedin.com/in/luismendoza2007/)
