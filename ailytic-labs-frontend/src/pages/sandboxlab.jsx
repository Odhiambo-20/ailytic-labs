import React, { useState } from 'react';
import { Play, Code, Settings, Zap, Bot, Plane, Sun, Copy, Check, ChevronDown, ChevronRight } from 'lucide-react';

const AllyticLabsSandbox = () => {
  const [activeTab, setActiveTab] = useState('robotics');
  const [selectedEndpoint, setSelectedEndpoint] = useState('');
  const [apiResponse, setApiResponse] = useState('');
  const [requestBody, setRequestBody] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [copiedCode, setCopiedCode] = useState('');
  const [expandedSections, setExpandedSections] = useState({});

  const apiCategories = {
    robotics: {
      name: 'Robotics',
      icon: Bot,
      color: 'blue',
      endpoints: [
        {
          name: 'Robot Control',
          method: 'POST',
          path: '/api/v1/robots/control',
          description: 'Send movement commands to connected robots',
          params: { robot_id: 'string', command: 'string', speed: 'number' }
        },
        {
          name: 'Robot Status',
          method: 'GET',
          path: '/api/v1/robots/{id}/status',
          description: 'Get real-time status of a specific robot',
          params: { id: 'string' }
        },
        {
          name: 'Robot Analytics',
          method: 'GET',
          path: '/api/v1/robots/analytics',
          description: 'Retrieve performance analytics for robot fleet',
          params: { timeframe: 'string', robot_ids: 'array' }
        }
      ]
    },
    drones: {
      name: 'Drones',
      icon: Plane,
      color: 'green',
      endpoints: [
        {
          name: 'Flight Plan',
          method: 'POST',
          path: '/api/v1/drones/flight-plan',
          description: 'Create and execute drone flight plans',
          params: { drone_id: 'string', waypoints: 'array', altitude: 'number' }
        },
        {
          name: 'Live Telemetry',
          method: 'GET',
          path: '/api/v1/drones/{id}/telemetry',
          description: 'Stream real-time drone telemetry data',
          params: { id: 'string' }
        },
        {
          name: 'Drone Fleet',
          method: 'GET',
          path: '/api/v1/drones/fleet',
          description: 'Manage and monitor drone fleet operations',
          params: { sector: 'string', status: 'string' }
        }
      ]
    },
    solar: {
      name: 'Solar Panels',
      icon: Sun,
      color: 'yellow',
      endpoints: [
        {
          name: 'Energy Production',
          method: 'GET',
          path: '/api/v1/solar/production',
          description: 'Get solar panel energy production data',
          params: { panel_id: 'string', period: 'string' }
        },
        {
          name: 'Panel Diagnostics',
          method: 'POST',
          path: '/api/v1/solar/diagnostics',
          description: 'Run diagnostic tests on solar panel systems',
          params: { panel_ids: 'array', test_type: 'string' }
        },
        {
          name: 'Efficiency Metrics',
          method: 'GET',
          path: '/api/v1/solar/efficiency',
          description: 'Analyze solar panel efficiency and performance',
          params: { installation_id: 'string', date_range: 'object' }
        }
      ]
    }
  };

  const sampleResponses = {
    '/api/v1/robots/control': {
      status: 'success',
      robot_id: 'robot_001',
      command_executed: 'move_forward',
      timestamp: '2025-06-28T10:30:00Z'
    },
    '/api/v1/drones/flight-plan': {
      flight_plan_id: 'fp_12345',
      status: 'active',
      estimated_duration: '15 minutes',
      waypoints_count: 5
    },
    '/api/v1/solar/production': {
      panel_id: 'solar_001',
      current_output: '4.2 kW',
      daily_production: '28.5 kWh',
      efficiency: '94.2%'
    }
  };

  const codeExamples = {
    javascript: `
// Allytic Labs API Client
const allyticClient = {
  baseURL: 'https://api.allytic-labs.com',
  apiKey: 'your-api-key-here',
  
  async request(endpoint, options = {}) {
    const response = await fetch(\`\${this.baseURL}\${endpoint}\`, {
      headers: {
        'Authorization': \`Bearer \${this.apiKey}\`,
        'Content-Type': 'application/json',
        ...options.headers
      },
      ...options
    });
    return response.json();
  },
  
  // Robotics API
  async controlRobot(robotId, command, speed = 50) {
    return this.request('/api/v1/robots/control', {
      method: 'POST',
      body: JSON.stringify({ robot_id: robotId, command, speed })
    });
  },
  
  // Drones API
  async createFlightPlan(droneId, waypoints, altitude = 100) {
    return this.request('/api/v1/drones/flight-plan', {
      method: 'POST',
      body: JSON.stringify({ drone_id: droneId, waypoints, altitude })
    });
  },
  
  // Solar API
  async getSolarProduction(panelId, period = 'today') {
    return this.request(\`/api/v1/solar/production?panel_id=\${panelId}&period=\${period}\`);
  }
};

// Usage Example
const robot = await allyticClient.controlRobot('robot_001', 'move_forward', 75);
console.log(robot);`,
    
    python: `
import requests
import json

class AllyticLabsAPI:
    def __init__(self, api_key):
        self.base_url = "https://api.allytic-labs.com"
        self.api_key = api_key
        self.headers = {
            "Authorization": f"Bearer {api_key}",
            "Content-Type": "application/json"
        }
    
    def _request(self, endpoint, method="GET", data=None):
        url = f"{self.base_url}{endpoint}"
        response = requests.request(method, url, headers=self.headers, json=data)
        return response.json()
    
    # Robotics API
    def control_robot(self, robot_id, command, speed=50):
        data = {"robot_id": robot_id, "command": command, "speed": speed}
        return self._request("/api/v1/robots/control", "POST", data)
    
    # Drones API
    def create_flight_plan(self, drone_id, waypoints, altitude=100):
        data = {"drone_id": drone_id, "waypoints": waypoints, "altitude": altitude}
        return self._request("/api/v1/drones/flight-plan", "POST", data)
    
    # Solar API
    def get_solar_production(self, panel_id, period="today"):
        endpoint = f"/api/v1/solar/production?panel_id={panel_id}&period={period}"
        return self._request(endpoint)

# Usage
client = AllyticLabsAPI("your-api-key-here")
result = client.control_robot("robot_001", "move_forward", 75)
print(result)`,
    
    curl: `
# Robotics API - Control Robot
curl -X POST "https://api.allytic-labs.com/api/v1/robots/control" \\
  -H "Authorization: Bearer your-api-key-here" \\
  -H "Content-Type: application/json" \\
  -d '{
    "robot_id": "robot_001",
    "command": "move_forward",
    "speed": 75
  }'

# Drones API - Create Flight Plan
curl -X POST "https://api.allytic-labs.com/api/v1/drones/flight-plan" \\
  -H "Authorization: Bearer your-api-key-here" \\
  -H "Content-Type: application/json" \\
  -d '{
    "drone_id": "drone_001",
    "waypoints": [
      {"lat": -1.2921, "lng": 36.8219},
      {"lat": -1.2922, "lng": 36.8220}
    ],
    "altitude": 100
  }'

# Solar API - Get Production Data
curl -X GET "https://api.allytic-labs.com/api/v1/solar/production?panel_id=solar_001&period=today" \\
  -H "Authorization: Bearer your-api-key-here"`
  };

  const executeApiCall = async (endpoint) => {
    setIsLoading(true);
    setApiResponse('');
    
    // Simulate API call
    setTimeout(() => {
      const mockResponse = sampleResponses[endpoint.path] || {
        status: 'success',
        message: 'API call executed successfully',
        data: { example: 'This is sample data' }
      };
      
      setApiResponse(JSON.stringify(mockResponse, null, 2));
      setIsLoading(false);
    }, 1500);
  };

  const copyToClipboard = (text, type) => {
    navigator.clipboard.writeText(text);
    setCopiedCode(type);
    setTimeout(() => setCopiedCode(''), 2000);
  };

  const toggleSection = (section) => {
    setExpandedSections(prev => ({
      ...prev,
      [section]: !prev[section]
    }));
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-blue-900 to-slate-900">
      {/* Header */}
      <div className="bg-black/20 backdrop-blur-sm border-b border-white/10">
        <div className="max-w-7xl mx-auto px-6 py-6">
          <div className="flex items-center gap-4">
            <div className="w-12 h-12 bg-gradient-to-r from-blue-500 to-purple-600 rounded-xl flex items-center justify-center">
              <Settings className="w-6 h-6 text-white" />
            </div>
            <div>
              <h1 className="text-3xl font-bold text-white">Allytic Labs</h1>
              <p className="text-blue-200">API Sandbox & Developer Portal</p>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-6 py-8">
        {/* Welcome Section */}
        <div className="bg-gradient-to-r from-blue-500/10 to-purple-500/10 rounded-2xl p-8 mb-8 border border-blue-500/20">
          <h2 className="text-2xl font-bold text-white mb-4">Welcome to Allytic Labs API Sandbox</h2>
          <p className="text-blue-100 mb-6">
            Explore our comprehensive APIs for robotics control, drone operations, and solar panel management. 
            Test endpoints, view real-time responses, and integrate our technology into your applications.
          </p>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="bg-blue-500/20 rounded-lg p-4 border border-blue-400/30">
              <Bot className="w-8 h-8 text-blue-400 mb-2" />
              <h3 className="text-white font-semibold">Robotics APIs</h3>
              <p className="text-blue-200 text-sm">Control and monitor robotic systems</p>
            </div>
            <div className="bg-green-500/20 rounded-lg p-4 border border-green-400/30">
              <Plane className="w-8 h-8 text-green-400 mb-2" />
              <h3 className="text-white font-semibold">Drone Operations</h3>
              <p className="text-green-200 text-sm">Manage autonomous drone fleets</p>
            </div>
            <div className="bg-yellow-500/20 rounded-lg p-4 border border-yellow-400/30">
              <Sun className="w-8 h-8 text-yellow-400 mb-2" />
              <h3 className="text-white font-semibold">Solar Management</h3>
              <p className="text-yellow-200 text-sm">Monitor solar panel performance</p>
            </div>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* API Categories */}
          <div className="lg:col-span-1">
            <div className="bg-white/5 backdrop-blur-sm rounded-2xl p-6 border border-white/10 sticky top-8">
              <h3 className="text-xl font-semibold text-white mb-6">API Categories</h3>
              <div className="space-y-3">
                {Object.entries(apiCategories).map(([key, category]) => {
                  const Icon = category.icon;
                  return (
                    <button
                      key={key}
                      onClick={() => setActiveTab(key)}
                      className={`w-full flex items-center gap-3 p-4 rounded-xl transition-all ${
                        activeTab === key
                          ? 'bg-blue-500/20 border-blue-400/50 text-white'
                          : 'bg-white/5 border-white/10 text-gray-300 hover:bg-white/10'
                      } border`}
                    >
                      <Icon className="w-5 h-5" />
                      <span className="font-medium">{category.name}</span>
                    </button>
                  );
                })}
              </div>

              {/* API Endpoints */}
              <div className="mt-8">
                <h4 className="text-lg font-semibold text-white mb-4">Endpoints</h4>
                <div className="space-y-2">
                  {apiCategories[activeTab]?.endpoints.map((endpoint, index) => (
                    <button
                      key={index}
                      onClick={() => setSelectedEndpoint(endpoint)}
                      className={`w-full text-left p-3 rounded-lg transition-all border ${
                        selectedEndpoint.path === endpoint.path
                          ? 'bg-blue-500/20 border-blue-400/50 text-white'
                          : 'bg-white/5 border-white/10 text-gray-300 hover:bg-white/10'
                      }`}
                    >
                      <div className="flex items-center gap-2 mb-1">
                        <span className={`px-2 py-1 text-xs rounded font-medium ${
                          endpoint.method === 'GET' ? 'bg-green-500/20 text-green-300' : 'bg-blue-500/20 text-blue-300'
                        }`}>
                          {endpoint.method}
                        </span>
                        <span className="text-sm font-medium">{endpoint.name}</span>
                      </div>
                      <p className="text-xs text-gray-400">{endpoint.description}</p>
                    </button>
                  ))}
                </div>
              </div>
            </div>
          </div>

          {/* Main Content */}
          <div className="lg:col-span-2 space-y-6">
            {/* API Testing */}
            {selectedEndpoint && (
              <div className="bg-white/5 backdrop-blur-sm rounded-2xl p-6 border border-white/10">
                <div className="flex items-center justify-between mb-6">
                  <h3 className="text-xl font-semibold text-white">API Testing</h3>
                  <button
                    onClick={() => executeApiCall(selectedEndpoint)}
                    disabled={isLoading}
                    className="flex items-center gap-2 px-4 py-2 bg-blue-600 hover:bg-blue-700 disabled:opacity-50 rounded-lg text-white transition-colors"
                  >
                    <Play className="w-4 h-4" />
                    {isLoading ? 'Executing...' : 'Execute'}
                  </button>
                </div>

                <div className="space-y-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-300 mb-2">Endpoint</label>
                    <div className="flex items-center gap-2 p-3 bg-black/20 rounded-lg border border-white/10">
                      <span className={`px-2 py-1 text-xs rounded font-medium ${
                        selectedEndpoint.method === 'GET' ? 'bg-green-500/20 text-green-300' : 'bg-blue-500/20 text-blue-300'
                      }`}>
                        {selectedEndpoint.method}
                      </span>
                      <code className="text-blue-300 font-mono text-sm">{selectedEndpoint.path}</code>
                    </div>
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-300 mb-2">Description</label>
                    <p className="text-gray-400 text-sm">{selectedEndpoint.description}</p>
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-300 mb-2">Parameters</label>
                    <div className="bg-black/20 rounded-lg p-3 border border-white/10">
                      <pre className="text-sm text-blue-300 font-mono">
                        {JSON.stringify(selectedEndpoint.params, null, 2)}
                      </pre>
                    </div>
                  </div>

                  {selectedEndpoint.method === 'POST' && (
                    <div>
                      <label className="block text-sm font-medium text-gray-300 mb-2">Request Body</label>
                      <textarea
                        value={requestBody}
                        onChange={(e) => setRequestBody(e.target.value)}
                        placeholder="Enter JSON request body..."
                        className="w-full h-32 p-3 bg-black/20 border border-white/10 rounded-lg text-white font-mono text-sm resize-none focus:outline-none focus:border-blue-500/50"
                      />
                    </div>
                  )}

                  {apiResponse && (
                    <div>
                      <label className="block text-sm font-medium text-gray-300 mb-2">Response</label>
                      <div className="bg-black/40 rounded-lg p-4 border border-green-500/20">
                        <pre className="text-sm text-green-300 font-mono whitespace-pre-wrap">
                          {apiResponse}
                        </pre>
                      </div>
                    </div>
                  )}
                </div>
              </div>
            )}

            {/* Code Examples */}
            <div className="bg-white/5 backdrop-blur-sm rounded-2xl p-6 border border-white/10">
              <h3 className="text-xl font-semibold text-white mb-6">Code Examples</h3>
              
              {Object.entries(codeExamples).map(([language, code]) => (
                <div key={language} className="mb-6">
                  <button
                    onClick={() => toggleSection(language)}
                    className="flex items-center gap-2 w-full p-3 bg-black/20 rounded-lg border border-white/10 hover:bg-black/30 transition-colors"
                  >
                    {expandedSections[language] ? <ChevronDown className="w-4 h-4" /> : <ChevronRight className="w-4 h-4" />}
                    <Code className="w-4 h-4" />
                    <span className="font-medium text-white capitalize">{language}</span>
                    <button
                      onClick={(e) => {
                        e.stopPropagation();
                        copyToClipboard(code, language);
                      }}
                      className="ml-auto p-1 rounded hover:bg-white/10 transition-colors"
                    >
                      {copiedCode === language ? <Check className="w-4 h-4 text-green-400" /> : <Copy className="w-4 h-4 text-gray-400" />}
                    </button>
                  </button>
                  
                  {expandedSections[language] && (
                    <div className="mt-2 bg-black/40 rounded-lg p-4 border border-white/10">
                      <pre className="text-sm text-blue-300 font-mono whitespace-pre-wrap overflow-x-auto">
                        {code}
                      </pre>
                    </div>
                  )}
                </div>
              ))}
            </div>

            {/* Getting Started */}
            <div className="bg-white/5 backdrop-blur-sm rounded-2xl p-6 border border-white/10">
              <h3 className="text-xl font-semibold text-white mb-4">Getting Started</h3>
              <div className="space-y-4">
                <div className="flex items-start gap-3">
                  <div className="w-6 h-6 bg-blue-500 rounded-full flex items-center justify-center text-white text-sm font-bold">1</div>
                  <div>
                    <h4 className="font-medium text-white">Get API Key</h4>
                    <p className="text-gray-400 text-sm">Sign up for an Allytic Labs developer account to get your API key.</p>
                  </div>
                </div>
                <div className="flex items-start gap-3">
                  <div className="w-6 h-6 bg-blue-500 rounded-full flex items-center justify-center text-white text-sm font-bold">2</div>
                  <div>
                    <h4 className="font-medium text-white">Choose Your Platform</h4>
                    <p className="text-gray-400 text-sm">Select from robotics, drone operations, or solar panel management APIs.</p>
                  </div>
                </div>
                <div className="flex items-start gap-3">
                  <div className="w-6 h-6 bg-blue-500 rounded-full flex items-center justify-center text-white text-sm font-bold">3</div>
                  <div>
                    <h4 className="font-medium text-white">Start Building</h4>
                    <p className="text-gray-400 text-sm">Use our code examples and API documentation to integrate with your application.</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AllyticLabsSandbox;