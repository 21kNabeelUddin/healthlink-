-- Check if any admin accounts exist
SELECT 
    id,
    email,
    username,
    full_name,
    role,
    approval_status,
    is_active,
    is_email_verified,
    created_at
FROM users
WHERE role = 'ADMIN' 
  AND deleted_at IS NULL
ORDER BY created_at DESC;

-- If you see any results, try logging in with that email
-- Password would be whatever was set when the account was created

