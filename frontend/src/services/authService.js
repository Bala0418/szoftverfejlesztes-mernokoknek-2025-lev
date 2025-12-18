/**
 * Authentication Service
 * 
 * Handles login, registration, logout operations.
 * No business logic here — only backend communication.
 */

import { apiPost } from './api'


/**
 * Login with username/email and password
 * @param {string} usernameOrEmail
 * @param {string} password
 * @returns {Promise<{user: object, token?: string}>}
 */
export async function login(usernameOrEmail, password) {
  return apiPost(`/login`, { usernameOrEmail, password })
}

/**
 * Register a new user
 * @param {object} userData - { username, email, name, password }
 * @returns {Promise<{user: object, token?: string}>}
 */
export async function register(userData) {
  return apiPost(`/registration`, userData)
}

/**
 * Logout (if backend tracks sessions)
 * @returns {Promise<void>}
 */
export async function logout() {
  return apiPost('/logout', {})
}
