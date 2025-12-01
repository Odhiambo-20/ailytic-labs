import React, { useState, useEffect } from 'react';
import { ChevronRight, Instagram, Github, Youtube, Linkedin } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { robotAPI } from '../services/api';
import AllyticVideo from '../assets/Allytic.mp4';
import FoodTestingRobot from '../assets/Food Testing Robot.webm';
import AgriculturalRobotVideo from '../assets/Agricultural Robot.webm';
import RoboticDog from '../assets/Robotic Dog.mp4';
import IndustrialRobotVideo from '../assets/Industrial Robot.webm';

function Robots() {
  const navigate = useNavigate();
  const [robots, setRobots] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedType, setSelectedType] = useState('all');

  // Fetch robots from backend on component mount
  useEffect(() => {
    const fetchRobots = async () => {
      try {
        setLoading(true);
        const robotsData = await robotAPI.getAll();
        setRobots(robotsData);
        setError(null);
      } catch (err) {
        console.error('Failed to fetch robots:', err);
        setError('Failed to load robots. Please try again later.');
        setRobots([]);
      } finally {
        setLoading(false);
      }
    };

    fetchRobots();
  }, []);

  // Filter robots by type
  const filteredRobots = selectedType === 'all' 
    ? robots 
    : robots.filter(robot => robot.type === selectedType);

  // Get unique robot types for filtering
  const robotTypes = ['all', ...new Set(robots.map(robot => robot.type))];

  // Handle navigation to catalog page
  const handleExploreCatalog = () => {
    navigate('/robots/catalog');
  };

  // Handle navigation to order page with robot data
  const handleOrderNow = (robot) => {
    navigate('/order', { state: { robot } });
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-blue-900 to-slate-900">
      {/* Hero Section */}
      <div className="relative overflow-hidden min-h-screen flex items-center pt-20">
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
          <div className="absolute inset-0 bg-black/40"></div>
        </div>

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
              <button 
                onClick={handleExploreCatalog}
                className="px-8 py-4 bg-gradient-to-r from-blue-600 to-purple-600 text-white font-semibold rounded-full hover:from-blue-700 hover:to-purple-700 transition-all duration-300 transform hover:scale-105 shadow-lg"
              >
                Explore Robots
              </button>
              <button className="px-8 py-4 border-2 border-blue-300 text-blue-100 font-semibold rounded-full hover:bg-blue-600 hover:border-blue-600 transition-all duration-300">
                Watch Demo
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* Featured Video Sections */}
      <section className="py-20 bg-slate-900/50">
        <div className="max-w-7xl mx-auto px-6">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-12 items-center">
            <div className="relative group">
              <video
                autoPlay
                muted
                loop
                playsInline
                className="w-full h-[500px] object-cover rounded-3xl shadow-2xl"
              >
                <source src={FoodTestingRobot} type="video/webm" />
                Your browser does not support the video tag.
              </video>
              <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-black/40 to-transparent rounded-3xl flex flex-col justify-end p-8">
                <h3 className="text-3xl font-bold text-white mb-4">
                  Precision Food Safety Testing
                </h3>
                <p className="text-blue-100 mb-6 text-lg leading-relaxed">
                  Revolutionary AI-powered robot that ensures food quality and safety with 99.9% accuracy. 
                  Detect contaminants, measure nutritional content, and guarantee compliance in seconds.
                </p>
                <button 
                  onClick={() => handleOrderNow({
                    id: 'food-testing-1',
                    name: 'SafeTest 3000',
                    type: 'Food Testing',
                    price: '$78,000',
                    description: 'Revolutionary food safety testing robot with 99.9% accuracy in detecting contaminants, measuring nutritional content, and ensuring compliance with food safety standards.',
                    image: 'https://images.pexels.com/photos/2085832/pexels-photo-2085832.jpeg?auto=compress&cs=tinysrgb&w=800'
                  })}
                  className="w-fit px-8 py-4 bg-gradient-to-r from-blue-600 to-purple-600 text-white font-semibold rounded-full hover:from-blue-700 hover:to-purple-700 transition-all duration-300 transform hover:scale-105 shadow-lg flex items-center gap-2"
                >
                  Order Now
                  <ChevronRight className="w-5 h-5" />
                </button>
              </div>
            </div>

            <div className="relative group">
              <video
                autoPlay
                muted
                loop
                playsInline
                className="w-full h-[500px] object-cover rounded-3xl shadow-2xl"
              >
                <source src={AgriculturalRobotVideo} type="video/webm" />
                Your browser does not support the video tag.
              </video>
              <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-black/40 to-transparent rounded-3xl flex flex-col justify-end p-8">
                <h3 className="text-3xl font-bold text-white mb-4">
                  Smart Agricultural Innovation
                </h3>
                <p className="text-blue-100 mb-6 text-lg leading-relaxed">
                  Autonomous farming robot with advanced crop monitoring, precision planting, and harvesting capabilities. 
                  Boost yields by 40% while reducing water usage and environmental impact.
                </p>
                <button 
                  onClick={() => handleOrderNow({
                    id: 'agricultural-1',
                    name: 'AgroBot Pro X1',
                    type: 'Agricultural',
                    price: '$45,000',
                    description: 'Advanced autonomous farming robot with AI-powered crop monitoring, precision planting, and smart harvesting capabilities. Increases yield by 40% while reducing water usage.',
                    image: 'https://images.pexels.com/photos/2085831/pexels-photo-2085831.jpeg?auto=compress&cs=tinysrgb&w=800'
                  })}
                  className="w-fit px-8 py-4 bg-gradient-to-r from-green-600 to-teal-600 text-white font-semibold rounded-full hover:from-green-700 hover:to-teal-700 transition-all duration-300 transform hover:scale-105 shadow-lg flex items-center gap-2"
                >
                  Order Now
                  <ChevronRight className="w-5 h-5" />
                </button>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* API-Driven Robots Catalog Section */}
      <section className="py-20 bg-gradient-to-r from-slate-900/80 to-blue-900/80">
        <div className="max-w-7xl mx-auto px-6">
          <div className="text-center mb-12">
            <h2 className="text-4xl md:text-5xl font-bold text-white mb-6">
              Our Robot Collection
            </h2>
            <p className="text-xl text-blue-100 mb-8 max-w-3xl mx-auto">
              Explore our complete lineup of intelligent robots powered by advanced AI and cutting-edge technology
            </p>

            {/* Filter Buttons */}
            {robotTypes.length > 1 && (
              <div className="flex flex-wrap justify-center gap-3 mb-8">
                {robotTypes.map((type) => (
                  <button
                    key={type}
                    onClick={() => setSelectedType(type)}
                    className={`px-6 py-2 rounded-full font-semibold transition-all duration-300 ${
                      selectedType === type
                        ? 'bg-gradient-to-r from-blue-600 to-purple-600 text-white'
                        : 'bg-white/10 text-blue-100 hover:bg-white/20'
                    }`}
                  >
                    {type === 'all' ? 'All Robots' : type}
                  </button>
                ))}
              </div>
            )}
          </div>

          {/* Loading State */}
          {loading && (
            <div className="text-center py-20">
              <div className="inline-block animate-spin rounded-full h-16 w-16 border-t-4 border-b-4 border-blue-500 mb-4"></div>
              <p className="text-xl text-blue-100">Loading robots from database...</p>
            </div>
          )}

          {/* Error State */}
          {error && !loading && (
            <div className="text-center py-20">
              <div className="max-w-md mx-auto bg-red-900/20 border border-red-500/50 rounded-lg p-8">
                <p className="text-red-400 text-lg mb-4">{error}</p>
                <button 
                  onClick={() => window.location.reload()}
                  className="px-6 py-3 bg-gradient-to-r from-blue-600 to-purple-600 text-white rounded-full hover:shadow-lg transition-all duration-300 font-semibold"
                >
                  Retry Loading
                </button>
              </div>
            </div>
          )}

          {/* Robots Grid - API Data */}
          {!loading && !error && filteredRobots.length > 0 && (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
              {filteredRobots.map((robot) => (
                <div
                  key={robot.id}
                  className="bg-gradient-to-br from-slate-800/80 to-blue-900/80 backdrop-blur-sm rounded-2xl overflow-hidden border border-blue-500/20 hover:border-blue-500/50 transition-all duration-300 transform hover:scale-105 shadow-xl"
                >
                  <div className="relative h-64 overflow-hidden">
                    <img
                      src={robot.image || "https://images.pexels.com/photos/8566473/pexels-photo-8566473.jpeg?auto=compress&cs=tinysrgb&w=600"}
                      alt={robot.name}
                      className="w-full h-full object-cover"
                      onError={(e) => {
                        e.target.src = "https://images.pexels.com/photos/8566473/pexels-photo-8566473.jpeg?auto=compress&cs=tinysrgb&w=600";
                      }}
                    />
                    <div className="absolute top-4 right-4 bg-blue-600/90 backdrop-blur-sm px-3 py-1 rounded-full">
                      <span className="text-white text-sm font-semibold">{robot.type}</span>
                    </div>
                  </div>

                  <div className="p-6">
                    <h3 className="text-2xl font-bold text-white mb-2">{robot.name}</h3>
                    <p className="text-blue-100 text-sm mb-4 line-clamp-3">
                      {robot.description || 'Advanced robotics technology for modern applications'}
                    </p>

                    {/* Capabilities */}
                    {robot.capabilities && robot.capabilities.length > 0 && (
                      <div className="mb-4">
                        <p className="text-xs text-blue-300 font-semibold uppercase tracking-wide mb-2">
                          Key Capabilities:
                        </p>
                        <div className="flex flex-wrap gap-2">
                          {robot.capabilities.slice(0, 3).map((capability, index) => (
                            <span
                              key={index}
                              className="text-xs bg-blue-600/30 text-blue-200 px-2 py-1 rounded-full"
                            >
                              {capability}
                            </span>
                          ))}
                        </div>
                      </div>
                    )}

                    {/* Price and Rating */}
                    <div className="flex justify-between items-center mb-4">
                      {robot.price && (
                        <p className="text-2xl font-bold text-blue-400">{robot.price}</p>
                      )}
                      {robot.rating && (
                        <div className="flex items-center gap-2">
                          <span className="text-yellow-400">★</span>
                          <span className="text-white font-semibold">{robot.rating}</span>
                          {robot.reviews && (
                            <span className="text-gray-400 text-sm">({robot.reviews})</span>
                          )}
                        </div>
                      )}
                    </div>

                    <button 
                      onClick={() => handleOrderNow(robot)}
                      className="w-full py-3 bg-gradient-to-r from-blue-600 to-purple-600 text-white font-semibold rounded-full hover:from-blue-700 hover:to-purple-700 transition-all duration-300 transform hover:scale-105 shadow-lg flex items-center justify-center gap-2"
                    >
                      Order Now
                      <ChevronRight className="w-4 h-4" />
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}

          {/* Empty State */}
          {!loading && !error && filteredRobots.length === 0 && (
            <div className="text-center py-20">
              <div className="max-w-md mx-auto bg-slate-800/50 border border-blue-700/50 rounded-lg p-8">
                <p className="text-blue-100 text-lg mb-4">
                  {selectedType === 'all' 
                    ? 'No robots available at the moment.' 
                    : `No ${selectedType} robots found.`}
                </p>
                <p className="text-blue-300 text-sm">
                  {selectedType !== 'all' && 'Try selecting a different category or '}
                  Please check back later.
                </p>
              </div>
            </div>
          )}
        </div>
      </section>

      {/* Ready to Welcome Section */}
      <section className="py-20 bg-gradient-to-r from-blue-900/30 to-purple-900/30">
        <div className="max-w-7xl mx-auto px-6">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-12 items-center">
            <div className="order-2 lg:order-1">
              <h2 className="text-4xl md:text-5xl font-bold text-white mb-6">
                Ready to Welcome the Future?
              </h2>
              <p className="text-xl text-blue-100 mb-8 leading-relaxed">
                Control cutting-edge robotics right from your smartphone. Experience seamless integration, 
                real-time monitoring, and intelligent automation at your fingertips. The future of robotics 
                is mobile, accessible, and incredibly powerful.
              </p>
              <div className="flex flex-col sm:flex-row gap-4">
                <button className="px-8 py-4 bg-gradient-to-r from-blue-600 to-purple-600 text-white font-semibold rounded-full hover:from-blue-700 hover:to-purple-700 transition-all duration-300 transform hover:scale-105 shadow-lg">
                  Get Started Today
                </button>
                <button className="px-8 py-4 border-2 border-blue-300 text-blue-100 font-semibold rounded-full hover:bg-blue-600 hover:border-blue-600 transition-all duration-300">
                  Contact Sales
                </button>
              </div>
            </div>
            <div className="order-1 lg:order-2">
              <video
                autoPlay
                muted
                loop
                playsInline
                className="w-full h-[500px] object-cover rounded-3xl shadow-2xl"
              >
                <source src={RoboticDog} type="video/mp4" />
                Your browser does not support the video tag.
              </video>
            </div>
          </div>
        </div>
      </section>

      {/* Industrial Excellence Section */}
      <section className="py-20 bg-slate-900/50">
        <div className="max-w-7xl mx-auto px-6">
          <div className="relative group">
            <video
              autoPlay
              muted
              loop
              playsInline
              className="w-full h-[600px] object-cover rounded-3xl shadow-2xl"
            >
              <source src={IndustrialRobotVideo} type="video/webm" />
              Your browser does not support the video tag.
            </video>
            <div className="absolute inset-0 bg-gradient-to-t from-black/90 via-black/50 to-transparent rounded-3xl flex flex-col justify-end p-12">
              <h2 className="text-4xl md:text-5xl font-bold text-white mb-6">
                Industrial Excellence in Motion
              </h2>
              <p className="text-xl text-blue-100 mb-8 leading-relaxed max-w-3xl">
                Witness the power of advanced industrial robotics. Our precision-engineered robots deliver 
                unmatched performance in manufacturing, assembly, and heavy-duty operations. Experience 
                24/7 productivity with zero downtime and maximum efficiency.
              </p>
              <button 
                onClick={() => handleOrderNow({
                  id: 'industrial-1',
                  name: 'IndustrialArm MAX',
                  type: 'Industrial',
                  price: '$125,000',
                  description: 'High-precision industrial robotic arm designed for manufacturing, assembly, and heavy-duty operations. Delivers 24/7 productivity with zero downtime.',
                  image: 'https://images.pexels.com/photos/1108101/pexels-photo-1108101.jpeg?auto=compress&cs=tinysrgb&w=800'
                })}
                className="w-fit px-8 py-4 bg-gradient-to-r from-orange-600 to-red-600 text-white font-semibold rounded-full hover:from-orange-700 hover:to-red-700 transition-all duration-300 transform hover:scale-105 shadow-lg flex items-center gap-2"
              >
                Order Now
                <ChevronRight className="w-5 h-5" />
              </button>
            </div>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="py-12 bg-gray-950/80 backdrop-blur-sm border-t border-gray-800">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-12 mb-12">
            <div>
              <div className="flex items-center space-x-2 mb-4">
                <div className="w-8 h-8 bg-gradient-to-r from-blue-500 to-cyan-600 rounded-lg flex items-center justify-center font-bold text-sm">
                  A
                </div>
                <span className="text-lg font-bold text-white">Allytic Labs</span>
              </div>
              <p className="text-gray-400 text-sm">Pioneering the future of robotics, drones, and renewable energy solutions.</p>
            </div>

            <div>
              <h4 className="font-semibold text-white mb-4">Solutions</h4>
              <ul className="space-y-2 text-gray-400 text-sm">
                <li><a href="/robots" className="hover:text-white transition-colors">Robotics</a></li>
                <li><a href="/drones" className="hover:text-white transition-colors">Drones</a></li>
                <li><a href="/solarpanels" className="hover:text-white transition-colors">Solar</a></li>
              </ul>
            </div>

            <div>
              <h4 className="font-semibold text-white mb-4">Company</h4>
              <ul className="space-y-2 text-gray-400 text-sm">
                <li><a href="#" className="hover:text-white transition-colors">About</a></li>
                <li><a href="#" className="hover:text-white transition-colors">Careers</a></li>
                <li><a href="#" className="hover:text-white transition-colors">News</a></li>
              </ul>
            </div>

            <div>
              <h4 className="font-semibold text-white mb-4">Contact</h4>
              <ul className="space-y-2 text-gray-400 text-sm">
                <li><a href="/contact" className="hover:text-white transition-colors">Get in Touch</a></li>
                <li><a href="#" className="hover:text-white transition-colors">Support</a></li>
                <li><a href="#" className="hover:text-white transition-colors">Partnerships</a></li>
              </ul>
            </div>
          </div>

          <div className="border-t border-gray-800 pt-8">
            <div className="flex flex-col md:flex-row justify-between items-center gap-8">
              <div className="text-sm text-gray-400">
                <p>&copy; 2025 Allytic Labs. All rights reserved.</p>
                <div className="flex space-x-4 mt-4">
                  <a href="#" className="hover:text-white transition-colors">Terms and Conditions</a>
                  <span>|</span>
                  <a href="#" className="hover:text-white transition-colors">Privacy Policy</a>
                </div>
              </div>

              <div>
                <p className="text-sm text-gray-400 mb-4 text-center md:text-right">Follow us on social media</p>
                <div className="flex gap-3">
                  <a 
                    href="https://www.instagram.com/ailyticslabs" 
                    target="_blank" 
                    rel="noopener noreferrer"
                    className="w-10 h-10 bg-white/10 hover:bg-white/20 rounded-lg flex items-center justify-center transition-all"
                    aria-label="Follow us on Instagram"
                  >
                    <Instagram className="h-5 w-5 text-white" />
                  </a>
                  <a 
                    href="https://github.com/ailyticslabs" 
                    target="_blank" 
                    rel="noopener noreferrer"
                    className="w-10 h-10 bg-white/10 hover:bg-white/20 rounded-lg flex items-center justify-center transition-all"
                    aria-label="Follow us on GitHub"
                  >
                    <Github className="h-5 w-5 text-white" />
                  </a>
                  <a 
                    href="https://www.youtube.com/@ailyticslabs" 
                    target="_blank" 
                    rel="noopener noreferrer"
                    className="w-10 h-10 bg-white/10 hover:bg-white/20 rounded-lg flex items-center justify-center transition-all"
                    aria-label="Subscribe on YouTube"
                  >
                    <Youtube className="h-5 w-5 text-white" />
                  </a>
                  <a 
                    href="https://www.linkedin.com/company/ailyticslabs" 
                    target="_blank" 
                    rel="noopener noreferrer"
                    className="w-10 h-10 bg-white/10 hover:bg-white/20 rounded-lg flex items-center justify-center transition-all"
                    aria-label="Connect on LinkedIn"
                  >
                    <Linkedin className="h-5 w-5 text-white" />
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