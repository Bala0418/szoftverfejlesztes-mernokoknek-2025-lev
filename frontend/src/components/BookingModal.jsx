import React, { useState } from 'react'

export default function BookingModal({ room, onClose, onBook }) {
  const today = new Date().toISOString().split('T')[0]
  
  const [bookingData, setBookingData] = useState({
    date: today,
    startTime: '09:00',
    endTime: '11:00'
  })
  const [loading, setLoading] = useState(false)
  const [errors, setErrors] = useState({})

  const handleChange = e => {
    const { name, value } = e.target
    setBookingData(prev => ({ ...prev, [name]: value }))
    // Clear error when user types
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }))
    }
  }

  const handleSubmit = async e => {
    e.preventDefault()
    setErrors({})
    
    if (!bookingData.date) {
      setErrors({ date: 'A dátum megadása kötelező.' })
      return
    }
    
    setLoading(true)
    try {
      await onBook({ ...bookingData, room })
      onClose()
    } catch (err) {
      // Map backend errors
      const errorMessages = {}
      if (err.errors && typeof err.errors === 'object') {
        Object.keys(err.errors).forEach(key => {
          const value = err.errors[key]
          errorMessages[key] = typeof value === 'string' ? value : String(value)
        })
      }
      
      // Backend might send error in different formats
      const generalError = err.error || err.message || 'Hiba történt a foglalás során.'
      setErrors({ general: generalError, ...errorMessages })
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={e => e.stopPropagation()}>
        <div className="modal-header">
          <h2>Teremfoglalás</h2>
          <button className="modal-close" onClick={onClose}>&times;</button>
        </div>
        
        <div className="modal-body">
          <div className="booking-room-info">
            <h3>{room.name}</h3>
            <p className="muted">{room.building}</p>
            <p>Férőhely: {room.capacity} fő</p>
            {room.equipment && room.equipment.length > 0 && (
              <div className="tag-row">
                {room.equipment.map(eq => (
                  <span key={eq} className="pill pill-light">{eq}</span>
                ))}
              </div>
            )}
          </div>

          <form onSubmit={handleSubmit}>
            {errors.general && <p style={{ color: 'red', marginBottom: '1rem' }}>{String(errors.general)}</p>}
            
            <div className="form-group">
              <label htmlFor="booking-date">Dátum *</label>
              <input
                id="booking-date"
                name="date"
                type="date"
                value={bookingData.date}
                onChange={handleChange}
                style={errors.date ? { borderColor: 'red', borderWidth: '2px' } : {}}
                required
              />
              {errors.date && <p className="hint" style={{ color: 'red', marginTop: '4px' }}>{String(errors.date)}</p>}
            </div>

            <div className="form-group">
              <label>Időtartam *</label>
              <div className="inline-inputs">
                <input
                  name="startTime"
                  type="time"
                  value={bookingData.startTime}
                  onChange={handleChange}
                  style={errors.startTime ? { borderColor: 'red', borderWidth: '2px' } : {}}
                  required
                />
                <span>–</span>
                <input
                  name="endTime"
                  type="time"
                  value={bookingData.endTime}
                  onChange={handleChange}
                  style={errors.endTime ? { borderColor: 'red', borderWidth: '2px' } : {}}
                  required
                />
              </div>
              {(errors.startTime || errors.endTime) && (
                <p className="hint" style={{ color: 'red', marginTop: '4px' }}>
                  {String(errors.startTime || errors.endTime)}
                </p>
              )}
              {errors.roomCode && <p className="hint" style={{ color: 'red', marginTop: '4px' }}>{String(errors.roomCode)}</p>}
            </div>

            <div className="modal-actions">
              <button type="button" className="ghost" onClick={onClose}>
                Mégse
              </button>
              <button type="submit" className="primary" disabled={loading}>
                {loading ? 'Foglalás...' : 'Foglalás megerősítése'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  )
}
