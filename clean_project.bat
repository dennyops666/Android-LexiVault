@echo off
echo Stopping Android Studio and related processes...
taskkill /F /IM java.exe 2>nul
taskkill /F /IM gradle.exe 2>nul
taskkill /F /IM studio64.exe 2>nul
timeout /t 5

echo Cleaning Gradle processes...
wmic process where "name like '%%java%%'" delete 2>nul
wmic process where "name like '%%gradle%%'" delete 2>nul
timeout /t 5

echo Cleaning Gradle caches...
rmdir /s /q "%USERPROFILE%\.gradle\caches" 2>nul
rmdir /s /q "%USERPROFILE%\.gradle\daemon" 2>nul
rmdir /s /q "%USERPROFILE%\.gradle\native" 2>nul
timeout /t 2

echo Cleaning Android Studio temporary files...
rmdir /s /q "%USERPROFILE%\.android" 2>nul
rmdir /s /q "%LOCALAPPDATA%\Google\AndroidStudio*" 2>nul
timeout /t 2

echo Cleaning project files...
rmdir /s /q ".gradle" 2>nul
rmdir /s /q ".idea" 2>nul
rmdir /s /q "build" 2>nul
rmdir /s /q "app\build" 2>nul
timeout /t 2

echo Cleaning Android project...

rem Stop Gradle Daemon
call gradlew.bat --stop

rem Remove build directories
rmdir /s /q build 2>nul
rmdir /s /q app\build 2>nul
rmdir /s /q .gradle 2>nul
rmdir /s /q .idea 2>nul

rem Clean Android Studio caches
rmdir /s /q "%LOCALAPPDATA%\Google\AndroidStudio*\system\caches" 2>nul

rem Clean Gradle caches
rmdir /s /q "%USERPROFILE%\.gradle\caches" 2>nul
rmdir /s /q "%USERPROFILE%\.gradle\wrapper" 2>nul

rem Initialize project
call gradlew.bat clean --refresh-dependencies --no-daemon

echo Project cleaned successfully!
echo Please restart your computer before restarting Android Studio.
pause
