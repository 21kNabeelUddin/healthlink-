# Chatbot Setup Guide

## Issue: "Failed to reach the AI assistant"

The chatbot requires a Google Gemini API key to function. If you're seeing this error, follow these steps:

## Step 1: Get a Gemini API Key

1. Go to [Google AI Studio](https://makersuite.google.com/app/apikey) or [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new API key for the Generative Language API
3. Copy the API key (it will look like: `AIzaSy...`)

## Step 2: Configure the API Key

Create a file called `.env.local` in the `frontend/` directory:

```bash
cd frontend
```

Create the file and add:

```env
GEMINI_API_KEY=your-api-key-here
GEMINI_MODEL_NAME=gemini-1.5-flash
```

**Important:** 
- Replace `your-api-key-here` with your actual API key
- The `.env.local` file is already in `.gitignore` so it won't be committed
- Never share your API key publicly

## Step 3: Restart the Development Server

After creating/updating `.env.local`, you **must** restart the Next.js dev server:

1. Stop the current server (Ctrl+C in the terminal)
2. Run `npm run dev` again
3. The chatbot should now work

## Step 4: Verify It's Working

1. Navigate to `/patient/chatbot` in your browser
2. Try sending a message
3. You should receive a response from the AI

## Troubleshooting

### Still seeing errors?

1. **Check the API key is correct:**
   - Make sure there are no extra spaces or quotes around the key
   - Verify the key is active in Google AI Studio

2. **Check the model name:**
   - Valid models: `gemini-1.5-flash`, `gemini-1.5-pro`, `gemini-2.0-flash`
   - Default is `gemini-1.5-flash` if not specified

3. **Check server logs:**
   - Look at the terminal where `npm run dev` is running
   - Any errors will be logged there

4. **Verify the file location:**
   - The `.env.local` file must be in `frontend/.env.local`
   - Not in the root directory

5. **Check API quotas:**
   - Free tier has rate limits
   - If you hit the limit, wait a few minutes and try again

## Alternative: Check if API Key is Set

You can verify the API key is being read by checking the server logs when you make a request. If you see "GEMINI_API_KEY is not configured", the environment variable isn't being loaded.

