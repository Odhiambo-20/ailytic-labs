import React, { useState } from 'react';
import { Plane, Camera, Zap, Shield, Navigation, Cpu, ChevronRight, Star, Users, Award, Wind, Instagram, Github, Youtube, Linkedin } from 'lucide-react';

const drones = [
  {
    id: 1,
    name: "AeroVision Pro 4K",
    type: "Photography Drone",
    description: "Professional-grade aerial photography drone with 4K HDR camera and advanced stabilization for stunning cinematic shots.",
    specifications: ["4K HDR Camera", "3-Axis Gimbal", "Obstacle Avoidance", "Follow Me Mode"],
    image: "https://images.pexels.com/photos/442587/pexels-photo-442587.jpeg?auto=compress&cs=tinysrgb&w=400",
    price: "$1,299",
    rating: 4.8,
    reviews: 2341,
    flightTime: "34 min",
    range: "8 km"
  },
  {
    id: 2,
    name: "CargoLift Heavy",
    type: "Delivery Drone",
    description: "Industrial delivery drone capable of carrying heavy payloads across long distances with precision GPS navigation.",
    specifications: ["50kg Payload", "GPS Navigation", "Weather Resistant", "Auto Landing"],
    image: "https://images.pexels.com/photos/8566473/pexels-photo-8566473.jpeg?auto=compress&cs=tinysrgb&w=400",
    price: "$15,999",
    rating: 4.7,
    reviews: 892,
    flightTime: "45 min",
    range: "25 km"
  },
  {
    id: 3,
    name: "RaceFly Speed X",
    type: "Racing Drone",
    description: "High-performance racing drone built for speed and agility with carbon fiber frame and responsive controls.",
    specifications: ["Carbon Fiber Frame", "High-Speed Motors", "FPV Camera", "Acrobatic Mode"],
    image: "https://images.pexels.com/photos/724921/pexels-photo-724921.jpeg?auto=compress&cs=tinysrgb&w=400",
    price: "$899",
    rating: 4.9,
    reviews: 1567,
    flightTime: "12 min",
    range: "2 km"
  },
  {
    id: 4,
    name: "SurveyMaster Pro",
    type: "Survey Drone",
    description: "Professional surveying and mapping drone with high-resolution sensors and advanced data collection capabilities.",
    specifications: ["LiDAR Sensor", "Thermal Imaging", "Mapping Software", "RTK GPS"],
    image: "https://images.pexels.com/photos/1034662/pexels-photo-1034662.jpeg?auto=compress&cs=tinysrgb&w=400",
    price: "$28,500",
    rating: 4.6,
    reviews: 423,
    flightTime: "55 min",
    range: "15 km"
  },
  {
    id: 5,
    name: "GuardianEye Security",
    type: "Security Drone",
    description: "Advanced security and surveillance drone with night vision, motion detection, and real-time monitoring capabilities.",
    specifications: ["Night Vision", "Motion Detection", "Live Streaming", "Patrol Mode"],
    image: "https://images.pexels.com/photos/2876511/pexels-photo-2876511.jpeg?auto=compress&cs=tinysrgb&w=400",
    price: "$3,750",
    rating: 4.5,
    reviews: 756,
    flightTime: "28 min",
    range: "5 km"
  },
  {
    id: 6,
    name: "AgroSpray Elite",
    type: "Agricultural Drone",
    description: "Precision agriculture drone for crop monitoring, spraying, and field analysis with smart farming integration.",
    specifications: ["Precision Spraying", "Crop Analysis", "Weather Monitoring", "Field Mapping"],
    image: "https://images.pexels.com/photos/1034650/pexels-photo-1034650.jpeg?auto=compress&cs=tinysrgb&w=400",
    price: "$12,200",
    rating: 4.7,
    reviews: 634,
    flightTime: "40 min",
    range: "12 km"
  }
];

const categories = [
  { name: "Photography Drone", icon: Camera, color: "bg-pink-500", count: 1 },
  { name: "Delivery Drone", icon: Navigation, color: "bg-orange-500", count: 1 },
  { name: "Racing Drone", icon: Zap, color: "bg-yellow-500", count: 1 },
  { name: "Survey Drone", icon: Cpu, color: "bg-blue-500", count: 1 },
  { name: "Security Drone", icon: Shield, color: "bg-red-500", count: 1 },
  { name: "Agricultural Drone", icon: Wind, color: "bg-green-500", count: 1 }
];

