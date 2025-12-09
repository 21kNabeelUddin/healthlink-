package com.healthlink.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.UUID;
import java.time.LocalDateTime;

/**
 * Utility class to create an admin account
 * 
 * Run this as a standalone Java program or use it to generate the SQL INSERT statement
 * 
 * Usage:
 * 1. Update the email, username, password, and fullName variables below
 * 2. Run: java CreateAdminAccount
 * 3. Copy the generated SQL INSERT statement and run it in your database
 */
public class CreateAdminAccount {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
        
        // ============================================
        // CONFIGURE YOUR ADMIN ACCOUNT HERE
        // ============================================
        String email = "admin@healthlink.com";  // CHANGE THIS
        String username = "admin";  // CHANGE THIS
        String password = "Admin123!@#";  // CHANGE THIS - Use a strong password!
        String fullName = "System Administrator";  // CHANGE THIS
        String adminUsername = "admin";  // Should match username
        // ============================================
        
        // Generate BCrypt hash
        String passwordHash = encoder.encode(password);
        
        // Generate UUID
        String userId = UUID.randomUUID().toString();
        
        // Print SQL INSERT statement
        System.out.println("-- ============================================");
        System.out.println("-- ADMIN ACCOUNT CREATION SQL");
        System.out.println("-- ============================================");
        System.out.println();
        System.out.println("INSERT INTO users (");
        System.out.println("    id,");
        System.out.println("    user_type,");
        System.out.println("    email,");
        System.out.println("    username,");
        System.out.println("    password_hash,");
        System.out.println("    full_name,");
        System.out.println("    role,");
        System.out.println("    approval_status,");
        System.out.println("    is_active,");
        System.out.println("    is_email_verified,");
        System.out.println("    preferred_language,");
        System.out.println("    created_at,");
        System.out.println("    updated_at,");
        System.out.println("    admin_username,");
        System.out.println("    can_approve_doctors,");
        System.out.println("    can_approve_organizations,");
        System.out.println("    can_view_analytics");
        System.out.println(") VALUES (");
        System.out.println("    '" + userId + "',");
        System.out.println("    'ADMIN',");
        System.out.println("    '" + email + "',");
        System.out.println("    '" + username + "',");
        System.out.println("    '" + passwordHash + "',");
        System.out.println("    '" + fullName + "',");
        System.out.println("    'ADMIN',");
        System.out.println("    'APPROVED',");
        System.out.println("    true,");
        System.out.println("    true,");
        System.out.println("    'en',");
        System.out.println("    CURRENT_TIMESTAMP,");
        System.out.println("    CURRENT_TIMESTAMP,");
        System.out.println("    '" + adminUsername + "',");
        System.out.println("    true,");
        System.out.println("    true,");
        System.out.println("    true");
        System.out.println(")");
        System.out.println("ON CONFLICT (email) DO NOTHING;");
        System.out.println();
        System.out.println("-- ============================================");
        System.out.println("-- LOGIN CREDENTIALS:");
        System.out.println("-- Email: " + email);
        System.out.println("-- Password: " + password);
        System.out.println("-- ============================================");
    }
}

