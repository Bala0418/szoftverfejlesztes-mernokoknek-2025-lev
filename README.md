# Szoftverfejlesztés mérnököknek 2025 - Teremfoglaló rendszer

Ez egy teljes körű teremfoglaló alkalmazás Spring Boot backend-del és React frontend-del.

## 📋 Előkövetelmények

A projekt futtatásához az alábbiak szükségesek:

- **Java 17** vagy újabb (JDK)
- **Maven 3.6+** (Java build tool)
- **Node.js 18+** és **npm** (JavaScript runtime és package manager)
- **Git** (verziókezelés)

## 🚀 Gyors kezdés

### 1. Backend indítása

```bash
cd backend
mvn spring-boot:run
```

A backend elérhető lesz a `http://localhost:8080` címen.

Részletes backend dokumentáció: [backend/README.md](backend/README.md)

### 2. Frontend indítása

```bash
cd frontend
npm install
npm run dev
```

A frontend elérhető lesz a `http://localhost:5174` címen.

Részletes frontend dokumentáció: [frontend/README.md](frontend/README.md)

## 🧪 Tesztek futtatása

Backend unit tesztek (30 teszt):

```bash
cd backend
mvn test
```

## 📚 Projekt felépítés

```
.
├── backend/          # Spring Boot backend (Java 17)
│   ├── src/
│   │   ├── main/     # Backend forráskód
│   │   └── tests/    # Unit tesztek (JUnit + Mockito)
│   └── README.md
│
└── frontend/         # React + Vite frontend
    ├── src/
    │   ├── components/
    │   ├── pages/
    │   └── services/
    └── README.md
```

## 🔑 Funkciók

- Felhasználói regisztráció és bejelentkezés (JWT autentikáció)
- Terem keresés és szűrés
- Teremfoglalás létrehozása, módosítása, törlése
- Profil szerkesztés és jelszóváltoztatás
- Aktív foglalások kezelése

## 🛠️ Technológiák

### Backend
- Spring Boot 3.1.6
- Spring Security + JWT
- JPA/Hibernate
- H2 Database (in-memory)
- Maven
- JUnit 5 + Mockito

### Frontend
- React 18.2.0
- Vite 5.0.0
- React Router DOM 6.28.0
- Axios (HTTP kliens)