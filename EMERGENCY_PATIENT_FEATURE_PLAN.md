# Emergency Patient Feature - Implementation Plan

## Overview
Complete emergency patient workflow: Create patient → Send email with password → Doctor adds prescription → Patient logs in and views prescription

## Current State
✅ Emergency patient creation exists
✅ Prescription creation exists (separate page)
❌ Email doesn't include temporary password
❌ No prescription fields in emergency form
❌ No easy way to add prescription after emergency patient creation

## Implementation Steps

### 1. Backend Changes

#### 1.1 Update Emergency Patient Email Service
- Modify `sendEmergencyPatientWelcomeEmail` to accept and include temporary password
- Update email template to show password and change password instructions

#### 1.2 Update CreateEmergencyPatientRequest DTO
- Add optional prescription fields:
  - `prescriptionDetails` (string) - Initial prescription notes
  - `medications` (List<String>) - Initial medications list

#### 1.3 Update DoctorServiceImpl
- Pass temporary password to email service
- Optionally create prescription if prescription data provided

### 2. Frontend Changes

#### 2.1 Update Emergency Patient Form
- Add prescription section (collapsible/optional)
- Fields: Prescription Details (textarea), Medications (list)
- Make it clear this can be filled during/after meeting

#### 2.2 Create Prescription Interface for Emergency Patients
- New page: `/doctor/emergency/[patientId]/prescription`
- Accessible from emergency patient success page
- Pre-filled with patient info
- Similar to existing prescription form but streamlined for emergency cases

#### 2.3 Update Success Page
- Show "Add Prescription" button linking to prescription page
- Show patient credentials (for doctor's reference)

### 3. Email Template
- Update `emergency-patient-welcome.html` template
- Include temporary password prominently
- Clear instructions to change password on first login
- Link to login page

## Flow Diagram

```
1. Doctor clicks "Create Emergency Patient"
   ↓
2. Doctor fills form:
   - Patient Name
   - Email
   - Phone Number
   - (Optional) Prescription Details
   - (Optional) Medications
   ↓
3. Backend creates patient account with temporary password
   ↓
4. Backend sends email with:
   - Temporary password
   - Instructions to change password
   - Login link
   ↓
5. Doctor sees success page with:
   - Patient info
   - "Add Prescription" button
   - "View Patient" link
   ↓
6. During/After Meeting:
   - Doctor clicks "Add Prescription"
   - Fills prescription form
   - Saves prescription
   ↓
7. Patient goes home:
   - Receives email
   - Logs in with temporary password
   - Changes password
   - Views prescription in prescriptions page
```

## Files to Modify

### Backend
1. `EmailService.java` - Update `sendEmergencyPatientWelcomeEmail`
2. `DoctorServiceImpl.java` - Pass password to email, optionally create prescription
3. `CreateEmergencyPatientRequest.java` - Add prescription fields
4. Email template: `emergency-patient-welcome.html`

### Frontend
1. `app/doctor/emergency/new/page.tsx` - Add prescription fields
2. `app/doctor/emergency/[patientId]/prescription/page.tsx` - New prescription page
3. `types/index.ts` - Update types
4. `lib/api.ts` - Update API calls

