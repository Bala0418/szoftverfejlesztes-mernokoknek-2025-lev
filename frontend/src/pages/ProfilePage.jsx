import React, { useEffect, useState } from 'react'
import { updateProfile } from '../services/profileService'
import { getActiveBookings, getBookingHistory } from '../services/bookingService'
import EditBookingModal from '../components/EditBookingModal'
import DeleteBookingModal from '../components/DeleteBookingModal'
import ChangePasswordModal from '../components/ChangePasswordModal'
import Snackbar from '../components/Snackbar'

export default function ProfilePage({ user, onSave }) {
  const [form, setForm] = useState({
    email: user?.email || '',
    name: user?.name || '',
    currentPassword: ''
  })
  const [activeBookings, setActiveBookings] = useState([])
  const [historyBookings, setHistoryBookings] = useState([])
  const [loading, setLoading] = useState(false)
  const [errors, setErrors] = useState({})
  const [editingBooking, setEditingBooking] = useState(null)
  const [deletingBooking, setDeletingBooking] = useState(null)
  const [showPasswordModal, setShowPasswordModal] = useState(false)
  const [snackbar, setSnackbar] = useState(null)

  useEffect(() => {
    fetchBookings()
  }, [])

  const fetchBookings = async () => {
    try {
      const [active, history] = await Promise.all([
        getActiveBookings(),
        getBookingHistory(5)
      ])
      setActiveBookings(active)
      setHistoryBookings(history)
    } catch (err) {
      console.error('Foglalások betöltése sikertelen:', err)
    }
  }

  const formatDateTime = (isoString) => {
    if (!isoString) return '—'
    return new Date(isoString).toLocaleString('hu-HU', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    })
  }

  const handleChange = e => {
    const { name, value } = e.target
    setForm(prev => ({ ...prev, [name]: value }))
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }))
    }
  }

  const handleSaveProfile = async () => {
    setLoading(true)
    setErrors({})
    
    const errorMessages = {}
    
    // Validation: name change requires current password
    if (form.name !== user?.name && !form.currentPassword) {
      errorMessages.currentPassword = 'A név módosításához add meg a jelenlegi jelszavadat.'
    }
    
    if (Object.keys(errorMessages).length > 0) {
      setErrors(errorMessages)
      setLoading(false)
      return
    }
    
    try {
      const updateData = {
        currentPassword: form.currentPassword,
        email: form.email,
        fullName: form.name
      }
      
      const updated = await updateProfile(user.username, updateData)
      // Update user with new data
      const updatedUser = {
        ...user,
        email: updated.email || user.email,
        name: updated.fullName || user.name
      }
      onSave(updatedUser)
      setSnackbar('Profil sikeresen frissítve!')
      setForm(prev => ({ ...prev, currentPassword: '' }))
    } catch (err) {
      const generalError = err.error || err.message || 'Hiba történt a profil mentése során.'
      setErrors({ general: generalError, ...err.errors })
    } finally {
      setLoading(false)
    }
  }

  const handlePasswordModalSuccess = (message) => {
    setSnackbar(message)
  }

  const handleEditClick = (booking) => {
    setEditingBooking(booking)
  }

  const handleDeleteClick = (booking) => {
    setDeletingBooking(booking)
  }

  const handleEditSuccess = (message) => {
    setSnackbar(message)
    fetchBookings()
  }

  const handleDeleteSuccess = (message) => {
    setSnackbar(message)
    fetchBookings()
  }

  return (
    <section className="profile-grid">
      <div className="panel form-panel">
        <h2>Felhasználói adatok</h2>

        {errors.general && <p style={{ color: 'red', marginBottom: '1rem' }}>{errors.general}</p>}
        
        <div className="form-group">
          <label htmlFor="pf-username">Felhasználónév</label>
          <input 
            id="pf-username" 
            name="username" 
            type="text" 
            value={user?.username || ''} 
            disabled 
            style={{ background: '#f3f4f6', cursor: 'not-allowed' }}
          />
          <p className="hint">A felhasználónév nem módosítható.</p>
        </div>

        <div className="form-group">
          <label htmlFor="pf-email">Email</label>
          <input 
            id="pf-email" 
            name="email" 
            type="email" 
            value={form.email} 
            onChange={handleChange}
            style={errors.email ? { borderColor: 'red' } : {}}
          />
          {errors.email && <p className="hint" style={{ color: 'red' }}>{errors.email}</p>}
        </div>

        <div className="form-group">
          <label htmlFor="pf-name">Név</label>
          <input 
            id="pf-name" 
            name="name" 
            type="text" 
            value={form.name} 
            onChange={handleChange}
            style={errors.name ? { borderColor: 'red' } : {}}
          />
          {errors.name && <p className="hint" style={{ color: 'red' }}>{errors.name}</p>}
          <p className="hint">A név módosításához add meg alább a jelenlegi jelszavadat.</p>
        </div>

        <div className="form-group">
          <label htmlFor="pf-current-password">Jelenlegi jelszó</label>
          <input 
            id="pf-current-password" 
            name="currentPassword" 
            type="password" 
            value={form.currentPassword} 
            onChange={handleChange}
            placeholder="Csak név módosításhoz szükséges"
            style={errors.currentPassword ? { borderColor: 'red' } : {}}
          />
          {errors.currentPassword && <p className="hint" style={{ color: 'red' }}>{errors.currentPassword}</p>}
        </div>

        <div className="form-actions" style={{ marginTop: '1.5rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <button type="button" className="secondary" onClick={() => setShowPasswordModal(true)}>
            Jelszó módosítása
          </button>
          <button type="button" className="primary" onClick={handleSaveProfile} disabled={loading}>
            {loading ? 'Mentés...' : 'Profil mentése'}
          </button>
        </div>
      </div>

      <div className="panel">
        <h3>Aktív foglalások</h3>
        <ul className="list">
          {activeBookings.length === 0 ? (
            <li className="list-item">Nincs aktív foglalás</li>
          ) : (
            activeBookings.map(item => (
              <li key={item.id} className="list-item booking-item">
                <div className="booking-details">
                  <div>
                    <strong>{item.roomName}</strong>
                    <div className="muted" style={{ fontSize: '0.9em' }}>
                      {formatDateTime(item.startTime)} – {new Date(item.endTime).toLocaleTimeString('hu-HU', { hour: '2-digit', minute: '2-digit' })}
                    </div>
                  </div>
                </div>
                <div className="booking-actions">
                  <button className="btn-small secondary" onClick={() => handleEditClick(item)}>Módosítás</button>
                  <button className="btn-small danger" onClick={() => handleDeleteClick(item)}>Törlés</button>
                </div>
              </li>
            ))
          )}
        </ul>
      </div>

      <div className="panel">
        <h3>Legutóbbi foglalások (5)</h3>
        <ul className="list">
          {historyBookings.length === 0 ? (
            <li className="list-item">Nincs korábbi foglalás</li>
          ) : (
            historyBookings.map(item => (
              <li key={item.id} className="list-item">
                <div>
                  <strong>{item.roomName}</strong>
                  <div className="muted" style={{ fontSize: '0.9em' }}>
                    {formatDateTime(item.startTime)} – {new Date(item.endTime).toLocaleTimeString('hu-HU', { hour: '2-digit', minute: '2-digit' })}
                  </div>
                </div>
              </li>
            ))
          )}
        </ul>
      </div>

      {editingBooking && (
        <EditBookingModal
          booking={editingBooking}
          onClose={() => setEditingBooking(null)}
          onSuccess={handleEditSuccess}
        />
      )}

      {deletingBooking && (
        <DeleteBookingModal
          booking={deletingBooking}
          onClose={() => setDeletingBooking(null)}
          onSuccess={handleDeleteSuccess}
        />
      )}

      {showPasswordModal && (
        <ChangePasswordModal
          user={user}
          onClose={() => setShowPasswordModal(false)}
          onSuccess={handlePasswordModalSuccess}
        />
      )}

      {snackbar && (
        <Snackbar
          message={snackbar}
          onClose={() => setSnackbar(null)}
          type="success"
          duration={15000}
        />
      )}
    </section>
  )
}
