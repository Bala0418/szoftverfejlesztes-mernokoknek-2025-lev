# Backend (Spring Boot) - futtatási útmutató

Hogyan futtassuk (helyben Windows PowerShell-ben):

1. Nyisd meg a projekt `backend` mappáját:
   ```powershell
   cd path\to\repo\backend
   ```

2. Futtasd a Maven build-et és indítsd el az alkalmazást:

   ```java
   mvn spring-boot:run
   ```

   vagy build + futtatás:

   ```java
   mvn -DskipTests package; java -jar target\backend-0.0.1-SNAPSHOT.jar
   ```

3. Tesztek futtatása:

   ```java
   mvn clean test
   ```
