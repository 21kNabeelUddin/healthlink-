-- SQL Query to convert user k214558@nu.edu.pk from PATIENT to DOCTOR
-- User ID: 939162eb-0b47-4f1c-a6c8-e9a5f679691b

UPDATE users
SET 
    -- Change user type and role
    user_type = 'DOCTOR',
    role = 'DOCTOR',
    
    -- Required doctor fields (UPDATE THESE VALUES AS NEEDED)
    -- ⚠️ IMPORTANT: Use a UNIQUE PMDC ID that doesn't exist in the database
    -- Check existing PMDC IDs first: SELECT pmdc_id FROM users WHERE pmdc_id IS NOT NULL;
    pmdc_id = '99999-P',  -- ⚠️ CHANGE THIS to actual UNIQUE PMDC ID (format: xxxxx-P)
    specialization = 'General Medicine',  -- ⚠️ CHANGE THIS to actual specialization
    
    -- Doctor-specific settings (using defaults from existing doctor)
    pmdc_verified = false,  -- Set to true after PMDC verification
    allow_early_checkin = false,
    early_checkin_minutes = 15,
    slot_duration_minutes = 15,
    refund_cutoff_minutes = 1440,  -- 24 hours
    refund_deduction_percent = 0.0,
    allow_full_refund_on_doctor_cancellation = true,
    average_rating = 0.0,
    total_reviews = 0,
    years_of_experience = NULL,  -- Set this to actual years if known
    
    -- Update timestamp
    updated_at = CURRENT_TIMESTAMP,
    version = version + 1
    
WHERE email = 'k214558@nu.edu.pk'
  AND user_type = 'PATIENT';

-- Verify the update
SELECT 
    id,
    email,
    user_type,
    role,
    pmdc_id,
    specialization,
    pmdc_verified,
    years_of_experience,
    slot_duration_minutes,
    is_active,
    approval_status
FROM users
WHERE email = 'k214558@nu.edu.pk';

