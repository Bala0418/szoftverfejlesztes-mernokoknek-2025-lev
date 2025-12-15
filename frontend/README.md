Teremfoglalás frontend

Ez a mappa egy egyszerű React (Vite) alapú frontendet tartalmaz.

Futtatás (Windows PowerShell):

```powershell
cd frontend
npm install
npm run dev
```

A böngészőben a megadott `localhost` címen jelenik meg a "Teremfoglalás projekt" cím.
 
Fő oldalak (wireframe):
- Főoldal: rövid marketing leírás.
- Bejelentkezés: felhasználónév/email + jelszó mezők, helyfoglaló hibák, demo bejelentkezés gomb (user / user).
- Regisztráció: felhasználónév, email, név, jelszó, jelszó ismét; helyfoglaló hibák.
- Teremfoglalás: kártyanézet mint a csatolt minta, szűrők oldalt; demo gombok.
- Profil: adatmódosítás form, aktív és legutóbbi foglalások listája.

Navigáció és jogosultság:
- Vendég: Főoldal, Bejelentkezés, Regisztráció.
- Bejelentkezve: Főoldal, Teremfoglalás, Profil, Kijelentkezés.
- A bejelentkezés demo mód (backend nélkül) a "Bejelentkezés" oldalon található gombbal történik.
