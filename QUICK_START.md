# Quick Start Guide

## Prerequisites Check

Before starting, ensure you have:
- ✅ Java 21 installed (`java -version`)
- ✅ MySQL installed and running
- ✅ Node.js 18+ installed (`node -version`)

---

## 1. Setup Database

1. Start MySQL
2. Update password in `src/main/resources/application.properties` (line 7)

---

## 2. Start Backend

**Windows:**
```bash
.\mvnw.cmd spring-boot:run
```

**Linux/Mac:**
```bash
./mvnw spring-boot:run
```

Wait for: `Started Application in X.XXX seconds`

---

## 3. Start Frontend

Open a **new terminal window**:

```bash
cd frontend
npm install
npm run dev
```

Wait for: `Ready on http://localhost:3000`

---

## 4. Open Browser

Go to: **http://localhost:3000**

---

## That's it! 🎉

You should now see the HealthLink+ homepage.

---

## Common Issues

**Backend won't start:**
- Check MySQL is running
- Verify Java 21 is installed
- Check port 8080 is not in use

**Frontend won't start:**
- Run `npm install` first
- Check Node.js version (need 18+)
- Delete `node_modules` and try again

**Can't connect:**
- Make sure both backend and frontend are running
- Backend on port 8080
- Frontend on port 3000

