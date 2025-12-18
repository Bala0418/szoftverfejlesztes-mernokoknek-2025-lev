import React, { useEffect } from 'react'

export default function Snackbar({ message, onClose, type = 'success', duration = 15000 }) {
  useEffect(() => {
    const timer = setTimeout(() => {
      onClose()
    }, duration)

    return () => clearTimeout(timer)
  }, [duration, onClose])

  return (
    <div className={`snackbar snackbar-${type}`}>
      <span>{message}</span>
      <button className="snackbar-close" onClick={onClose}>
        &times;
      </button>
    </div>
  )
}
