@echo off
setlocal

cd /d "%~dp0"
call "%~dp0mvnw.cmd" -DskipTests package %*
exit /b %ERRORLEVEL%
