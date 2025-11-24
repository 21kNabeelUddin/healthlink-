# Fix JAVA_HOME Error

## The Problem
```
The JAVA_HOME environment variable is not defined correctly,
this environment variable is needed to run this program.
```

This means Java is either:
1. Not installed
2. Installed but JAVA_HOME is not set
3. JAVA_HOME points to wrong location

---

## Solution

### Step 1: Check if Java is Installed

Open PowerShell and run:
```powershell
java -version
```

**If you see an error** like "java is not recognized":
- Java is not installed or not in PATH
- Install Java 21 from: https://www.oracle.com/java/technologies/downloads/#java21
- Choose "Windows x64 Installer" (JDK 21)

**If you see version info** (like "java version 21.x.x"):
- Java is installed, proceed to Step 2

---

### Step 2: Find Java Installation Path

After installing Java, find where it's installed. Common locations:

- `C:\Program Files\Java\jdk-21`
- `C:\Program Files\Java\jdk-21.0.x`
- `C:\Program Files (x86)\Java\jdk-21`

**To find it automatically:**
```powershell
where.exe java
```

This shows the Java executable path. The JDK folder is usually one level up.

---

### Step 3: Set JAVA_HOME (Temporary - Current Session Only)

**Quick fix for this session:**
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
```

Replace the path with your actual Java installation path.

Then verify:
```powershell
echo $env:JAVA_HOME
```

Now try running the backend again:
```powershell
.\mvnw.cmd spring-boot:run
```

---

### Step 4: Set JAVA_HOME Permanently (Recommended)

**Option A: Using PowerShell (Run as Administrator)**

```powershell
# Set JAVA_HOME system-wide
[System.Environment]::SetEnvironmentVariable('JAVA_HOME', 'C:\Program Files\Java\jdk-21', [System.EnvironmentVariableTarget]::Machine)

# Add to PATH
$currentPath = [System.Environment]::GetEnvironmentVariable('Path', [System.EnvironmentVariableTarget]::Machine)
$newPath = "$currentPath;C:\Program Files\Java\jdk-21\bin"
[System.Environment]::SetEnvironmentVariable('Path', $newPath, [System.EnvironmentVariableTarget]::Machine)
```

**Option B: Using Windows GUI**

1. Press `Win + X` and select "System"
2. Click "Advanced system settings"
3. Click "Environment Variables"
4. Under "System variables", click "New"
5. Variable name: `JAVA_HOME`
6. Variable value: `C:\Program Files\Java\jdk-21` (your actual path)
7. Click "OK"
8. Find "Path" in System variables, click "Edit"
9. Click "New" and add: `%JAVA_HOME%\bin`
10. Click "OK" on all dialogs
11. **Close and reopen PowerShell** for changes to take effect

---

### Step 5: Verify Setup

Close and reopen PowerShell, then run:

```powershell
java -version
echo $env:JAVA_HOME
```

Both should show correct values.

---

## Alternative: Use Full Java Path in Maven Wrapper

If you can't set JAVA_HOME, you can modify `mvnw.cmd` to use Java directly, but this is not recommended.

---

## Quick Test

After setting JAVA_HOME, test with:

```powershell
.\mvnw.cmd --version
```

This should show Maven version. If it works, you're good to go!

---

## Still Having Issues?

1. **Make sure you installed JDK (not just JRE)**
   - JDK = Java Development Kit (includes compiler)
   - JRE = Java Runtime Environment (only runs programs)

2. **Check the path is correct**
   - JAVA_HOME should point to the JDK folder (not bin subfolder)
   - Example: `C:\Program Files\Java\jdk-21` ✅
   - Wrong: `C:\Program Files\Java\jdk-21\bin` ❌

3. **Restart your terminal/PowerShell** after setting environment variables

4. **Check for multiple Java installations**
   ```powershell
   Get-ChildItem "C:\Program Files\Java" -Directory
   ```
   Use the latest JDK 21 version.

