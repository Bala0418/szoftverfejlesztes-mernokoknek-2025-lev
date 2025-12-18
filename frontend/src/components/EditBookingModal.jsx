import React, { useState } from 'react'
import { updateBooking } from '../services/bookingService'

export default function EditBookingModal({ booking, onClose, onSuccess }) {
  const today = new Date().toISOString().split('T')[0]
  
  // Parse ISO datetime to date and time
  const parseDateTime = (isoString) => {
    if (!isoString) return { date: today, time: '09:00' }
    const dt = new Date(isoString)
    const date = dt.toISOString().split('T')[0]
    const time = dt.toTimeString().slice(0, 5)
    return { date, time }
  }

  const startParsed = parseDateTime(booking.startTime)
  const endParsed = parseDateTime(booking.endTime)

  const [form, setForm] = useState({
    date: startParsed.date,
    startTime: startParsed.time,
    endTime: endParsed.time
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
    if (!form.date) errorMessages.date = 'A dátum megadása kötelező.'
    if (!form.startTime) errorMessages.startTime = 'A kezdő időpont megadása kötelező.'
    if (!form.endTime) errorMessages.endTime = 'A vég időpont megadása kötelező.'

    if (Object.keys(errorMessages).length > 0) {
      setErrors(errorMessages)
      setLoading(false)
      return
    }

    try {
      const bookingData = {
        roomCode: booking.roomCode,
        startTime: `${form.date}T${form.startTime}:00`,
        endTime: `${form.date}T${form.endTime}:00`,
        title: booking.title || `Foglalás - ${booking.roomName}`
      }
      
      await updateBooking(booking.id, bookingData)
      
      const formattedDate = new Date(`${form.date}T${form.startTime}`).toLocaleString('hu-HU', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      })
      
      onSuccess(`A foglalás sikeresen módosítva lett. Új időpont a ${booking.roomName} teremben: ${formattedDate}`)
      onClose()
    } catch (err) {
      const generalError = err.error || err.message || 'Hiba történt a foglalás módosítása során.'
      const fieldErrors = err.errors || {}
      
      const errorMessages = {}
      if (fieldErrors.roomCode) errorMessages.roomCode = String(fieldErrors.roomCode)
      if (fieldErrors.startTime) errorMessages.startTime = String(fieldErrors.startTime)
      if (fieldErrors.endTime) errorMessages.endTime = String(fieldErrors.endTime)
      if (fieldErrors.date) errorMessages.date = String(fieldErrors.date)

      setErrors({ general: generalError, ...errorMessages })
    } finally {
      setLoading(false)
    }
  }

  return (
      <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>Foglalás módosítása</h2>
          <button className="modal-close" onClick={onClose}>&times;</button>
        </div>
        
        <form onSubmit={handleSubmit} style={{ padding: '1.5rem' }}>
          <div className="room-info" style={{ marginBottom: '1.5rem' }}>
            <strong style={{ fontSize: '1.1rem' }}>{booking.roomName}</strong>
          </div>
          {errors.general && <p style={{ color: 'red', marginBottom: '16px' }}>{errors.general}</p>}
          
          <div className="form-group" style={{ marginBottom: '1rem' }}>
            <label htmlFor="edit-date">Dátum</label>
            <input
              id="edit-date"
              type="date"
              name="date"
              value={form.date}
              onChange={handleChange}
              style={errors.date ? { borderColor: 'red' } : {}}
            />
            {errors.date && <p className="hint" style={{ color: 'red' }}>{errors.date}</p>}
          </div>

          <div className="form-group" style={{ marginBottom: '1rem' }}>
            <label htmlFor="edit-start">Kezdő időpont</label>
            <input
              id="edit-start"
              type="time"
              name="startTime"
              value={form.startTime}
              onChange={handleChange}
              style={errors.startTime ? { borderColor: 'red' } : {}}
            />
            {errors.startTime && <p className="hint" style={{ color: 'red' }}>{errors.startTime}</p>}
          </div>

          <div className="form-group" style={{ marginBottom: '1rem' }}>
            <label htmlFor="edit-end">Vég időpont</label>
            <input
              id="edit-end"
              type="time"
              name="endTime"
              value={form.endTime}
              onChange={handleChange}
              style={errors.endTime ? { borderColor: 'red' } : {}}
            />
            {errors.endTime && <p className="hint" style={{ color: 'red' }}>{errors.endTime}</p>}
          </div>

          <div className="modal-actions">
            <button type="button" className="secondary" onClick={onClose}>Mégse</button>
            <button type="submit" className="primary" disabled={loading}>
              {loading ? 'Mentés...' : 'Mentés'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
