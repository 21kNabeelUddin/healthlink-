-- ============================================
-- CREATE ADMIN ACCOUNT - READY TO USE
-- ============================================
-- 
-- STEP 1: Generate BCrypt hash at https://bcrypt-generator.com/
--         Password: Admin123!@#
--         Rounds: 10
--         Copy the generated hash
--
-- STEP 2: Replace YOUR_HASH_HERE below with the hash you copied
-- STEP 3: Run this entire script
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
    'admin@healthlink.com',  -- You can change this email
    'admin',  -- You can change this username
    '$2a$12$uDfmvMAgnZh/RaOBC5GeF.uwZe/8S6pFw/h2YRuONCcTy63g99x22',  -- ⚠️ REPLACE THIS with hash from bcrypt-generator.com
    'System Administrator',
    'ADMIN',
    'APPROVED',
    true,
    true,
    'en',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'admin',  -- Should match username above
    true,
    true,
    true
)
ON CONFLICT (email) DO NOTHING;

-- Verify it was created
SELECT 
    email,
    username,
    full_name,
    role,
    approval_status,
    is_active,
    is_email_verified
FROM users
WHERE email = 'admin@healthlink.com' 
  AND role = 'ADMIN';

-- ============================================
-- After running, login with:
-- Email: admin@healthlink.com
-- Password: Admin123!@#
-- ============================================

