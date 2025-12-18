import React, { useEffect, useState } from 'react'
import { searchRooms, createBooking } from '../services/bookingService'
import BookingModal from '../components/BookingModal'
import Snackbar from '../components/Snackbar'

export default function BookingPage() {
  const today = new Date().toISOString().split('T')[0]
  
  const [rooms, setRooms] = useState([])
  const [availableEquipment, setAvailableEquipment] = useState([])
  const [filters, setFilters] = useState({
    date: today,
    startTime: '09:00',
    endTime: '11:00',
    capacity: 1,
    equipment: []
  })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [searched, setSearched] = useState(false)
  const [selectedRoom, setSelectedRoom] = useState(null)
  const [snackbar, setSnackbar] = useState(null)

  useEffect(() => {
    // Load rooms without filters on initial load
    const fetchInitialRooms = async () => {
      setLoading(true)
      setError('')
      try {
        const data = await searchRooms({}) // Empty filters
        setRooms(data)
        setSearched(true)
        
        // Extract unique features from all rooms
        const equipmentSet = new Set()
        data.forEach(room => {
          if (room.features && Array.isArray(room.features)) {
            room.features.forEach(feature => equipmentSet.add(feature))
          }
        })
        setAvailableEquipment(Array.from(equipmentSet).sort())
      } catch (err) {
        setError(err.message)
      } finally {
        setLoading(false)
      }
    }
    fetchInitialRooms()
  }, [])

  const fetchRooms = async () => {
    setLoading(true)
    setError('')
    try {
      const data = await searchRooms(filters)
      setRooms(data)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  const handleFilterChange = e => {
    const { name, value, type, checked } = e.target
    if (type === 'checkbox') {
      setFilters(prev => ({
        ...prev,
        equipment: checked
          ? [...prev.equipment, value]
          : prev.equipment.filter(eq => eq !== value)
      }))
    } else {
      setFilters(prev => ({ ...prev, [name]: value }))
    }
  }

  const handleSearch = () => {
    setSearched(true)
    fetchRooms()
  }

  const handleOpenBookingModal = room => {
    setSelectedRoom(room)
  }

  const handleCloseBookingModal = () => {
    setSelectedRoom(null)
  }

  const handleBook = async ({ date, startTime, endTime, room }) => {
    const bookingData = {
      roomCode: room.code || room.name || room.id,
      startTime: `${date}T${startTime}:00`,
      endTime: `${date}T${endTime}:00`,
      title: `Foglalás - ${room.name}`
    }
    await createBooking(bookingData)
    
    // Format the date and time for display
    const formattedDate = new Date(`${date}T${startTime}`).toLocaleString('hu-HU', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    })
    
    setSnackbar(`A foglalás sikeres volt. Foglalás kezdete a ${room.name} teremben: ${formattedDate}`)
    
    if (searched) {
      fetchRooms()
    }
  }
  return (
    <section className="booking-layout">
      <aside className="panel filters">
        <h3>Szűrők</h3>
        <div className="form-group">
          <label>Dátum</label>
          <input type="date" name="date" value={filters.date} onChange={handleFilterChange} />
        </div>
        <div className="form-group">
          <label>Idősáv</label>
          <div className="inline-inputs">
            <input type="time" name="startTime" value={filters.startTime} onChange={handleFilterChange} />
            <span>–</span>
            <input type="time" name="endTime" value={filters.endTime} onChange={handleFilterChange} />
          </div>
        </div>
        <div className="form-group">
          <label>Férőhely</label>
          <input type="number" name="capacity" min="1" value={filters.capacity} onChange={handleFilterChange} />
          <p className="hint">Teremfüggő létszám (2–30)</p>
        </div>
        <div className="form-group">
          <label>Felszereltség</label>
          {availableEquipment.length === 0 ? (
            <p className="hint">Nincs elérhető felszereltség</p>
          ) : (
            availableEquipment.map(eq => (
              <label key={eq} className="check">
                <input 
                  type="checkbox" 
                  value={eq} 
                  checked={filters.equipment.includes(eq)}
                  onChange={handleFilterChange} 
                /> 
                {eq}
              </label>
            ))
          )}
        </div>
        <button type="button" className="secondary" onClick={handleSearch} disabled={loading}>Keresés</button>
      </aside>

      <div className="booking-content">
        <div className="booking-header">
          <div>
            <h2>Elérhető termek ma</h2>
            <p>Válassz termet a részletekhez, majd foglalj időpontot.</p>
          </div>
          <div className="toggle-group">
            <button className="secondary">Kártyanézet</button>
            <button className="ghost" disabled>Naptár nézet (hamarosan)</button>
          </div>
        </div>

        {loading && <p>Betöltés...</p>}
        {!loading && searched && rooms.length === 0 && (
          <div style={{ padding: '2rem', textAlign: 'center', color: '#666' }}>
            <p style={{ fontSize: '1.1rem', marginBottom: '0.5rem' }}>Nincs találat</p>
            <p>A kiválasztott szűrők alapján nem található elérhető terem.</p>
          </div>
        )}
        {!loading && !searched && (
          <div style={{ padding: '2rem', textAlign: 'center', color: '#666' }}>
            <p>Válassz szűrőket és nyomd meg a Keresés gombot a termek megjelenítéséhez.</p>
          </div>
        )}

        <div className="room-grid">
          {rooms.map(room => (
            <article key={room.id || room.name} className="room-card">
              <div className="room-head">
                <div>
                  <h3>{room.name}</h3>
                  <p className="muted">{room.building}</p>
                </div>
                <span className="status-chip">{room.status}</span>
              </div>
              <p className="muted">Férőhely: {room.capacity} fő</p>
              <div className="tag-row">
                {room.equipment?.map(tag => (
                  <span key={tag} className="pill pill-light">{tag}</span>
                ))}
              </div>
              <div className="room-actions">
                <div>
                  <small className="muted">Következő időpont</small>
                  <div className="strong-text">{room.nextSlot || '—'}</div>
                </div>
                <button type="button" className="primary" onClick={() => handleOpenBookingModal(room)}>Foglalás</button>
              </div>
            </article>
          ))}
        </div>
      </div>

      {selectedRoom && (
        <BookingModal
          room={selectedRoom}
          onClose={handleCloseBookingModal}
          onBook={handleBook}
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
