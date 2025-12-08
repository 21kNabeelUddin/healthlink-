-- Comprehensive migration script to update ALL appointment statuses
-- This ensures no appointments are left with PENDING_PAYMENT or CONFIRMED status

-- First, check what statuses exist (run this to verify):
-- SELECT DISTINCT status FROM appointments;

-- Update PENDING_PAYMENT appointments to IN_PROGRESS
UPDATE appointments 
SET status = 'IN_PROGRESS' 
WHERE status = 'PENDING_PAYMENT';

-- Update CONFIRMED appointments to IN_PROGRESS
UPDATE appointments 
SET status = 'IN_PROGRESS' 
WHERE status = 'CONFIRMED';

-- Verify the migration (run this after the UPDATE statements):
-- SELECT status, COUNT(*) as count 
-- FROM appointments 
-- GROUP BY status;

-- If you see any PENDING_PAYMENT or CONFIRMED in the results, 
-- those appointments need to be manually reviewed and updated.

