/**
 * API Configuration & Base Fetcher
 * 
 * Centralized HTTP client for all backend calls.
 * Keeps coupling loose: pages only call service functions, not raw fetch.
 */

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

/**
 * Generic fetch wrapper with error handling
 * @param {string} endpoint - API endpoint path (e.g., '/auth/login')
 * @param {object} options - fetch options (method, body, headers)
 * @returns {Promise<object>} JSON response or throws error
 */
export async function apiCall(endpoint, options = {}) {
  const url = `${API_BASE_URL}${endpoint}`
  
  // Get token from localStorage
  const token = localStorage.getItem('authToken')
  
  const config = {
    headers: {
      'Content-Type': 'application/json',
      ...(token && { 'Authorization': `Bearer ${token}` }),
      ...options.headers
    },
    ...options
  }

  try {
    const response = await fetch(url, config)
    
    let data = {}
    try {
      data = await response.json()
    } catch (jsonError) {
      // If JSON parsing fails, use empty object
      console.warn('Failed to parse JSON response:', jsonError)
    }

    if (!response.ok) {
      throw {
        status: response.status,
        message: data.message || data.errorMessage || `Request failed with status ${response.status}`,
        error: data.error,
        errors: data.errors || {}
      }
    }

    return data
  } catch (error) {
    if (error.status) throw error
    throw { status: 0, message: 'Network error or server unreachable', errors: {} }
  }
}

/**
 * Helper for GET requests
 */
export function apiGet(endpoint, options = {}) {
  return apiCall(endpoint, { method: 'GET', ...options })
}

/**
 * Helper for POST requests
 */
export function apiPost(endpoint, body, options = {}) {
  return apiCall(endpoint, {
    method: 'POST',
    body: JSON.stringify(body),
    ...options
  })
}

/**
 * Helper for PUT requests
 */
export function apiPut(endpoint, body, options = {}) {
  return apiCall(endpoint, {
    method: 'PUT',
    body: JSON.stringify(body),
    ...options
  })
}

/**
 * Helper for DELETE requests
 */
export function apiDelete(endpoint, options = {}) {
  return apiCall(endpoint, { method: 'DELETE', ...options })
}
