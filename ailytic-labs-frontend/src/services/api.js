const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

// Generic fetch wrapper with error handling
const fetchAPI = async (endpoint, options = {}) => {
  try {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
      headers: {
        'Content-Type': 'application/json',
        ...options.headers,
      },
      ...options,
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    return await response.json();
  } catch (error) {
    console.error(`API Error (${endpoint}):`, error);
    throw error;
  }
};

// Drone API calls
export const droneAPI = {
  // Get all drones
  getAll: () => fetchAPI('/drones'),
  
  // Get drone by ID
  getById: (id) => fetchAPI(`/drones/${id}`),
  
  // Create new drone (requires auth)
  create: (droneData, credentials) => fetchAPI('/drones', {
    method: 'POST',
    body: JSON.stringify(droneData),
    headers: {
      'Authorization': `Basic ${btoa(`${credentials.username}:${credentials.password}`)}`,
    },
  }),
  
  // Update drone (requires auth)
  update: (id, droneData, credentials) => fetchAPI(`/drones/${id}`, {
    method: 'PUT',
    body: JSON.stringify(droneData),
    headers: {
      'Authorization': `Basic ${btoa(`${credentials.username}:${credentials.password}`)}`,
    },
  }),
  
  // Delete drone (requires auth)
  delete: (id, credentials) => fetchAPI(`/drones/${id}`, {
    method: 'DELETE',
    headers: {
      'Authorization': `Basic ${btoa(`${credentials.username}:${credentials.password}`)}`,
    },
  }),
};

// Robot API calls
export const robotAPI = {
  getAll: () => fetchAPI('/robots'),
  getById: (id) => fetchAPI(`/robots/${id}`),
  getByType: (type) => fetchAPI(`/robots?type=${type}`),
};

// Solar Panel API calls
export const solarPanelAPI = {
  getAll: () => fetchAPI('/solar-panels'),
  getById: (id) => fetchAPI(`/solar-panels/${id}`),
};

// Contact API calls
export const contactAPI = {
  submit: (contactData) => fetchAPI('/contact', {
    method: 'POST',
    body: JSON.stringify(contactData),
  }),
  
  // Get all contacts (requires auth)
  getAll: (credentials) => fetchAPI('/contact', {
    method: 'GET',
    headers: {
      'Authorization': `Basic ${btoa(`${credentials.username}:${credentials.password}`)}`,
    },
  }),
};

// Newsletter API calls
export const newsletterAPI = {
  subscribe: (email) => fetchAPI('/newsletter', {
    method: 'POST',
    body: JSON.stringify({ email }),
  }),
  
  // Get all subscribers (requires auth)
  getAll: (credentials) => fetchAPI('/newsletter', {
    method: 'GET',
    headers: {
      'Authorization': `Basic ${btoa(`${credentials.username}:${credentials.password}`)}`,
    },
  }),
};

export default {
  droneAPI,
  robotAPI,
  solarPanelAPI,
  contactAPI,
  newsletterAPI,
};
