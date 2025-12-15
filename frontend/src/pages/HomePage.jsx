import React from 'react'

export default function HomePage() {
  return (
    <section className="panel">
      <h1>Teremfoglalás, egyszerűen</h1>
      <p className="lead">
        Gyors keresés, szűrés és foglalás campus- vagy irodatermekre. A rendszer támogatja az egyszeri és
        visszatérő foglalásokat, valamint az erőforrások (projektor, hangszigetelés, online meeting)
        kiválasztását.
      </p>
      <div className="pill-row">
        <span className="pill">Egyszerű kereső</span>
        <span className="pill">Visszaigazolás e-mailben</span>
        <span className="pill">Profil és előzmények</span>
      </div>
    </section>
  )
}
