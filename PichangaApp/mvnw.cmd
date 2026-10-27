@echo off
setlocal

set MAVEN_WRAPPER_VERSION=3.1.0
set MAVEN_VERSION=3.10.0
set MAVEN_HOME=%USERPROFILE%\.m2\wrapper\dists\apache-maven-%MAVEN_VERSION%-bin

if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
    echo Descargando Maven %MAVEN_VERSION%...
    if not exist "%MAVEN_HOME%" mkdir "%MAVEN_HOME%"
    powershell -Command "Invoke-WebRequest -Uri 'https://archive.apache.org/dist/maven/maven-3/%MAVEN_VERSION%/binaries/apache-maven-%MAVEN_VERSION%-bin.zip' -OutFile '%TEMP%\maven.zip'; Expand-Archive -Path '%TEMP%\maven.zip' -DestinationPath '%MAVEN_HOME%' -Force"
    robocopy "%MAVEN_HOME%\apache-maven-%MAVEN_VERSION%" "%MAVEN_HOME%" /e /ndl /njs /nc /ns >nul
)

"%MAVEN_HOME%\bin\mvn.cmd" %*
