Author: Mateusz Żurowski
e-mail: mateusz.zurowski@gmail.com

1. The application is my pet project. I created it for three reasons: learn more about REST API, get first experience
with Spring and build something with JavaFX. Many thanks to Marco Jakob for a great tutorial:
http://code.makery.ch/library/javafx-8-tutorial/

2. This application was not designed to be safe for communication.

HOW TO RUN

1. Ensure that port 8080 on localhost is unused.
2. Run gs-rest-service-*PROPER_VERSIO*.jar. Make sure to run it in command prompt - otherwise you will need to end the process manually.
You can also build gs-rest-service:
Run command prompt. Go to application directory and then to "mess" folder. Run command
	gradlew bootrun
on Windows or
	./gradlew bootrun
on Unix.  You will need at least Java 1.8_u91. Note: you can run gradlew.bat without cmd.exe, but you will need to kill the process manually.
Wait for Spring bootrun to run the server.
3. Run FX-MessangerClient.jar. You will need at least Java 1.8_u91. 
4. To stop the server press CTRL+C in cmd and confirm.

USAGE

1. You'll be asked for service URL, leave default if you didn't change it.
2. Default admin credentials:
login: admin
passowrd: admin
3. You can add user request and specify your login and password.
4. You can run multiple clients to test functionality.
5. If you sign in as admin, you will have an access to administration window. You can kick/delete user, add 
new user, accept or reject user request.
6. You can run multiple client apps to test communication.
