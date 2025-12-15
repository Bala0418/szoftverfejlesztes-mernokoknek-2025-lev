import React, { useState } from 'react'

const activeBookings = [
  { room: 'Gépterem 101', time: 'Holnap 09:00–10:30' },
  { room: 'Tárgyaló 2', time: 'Péntek 11:00–12:00' }
]

const historyBookings = [
  { room: 'Konferenciaterem A', time: 'Nov 10. 10:00–12:00' },
  { room: 'Csendes szoba 3', time: 'Nov 8. 15:00–16:00' },
  { room: 'Gépterem 101', time: 'Nov 2. 09:00–10:00' },
  { room: 'Tárgyaló 2', time: 'Oct 28. 13:00–14:00' },
  { room: 'Konferenciaterem A', time: 'Oct 20. 09:00–10:30' }
]

export default function ProfilePage({ user, onSave }) {
  const [form, setForm] = useState({
    name: user?.name || '',
    email: user?.email || '',
    username: user?.username || ''
  })

  const handleChange = e => {
    const { name, value } = e.target
    setForm(prev => ({ ...prev, [name]: value }))
  }

  return (
    <section className="profile-grid">
      <div className="panel form-panel">
        <h2>Felhasználói adatok</h2>
        <div className="form-group">
          <label htmlFor="pf-name">Név</label>
          <input id="pf-name" name="name" type="text" value={form.name} onChange={handleChange} />
          <p className="hint">Itt jelenik meg majd a backend visszajelzés.</p>
        </div>
        <div className="form-group">
          <label htmlFor="pf-email">Email</label>
          <input id="pf-email" name="email" type="email" value={form.email} onChange={handleChange} />
          <p className="hint">Itt jelenik meg majd a backend visszajelzés.</p>
        </div>
        <div className="form-group">
          <label htmlFor="pf-username">Felhasználónév</label>
          <input id="pf-username" name="username" type="text" value={form.username} onChange={handleChange} />
          <p className="hint">Itt jelenik meg majd a backend visszajelzés.</p>
        </div>
        <div className="form-actions">
          <button type="button" className="primary" onClick={() => onSave(form)}>
            Mentés (wireframe)
          </button>
        </div>
      </div>

      <div className="panel">
        <h3>Aktív foglalások</h3>
        <ul className="list">
          {activeBookings.map(item => (
            <li key={item.room} className="list-item">
              <div>{item.room}</div>
              <span className="muted">{item.time}</span>
            </li>
          ))}
        </ul>
      </div>

      <div className="panel">
        <h3>Legutóbbi foglalások (5)</h3>
        <ul className="list">
          {historyBookings.map(item => (
            <li key={`${item.room}-${item.time}`} className="list-item">
              <div>{item.room}</div>
              <span className="muted">{item.time}</span>
            </li>
          ))}
        </ul>
      </div>
    </section>
  )
}
