-- Migration script to update appointment statuses
-- Removes PENDING_PAYMENT and CONFIRMED statuses
-- Updates existing appointments to IN_PROGRESS

-- Update PENDING_PAYMENT appointments to IN_PROGRESS
UPDATE appointments 
SET status = 'IN_PROGRESS' 
WHERE status = 'PENDING_PAYMENT';

-- Update CONFIRMED appointments to IN_PROGRESS
UPDATE appointments 
SET status = 'IN_PROGRESS' 
WHERE status = 'CONFIRMED';

-- Note: The enum change in AppointmentStatus.java will be applied by Hibernate
-- on next application startup. This script handles existing data migration.

