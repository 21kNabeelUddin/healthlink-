-- Migration: Make appointment_id nullable in prescriptions table
-- This allows emergency prescriptions to be created without an appointment

-- Check if column is currently NOT NULL before altering
DO $$
BEGIN
    -- Check if the column exists and is NOT NULL
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'prescriptions' 
        AND column_name = 'appointment_id' 
        AND is_nullable = 'NO'
    ) THEN
        -- Alter the column to allow NULL values
        ALTER TABLE prescriptions 
        ALTER COLUMN appointment_id DROP NOT NULL;
        
        RAISE NOTICE 'Successfully made appointment_id nullable in prescriptions table';
    ELSE
        RAISE NOTICE 'appointment_id column is already nullable or does not exist';
    END IF;
END $$;

