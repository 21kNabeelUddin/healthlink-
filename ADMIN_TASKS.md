# Admin Tasks & Responsibilities

Based on the HealthLink+ API and codebase, here are the tasks that administrators can perform:

## 1. Dashboard & Analytics
- **View Dashboard Statistics** (`GET /api/admin/dashboard`)
  - Total patients, doctors, admins
  - Total appointments, clinics, medical histories
  - Pending, confirmed, and completed appointments count
  - Overall platform health metrics

## 2. Patient Management
- **View All Patients** (`GET /api/admin/patients`)
  - List all registered patients in the system
- **View Patient Details** (`GET /api/admin/patients/{patientId}`)
  - Get detailed information about a specific patient
- **Delete Patient** (`DELETE /api/admin/patients/{patientId}`)
  - Remove a patient account from the system

## 3. Doctor Management
- **View All Doctors** (`GET /api/admin/doctors`)
  - List all registered doctors
- **View Doctor Details** (`GET /api/admin/doctors/{doctorId}`)
  - Get detailed information about a specific doctor
- **Delete Doctor** (`DELETE /api/admin/doctors/{doctorId}`)
  - Remove a doctor account from the system

## 4. Admin Management
- **View All Admins** (`GET /api/admin/admins`)
  - List all administrator accounts
- **View Admin Details** (`GET /api/admin/admins/{adminId}`)
  - Get detailed information about a specific admin
- **Delete Admin** (`DELETE /api/admin/admins/{adminId}`)
  - Remove an admin account from the system

## 5. Appointment Management
- **View All Appointments** (`GET /api/admin/appointments`)
  - Optional filters: `status` (PENDING, CONFIRMED, CANCELLED, COMPLETED, REJECTED)
  - Optional filters: `appointmentType` (ONLINE, ONSITE)
- **View Appointment Details** (`GET /api/admin/appointments/{appointmentId}`)
  - Get detailed information about a specific appointment
- **Update Appointment Status** (`PATCH /api/admin/appointments/{appointmentId}/status?status={status}`)
  - Manually change appointment status (useful for resolving disputes or corrections)
- **Delete Appointment** (`DELETE /api/admin/appointments/{appointmentId}`)
  - Remove an appointment from the system

## 6. Clinic Management
- **View All Clinics** (`GET /api/admin/clinics`)
  - Optional filter: `doctorId` to filter by specific doctor
- **View Clinic Details** (`GET /api/admin/clinics/{clinicId}`)
  - Get detailed information about a specific clinic
- **Delete Clinic** (`DELETE /api/admin/clinics/{clinicId}`)
  - Remove a clinic from the system

## 7. Medical History Management
- **View All Medical Histories** (`GET /api/admin/medical-histories`)
  - Optional filters: `patientId` to filter by specific patient
  - Optional filters: `status` (ACTIVE, RESOLVED, CHRONIC, UNDER_TREATMENT)
- **View Medical History Details** (`GET /api/admin/medical-histories/{historyId}`)
  - Get detailed information about a specific medical record
- **Delete Medical History** (`DELETE /api/admin/medical-histories/{historyId}`)
  - Remove a medical history record from the system

## Summary
Admins have **full read and delete access** to all entities in the system, plus the ability to:
- Monitor platform health through dashboard statistics
- Manage user accounts (patients, doctors, admins)
- Oversee appointments and update their status when needed
- Manage clinics and medical history records
- Resolve disputes or correct data issues

**Note:** Admins cannot create or update most entities (except appointment status). Creation is handled by the respective user types (patients create appointments, doctors create clinics, etc.), and updates are typically done by the entity owners.