function Drones() {
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [hoveredDrone, setHoveredDrone] = useState(null);

  const filteredDrones = selectedCategory 
    ? drones.filter(drone => drone.type === selectedCategory)
    : drones;

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-indigo-900 to-slate-900">
      {/* Header */}
      <header className="relative overflow-hidden">
        <div className="absolute inset-0 bg-gradient-to-r from-indigo-600/20 to-cyan-600/20 backdrop-blur-sm"></div>
        <div className="relative max-w-7xl mx-auto px-6 py-16">
          <div className="text-center">
            
            <h1 className="text-5xl md:text-7xl font-bold text-white mb-6 bg-gradient-to-r from-white to-cyan-200 bg-clip-text text-transparent">
              Ailytic Labs Drones
            </h1>
            <p className="text-xl text-indigo-100 mb-8 max-w-2xl mx-auto leading-relaxed">
              Experience the future of flight with our cutting-edge drone technology. From aerial photography 
              to industrial applications, discover drones that redefine what's possible in the sky.
            </p>
            <div className="flex flex-col sm:flex-row gap-4 justify-center">
              <button className="px-8 py-4 bg-gradient-to-r from-indigo-600 to-cyan-600 text-white font-semibold rounded-full hover:from-indigo-700 hover:to-cyan-700 transition-all duration-300 transform hover:scale-105 shadow-lg">
                Explore Drones
              </button>
              <button className="px-8 py-4 border-2 border-indigo-300 text-indigo-100 font-semibold rounded-full hover:bg-indigo-600 hover:border-indigo-600 transition-all duration-300">
                Watch Flight Demo
              </button>
            </div>
          </div>
        </div>
      </header>


      {/* Categories */}
      <section className="py-16">
        <div className="max-w-7xl mx-auto px-6">
          <h2 className="text-4xl font-bold text-white text-center mb-12">Drone Categories</h2>
          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4 mb-12">
            {categories.map((category) => (
              <button
                key={category.name}
                onClick={() => setSelectedCategory(
                  selectedCategory === category.name ? null : category.name
                )}
                className={`p-6 rounded-2xl border-2 transition-all duration-300 hover:scale-105 ${
                  selectedCategory === category.name
                    ? 'border-indigo-400 bg-indigo-500/20'
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

      {/* Drones Grid */}
      <section className="py-16">
        <div className="max-w-7xl mx-auto px-6">
          <div className="flex justify-between items-center mb-12">
            <h2 className="text-4xl font-bold text-white">
              {selectedCategory ? `${selectedCategory}s` : 'All Drones'}
            </h2>
            {selectedCategory && (
              <button
                onClick={() => setSelectedCategory(null)}
                className="text-indigo-400 hover:text-indigo-300 transition-colors"
              >
                Clear Filter
              </button>
            )}
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            {filteredDrones.map((drone) => (
              <div
                key={drone.id}
                onMouseEnter={() => setHoveredDrone(drone.id)}
                onMouseLeave={() => setHoveredDrone(null)}
                className="bg-slate-800/50 backdrop-blur-sm rounded-2xl overflow-hidden border border-slate-700 hover:border-indigo-500/50 transition-all duration-500 hover:scale-105 hover:shadow-2xl hover:shadow-indigo-500/20"
              >
                <div className="relative overflow-hidden">
                  <img
                    src={drone.image}
                    alt={drone.name}
                    className="w-full h-48 object-cover transition-transform duration-500 hover:scale-110"
                  />
                  <div className="absolute top-4 left-4">
                    <span className="px-3 py-1 bg-gradient-to-r from-indigo-600 to-cyan-600 text-white text-xs font-semibold rounded-full">
                      {drone.type}
                    </span>
                  </div>
                  <div className="absolute top-4 right-4">
                    <div className="flex items-center gap-1 bg-black/50 backdrop-blur-sm rounded-full px-2 py-1">
                      <Star className="w-3 h-3 text-yellow-400 fill-current" />
                      <span className="text-white text-xs">{drone.rating}</span>
                    </div>
                  </div>
                </div>
                
                <div className="p-6">
                  <h3 className="text-xl font-bold text-white mb-2">{drone.name}</h3>
                  <p className="text-slate-300 text-sm mb-4 leading-relaxed">{drone.description}</p>
                  
                  <div className="grid grid-cols-2 gap-4 mb-4">
                    <div className="text-center p-3 bg-slate-700/50 rounded-lg">
                      <div className="text-indigo-400 text-xs font-semibold mb-1">Flight Time</div>
                      <div className="text-white font-bold">{drone.flightTime}</div>
                    </div>
                    <div className="text-center p-3 bg-slate-700/50 rounded-lg">
                      <div className="text-indigo-400 text-xs font-semibold mb-1">Range</div>
                      <div className="text-white font-bold">{drone.range}</div>
                    </div>
                  </div>
                  
                  <div className="mb-4">
                    <h4 className="text-sm font-semibold text-indigo-400 mb-2">Key Features:</h4>
                    <div className="flex flex-wrap gap-1">
                      {drone.specifications.slice(0, 2).map((spec, index) => (
                        <span
                          key={index}
                          className="px-2 py-1 bg-slate-700 text-slate-300 text-xs rounded-full"
                        >
                          {spec}
                        </span>
                      ))}
                      {drone.specifications.length > 2 && (
                        <span className="px-2 py-1 bg-slate-700 text-slate-300 text-xs rounded-full">
                          +{drone.specifications.length - 2} more
                        </span>
                      )}
                    </div>
                  </div>
                  
                  <div className="flex items-center justify-between mb-4">
                    <div className="text-2xl font-bold text-white">{drone.price}</div>
                    <div className="text-sm text-slate-400">
                      {drone.reviews} reviews
                    </div>
                  </div>
                  
                  <button className="w-full py-3 bg-gradient-to-r from-indigo-600 to-cyan-600 text-white font-semibold rounded-xl hover:from-indigo-700 hover:to-cyan-700 transition-all duration-300 transform hover:scale-105 flex items-center justify-center gap-2">
                    View Details
                    <ChevronRight className="w-4 h-4" />
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="py-20 bg-gradient-to-r from-indigo-600/10 to-cyan-600/10 backdrop-blur-sm">
        <div className="max-w-7xl mx-auto px-6">
          <div className="text-center mb-16">
            <h2 className="text-4xl md:text-5xl font-bold text-white mb-6">
              Why Choose SkyTech Drones?
            </h2>
            <p className="text-xl text-indigo-100 max-w-3xl mx-auto">
              Experience unmatched performance, reliability, and innovation in every flight
            </p>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            {[
              {
                icon: Shield,
                title: "Advanced Safety",
                description: "Multi-layer safety systems with obstacle avoidance, emergency landing, and fail-safe protocols"
              },
              {
                icon: Camera,
                title: "Professional Quality",
                description: "Cinema-grade cameras with 4K recording, professional stabilization, and advanced imaging"
              },
              {
                icon: Navigation,
                title: "Intelligent Flight",
                description: "AI-powered flight modes, autonomous navigation, and smart tracking capabilities"
              }
            ].map((feature, index) => (
              <div key={index} className="text-center group">
                <div className="inline-flex items-center justify-center w-20 h-20 bg-gradient-to-r from-indigo-500 to-cyan-600 rounded-full mb-6 group-hover:scale-110 transition-transform duration-300">
                  <feature.icon className="w-10 h-10 text-white" />
                </div>
                <h3 className="text-2xl font-bold text-white mb-4">{feature.title}</h3>
                <p className="text-indigo-200 leading-relaxed">{feature.description}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-20 bg-gradient-to-r from-indigo-600/20 to-cyan-600/20 backdrop-blur-sm">
        <div className="max-w-4xl mx-auto text-center px-6">
          <h2 className="text-4xl md:text-5xl font-bold text-white mb-6">
            Ready to Take Flight?
          </h2>
          <p className="text-xl text-indigo-100 mb-8 leading-relaxed">
            Join the aerial revolution with our cutting-edge drone technology. 
            From hobbyists to professionals, we have the perfect drone for your needs.
          </p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <button className="px-8 py-4 bg-gradient-to-r from-indigo-600 to-cyan-600 text-white font-semibold rounded-full hover:from-indigo-700 hover:to-cyan-700 transition-all duration-300 transform hover:scale-105 shadow-lg">
              Shop Drones Now
            </button>
            <button className="px-8 py-4 border-2 border-indigo-300 text-indigo-100 font-semibold rounded-full hover:bg-indigo-600 hover:border-indigo-600 transition-all duration-300">
              Schedule Demo Flight
            </button>
          </div>
        </div>
      </section>

      {/* Footer */}
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

export default Drones;