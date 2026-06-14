@echo off
cd /d "%~dp0"
echo Starting FastAIMemory German Corrector Demo...
mvn test-compile exec:java "-Dexec.mainClass=fastaimemory.Demo" "-Dexec.classpathScope=test"
pause
