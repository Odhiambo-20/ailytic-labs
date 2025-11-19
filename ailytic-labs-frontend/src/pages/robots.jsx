import React, { useState } from 'react';
import { Bot, Cpu, Zap, Shield, Brain, Cog, ChevronRight, Star, Users, Award, Instagram, Github, Youtube, Linkedin } from 'lucide-react';
import AllyticVideo from '../assets/Allytic.mp4';

const robots = [
  {
    id: 1,
    name: "NeuroBot X1",
    type: "AI Assistant",
    description: "Advanced neural network processing with human-like conversation abilities and emotional intelligence.",
    capabilities: ["Natural Language Processing", "Emotional Recognition", "Learning Adaptation", "Multi-task Execution"],
    image: "https://images.pexels.com/photos/8386440/pexels-photo-8386440.jpeg?auto=compress&cs=tinysrgb&w=400",
    price: "$12,999",
    rating: 4.9,
    reviews: 1247
  },
  {
    id: 2,
    name: "IndustroMax Pro",
    type: "Industrial Robot",
    description: "Heavy-duty manufacturing robot with precision control and 24/7 operational capability.",
    capabilities: ["Precision Assembly", "Quality Control", "Heavy Lifting", "Continuous Operation"],
    image: "https://images.pexels.com/photos/2085831/pexels-photo-2085831.jpeg?auto=compress&cs=tinysrgb&w=400",
    price: "$45,000",
    rating: 4.8,
    reviews: 892
  },
  {
    id: 3,
    name: "CompanionBot Mini",
    type: "Personal Assistant",
    description: "Compact home companion with smart home integration and personalized care features.",
    capabilities: ["Smart Home Control", "Health Monitoring", "Entertainment", "Security Alerts"],
    image: "https://images.pexels.com/photos/8439093/pexels-photo-8439093.jpeg?auto=compress&cs=tinysrgb&w=400",
    price: "$2,499",
    rating: 4.7,
    reviews: 2156
  },
  {
    id: 4,
    name: "MedBot Advanced",
    type: "Medical Robot",
    description: "Specialized medical assistant for patient care, surgery assistance, and health monitoring.",
    capabilities: ["Patient Monitoring", "Surgery Assistance", "Medication Management", "Emergency Response"],
    image: "https://images.pexels.com/photos/8386434/pexels-photo-8386434.jpeg?auto=compress&cs=tinysrgb&w=400",
    price: "$89,999",
    rating: 4.9,
    reviews: 543
  },
  {
    id: 5,
    name: "ExploreTech Rover",
    type: "Exploration Robot",
    description: "Rugged exploration robot designed for extreme environments and autonomous navigation.",
    capabilities: ["Terrain Navigation", "Environmental Analysis", "Data Collection", "Remote Control"],
    image: "https://images.pexels.com/photos/8439086/pexels-photo-8439086.jpeg?auto=compress&cs=tinysrgb&w=400",
    price: "$67,500",
    rating: 4.6,
    reviews: 324
  },
  {
    id: 6,
    name: "EduBot Interactive",
    type: "Educational Robot",
    description: "Interactive learning companion designed for educational institutions and personal development.",
    capabilities: ["Interactive Learning", "Curriculum Adaptation", "Progress Tracking", "Multi-language Support"],
    image: "https://images.pexels.com/photos/8386422/pexels-photo-8386422.jpeg?auto=compress&cs=tinysrgb&w=400",
    price: "$8,750",
    rating: 4.8,
    reviews: 1689
  }
];

const categories = [
  { name: "AI Assistant", icon: Brain, color: "bg-blue-500", count: 1 },
  { name: "Industrial Robot", icon: Cog, color: "bg-orange-500", count: 1 },
  { name: "Personal Assistant", icon: Bot, color: "bg-green-500", count: 1 },
  { name: "Medical Robot", icon: Shield, color: "bg-red-500", count: 1 },
  { name: "Exploration Robot", icon: Zap, color: "bg-purple-500", count: 1 },
  { name: "Educational Robot", icon: Cpu, color: "bg-teal-500", count: 1 }
];

