-- ============================================
-- CREATE ADMIN ACCOUNT MANUALLY
-- ============================================
-- 
-- IMPORTANT: You need to generate a BCrypt hash for your password first!
-- 
-- Option A: Use online tool (for testing only):
--   https://bcrypt-generator.com/
--   Enter your password (e.g., "Admin123!@#")
--   Use rounds: 10
--   Copy the generated hash
--
-- Option B: Use Java code (recommended):
--   Run: CreateAdminAccount.java (see below)
--
-- ============================================

-- Step 1: Generate a BCrypt hash for your password
-- Example: Password "Admin123!@#" generates hash like:
-- $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy

-- Step 2: Insert admin account (replace the password_hash with your generated hash)
INSERT INTO users (
    id,
    user_type,
    email,
    username,
    password_hash,
    full_name,
    role,
    approval_status,
    is_active,
    is_email_verified,
    preferred_language,
    created_at,
    updated_at,
    admin_username,
    can_approve_doctors,
    can_approve_organizations,
    can_view_analytics
) VALUES (
    gen_random_uuid(),  -- PostgreSQL UUID generation
    'ADMIN',
    'admin@healthlink.com',  -- CHANGE THIS EMAIL
    'admin',  -- CHANGE THIS USERNAME
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',  -- CHANGE THIS: Generate BCrypt hash for your password
    'System Administrator',  -- CHANGE THIS NAME
    'ADMIN',
    'APPROVED',  -- Must be APPROVED to login
    true,  -- Must be active
    true,  -- Email verified
    'en',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'admin',  -- Must match username
    true,
    true,
    true
)
ON CONFLICT (email) DO NOTHING;  -- Prevents duplicate if email exists

-- Step 3: Verify the admin was created
SELECT 
    id,
    email,
    username,
    full_name,
    role,
    approval_status,
    is_active,
    is_email_verified
FROM users
WHERE email = 'admin@healthlink.com'  -- CHANGE THIS EMAIL
  AND role = 'ADMIN';

-- ============================================
-- LOGIN CREDENTIALS:
-- Email: admin@healthlink.com (or whatever you set)
-- Password: Admin123!@# (or whatever password you hashed)
-- ============================================

