/**
 * Booking Service
 * 
 * Handles room search, filtering, booking creation.
 * Backend owns the business logic; this only sends/receives data.
 */

import { apiGet, apiPost, apiPut, apiDelete } from './api'

/**
 * Fetch available rooms based on filters
 * @param {object} filters - { date, startTime, endTime, capacity, equipment }
 * @returns {Promise<Array<object>>} List of rooms
 */
export async function searchRooms(filters = {}) {
  const params = new URLSearchParams()
  if (filters.date) params.append('date', filters.date)
  if (filters.startTime) params.append('startTime', filters.startTime)
  if (filters.endTime) params.append('endTime', filters.endTime)
  if (filters.capacity) params.append('capacity', filters.capacity)
  if (filters.equipment) params.append('equipment', filters.equipment.join(','))

  const query = params.toString()
  return apiGet(`/rooms${query ? `?${query}` : ''}`)
}

/**
 * Create a new booking
 * @param {object} bookingData - { roomId, date, startTime, endTime, userId }
 * @returns {Promise<object>} Created booking
 */
export async function createBooking(bookingData) {
  return apiPost('/bookings', bookingData)
}

/**
 * Get user's active bookings
 * @returns {Promise<Array<object>>}
 */
export async function getActiveBookings() {
  return apiGet('/bookings/active')
}

/**
 * Get user's booking history
 * @param {number} limit - number of recent bookings
 * @returns {Promise<Array<object>>}
 */
export async function getBookingHistory(limit = 5) {
  return apiGet(`/bookings/history?limit=${limit}`)
}

/**
 * Update an existing booking
 * @param {number} bookingId - ID of the booking to update
 * @param {object} bookingData - { roomCode, startTime, endTime, title }
 * @returns {Promise<object>} Updated booking
 */
export async function updateBooking(bookingId, bookingData) {
  return apiPut(`/bookings/${bookingId}`, bookingData)
}

/**
 * Delete/cancel a booking
 * @param {number} bookingId - ID of the booking to delete
 * @returns {Promise<object>} Deletion response
 */
export async function deleteBooking(bookingId) {
  return apiDelete(`/bookings/${bookingId}`)
}

