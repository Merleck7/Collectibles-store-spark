# 🧩 Collectibles Store API — Sprint 1

This project implements the first stage of a collectible store website using **Java** and the **Spark Framework**. It provides a simple REST API to manage users, with JSON responses and basic logging.

## 🚀 Features

* Full CRUD operations for users (Create, Read, Update, Delete)
* JSON serialization and deserialization using Gson
* Logging support with Logback for monitoring server activity
* Lightweight in-memory data storage for rapid testing

## ⚙️ Requirements

* Java 17+
* Maven 3.8+
* Internet connection for dependency download

## 📂 Project Structure

```
collectibles-store-spark/
│
├── pom.xml          # Maven project configuration
├── README.md        # Project documentation
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── collectibles/
│                   ├── Main.java  # Main application and API routes
│                   └── User.java  # User model
└── .gitignore       # Files/folders to ignore in git
```

## ▶️ Run the Project

Build and run the project using Maven:

```bash
mvn clean package
java -cp target/collectibles-store-1.0.0-jar-with-dependencies.jar com.collectibles.Main
```

The server will start at `http://localhost:4567/`.

## 📡 API Routes

| Method  | Route        | Description                           |
| :------ | :----------- | :------------------------------------ |
| GET     | `/users`     | Retrieve all users                    |
| GET     | `/users/:id` | Retrieve one user by ID               |
| POST    | `/users/:id` | Create a new user with a specified ID |
| PUT     | `/users/:id` | Update an existing user               |
| OPTIONS | `/users/:id` | Check if a user exists                |
| DELETE  | `/users/:id` | Delete a user                         |

### Note

Initially, the API has no users. You can add users using the POST route before retrieving them.

## 🧠 Authors

Developed by **Luis Carlos Mendoza Romero** & **Emilio Flores Licea** - Digital NAO Challenge — Sprint 1.
