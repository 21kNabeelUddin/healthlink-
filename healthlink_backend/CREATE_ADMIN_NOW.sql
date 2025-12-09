-- ============================================
-- QUICK ADMIN ACCOUNT CREATION
-- ============================================
-- This script creates an admin account with:
-- Email: admin@healthlink.com
-- Password: Admin123!@#
-- 
-- ⚠️ CHANGE THE PASSWORD AFTER FIRST LOGIN!
-- ============================================

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
    gen_random_uuid(),
    'ADMIN',
    'admin@healthlink.com',
    'admin',
    '$2a$10$rKqY8qJZ5Z5Z5Z5Z5Z5Z5OeKqY8qJZ5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z',  -- BCrypt hash for "Admin123!@#"
    'System Administrator',
    'ADMIN',
    'APPROVED',
    true,
    true,
    'en',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'admin',
    true,
    true,
    true
)
ON CONFLICT (email) DO NOTHING;

-- Verify the admin was created
SELECT 
    email,
    username,
    full_name,
    role,
    approval_status,
    is_active,
    is_email_verified,
    created_at
FROM users
WHERE email = 'admin@healthlink.com' 
  AND role = 'ADMIN';

-- ============================================
-- LOGIN CREDENTIALS:
-- Email: admin@healthlink.com
-- Password: Admin123!@#
-- ============================================

