import React, { useState } from 'react'
import { Link, Navigate } from 'react-router-dom'
import { login } from '../services/authService'

export default function LoginPage({ onLogin, isAuthenticated }) {
  const [form, setForm] = useState({ usernameOrEmail: '', password: '' })
  const [errors, setErrors] = useState({})
  const [loading, setLoading] = useState(false)

  if (isAuthenticated) {
    return <Navigate to="/booking" replace />
  }

  const handleChange = e => {
    const { name, value } = e.target
    setForm(prev => ({ ...prev, [name]: value }))
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }))
    }
  }

  const handleSubmit = async () => {
    setErrors({})
    setLoading(true)
    try {
      const response = await login(form.usernameOrEmail, form.password)
      if (response.token) {
        localStorage.setItem('authToken', response.token)
        // Store user data from backend response
        const user = {
          username: response.username,
          email: response.email,
          name: response.name
        }
        onLogin(user)
      }
    } catch (error) {
      // Convert error.errors object values to strings
      const errorMessages = {}
      if (error.errors && typeof error.errors === 'object') {
        Object.keys(error.errors).forEach(key => {
          const value = error.errors[key]
          errorMessages[key] = typeof value === 'string' ? value : JSON.stringify(value)
        })
      }
      // Check for error.error (backend authentication error)
      const generalError = error.error || error.message
      setErrors({ general: generalError, ...errorMessages })
    } finally {
      setLoading(false)
    }
  }

  return (
    <section className="panel form-panel">
      <h2>Bejelentkezés</h2>
      <div className="form-group">
        <label htmlFor="login-username">Felhasználónév vagy email</label>
        <input
          id="login-username"
          name="usernameOrEmail"
          type="text"
          placeholder="user vagy user@example.com"
          value={form.usernameOrEmail}
          onChange={handleChange}
          style={errors.usernameOrEmail ? { borderColor: 'red', borderWidth: '2px' } : {}}
        />
        {errors.usernameOrEmail && <p className="hint" style={{ color: 'red', marginTop: '4px' }}>{String(errors.usernameOrEmail)}</p>}
      </div>
      <div className="form-group">
        <label htmlFor="login-password">Jelszó</label>
        <input
          id="login-password"
          name="password"
          type="password"
          placeholder="******"
          value={form.password}
          onChange={handleChange}
          style={errors.password ? { borderColor: 'red', borderWidth: '2px' } : {}}
        />
        {errors.password && <p className="hint" style={{ color: 'red', marginTop: '4px' }}>{String(errors.password)}</p>}
        {errors.general && !errors.password && !errors.usernameOrEmail && (
          <p className="hint" style={{ color: 'red', marginTop: '4px' }}>{String(errors.general)}</p>
        )}
      </div>
      <div className="form-actions">
        <Link to="/register" className="link-btn">Még nincs fiókod? Regisztrálj egyet!</Link>
        <button type="button" className="primary" onClick={handleSubmit} disabled={loading}>
          {loading ? 'Betöltés...' : 'Bejelentkezés'}
        </button>
      </div>
    </section>
  )
}
