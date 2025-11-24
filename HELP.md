# HealthLink+ – Quick Guide

## Repo layout

```
HealthLink+/
├─ src/…                       # Spring Boot backend (Maven, MySQL, email OTPs)
├─ frontend/                   # Next.js 14 + Tailwind marketing + dashboards
│  ├─ app/api/chatbot/route.ts # Gemini proxy endpoint
│  └─ app/patient/chatbot/…    # Gemini-powered UI
├─ HELP.md (this file)
├─ SETUP.md, QUICK_START.md    # Extra onboarding notes
```

## Backend (Spring Boot)
- Java 21 + Maven (`.\mvnw.cmd spring-boot:run`).
- Config file: `src/main/resources/application.properties`.
  - MySQL connection, email OTP SMTP creds.
  - OTP logging for dev is in `PatientService/DoctorService/AdminService`.
- Endpoints documented in `HealthLink_API_Documentation.txt`.

## Frontend (Next.js 14, TypeScript, Tailwind)
- Located in `frontend/`.
- Install deps: `cd frontend && npm install`.
- Dev server: `npm run dev` → http://localhost:3000.
- Uses `contexts/AuthContext` for session state; dashboards require valid login.
- Marketing landing page lives in `frontend/marketing/*` and is imported into `app/page.tsx`.

### AI Chatbot
- Entry point: `app/patient/chatbot/page.tsx`.
- API route: `app/api/chatbot/route.ts`.
- Requires Google Gemini API credentials (Generative Language API enabled).
- Configure `frontend/.env.local`:
  ```
  GEMINI_API_KEY=<your-key>
  GEMINI_MODEL_NAME=gemini-2.0-flash   # any allowed model, e.g. gemini-pro
  ```
  Restart `npm run dev` after changes. Keys are intentionally not committed.

## Deployment/Testing Tips
- Backend must be running for the dashboards/auth flows.
- MySQL must be accessible at the DSN defined in `application.properties` (default `healthlinkdb`).
- OTP email requires valid Google “App Password” and SMTP credentials.
- For Gemini, ensure the key has access to the requested model (test with curl: `curl -H "X-goog-api-key: $KEY" ...`).

## Version control
- Initialize git, add remote, and push:
  ```
  git init
  git remote add origin https://github.com/21kNabeelUddin/healthlink-.git
  git add .
  git commit -m "Initial HealthLink+ commit"
  git push -u origin main
  ```

That’s everything a fresh Cursor agent (or teammate) needs to reproduce the setup quickly. Good luck!

