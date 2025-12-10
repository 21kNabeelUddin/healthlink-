# Railway Email Setup Guide

## Problem
Railway (and many cloud platforms) block outbound SMTP connections to Gmail on ports 587/465. This causes email sending to fail with connection timeouts.

## Error Symptoms
```
java.net.ConnectException: Connection timed out
Couldn't connect to host, port: smtp.gmail.com, 587
```

## Solutions

### Option 1: Use SendGrid (Recommended for Production)

1. **Sign up for SendGrid** (free tier: 100 emails/day)
   - Go to https://sendgrid.com
   - Create account and verify email
   - Create API key in Settings > API Keys

2. **Update Railway Environment Variables:**
   ```env
   MAIL_HOST=smtp.sendgrid.net
   MAIL_PORT=587
   MAIL_USERNAME=apikey
   MAIL_PASSWORD=YOUR_SENDGRID_API_KEY
   MAIL_SMTP_AUTH=true
   MAIL_SMTP_STARTTLS_ENABLE=true
   MAIL_SMTP_STARTTLS_REQUIRED=true
   HEALTHLINK_MAIL_FROM=your-verified-sender@yourdomain.com
   HEALTHLINK_MAIL_FROM_NAME=HealthLink Platform
   ```

3. **Verify sender email** in SendGrid dashboard (Settings > Sender Authentication)

### Option 2: Use Mailgun (Alternative)

1. **Sign up for Mailgun** (free tier: 5,000 emails/month)
   - Go to https://www.mailgun.com
   - Create account and verify domain
   - Get SMTP credentials from Settings > Sending > SMTP credentials

2. **Update Railway Environment Variables:**
   ```env
   MAIL_HOST=smtp.mailgun.org
   MAIL_PORT=587
   MAIL_USERNAME=postmaster@YOUR_DOMAIN.mailgun.org
   MAIL_PASSWORD=YOUR_MAILGUN_SMTP_PASSWORD
   MAIL_SMTP_AUTH=true
   MAIL_SMTP_STARTTLS_ENABLE=true
   MAIL_SMTP_STARTTLS_REQUIRED=true
   HEALTHLINK_MAIL_FROM=noreply@YOUR_DOMAIN.com
   HEALTHLINK_MAIL_FROM_NAME=HealthLink Platform
   ```

### Option 3: Use AWS SES (For AWS Users)

1. **Set up AWS SES**
   - Go to AWS Console > SES
   - Verify sender email/domain
   - Get SMTP credentials

2. **Update Railway Environment Variables:**
   ```env
   MAIL_HOST=email-smtp.REGION.amazonaws.com
   MAIL_PORT=587
   MAIL_USERNAME=YOUR_AWS_SES_SMTP_USERNAME
   MAIL_PASSWORD=YOUR_AWS_SES_SMTP_PASSWORD
   MAIL_SMTP_AUTH=true
   MAIL_SMTP_STARTTLS_ENABLE=true
   MAIL_SMTP_STARTTLS_REQUIRED=true
   HEALTHLINK_MAIL_FROM=your-verified-email@yourdomain.com
   HEALTHLINK_MAIL_FROM_NAME=HealthLink Platform
   ```

### Option 4: Temporary Workaround - Check Logs for OTP

If email fails, the OTP is now logged in the application logs. You can:
1. Check Railway logs for the OTP
2. Look for: `⚠️ OTP for {email} is: {otp}`
3. Use the OTP from logs to verify the account

**Note:** This is only for development/testing. Use a proper email service for production.

## Testing Email Configuration

After updating environment variables:
1. Redeploy on Railway
2. Try doctor signup
3. Check logs for email sending status
4. Verify email is received

## Current Configuration Check

Your current Railway config shows:
- ✅ `MAIL_HOST=smtp.gmail.com` (will fail on Railway)
- ✅ `MAIL_PORT=587` (correct for STARTTLS)
- ✅ `MAIL_USERNAME` and `MAIL_PASSWORD` are set
- ✅ `MAIL_SMTP_AUTH=true` and `MAIL_SMTP_STARTTLS_ENABLE=true`

**Action Required:** Change `MAIL_HOST` to a cloud-friendly service (SendGrid, Mailgun, or AWS SES).

## Additional Notes

- Gmail SMTP works fine on localhost but is blocked by most cloud platforms
- SendGrid is the easiest to set up and has a generous free tier
- Make sure to verify your sender email/domain in the chosen service
- OTP emails are now logged if sending fails (check Railway logs)

