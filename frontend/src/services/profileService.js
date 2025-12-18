/**
 * Profile Service
 * 
 * Handles user profile updates.
 * Backend validates and persists; frontend only sends requests.
 */

import { apiPut, apiGet } from './api'

/**
 * Get user profile by ID
 * @param {number} userId
 * @returns {Promise<object>} User profile
 */
export async function getProfile(userId) {
  return apiGet(`/users/${userId}`)
}

/**
 * Update user profile
 * @param {string} username - Username of the user
 * @param {object} updates - { currentPassword, fullName?, email?, newPassword? }
 * @returns {Promise<object>} Updated user
 */
export async function updateProfile(username, updates) {
  return apiPut(`/user/${username}/modify-contact-info`, updates)
}
