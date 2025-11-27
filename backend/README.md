# Backend (Spring Boot) - futtatási útmutató

Ez a mappa egy nagyon egyszerű Spring Boot backend skeleton-t tartalmaz Java/Maven alapokon.

Mi található itt?
- Egy minimális REST kontroller (`/api/items`) amely egyetlen listázó (GET) végpontot szolgál ki.
- Egy egyszerű in-memory repository (`ItemRepository`) dummy adatokhoz.

Hogyan futtassuk (helyben Windows PowerShell-ben):

1. Nyisd meg a projekt `backend` mappáját:

   cd path\to\repo\backend

2. Futtasd a Maven build-et és indítsd el az alkalmazást:

   mvn spring-boot:run

   vagy build + futtatás:

   mvn -DskipTests package; java -jar target\backend-0.0.1-SNAPSHOT.jar

Alapértelmezett API:
- GET /api/items  — visszaadja az összes item-et (kezdésnek együttesen ez a dummy CRUD endpoint).
