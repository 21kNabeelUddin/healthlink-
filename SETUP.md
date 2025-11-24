# HealthLink+ Setup Guide

This project consists of:
- **Backend**: Spring Boot (Java) application
- **Frontend**: Next.js (Node.js) application

## Prerequisites

### For Backend (Java/Spring Boot):
- **Java 21** (JDK 21) - [Download here](https://www.oracle.com/java/technologies/downloads/#java21)
- **Maven 3.6+** (or use the included Maven wrapper `mvnw`)
- **MySQL 8.0+** - [Download here](https://dev.mysql.com/downloads/mysql/)

### For Frontend (Next.js):
- **Node.js 18+** and npm - [Download here](https://nodejs.org/)

---

## Step 1: Setup MySQL Database

1. Install and start MySQL server
2. Create a database (or it will be auto-created):
   ```sql
   CREATE DATABASE IF NOT EXISTS healthlinkdb;
   ```
3. Update database credentials in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.username=root
   spring.datasource.password=YOUR_MYSQL_PASSWORD
   ```

---

## Step 2: Configure Backend Settings

Edit `src/main/resources/application.properties`:

1. **Update MySQL password** (line 7):
   ```properties
   spring.datasource.password=YOUR_MYSQL_PASSWORD
   ```

2. **Update Email Configuration** (for OTP emails):
   ```properties
   spring.mail.username=YOUR_EMAIL@gmail.com
   spring.mail.password=YOUR_APP_PASSWORD
   ```
   > Note: For Gmail, you need to generate an "App Password" from your Google Account settings.

---

## Step 3: Run the Backend (Spring Boot)

### Option A: Using Maven Wrapper (Recommended - No Maven installation needed)

**Windows:**
```bash
.\mvnw.cmd spring-boot:run
```

**Linux/Mac:**
```bash
./mvnw spring-boot:run
```

### Option B: Using Maven (if installed)

```bash
mvn spring-boot:run
```

### Option C: Using IDE (IntelliJ IDEA / Eclipse / VS Code)

1. Open the project in your IDE
2. Wait for Maven dependencies to download
3. Run the `Application.java` file (located at `src/main/java/HealthLink/HelathLink/Application.java`)

The backend will start on **http://localhost:8080**

---

## Step 4: Run the Frontend (Next.js)

1. **Navigate to the frontend directory:**
   ```bash
   cd frontend
   ```

2. **Install dependencies:**
   ```bash
   npm install
   ```
   > This will install all Node.js packages (similar to `pip install -r requirements.txt` in Python)

3. **Start the development server:**
   ```bash
   npm run dev
   ```

The frontend will start on **http://localhost:3000**

---

## Step 5: Access the Application

- **Frontend**: Open http://localhost:3000 in your browser
- **Backend API**: http://localhost:8080

---

## Troubleshooting

### Backend Issues:

1. **Port 8080 already in use:**
   - Change the port in `application.properties`:
     ```properties
     server.port=8081
     ```
   - Update frontend `next.config.js` to match the new port

2. **MySQL Connection Error:**
   - Ensure MySQL is running
   - Check username/password in `application.properties`
   - Verify database exists or allow auto-creation

3. **Java Version Error:**
   - Ensure Java 21 is installed: `java -version`
   - Update JAVA_HOME environment variable if needed

### Frontend Issues:

1. **npm install fails:**
   - Clear cache: `npm cache clean --force`
   - Delete `node_modules` and `package-lock.json`, then run `npm install` again

2. **Cannot connect to backend:**
   - Ensure backend is running on port 8080
   - Check `NEXT_PUBLIC_API_URL` in `next.config.js`
   - Verify CORS is enabled in backend (should be in `CorsConfig.java`)

---

## Project Structure

```
HealthLink+/
├── src/                    # Backend (Spring Boot)
│   └── main/
│       ├── java/          # Java source code
│       └── resources/     # Configuration files
├── frontend/              # Frontend (Next.js)
│   ├── app/              # Pages and routes
│   ├── components/       # React components
│   └── lib/              # API client
├── pom.xml               # Maven dependencies (like requirements.txt)
├── mvnw                  # Maven wrapper (Linux/Mac)
└── mvnw.cmd              # Maven wrapper (Windows)
```

---

## Quick Start Commands

### Backend:
```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

### Frontend:
```bash
cd frontend
npm install
npm run dev
```

---

## Notes

- **No Python/venv needed** - This is a Java project, not Python
- **No requirements.txt** - Java uses `pom.xml` for dependencies (Maven)
- **Maven wrapper included** - You don't need to install Maven separately
- **Database auto-creates** - The database will be created automatically if it doesn't exist
- **Email required for OTP** - Update email settings to receive OTP codes

---

## Development Tips

1. **Backend logs**: Check console output for Spring Boot logs
2. **Frontend hot-reload**: Changes auto-reload in browser
3. **API testing**: Use Postman or the frontend to test endpoints
4. **Database**: Use MySQL Workbench or command line to view database

---

For API documentation, see `HealthLink_API_Documentation.txt`

