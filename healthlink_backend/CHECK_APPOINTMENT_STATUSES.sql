-- Check script to see what appointment statuses exist in the database
-- Run this BEFORE the migration to see what needs to be updated

-- Show all distinct statuses and their counts
SELECT status, COUNT(*) as count 
FROM appointments 
GROUP BY status 
ORDER BY count DESC;

-- Show appointments with old statuses (if any)
SELECT id, status, appointment_time, created_at
FROM appointments 
WHERE status IN ('PENDING_PAYMENT', 'CONFIRMED')
ORDER BY created_at DESC;

