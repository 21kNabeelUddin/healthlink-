# Email Troubleshooting Guide

## Current Status
Your Railway environment variables are **correctly configured**:
- ✅ `HEALTHLINK_OTP_EMAIL_ENABLED="true"`
- ✅ All email SMTP settings are present
- ✅ Mail from address is set

## What to Check After Redeploying

After pushing the updated code with enhanced logging, check Railway logs for:

### 1. Startup Configuration Check
Look for this on application startup:
```
📧 Email Service Configuration:
   Host: smtp.gmail.com
   Port: 587
   Username: k214558@nu.edu.pk
   Auth: true
   STARTTLS: true
   From: k214558@nu.edu.pk
```

If you see `NOT SET` or `localhost`, the environment variables aren't being read.

### 2. OTP Service Initialization
Look for:
```
OTP Service initialized
  redis_enabled: false
  email_enabled: true
```

If you see `email_enabled: false`, the `HEALTHLINK_OTP_EMAIL_ENABLED` variable isn't being read.

### 3. When User Signs Up
Look for these log messages in sequence:

**Success Path:**
```
EVENT: otp_generated_dev_mode | email: *** | otp: 123456
Attempting to send OTP email to: user@example.com
📧 Attempting to send email to: user@example.com with subject: Your HealthLink verification code
📧 Email from: k214558@nu.edu.pk
📧 Sending email via mailSender...
✅ Email sent successfully to: user@example.com
EVENT: email_sent | type: simple
```

**Failure Path:**
```
EVENT: otp_generated_dev_mode | email: *** | otp: 123456
Attempting to send OTP email to: user@example.com
📧 Attempting to send email to: user@example.com...
❌ Failed to send email to: user@example.com - Error: [error message]
❌ Exception type: [exception class]
EVENT: email_send_failed
```

## Common Gmail Issues

### Issue 1: Gmail App Password
If you're using Gmail, make sure:
- You're using an **App Password**, not your regular password
- To create an App Password:
  1. Go to Google Account → Security
  2. Enable 2-Step Verification (if not already)
  3. Go to App Passwords
  4. Generate a new app password for "Mail"
  5. Use that 16-character password (no spaces) in `MAIL_PASSWORD`

### Issue 2: Gmail Rate Limiting
Gmail has limits:
- 500 emails per day for free accounts
- 2000 emails per day for Google Workspace
- If you hit the limit, you'll see authentication errors

### Issue 3: Gmail Spam Filter
Even if emails are sent successfully, they might:
- Go to spam/junk folder
- Be delayed by Gmail's filters
- Be blocked if sending to many recipients

## Testing Steps

1. **Check Startup Logs**
   - Verify email configuration is loaded
   - Verify OTP email is enabled

2. **Sign Up a Test User**
   - Watch Railway logs in real-time
   - Look for the log sequence above

3. **Check Email**
   - Check inbox
   - Check spam/junk folder
   - Wait 1-2 minutes (Gmail can delay)

4. **If Still Not Working**
   - Share the Railway logs showing the email attempt
   - Look for any `❌` or `ERROR` messages
   - Check for SMTP authentication errors

## Next Steps

1. Push the updated code to Railway
2. Redeploy
3. Check startup logs for configuration
4. Try signing up a new user
5. Share the logs if emails still don't send

The enhanced logging will show exactly where the process fails!

