import React from 'react'
import { Link, Navigate } from 'react-router-dom'

export default function LoginPage({ onLogin, isAuthenticated }) {
  if (isAuthenticated) {
    return <Navigate to="/booking" replace />
  }

  return (
    <section className="panel form-panel">
      <h2>Bejelentkezés</h2>
      <div className="form-group">
        <label htmlFor="login-username">Felhasználónév vagy email</label>
        <input id="login-username" type="text" placeholder="user vagy user@example.com" />
        <p className="hint">Hibaüzenet helye (backend után)</p>
      </div>
      <div className="form-group">
        <label htmlFor="login-password">Jelszó</label>
        <input id="login-password" type="password" placeholder="******" />
        <p className="hint">Hibaüzenet helye (backend után)</p>
      </div>
      <div className="form-actions">
        <Link to="/register" className="link-btn">Még nincs fiókod? Regisztrálj egyet!</Link>
        <button type="button" className="primary" onClick={onLogin}>
          Bejelentkezés (demo: user / user)
        </button>
      </div>
    </section>
  )
}
