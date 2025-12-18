import React, { useState } from 'react'
import { deleteBooking } from '../services/bookingService'

export default function DeleteBookingModal({ booking, onClose, onSuccess }) {
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const handleDelete = async () => {
    setLoading(true)
    setError('')
    
    try {
      await deleteBooking(booking.id)
      onSuccess(`A foglalás (${booking.roomName}) sikeresen törölve lett.`)
      onClose()
    } catch (err) {
      const errorMsg = err.error || err.message || 'Hiba történt a foglalás törlése során.'
      setError(errorMsg)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content modal-confirm" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>Foglalás törlése</h2>
          <button className="modal-close" onClick={onClose}>&times;</button>
        </div>
        
        <div style={{ padding: '1.5rem' }}>
          {error && <p style={{ color: 'red', marginBottom: '16px' }}>{error}</p>}
          
          <p style={{ marginBottom: '1.5rem', fontSize: '0.95rem', lineHeight: '1.5' }}>
            Biztosan törölni szeretnéd ezt a foglalást?
          </p>
          
          <div style={{ 
            padding: '1rem', 
            background: '#fef2f2', 
            border: '1px solid #fecaca',
            borderRadius: '8px',
            marginBottom: '1.5rem'
          }}>
            <div style={{ marginBottom: '0.5rem' }}>
              <strong style={{ fontSize: '1rem', color: '#1f2933' }}>{booking.roomName}</strong>
            </div>
            <div style={{ fontSize: '0.875rem', color: '#6b7280' }}>
              {new Date(booking.startTime).toLocaleString('hu-HU', {
                year: 'numeric',
                month: 'long',
                day: 'numeric',
                hour: '2-digit',
                minute: '2-digit'
              })}
              {' – '}
              {new Date(booking.endTime).toLocaleString('hu-HU', {
                hour: '2-digit',
                minute: '2-digit'
              })}
            </div>
          </div>

          <div className="modal-actions" style={{ marginTop: 0 }}>
            <button type="button" className="secondary" onClick={onClose} disabled={loading}>
              Mégse
            </button>
            <button type="button" className="danger" onClick={handleDelete} disabled={loading}>
              {loading ? 'Törlés...' : 'Törlés'}
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}
