A starter project containing:
- Spring boot 2
- Gradle 6x
- Java 11
- App Engine
- Lombok
- Spock
- Google PubSub (disabled in the main class by default)

To Deploy:
- Run the command  `./gradlew appengineDeploy -PgoogleProjectId <your projectid>` 

To Run Locally:
- Start the local Datastore emulator with `gcloud beta emulators datastore start`
- Start the application with `./gradle bootRun`