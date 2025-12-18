import React, { useState } from 'react'
import { updateProfile } from '../services/profileService'

export default function ChangePasswordModal({ user, onClose, onSuccess }) {
  const [form, setForm] = useState({
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  })
  const [errors, setErrors] = useState({})
  const [loading, setLoading] = useState(false)

  const handleChange = (e) => {
    const { name, value } = e.target
    setForm(prev => ({ ...prev, [name]: value }))
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }))
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    setErrors({})
    
    const errorMessages = {}
    
    if (!form.currentPassword) {
      errorMessages.currentPassword = 'Add meg a jelenlegi jelszavadat.'
    }
    if (!form.newPassword) {
      errorMessages.newPassword = 'Add meg az új jelszót.'
    }
    if (!form.confirmPassword) {
      errorMessages.confirmPassword = 'Erősítsd meg az új jelszót.'
    }
    if (form.newPassword && form.confirmPassword && form.newPassword !== form.confirmPassword) {
      errorMessages.confirmPassword = 'A jelszavak nem egyeznek.'
    }
    
    if (Object.keys(errorMessages).length > 0) {
      setErrors(errorMessages)
      setLoading(false)
      return
    }
    
    try {
      const updateData = {
        currentPassword: form.currentPassword,
        newPassword: form.newPassword
      }
      
      await updateProfile(user.username, updateData)
      onSuccess('Jelszó sikeresen megváltoztatva!')
      onClose()
    } catch (err) {
      const generalError = err.error || err.message || 'Hiba történt a jelszó módosítása során.'
      const fieldErrors = err.errors || {}
      
      const errorMessages = {}
      if (fieldErrors.currentPassword) errorMessages.currentPassword = String(fieldErrors.currentPassword)
      if (fieldErrors.newPassword) errorMessages.newPassword = String(fieldErrors.newPassword)

      setErrors({ general: generalError, ...errorMessages })
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>Jelszó módosítása</h2>
          <button className="modal-close" onClick={onClose}>&times;</button>
        </div>
        
        <form onSubmit={handleSubmit} style={{ padding: '1.5rem' }}>
          {errors.general && <p style={{ color: 'red', marginBottom: '16px' }}>{errors.general}</p>}
          
          <div className="form-group" style={{ marginBottom: '1rem' }}>
            <label htmlFor="pwd-current">Jelenlegi jelszó</label>
            <input
              id="pwd-current"
              type="password"
              name="currentPassword"
              value={form.currentPassword}
              onChange={handleChange}
              style={errors.currentPassword ? { borderColor: 'red' } : {}}
            />
            {errors.currentPassword && <p className="hint" style={{ color: 'red' }}>{errors.currentPassword}</p>}
          </div>

          <div className="form-group" style={{ marginBottom: '1rem' }}>
            <label htmlFor="pwd-new">Új jelszó</label>
            <input
              id="pwd-new"
              type="password"
              name="newPassword"
              value={form.newPassword}
              onChange={handleChange}
              style={errors.newPassword ? { borderColor: 'red' } : {}}
            />
            {errors.newPassword && <p className="hint" style={{ color: 'red' }}>{errors.newPassword}</p>}
          </div>

          <div className="form-group" style={{ marginBottom: '1rem' }}>
            <label htmlFor="pwd-confirm">Új jelszó megerősítése</label>
            <input
              id="pwd-confirm"
              type="password"
              name="confirmPassword"
              value={form.confirmPassword}
              onChange={handleChange}
              style={errors.confirmPassword ? { borderColor: 'red' } : {}}
            />
            {errors.confirmPassword && <p className="hint" style={{ color: 'red' }}>{errors.confirmPassword}</p>}
          </div>

          <div className="modal-actions">
            <button type="button" className="secondary" onClick={onClose}>Mégse</button>
            <button type="submit" className="primary" disabled={loading}>
              {loading ? 'Mentés...' : 'Jelszó módosítása'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
