# Admin Portal Login Guide

## Quick Steps to Access Admin Portal

### Step 1: Check if Admin Account Exists

Run this SQL query on your database:

```sql
SELECT email, username, approval_status, is_active, is_email_verified
FROM users
WHERE role = 'ADMIN' AND deleted_at IS NULL;
```

**If you see results:**
- Try logging in with that email and password
- If you don't know the password, proceed to Step 2

**If no results:**
- You need to create an admin account (Step 2)

---

### Step 2: Create Admin Account

#### Option A: Using SQL (Recommended)

1. **Generate BCrypt hash for your password:**
   - Go to: https://bcrypt-generator.com/
   - Enter your password (e.g., `Admin123!@#`)
   - Set rounds: `10`
   - Click "Generate Hash"
   - Copy the hash (starts with `$2a$10$...`)

2. **Run this SQL** (replace the values):

```sql
INSERT INTO users (
    id, user_type, email, username, password_hash, full_name,
    role, approval_status, is_active, is_email_verified,
    preferred_language, created_at, updated_at,
    admin_username, can_approve_doctors, can_approve_organizations, can_view_analytics
) VALUES (
    gen_random_uuid(),
    'ADMIN',
    'admin@healthlink.com',  -- CHANGE THIS
    'admin',  -- CHANGE THIS
    '$2a$10$YOUR_BCRYPT_HASH_HERE',  -- PASTE YOUR HASH HERE
    'System Administrator',  -- CHANGE THIS
    'ADMIN',
    'APPROVED',  -- MUST be APPROVED
    true,  -- MUST be active
    true,  -- Email verified
    'en',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'admin',  -- Should match username
    true,
    true,
    true
)
ON CONFLICT (email) DO NOTHING;
```

3. **Verify creation:**
```sql
SELECT email, username, approval_status, is_active 
FROM users 
WHERE email = 'admin@healthlink.com' AND role = 'ADMIN';
```

#### Option B: Using Java Utility

1. Open `CreateAdminAccount.java`
2. Update the email, username, password, and fullName variables
3. Compile and run:
```bash
cd healthlink_backend
./gradlew compileJava
java -cp build/classes/java/main com.healthlink.util.CreateAdminAccount
```
4. Copy the generated SQL and run it in your database

---

### Step 3: Login to Admin Portal

1. **Go to:** `http://localhost:3000/auth/admin/login` (or your frontend URL)

2. **Enter credentials:**
   - **Email:** The email you set in Step 2
   - **Password:** The password you hashed in Step 2

3. **You should be redirected to:** `/admin/dashboard`

---

## Important Notes

⚠️ **Admin accounts MUST have:**
- `approval_status = 'APPROVED'` (not PENDING)
- `is_active = true`
- `is_email_verified = true`

⚠️ **If login fails:**
- Check that all three conditions above are met
- Verify the password hash was generated correctly
- Check backend logs for authentication errors
- Ensure the backend is running and connected to the database

---

## Troubleshooting

### "Invalid credentials"
- Double-check the password hash matches your password
- Verify the email exists in the database
- Check that `approval_status = 'APPROVED'`

### "Account not approved"
- Update: `UPDATE users SET approval_status = 'APPROVED' WHERE email = 'your@email.com';`

### "Email not verified"
- Update: `UPDATE users SET is_email_verified = true WHERE email = 'your@email.com';`

### "Account inactive"
- Update: `UPDATE users SET is_active = true WHERE email = 'your@email.com';`

---

## Security Reminder

🔒 **After creating the admin account:**
- Change the password to something strong
- Don't share admin credentials
- Consider setting up 2FA if available
- Use environment-specific admin accounts (different for dev/prod)

