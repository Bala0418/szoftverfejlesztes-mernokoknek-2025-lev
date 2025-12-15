import React from 'react'
import { Link, Navigate } from 'react-router-dom'

export default function RegisterPage({ onRegister, isAuthenticated }) {
  if (isAuthenticated) {
    return <Navigate to="/booking" replace />
  }

  return (
    <section className="panel form-panel">
      <h2>Regisztráció</h2>
      <div className="form-grid">
        <div className="form-group">
          <label htmlFor="reg-username">Felhasználónév</label>
          <input id="reg-username" type="text" placeholder="pl. user" />
          <p className="hint">Hibaüzenet helye (backend után)</p>
        </div>
        <div className="form-group">
          <label htmlFor="reg-email">Email</label>
          <input id="reg-email" type="email" placeholder="user@example.com" />
          <p className="hint">Hibaüzenet helye (backend után)</p>
        </div>
        <div className="form-group">
          <label htmlFor="reg-name">Név</label>
          <input id="reg-name" type="text" placeholder="Teljes név" />
          <p className="hint">Hibaüzenet helye (backend után)</p>
        </div>
        <div className="form-group">
          <label htmlFor="reg-pass">Jelszó</label>
          <input id="reg-pass" type="password" placeholder="******" />
          <p className="hint">Hibaüzenet helye (backend után)</p>
        </div>
        <div className="form-group">
          <label htmlFor="reg-pass2">Jelszó megerősítése</label>
          <input id="reg-pass2" type="password" placeholder="******" />
          <p className="hint">Hibaüzenet helye (backend után)</p>
        </div>
      </div>
      <div className="form-actions">
        <Link to="/login" className="link-btn">Már van fiókod? Jelentkezz be!</Link>
        <button type="button" className="primary" onClick={onRegister}>
          Regisztráció (demo)
        </button>
      </div>
    </section>
  )
}
