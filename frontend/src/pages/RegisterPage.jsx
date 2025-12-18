import React, { useState } from 'react'
import { Link, Navigate } from 'react-router-dom'
import { register, login } from '../services/authService'

export default function RegisterPage({ onRegister, isAuthenticated }) {
  const [form, setForm] = useState({
    username: '',
    email: '',
    name: '',
    password: '',
    passwordConfirm: ''
  })
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
      const { passwordConfirm, name, ...rest } = form
      const userData = { ...rest, fullName: name }
      const response = await register(userData)
      // Registration successful, now auto-login
      if (response.message) {
        const loginResponse = await login(userData.username, userData.password)
        if (loginResponse.token) {
          localStorage.setItem('authToken', loginResponse.token)
          const user = { username: userData.username, email: userData.email, name: userData.fullName }
          onRegister(user)
        }
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
      
      // Client-side password confirmation validation
      if (!errorMessages.passwordConfirm) {
        if (!form.passwordConfirm && !form.password) {
          // If both empty
          errorMessages.passwordConfirm = 'A jelszó megadása kötelező.'
        }
        else if (!form.passwordConfirm && form.password) {
          // If both empty
          errorMessages.passwordConfirm = 'A jelszó nem egyezik.'
        } else if (form.password !== form.passwordConfirm) {
          // If confirmation doesn't match
          errorMessages.passwordConfirm = 'A jelszó nem egyezik.'
        }
      }
      
      setErrors(errorMessages)
    } finally {
      setLoading(false)
    }
  }

  return (
    <section className="panel form-panel">
      <h2>Regisztráció</h2>
      <div className="form-grid">
        <div className="form-group">
          <label htmlFor="reg-username">Felhasználónév</label>
          <input
            id="reg-username"
            name="username"
            type="text"
            placeholder="pl. user"
            value={form.username}
            onChange={handleChange}
            style={errors.username ? { borderColor: 'red', borderWidth: '2px' } : {}}
          />
          {errors.username && <p className="hint" style={{ color: 'red', marginTop: '4px' }}>{String(errors.username)}</p>}
        </div>
        <div className="form-group">
          <label htmlFor="reg-email">Email</label>
          <input
            id="reg-email"
            name="email"
            type="email"
            placeholder="user@example.com"
            value={form.email}
            onChange={handleChange}
            style={errors.email ? { borderColor: 'red', borderWidth: '2px' } : {}}
          />
          {errors.email && <p className="hint" style={{ color: 'red', marginTop: '4px' }}>{String(errors.email)}</p>}
        </div>
        <div className="form-group">
          <label htmlFor="reg-name">Név</label>
          <input
            id="reg-name"
            name="name"
            type="text"
            placeholder="Teljes név"
            value={form.name}
            onChange={handleChange}
            style={errors.name || errors.fullName ? { borderColor: 'red', borderWidth: '2px' } : {}}
          />
          {(errors.name || errors.fullName) && <p className="hint" style={{ color: 'red', marginTop: '4px' }}>{String(errors.name || errors.fullName)}</p>}
        </div>
        <div className="form-group">
          <label htmlFor="reg-pass">Jelszó</label>
          <input
            id="reg-pass"
            name="password"
            type="password"
            placeholder="******"
            value={form.password}
            onChange={handleChange}
            style={errors.password ? { borderColor: 'red', borderWidth: '2px' } : {}}
          />
          {errors.password && <p className="hint" style={{ color: 'red', marginTop: '4px' }}>{String(errors.password)}</p>}
        </div>
        <div className="form-group">
          <label htmlFor="reg-pass2">Jelszó megerősítése</label>
          <input
            id="reg-pass2"
            name="passwordConfirm"
            type="password"
            placeholder="******"
            value={form.passwordConfirm}
            onChange={handleChange}
            style={errors.passwordConfirm ? { borderColor: 'red', borderWidth: '2px' } : {}}
          />
          {errors.passwordConfirm && <p className="hint" style={{ color: 'red', marginTop: '4px' }}>{String(errors.passwordConfirm)}</p>}
        </div>
      </div>
      <div className="form-actions">
        <Link to="/login" className="link-btn">Már van fiókod? Jelentkezz be!</Link>
        <button type="button" className="primary" onClick={handleSubmit} disabled={loading}>
          {loading ? 'Betöltés...' : 'Regisztráció'}
        </button>
      </div>
    </section>
  )
}
