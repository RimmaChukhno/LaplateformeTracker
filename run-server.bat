@echo off
REM Script to launch the TrackerHttpServer
mvn clean compile exec:java -Dexec.mainClass="com.laplateforme.tracker.server.TrackerHttpServer"
pause 