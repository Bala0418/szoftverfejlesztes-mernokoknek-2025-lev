import React, { useMemo, useState } from 'react'
import { Navigate, Route, Routes, useLocation } from 'react-router-dom'
import NavBar from './components/NavBar'
import HomePage from './pages/HomePage'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import BookingPage from './pages/BookingPage'
import ProfilePage from './pages/ProfilePage'

const mockUser = {
  username: 'user',
  email: 'user@example.com',
  name: 'Minta Felhasználó'
}

function RequireAuth({ isAuthenticated, children }) {
  const location = useLocation()
  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />
  }
  return children
}

export default function App() {
  const [auth, setAuth] = useState({ isAuthenticated: false, user: null })

  const handleLogin = () => {
    setAuth({ isAuthenticated: true, user: mockUser })
  }

  const handleLogout = () => {
    setAuth({ isAuthenticated: false, user: null })
  }

  const handleProfileSave = updates => {
    setAuth(prev => ({ ...prev, user: { ...prev.user, ...updates } }))
  }

  const navModel = useMemo(
    () => ({
      isAuthenticated: auth.isAuthenticated,
      links: auth.isAuthenticated
        ? [
            { to: '/', label: 'Főoldal' },
            { to: '/booking', label: 'Teremfoglalás' },
            { to: '/profile', label: 'Profil' },
            { to: '/logout', label: 'Kijelentkezés', action: handleLogout }
          ]
        : [
            { to: '/', label: 'Főoldal' },
            { to: '/login', label: 'Bejelentkezés' },
            { to: '/register', label: 'Regisztráció' }
          ]
    }),
    [auth.isAuthenticated]
  )

  return (
    <div className="app-shell">
      <NavBar navModel={navModel} user={auth.user} />
      <main className="content">
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route
            path="/login"
            element={<LoginPage onLogin={handleLogin} isAuthenticated={auth.isAuthenticated} />}
          />
          <Route
            path="/register"
            element={<RegisterPage onRegister={handleLogin} isAuthenticated={auth.isAuthenticated} />}
          />
          <Route
            path="/booking"
            element={
              <RequireAuth isAuthenticated={auth.isAuthenticated}>
                <BookingPage />
              </RequireAuth>
            }
          />
          <Route
            path="/profile"
            element={
              <RequireAuth isAuthenticated={auth.isAuthenticated}>
                <ProfilePage user={auth.user} onSave={handleProfileSave} />
              </RequireAuth>
            }
          />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </main>
    </div>
  )
}
