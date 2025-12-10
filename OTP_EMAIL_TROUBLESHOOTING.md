# OTP Email Troubleshooting Guide

## Issue: Doctor signup not receiving OTP email (but patient signup works)

### Root Causes

The OTP email system has two key configuration flags:

1. **`HEALTHLINK_OTP_EMAIL_ENABLED`** - Must be `true` to send OTP emails
2. **Email Service Configuration** - SMTP settings must be correct

### How to Check

#### 1. Check Backend Logs

Look for these log messages when the doctor signs up:

**If OTP email is DISABLED:**
```
⚠️ OTP EMAIL IS DISABLED! Set HEALTHLINK_OTP_EMAIL_ENABLED=true to enable email sending.
OTP email skipped - email is disabled. OTP: 123456
```

**If OTP email is ATTEMPTING:**
```
Attempting to send OTP email to: doctor@example.com
📧 Attempting to send email to: doctor@example.com with subject: Your HealthLink verification code
```

**If OTP email FAILED:**
```
❌ Failed to send email to: doctor@example.com - Error: [error message]
Failed to send OTP email to: doctor@example.com - Error: [error message]
```

**If OTP email SUCCEEDED:**
```
✅ Email sent successfully to: doctor@example.com
OTP email sent successfully to: doctor@example.com
```

#### 2. Check Environment Variables

In your `.env` file (or environment), ensure:

```bash
# Enable OTP emails
HEALTHLINK_OTP_EMAIL_ENABLED=true

# Email service configuration
SPRING_MAIL_HOST=smtp.gmail.com  # or your SMTP server
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=your-email@gmail.com
SPRING_MAIL_PASSWORD=your-app-password
SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH=true
SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE=true
```

#### 3. Check Application Startup Logs

When the backend starts, you should see:

```
📧 Email Service Configuration:
   Host: smtp.gmail.com
   Port: 587
   Username: your-email@gmail.com
   Auth: true
   STARTTLS: true
   From: noreply@healthlink.com
   From Name: HealthLink Platform
```

And:

```
OTP Service initialized
   redis_enabled: false
   email_enabled: true  ← Should be TRUE
```

### Why Patient Worked But Doctor Didn't

If patient signup worked but doctor didn't, possible reasons:

1. **Timing Issue**: Environment variable was changed between attempts
2. **Email Service Error**: The email service threw an exception for the doctor's email (check logs for specific error)
3. **Rate Limiting**: Too many OTP requests (max 5 per hour per email)
4. **Email Delivery Issue**: The email was sent but went to spam, or the doctor's email provider blocked it

### Quick Fix

1. **Set the environment variable:**
   ```bash
   export HEALTHLINK_OTP_EMAIL_ENABLED=true
   ```
   Or add to your `.env` file:
   ```
   HEALTHLINK_OTP_EMAIL_ENABLED=true
   ```

2. **Restart the backend** to pick up the new configuration

3. **Check the logs** when the doctor signs up again

### Development Mode (No Email Required)

If you're in development and don't want to configure email:

1. The OTP is still generated and stored in memory
2. Check the backend logs - the OTP will be printed:
   ```
   OTP email skipped - email is disabled. OTP: 123456
   ```
3. Use that OTP to verify the account

### Testing OTP Manually

You can also manually request an OTP:

```bash
curl -X POST http://localhost:8080/api/v1/auth/send-otp \
  -H "Content-Type: application/json" \
  -d '{"email": "doctor@example.com"}'
```

The response will include the OTP in dev mode:
```json
{
  "message": "OTP sent successfully to doctor@example.com (DEV: 123456)"
}
```

