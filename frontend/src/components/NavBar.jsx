import React from 'react'
import { Link, useNavigate } from 'react-router-dom'

export default function NavBar({ navModel, user }) {
  const navigate = useNavigate()

  const handleClick = link => {
    if (link.action) {
      link.action()
      navigate('/')
      return
    }
    navigate(link.to)
  }

  return (
    <header className="nav">
      <div className="nav__brand">TeremFoglaló</div>
      <nav className="nav__links">
        {navModel.links.map(link => (
          <button
            key={link.label}
            className="nav__link"
            type="button"
            onClick={() => handleClick(link)}
          >
            {link.label}
          </button>
        ))}
      </nav>
      <div className="nav__user">
        {navModel.isAuthenticated && user ? `Bejelentkezve: ${user.name}` : 'Vendég mód'}
      </div>
    </header>
  )
}
