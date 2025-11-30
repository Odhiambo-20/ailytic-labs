const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

// Log API configuration on module load
console.log('API Configuration Loaded');
console.log('Base URL:', API_BASE_URL);
console.log('Environment:', import.meta.env.MODE);

// Generic fetch wrapper with comprehensive error handling
const fetchAPI = async (endpoint, options = {}) => {
  const url = `${API_BASE_URL}${endpoint}`;
  
  console.log('Request Details:', {
    method: options.method || 'GET',
    url: url,
    timestamp: new Date().toISOString()
  });

  try {
    const response = await fetch(url, {
      headers: {
        'Content-Type': 'application/json',
        ...options.headers,
      },
      ...options,
    });

    console.log('Response received:', {
      status: response.status,
      statusText: response.statusText,
      url: url
    });

    // Handle different response statuses
    if (!response.ok) {
      let errorMessage = `HTTP ${response.status}: ${response.statusText}`;
      
      // Try to extract error details from response body
      try {
        const errorData = await response.json();
        if (errorData.message) {
          errorMessage = errorData.message;
        }
      } catch (parseError) {
        // If parsing fails, use default error message
        console.warn('Could not parse error response body');
      }

      const error = new Error(errorMessage);
      error.status = response.status;
      error.statusText = response.statusText;
      
      console.error('API request failed:', {
        endpoint: endpoint,
        status: response.status,
        message: errorMessage
      });
      
      throw error;
    }

    // Parse successful response
    const data = await response.json();
    console.log('Data successfully retrieved from:', endpoint);
    return data;

  } catch (error) {
    // Enhanced error logging
    if (error.name === 'TypeError' && error.message.includes('fetch')) {
      console.error('Network error - Cannot reach backend:', {
        endpoint: endpoint,
        baseURL: API_BASE_URL,
        message: 'Please ensure the backend server is running'
      });
      error.message = 'Cannot connect to server. Please check if the backend is running.';
    } else if (error.name === 'SyntaxError') {
      console.error('JSON parsing error:', {
        endpoint: endpoint,
        message: 'Invalid JSON response from server'
      });
      error.message = 'Invalid response from server';
    } else {
      console.error('API Error:', {
        endpoint: endpoint,
        error: error.message,
        stack: error.stack
      });
    }
    
    throw error;
  }
};

// Drone API calls
export const droneAPI = {
  /**
   * Get all drones
   * @returns {Promise<Array>} Array of drone objects
   */
  getAll: () => {
    console.log('Fetching all drones...');
    return fetchAPI('/drones');
  },
  
  /**
   * Get drone by ID
   * @param {string} id - Drone ID
   * @returns {Promise<Object>} Drone object
   */
  getById: (id) => {
    console.log('Fetching drone with ID:', id);
    return fetchAPI(`/drones/${id}`);
  },
  
  /**
   * Create new drone (requires authentication)
   * @param {Object} droneData - Drone data
   * @param {Object} credentials - Admin credentials
   * @returns {Promise<Object>} Created drone object
   */
  create: (droneData, credentials) => {
    console.log('Creating new drone...');
    return fetchAPI('/drones', {
      method: 'POST',
      body: JSON.stringify(droneData),
      headers: {
        'Authorization': `Basic ${btoa(`${credentials.username}:${credentials.password}`)}`,
      },
    });
  },
  
  /**
   * Update drone (requires authentication)
   * @param {string} id - Drone ID
   * @param {Object} droneData - Updated drone data
   * @param {Object} credentials - Admin credentials
   * @returns {Promise<Object>} Updated drone object
   */
  update: (id, droneData, credentials) => {
    console.log('Updating drone with ID:', id);
    return fetchAPI(`/drones/${id}`, {
      method: 'PUT',
      body: JSON.stringify(droneData),
      headers: {
        'Authorization': `Basic ${btoa(`${credentials.username}:${credentials.password}`)}`,
      },
    });
  },
  
  /**
   * Delete drone (requires authentication)
   * @param {string} id - Drone ID
   * @param {Object} credentials - Admin credentials
   * @returns {Promise<void>}
   */
  delete: (id, credentials) => {
    console.log('Deleting drone with ID:', id);
    return fetchAPI(`/drones/${id}`, {
      method: 'DELETE',
      headers: {
        'Authorization': `Basic ${btoa(`${credentials.username}:${credentials.password}`)}`,
      },
    });
  },
};

// Robot API calls
export const robotAPI = {
  /**
   * Get all robots
   * @returns {Promise<Array>} Array of robot objects
   */
  getAll: () => {
    console.log('Fetching all robots...');
    return fetchAPI('/robots');
  },
  
  /**
   * Get robot by ID
   * @param {string} id - Robot ID
   * @returns {Promise<Object>} Robot object
   */
  getById: (id) => {
    console.log('Fetching robot with ID:', id);
    return fetchAPI(`/robots/${id}`);
  },
  
  /**
   * Get robots by type
   * @param {string} type - Robot type
   * @returns {Promise<Array>} Array of robot objects
   */
  getByType: (type) => {
    console.log('Fetching robots of type:', type);
    return fetchAPI(`/robots?type=${encodeURIComponent(type)}`);
  },
};

// Solar Panel API calls
export const solarPanelAPI = {
  /**
   * Get all solar panels
   * @returns {Promise<Array>} Array of solar panel objects
   */
  getAll: () => {
    console.log('Fetching all solar panels...');
    return fetchAPI('/solar-panels');
  },
  
  /**
   * Get solar panel by ID
   * @param {string} id - Solar panel ID
   * @returns {Promise<Object>} Solar panel object
   */
  getById: (id) => {
    console.log('Fetching solar panel with ID:', id);
    return fetchAPI(`/solar-panels/${id}`);
  },
};

// Contact API calls
export const contactAPI = {
  /**
   * Submit contact form
   * @param {Object} contactData - Contact form data
   * @returns {Promise<Object>} Submitted contact object
   */
  submit: (contactData) => {
    console.log('Submitting contact form...');
    return fetchAPI('/contact', {
      method: 'POST',
      body: JSON.stringify(contactData),
    });
  },
  
  /**
   * Get all contacts (requires authentication)
   * @param {Object} credentials - Admin credentials
   * @returns {Promise<Array>} Array of contact objects
   */
  getAll: (credentials) => {
    console.log('Fetching all contacts...');
    return fetchAPI('/contact', {
      method: 'GET',
      headers: {
        'Authorization': `Basic ${btoa(`${credentials.username}:${credentials.password}`)}`,
      },
    });
  },
};

// Newsletter API calls
export const newsletterAPI = {
  /**
   * Subscribe to newsletter
   * @param {string} email - Subscriber email
   * @returns {Promise<Object>} Subscription confirmation
   */
  subscribe: (email) => {
    console.log('Subscribing email to newsletter:', email);
    return fetchAPI('/newsletter', {
      method: 'POST',
      body: JSON.stringify({ email }),
    });
  },
  
  /**
   * Get all newsletter subscribers (requires authentication)
   * @param {Object} credentials - Admin credentials
   * @returns {Promise<Array>} Array of subscriber objects
   */
  getAll: (credentials) => {
    console.log('Fetching all newsletter subscribers...');
    return fetchAPI('/newsletter', {
      method: 'GET',
      headers: {
        'Authorization': `Basic ${btoa(`${credentials.username}:${credentials.password}`)}`,
      },
    });
  },
};

// Export all APIs
export default {
  droneAPI,
  robotAPI,
  solarPanelAPI,
  contactAPI,
  newsletterAPI,
};