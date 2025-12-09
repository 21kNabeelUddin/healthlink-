# Railway Email Configuration Guide

## Problem: OTP Emails Not Being Sent

If OTP emails are not being sent on Railway, check the following:

## Required Environment Variables in Railway

Make sure these are set in your Railway project settings:

### 1. OTP Email Enable (CRITICAL)
```
HEALTHLINK_OTP_EMAIL_ENABLED=true
```
**This is the most common issue!** If this is not set to `true`, emails will NOT be sent.

### 2. Email Configuration
```
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=k214558@nu.edu.pk
MAIL_PASSWORD=xatx gtox mbse upzs
MAIL_SMTP_AUTH=true
MAIL_SMTP_STARTTLS_ENABLE=true
MAIL_SMTP_STARTTLS_REQUIRED=true
```

### 3. Email From Address
```
HEALTHLINK_MAIL_FROM=k214558@nu.edu.pk
HEALTHLINK_MAIL_FROM_NAME=HealthLink Platform
```

## How to Check Railway Environment Variables

1. Go to your Railway project dashboard
2. Click on your backend service
3. Go to the "Variables" tab
4. Verify all the above variables are set correctly

## How to Verify Email is Working

After setting the variables and redeploying, check the Railway logs for:

### ✅ Success Indicators:
- `OTP Service initialized` with `email_enabled: true`
- `Attempting to send OTP email to: ...`
- `Email sent successfully to: ...`
- `EVENT: email_sent`

### ❌ Failure Indicators:
- `⚠️ OTP EMAIL IS DISABLED!` - Means `HEALTHLINK_OTP_EMAIL_ENABLED` is not set or is false
- `OTP email skipped - email is disabled` - Same issue
- `Failed to send email` - Email configuration issue
- `Unable to connect to SMTP server` - Network/authentication issue

## Common Issues

### Issue 1: Email Not Enabled
**Symptom:** No email logs at all, or "OTP email skipped" messages
**Solution:** Set `HEALTHLINK_OTP_EMAIL_ENABLED=true` in Railway

### Issue 2: SMTP Authentication Failed
**Symptom:** "Authentication failed" errors in logs
**Solution:** 
- Verify `MAIL_USERNAME` and `MAIL_PASSWORD` are correct
- For Gmail, make sure you're using an App Password, not your regular password
- Check that "Less secure app access" is enabled (if using regular password)

### Issue 3: Connection Timeout
**Symptom:** "Connection refused" or "Timeout" errors
**Solution:**
- Verify `MAIL_HOST` and `MAIL_PORT` are correct
- Check Railway firewall/network settings
- Gmail SMTP: `smtp.gmail.com:587`

### Issue 4: Emails Going to Spam
**Symptom:** Emails sent but in spam/junk folder
**Solution:**
- This is normal for new email addresses
- Check spam/junk folder
- Add sender to contacts
- Configure SPF/DKIM records (advanced)

## Testing

After configuration, test by:
1. Signing up a new user
2. Check Railway logs for email sending activity
3. Check the user's email (including spam folder)

## Debug Logs

The updated code now includes detailed logging:
- `📧 Attempting to send email to: ...` - Email service called
- `✅ Email sent successfully` - Email sent
- `❌ Failed to send email` - Email failed with error details

Check Railway logs for these messages to diagnose issues.

