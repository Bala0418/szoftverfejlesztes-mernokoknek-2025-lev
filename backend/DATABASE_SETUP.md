# H2 Database Integration

## Változtatások

### 1. Függőségek hozzáadása (pom.xml)
- `spring-boot-starter-data-jpa` - Spring Data JPA támogatás
- `h2` - H2 adatbázis driver

### 2. Perzisztens tárolás beállítása

#### application.properties
A backend most file-alapú H2 adatbázist használ:
- **URL**: `jdbc:h2:file:./data/itemdb`
- **Dialect**: H2Dialect
- **DDL auto**: update (automatikusan létrehozza/frissíti a táblákat)
- **H2 Console**: Elérhető a `/h2-console` útvonalon

#### Adatbázis konzol használata
Az alkalmazás futása közben a H2 webes konzol elérhető:
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:file:./data/itemdb`
- Username: `sa`
- Password: (üres)

### 3. Módosított fájlok

#### Item.java
- JPA annotációk hozzáadva (`@Entity`, `@Table`, `@Id`, `@GeneratedValue`, `@Column`)
- Az osztály most perzisztens entitás

#### ItemRepository.java
- Átírtam Spring Data JPA repository interface-re
- Már nem kell manuálisan implementálni a CRUD műveleteket
- A `JpaRepository` automatikusan biztosítja a `findAll()`, `save()`, `findById()`, stb. metódusokat

#### DataInitializer.java (új)
- Inicializálja az adatbázist példaadatokkal az első indításkor
- Csak akkor fut le, ha az adatbázis üres

### 4. Unit tesztek

#### ItemControllerGherkinTest.java
- Frissítve, hogy működjön az új repository interface-el
- Most már Mockito-val lehet mockolni (mivel interface, nem konkrét osztály)

#### Test Configuration
- A tesztek in-memory H2 adatbázist használnak
- Minden teszt után az adatbázis törlődik (`ddl-auto=create-drop`)

## Adatbázis fájlok
Az adatbázis a `./data/itemdb.mv.db` fájlban tárolódik. Ez perzisztens, tehát az alkalmazás újraindítása után is megmaradnak az adatok.

## Tesztelés

```bash
# Unit tesztek futtatása
mvn test

# Alkalmazás buildelése
mvn clean package

# Alkalmazás futtatása
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

## API Endpoints
- `GET /api/items` - Összes item lekérése
- Új endpoints egyszerűen hozzáadhatók a controllerhez

