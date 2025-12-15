import React from 'react'

const rooms = [
  {
    name: 'Gépterem 101',
    building: 'Informatikai épület – 1. emelet',
    capacity: 24,
    equipment: ['Gépek: 24 db', 'Projektor'],
    status: 'Szabad',
    nextSlot: '09:00–10:30'
  },
  {
    name: 'Tárgyaló 2',
    building: 'Főépület – földszint',
    capacity: 6,
    equipment: ['Hangszigetelt', 'Online meeting'],
    status: 'Foglalt 09:00–10:30',
    nextSlot: '11:00–12:00'
  },
  {
    name: 'Konferenciaterem A',
    building: 'Konferencia központ – 2. emelet',
    capacity: 40,
    equipment: ['Profi hangosítás', 'Nagy vetítő'],
    status: 'Következő: 10:00–12:00'
  },
  {
    name: 'Csendes szoba 3',
    building: 'B épület – 3. emelet',
    capacity: 6,
    equipment: ['Laptopbarát', 'Hangszigetelt'],
    status: 'Foglalható egész nap 08:00–18:00'
  }
]

export default function BookingPage() {
  return (
    <section className="booking-layout">
      <aside className="panel filters">
        <h3>Szűrők</h3>
        <div className="form-group">
          <label>Dátum</label>
          <input type="date" />
        </div>
        <div className="form-group">
          <label>Idősáv</label>
          <div className="inline-inputs">
            <input type="time" defaultValue="09:00" />
            <span>–</span>
            <input type="time" defaultValue="11:00" />
          </div>
        </div>
        <div className="form-group">
          <label>Férőhely</label>
          <input type="number" min="1" defaultValue="1" />
          <p className="hint">Teremfüggő létszám (2–30)</p>
        </div>
        <div className="form-group">
          <label>Felszereltség</label>
          <label className="check"><input type="checkbox" /> Projektor</label>
          <label className="check"><input type="checkbox" /> Hangszigetelés</label>
          <label className="check"><input type="checkbox" /> Online meeting eszközök</label>
        </div>
        <button type="button" className="secondary">Keresés</button>
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

        <div className="room-grid">
          {rooms.map(room => (
            <article key={room.name} className="room-card">
              <div className="room-head">
                <div>
                  <h3>{room.name}</h3>
                  <p className="muted">{room.building}</p>
                </div>
                <span className="status-chip">{room.status}</span>
              </div>
              <p className="muted">Férőhely: {room.capacity} fő</p>
              <div className="tag-row">
                {room.equipment.map(tag => (
                  <span key={tag} className="pill pill-light">{tag}</span>
                ))}
              </div>
              <div className="room-actions">
                <div>
                  <small className="muted">Következő időpont</small>
                  <div className="strong-text">{room.nextSlot || '—'}</div>
                </div>
                <button type="button" className="primary">Foglalás (wireframe)</button>
              </div>
            </article>
          ))}
        </div>
      </div>
    </section>
  )
}
