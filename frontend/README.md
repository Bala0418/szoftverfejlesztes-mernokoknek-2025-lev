Teremfoglalás frontend

Ez a mappa egy React (Vite) alapú frontendet tartalmaz, amely a Spring Boot backend-hez csatlakozik.

## Futtatás (Windows PowerShell)

```powershell
cd frontend
```

```javascript
npm install
```

```javascript
npm run dev
```

A böngésző automatikusan megnyílik `http://localhost:5174/` címen.

## Backend kapcsolat

A frontend a `http://localhost:8080` címen futó backend-et hívja.
- Környezeti változó: `VITE_API_BASE_URL` (lásd `.env.example`)
- Backend CORS konfiguráció beállítva: `localhost:5173` és `localhost:5174`

**Fontos**: Előbb indítsd el a backend-et (`backend/` mappa), majd a frontend-et!

## Fő oldalak

- **Főoldal**: marketing leírás
- **Bejelentkezés**: felhasználónév/email + jelszó (backend validáció)
- **Regisztráció**: felhasználónév, email, név, jelszó, jelszó megerősítés (auto-login sikeres regisztráció után)
- **Teremfoglalás**: szűrők + kártyanézet (backend adatokkal)
- **Profil**: adatmódosítás, aktív/korábbi foglalások (backend adatokkal)

## Navigáció és jogosultság

- **Vendég**: Főoldal, Bejelentkezés, Regisztráció
- **Bejelentkezve**: Főoldal, Teremfoglalás, Profil, Kijelentkezés
- JWT token tárolás: `localStorage.authToken`
