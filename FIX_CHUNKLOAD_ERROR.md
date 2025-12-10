# Fix ChunkLoadError in Next.js

## Problem
You're getting a `ChunkLoadError: Loading chunk app/layout failed` error when accessing the doctor prescription page. This is a Next.js build cache corruption issue.

## Solution

### Step 1: Stop the Development Server
Press `Ctrl+C` in the terminal where your Next.js dev server is running.

### Step 2: Clear Next.js Cache
Run these commands in PowerShell from the `frontend` directory:

```powershell
cd frontend

# Delete .next folder (build cache)
Remove-Item -Recurse -Force .next -ErrorAction SilentlyContinue

# Delete node_modules/.cache if it exists
Remove-Item -Recurse -Force node_modules/.cache -ErrorAction SilentlyContinue

Write-Host "Cache cleared successfully!"
```

### Step 3: Restart Development Server
```powershell
npm run dev
```

Or if you're using a different command:
```powershell
yarn dev
# or
pnpm dev
```

### Step 4: Hard Refresh Browser
After the server restarts:
1. Open your browser
2. Press `Ctrl+Shift+R` (Windows) or `Cmd+Shift+R` (Mac) to hard refresh
3. Or clear browser cache and reload

## Alternative: Full Clean Rebuild

If the above doesn't work, try a full clean rebuild:

```powershell
cd frontend

# Stop server first (Ctrl+C)

# Delete all caches
Remove-Item -Recurse -Force .next -ErrorAction SilentlyContinue
Remove-Item -Recurse -Force node_modules/.cache -ErrorAction SilentlyContinue

# Reinstall dependencies (optional, but can help)
npm install

# Restart dev server
npm run dev
```

## Why This Happens

ChunkLoadError occurs when:
- Next.js build cache gets corrupted
- Development server is interrupted during build
- Browser cache conflicts with new chunks
- Network issues during chunk loading

The fix is to clear the `.next` folder which contains the build cache.

## Prevention

- Always stop the dev server properly (Ctrl+C) before closing terminal
- Avoid force-killing the Node.js process
- Clear cache if you see chunk errors after code changes

