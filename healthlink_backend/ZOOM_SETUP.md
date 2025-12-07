# Zoom API Integration Setup Guide

This guide explains how to set up Zoom API integration for online appointments in HealthLink+.

## Prerequisites

1. A Zoom account (Pro, Business, or Enterprise plan recommended)
2. Access to Zoom Marketplace: https://marketplace.zoom.us/

## Step 1: Create a Server-to-Server OAuth App

1. Go to [Zoom Marketplace](https://marketplace.zoom.us/)
2. Sign in with your Zoom account
3. Click **"Develop"** → **"Build App"**
4. Select **"Server-to-Server OAuth"** app type
5. Fill in the app details:
   - **App Name**: HealthLink+ Video Consultations
   - **Company Name**: Your company name
   - **Developer Contact Information**: Your email
6. Click **"Create"**

## Step 2: Configure App Scopes

1. In your app settings, go to **"Scopes"** tab
2. Add the following scopes:
   - `meeting:write` -  
   - `meeting:read` - Read meeting information
   - `user:read` - Read user information (optional, for user details)
3. Click **"Save"**

## Step 3: Get Credentials

1. Go to **"App Credentials"** tab
2. You'll need:
   - **Account ID** (found at the top of the page)
   - **Client ID** (under "OAuth 2.0")
   - **Client Secret** (click "Show" to reveal, then copy)

## Step 4: Activate the App

1. Go to **"Activation"** tab
2. Click **"Activate your app"**
3. Follow the prompts to activate

## Step 5: Configure Environment Variables

Add the following to your `.env` file in `healthlink_backend/`:

```env
# Zoom API Configuration
ZOOM_ENABLED=true
ZOOM_ACCOUNT_ID=your_account_id_here
ZOOM_CLIENT_ID=your_client_id_here
ZOOM_CLIENT_SECRET=your_client_secret_here
```

**Important Security Notes:**
- Never commit `.env` file to version control
- Use environment variables in production
- Rotate credentials if compromised

## Step 6: Test the Integration

1. Start your backend server
2. Create an appointment with `type: "ONLINE"`
3. Check the logs for Zoom meeting creation
4. Verify the appointment response includes Zoom meeting details:
   - `zoomMeetingId`
   - `zoomJoinUrl`
   - `zoomStartUrl`
   - `zoomMeetingPassword`

## How It Works

### Meeting Creation Flow

1. When a patient books an **ONLINE** appointment:
   - The system automatically creates a Zoom meeting
   - Meeting details are stored in the `appointments` table
   - Meeting is scheduled for the appointment time

2. Meeting Settings:
   - **Host video**: Enabled
   - **Participant video**: Enabled
   - **Join before host**: Disabled (participants wait for host)
   - **Waiting room**: Disabled
   - **Auto-recording**: Disabled
   - **Password**: Auto-generated (6 characters)

3. Meeting Access:
   - **Join URL**: For patients and participants
   - **Start URL**: For the doctor (host)
   - **Password**: Required for all participants

### API Endpoints

The Zoom integration is automatically triggered when:
- Creating an appointment with `type: "ONLINE"`
- The appointment is saved to the database

### Error Handling

- If Zoom API fails, the appointment is still created (Zoom is optional)
- Errors are logged but don't block appointment creation
- Check logs for Zoom-related errors

## Troubleshooting

### Common Issues

1. **"Failed to obtain Zoom access token"**
   - Verify `ZOOM_ACCOUNT_ID`, `ZOOM_CLIENT_ID`, and `ZOOM_CLIENT_SECRET` are correct
   - Check that the app is activated in Zoom Marketplace
   - Ensure scopes are properly configured

2. **"Failed to create Zoom meeting"**
   - Check Zoom API rate limits (100 requests per second)
   - Verify the account has meeting creation permissions
   - Check network connectivity to Zoom API

3. **Meetings not appearing in Zoom**
   - Verify the account ID matches the app owner
   - Check that the app is activated
   - Ensure meeting creation scopes are granted

### Testing Without Zoom

To test the system without Zoom:
```env
ZOOM_ENABLED=false
```

The appointment will be created without Zoom meeting details.

## Production Considerations

1. **Rate Limiting**: Zoom API has rate limits (100 req/sec). Implement retry logic if needed.

2. **Meeting Cleanup**: Consider implementing a job to delete old Zoom meetings after appointments are completed.

3. **Error Monitoring**: Set up alerts for Zoom API failures.

4. **Backup Plan**: Consider keeping WebRTC (Janus) as a fallback if Zoom fails.

## Additional Resources

- [Zoom API Documentation](https://developers.zoom.us/docs/api/)
- [Server-to-Server OAuth Guide](https://developers.zoom.us/docs/api/rest/using-zoom-apis/)
- [Meeting API Reference](https://developers.zoom.us/docs/api/rest/reference/zoom-api/methods/#tag/Meetings)

