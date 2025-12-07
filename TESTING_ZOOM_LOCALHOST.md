# Testing Zoom Video Calls Between Two Laptops (Localhost)

## How It Works

**Important:** The Zoom video call itself happens on **Zoom's servers**, not on localhost. The `zoomStartUrl` and `zoomJoinUrl` point to Zoom's infrastructure, so the actual video call will work from anywhere, even if your app is running on localhost.

The challenge is making sure **both laptops can access the same backend/database** so they can:
1. See each other's accounts (patient and doctor)
2. Book appointments together
3. Access the Zoom meeting URLs

---

## Setup Options

### Option 1: Shared Backend (Recommended for Testing)

**One person runs the backend, both access it.**

#### Setup Steps:

1. **On Laptop 1 (Backend Host):**
   ```bash
   # Find your local IP address
   # Windows:
   ipconfig
   # Look for "IPv4 Address" (e.g., 192.168.1.100)
   
   # Linux/Mac:
   ifconfig
   # Look for inet address (e.g., 192.168.1.100)
   ```

2. **Update Backend Configuration:**
   - Open `healthlink_backend/src/main/resources/application.properties` or `.env`
   - Make sure CORS allows your friend's laptop IP:
   ```properties
   # Allow requests from both laptops
   healthlink.cors.allowed-origins=http://localhost:3000,http://192.168.1.100:3000,http://192.168.1.101:3000
   ```

3. **Start Backend on Laptop 1:**
   ```bash
   cd healthlink_backend
   ./mvnw spring-boot:run
   # Or if using IDE, run HealthLinkApplication
   ```

4. **On Laptop 2 (Frontend Only):**
   - Update `frontend/.env.local` or `frontend/lib/api.ts`:
   ```typescript
   // In lib/api.ts, change the baseURL:
   const api = axios.create({
     baseURL: 'http://192.168.1.100:8080', // Laptop 1's IP
     // ... rest of config
   });
   ```

5. **Both Laptops:**
   - Start frontend normally:
   ```bash
   cd frontend
   npm run dev
   ```

6. **Access the App:**
   - **Laptop 1:** `http://localhost:3000`
   - **Laptop 2:** `http://localhost:3000` (but it connects to Laptop 1's backend)

---

### Option 2: Shared Database (Both Run Backend)

**Both run backend, but connect to the same database.**

#### Setup Steps:

1. **Set up a shared database:**
   - Use a cloud database (PostgreSQL on AWS RDS, Railway, Supabase, etc.)
   - Or use a database on one laptop and allow remote connections

2. **On Both Laptops:**
   - Update `healthlink_backend/.env`:
   ```env
   # Use the same database URL
   SPRING_DATASOURCE_URL=jdbc:postgresql://192.168.1.100:5432/healthlink
   SPRING_DATASOURCE_USERNAME=your_username
   SPRING_DATASOURCE_PASSWORD=your_password
   ```

3. **Start Backend on Both:**
   ```bash
   cd healthlink_backend
   ./mvnw spring-boot:run
   ```

4. **Start Frontend on Both:**
   ```bash
   cd frontend
   npm run dev
   ```

---

### Option 3: Use ngrok (Easiest for Quick Testing)

**Expose one backend to the internet temporarily.**

#### Setup Steps:

1. **On Laptop 1 (Backend Host):**
   ```bash
   # Install ngrok: https://ngrok.com/download
   # Start backend on port 8080
   cd healthlink_backend
   ./mvnw spring-boot:run
   
   # In another terminal, expose backend:
   ngrok http 8080
   # You'll get a URL like: https://abc123.ngrok.io
   ```

2. **Update Frontend on Both Laptops:**
   ```typescript
   // In lib/api.ts:
   const api = axios.create({
     baseURL: 'https://abc123.ngrok.io', // Your ngrok URL
     // ... rest of config
   });
   ```

3. **Both laptops can now access the same backend via the ngrok URL**

---

## Testing the Video Call Flow

### Step-by-Step Test:

1. **Laptop 1 (Patient):**
   - Sign up as a patient
   - Browse doctors
   - Book an ONLINE appointment with the doctor on Laptop 2

2. **Laptop 2 (Doctor):**
   - Sign up as a doctor
   - Verify email (OTP)
   - Create a clinic
   - Go to `/doctor/appointments`
   - Confirm the pending appointment

3. **When Appointment Time Arrives:**

   **On Laptop 2 (Doctor):**
   - Go to `/doctor/appointments`
   - Find the confirmed appointment
   - Click "Start Zoom Meeting" (uses `zoomStartUrl`)
   - This opens Zoom in browser/app
   - Doctor is the host

   **On Laptop 1 (Patient):**
   - Go to `/patient/appointments`
   - Find the confirmed appointment
   - Click "Join Meeting" (uses `zoomJoinUrl`)
   - This opens Zoom in browser/app
   - Patient enters the meeting password (if required)

4. **During the Call:**
   - Both are connected via Zoom's servers
   - Video/audio works normally
   - The call happens on Zoom, not localhost

5. **After the Call:**
   - Doctor creates prescription at `/doctor/prescriptions/new?appointmentId=...`
   - Doctor clicks "Conclude Appointment"
   - Patient is redirected to rating page
   - Patient submits review

---

## Important Notes

### Zoom Requirements:

1. **Zoom Account:** You need a Zoom account with API access (Pro, Business, or Enterprise)
2. **Zoom API Setup:** Make sure `ZOOM_ENABLED=true` in backend `.env` and credentials are configured
3. **Zoom App:** Both users need Zoom installed or use Zoom web client

### Network Requirements:

1. **Same Network:** Both laptops should be on the same Wi-Fi network for Option 1
2. **Firewall:** Make sure Windows Firewall allows connections on port 8080 (backend) and 3000 (frontend)
3. **CORS:** Backend must allow requests from both frontend URLs

### Troubleshooting:

**"Cannot connect to backend":**
- Check firewall settings
- Verify IP addresses are correct
- Make sure both are on same network
- Check backend logs for connection attempts

**"Zoom meeting not created":**
- Check `ZOOM_ENABLED=true` in backend `.env`
- Verify Zoom API credentials are correct
- Check backend logs for Zoom API errors

**"CORS error":**
- Update `application.properties` to include both frontend URLs
- Restart backend after changing CORS settings

---

## Quick Test Checklist

- [ ] Both laptops can access the same backend
- [ ] Patient can sign up and see doctors
- [ ] Doctor can sign up and create clinic
- [ ] Patient can book ONLINE appointment
- [ ] Doctor can confirm appointment
- [ ] Zoom meeting URLs appear in appointment details
- [ ] Doctor can click "Start Zoom Meeting"
- [ ] Patient can click "Join Meeting"
- [ ] Both connect to same Zoom meeting
- [ ] Video/audio works in Zoom
- [ ] Doctor can create prescription
- [ ] Doctor can conclude appointment
- [ ] Patient can rate appointment

---

## Alternative: Use Zoom Test Mode

If you don't have Zoom API credentials yet, you can test the flow without actual Zoom:

1. Set `ZOOM_ENABLED=false` in backend `.env`
2. Appointments will be created without Zoom URLs
3. You can still test the rest of the flow (prescription, completion, rating)
4. For actual video calls, you'll need proper Zoom setup

---

## Summary

**The key insight:** Zoom calls work from anywhere because they're hosted on Zoom's servers. You just need to ensure both laptops can:
1. Access the same backend/database (to see each other's accounts)
2. Book appointments together
3. Access the Zoom URLs that are stored in the database

The actual video call happens on Zoom's infrastructure, so network setup only matters for the app itself, not the video call.

