@echo off
setlocal
cd /d "%~dp0"
where node >nul 2>nul
if errorlevel 1 (
  echo Node.js is not installed or is not on PATH.
  echo Install Node.js 18 or newer from https://nodejs.org/ and run this file again.
  pause
  exit /b 1
)
echo Starting Tic-Tac-Toe Next...
echo Keep this window open while playing.
echo Open http://localhost:3000 in your browser.
node server.js
pause