function Robots() {
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [hoveredRobot, setHoveredRobot] = useState(null);

  const filteredRobots = selectedCategory 
    ? robots.filter(robot => robot.type === selectedCategory)
    : robots;

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-blue-900 to-slate-900">
      {/* Hero Section with Video Background */}
      <div className="relative overflow-hidden min-h-screen flex items-center pt-20">
        {/* Video Background */}
        <div className="absolute inset-0 z-0">
          <video
            width="1920"
            height="1080"
            autoPlay
            muted
            loop
            playsInline
            className="w-full h-full object-cover"
            style={{
              minWidth: '100%',
              minHeight: '100%',
              width: 'auto',
              height: 'auto',
              position: 'absolute',
              top: '50%',
              left: '50%',
              transform: 'translate(-50%, -50%)'
            }}
          >
            <source src={AllyticVideo} type="video/mp4" />
            Your browser does not support the video tag.
          </video>
          {/* Subtle dark overlay to ensure text readability */}
          <div className="absolute inset-0 bg-black/40"></div>
        </div>

        {/* Hero Content */}
        <div className="relative z-10 max-w-7xl mx-auto px-6 py-16">
          <div className="text-center">
            <h1 className="text-5xl md:text-7xl font-bold text-white mb-6 bg-gradient-to-r from-white to-blue-200 bg-clip-text text-transparent">
              Future Robotics
            </h1>
            <p className="text-xl text-blue-100 mb-8 max-w-2xl mx-auto leading-relaxed">
              Discover the next generation of intelligent robots designed to transform industries, 
              enhance lives, and push the boundaries of what's possible.
            </p>
            <div className="flex flex-col sm:flex-row gap-4 justify-center">
              <button className="px-8 py-4 bg-gradient-to-r from-blue-600 to-purple-600 text-white font-semibold rounded-full hover:from-blue-700 hover:to-purple-700 transition-all duration-300 transform hover:scale-105 shadow-lg">
                Explore Robots
              </button>
              <button className="px-8 py-4 border-2 border-blue-300 text-blue-100 font-semibold rounded-full hover:bg-blue-600 hover:border-blue-600 transition-all duration-300">
                Watch Demo
              </button>
            </div>
          </div>
        </div>
      </div>

      <section className="py-16">
        <div className="max-w-7xl mx-auto px-6">
          <h2 className="text-4xl font-bold text-white text-center mb-12">Robot Categories</h2>
          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4 mb-12">
            {categories.map((category) => (
              <button
                key={category.name}
                onClick={() => setSelectedCategory(
                  selectedCategory === category.name ? null : category.name
                )}
                className={`p-6 rounded-2xl border-2 transition-all duration-300 hover:scale-105 ${
                  selectedCategory === category.name
                    ? 'border-blue-400 bg-blue-500/20'
                    : 'border-slate-700 bg-slate-800/50 hover:border-slate-600'
                }`}
              >
                <div className={`inline-flex items-center justify-center w-12 h-12 ${category.color} rounded-full mb-3`}>
                  <category.icon className="w-6 h-6 text-white" />
                </div>
                <div className="text-white text-sm font-medium text-center">{category.name}</div>
              </button>
            ))}
          </div>
        </div>
      </section>

      <section className="py-16">
        <div className="max-w-7xl mx-auto px-6">
          <div className="flex justify-between items-center mb-12">
            <h2 className="text-4xl font-bold text-white">
              {selectedCategory ? `${selectedCategory}s` : 'All Robots'}
            </h2>
            {selectedCategory && (
              <button
                onClick={() => setSelectedCategory(null)}
                className="text-blue-400 hover:text-blue-300 transition-colors"
              >
                Clear Filter
              </button>
            )}
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            {filteredRobots.map((robot) => (
              <div
                key={robot.id}
                onMouseEnter={() => setHoveredRobot(robot.id)}
                onMouseLeave={() => setHoveredRobot(null)}
                className="bg-slate-800/50 backdrop-blur-sm rounded-2xl overflow-hidden border border-slate-700 hover:border-blue-500/50 transition-all duration-500 hover:scale-105 hover:shadow-2xl hover:shadow-blue-500/20"
              >
                <div className="relative overflow-hidden">
                  <img
                    src={robot.image}
                    alt={robot.name}
                    className="w-full h-48 object-cover transition-transform duration-500 hover:scale-110"
                  />
                  <div className="absolute top-4 left-4">
                    <span className="px-3 py-1 bg-gradient-to-r from-blue-600 to-purple-600 text-white text-xs font-semibold rounded-full">
                      {robot.type}
                    </span>
                  </div>
                  <div className="absolute top-4 right-4">
                    <div className="flex items-center gap-1 bg-black/50 backdrop-blur-sm rounded-full px-2 py-1">
                      <Star className="w-3 h-3 text-yellow-400 fill-current" />
                      <span className="text-white text-xs">{robot.rating}</span>
                    </div>
                  </div>
                </div>
                
                <div className="p-6">
                  <h3 className="text-xl font-bold text-white mb-2">{robot.name}</h3>
                  <p className="text-slate-300 text-sm mb-4 leading-relaxed">{robot.description}</p>
                  
                  <div className="mb-4">
                    <h4 className="text-sm font-semibold text-blue-400 mb-2">Key Capabilities:</h4>
                    <div className="flex flex-wrap gap-1">
                      {robot.capabilities.slice(0, 2).map((capability, index) => (
                        <span
                          key={index}
                          className="px-2 py-1 bg-slate-700 text-slate-300 text-xs rounded-full"
                        >
                          {capability}
                        </span>
                      ))}
                      {robot.capabilities.length > 2 && (
                        <span className="px-2 py-1 bg-slate-700 text-slate-300 text-xs rounded-full">
                          +{robot.capabilities.length - 2} more
                        </span>
                      )}
                    </div>
                  </div>
                  
                  <div className="flex items-center justify-between mb-4">
                    <div className="text-2xl font-bold text-white">{robot.price}</div>
                    <div className="text-sm text-slate-400">
                      {robot.reviews} reviews
                    </div>
                  </div>
                  
                  <button className="w-full py-3 bg-gradient-to-r from-blue-600 to-purple-600 text-white font-semibold rounded-xl hover:from-blue-700 hover:to-purple-700 transition-all duration-300 transform hover:scale-105 flex items-center justify-center gap-2">
                    Learn More
                    <ChevronRight className="w-4 h-4" />
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      <section className="py-20 bg-gradient-to-r from-blue-600/20 to-purple-600/20 backdrop-blur-sm">
        <div className="max-w-4xl mx-auto text-center px-6">
          <h2 className="text-4xl md:text-5xl font-bold text-white mb-6">
            Ready to Welcome the Future?
          </h2>
          <p className="text-xl text-blue-100 mb-8 leading-relaxed">
            Join thousands of businesses and individuals who have already embraced 
            the power of intelligent robotics. Start your journey today.
          </p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <button className="px-8 py-4 bg-gradient-to-r from-blue-600 to-purple-600 text-white font-semibold rounded-full hover:from-blue-700 hover:to-purple-700 transition-all duration-300 transform hover:scale-105 shadow-lg">
              Get Started Today
            </button>
            <button className="px-8 py-4 border-2 border-blue-300 text-blue-100 font-semibold rounded-full hover:bg-blue-600 hover:border-blue-600 transition-all duration-300">
              Contact Sales
            </button>
          </div>
        </div>
      </section>

      <footer className="py-12 bg-slate-900/80 backdrop-blur-sm">
        <div className="max-w-7xl mx-auto px-6">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
            <div>
              <div className="bg-black text-white px-3 py-2 font-bold inline-block mb-4">
                A
              </div>
              <p className="text-gray-400 text-sm">AIlyticslabs</p>
            </div>

            <div>
              <h4 className="font-semibold text-white mb-4">Solutions</h4>
              <ul className="space-y-2">
                <li><a href="#" className="text-gray-400 hover:text-white text-sm">Autopilots</a></li>
                <li><a href="#" className="text-gray-400 hover:text-white text-sm">Drones</a></li>
                <li><a href="#" className="text-gray-400 hover:text-white text-sm">Robots</a></li>
              </ul>
            </div>

            <div>
              <h4 className="font-semibold text-white mb-4">Applications</h4>
              <ul className="space-y-2">
                <li><a href="#" className="text-gray-400 hover:text-white text-sm">Academic Research</a></li>
                <li><a href="#" className="text-gray-400 hover:text-white text-sm">Agriculture</a></li>
                <li><a href="#" className="text-gray-400 hover:text-white text-sm">Construction</a></li>
                <li><a href="#" className="text-gray-400 hover:text-white text-sm">Industrial</a></li>
              </ul>
            </div>

            <div>
              <h4 className="font-semibold text-white mb-4">Company</h4>
              <ul className="space-y-2 mb-6">
                <li><a href="#" className="text-gray-400 hover:text-white text-sm">Our story</a></li>
                <li><a href="#" className="text-gray-400 hover:text-white text-sm">Careers</a></li>
                <li><a href="#" className="text-gray-400 hover:text-white text-sm">Press</a></li>
                <li><a href="#" className="text-gray-400 hover:text-white text-sm">Meet the Team</a></li>
              </ul>
              
              <div className="text-sm text-gray-400 space-y-1">
                <p><strong>COC</strong> To be provided</p>
                <p><strong>VAT</strong> To be provided</p>
                <p><strong>A</strong> P.O box 00100, Nairobi</p>
                <p><strong>E</strong> info@ailyticslabs.com</p>
                <p><strong>T</strong> +254748630243</p>
              </div>
            </div>
          </div>

          <div className="mt-12 pt-8 border-t border-gray-700">
            <div className="flex flex-col md:flex-row justify-between items-center">
              <div className="mb-4 md:mb-0">
                <div className="text-center mb-4">
                  <h4 className="font-semibold text-white mb-2">Subscribe to our newsletter</h4>
                  <p className="text-gray-400 text-sm mb-4">The latest news, articles, and resources, sent to your inbox monthly</p>
                  <div className="flex max-w-md mx-auto">
                    <input
                      type="email"
                      placeholder="Enter your email"
                      className="flex-1 px-4 py-2 border border-gray-600 bg-slate-800 text-white rounded-l-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    />
                    <button className="bg-blue-500 text-white px-4 py-2 rounded-r-lg hover:bg-blue-600">
                      <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 8l7.89 4.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 002 2z" />
                      </svg>
                    </button>
                  </div>
                </div>
              </div>
              
              <div className="flex items-center space-x-4">
                <div className="text-sm text-gray-400">
                  <p>© 2025 AIlyticslabs. All rights reserved</p>
                  <div className="flex space-x-4 mt-2">
                    <a href="#" className="hover:text-white">Terms and Conditions</a>
                    <span>|</span>
                    <a href="#" className="hover:text-white">EULA</a>
                  </div>
                </div>
                <div className="flex space-x-3">
                  <a href="#" className="text-gray-400 hover:text-white">
                    <Instagram className="h-5 w-5" />
                  </a>
                  <a href="#" className="text-gray-400 hover:text-white">
                    <Github className="h-5 w-5" />
                  </a>
                  <a href="#" className="text-gray-400 hover:text-white">
                    <Youtube className="h-5 w-5" />
                  </a>
                  <a href="#" className="text-gray-400 hover:text-white">
                    <Linkedin className="h-5 w-5" />
                  </a>
                </div>
              </div>
            </div>
          </div>
        </div>
      </footer>
    </div>
  );
}

export default Robots;