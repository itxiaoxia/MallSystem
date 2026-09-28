@echo off
setlocal

set "BASE_DIR=%~dp0"
set "MAVEN_VERSION=3.9.11"
set "MAVEN_DIR=%BASE_DIR%.mvn\apache-maven-%MAVEN_VERSION%"
set "MAVEN_ZIP=%BASE_DIR%.mvn\apache-maven-%MAVEN_VERSION%-bin.zip"
set "MAVEN_URL=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MAVEN_VERSION%/apache-maven-%MAVEN_VERSION%-bin.zip"

if not exist "%MAVEN_DIR%\bin\mvn.cmd" (
  if not exist "%MAVEN_ZIP%" (
    powershell.exe -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -UseBasicParsing -Uri '%MAVEN_URL%' -OutFile '%MAVEN_ZIP%'"
    if errorlevel 1 exit /b 1
  )
  powershell.exe -NoProfile -ExecutionPolicy Bypass -Command "Expand-Archive -LiteralPath '%MAVEN_ZIP%' -DestinationPath '%BASE_DIR%.mvn' -Force"
  if errorlevel 1 exit /b 1
)

call "%MAVEN_DIR%\bin\mvn.cmd" %*
exit /b %ERRORLEVEL%
