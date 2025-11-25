import React, { useState } from 'react';
import { ChevronLeft, ChevronRight, ArrowRight, Instagram, Github, Youtube, Linkedin } from 'lucide-react';

const Drones = () => {
  const [currentGalleryIndex, setCurrentGalleryIndex] = useState(0);

  const galleryItems = [
    {
      image: "https://images.pexels.com/photos/8566473/pexels-photo-8566473.jpeg?auto=compress&cs=tinysrgb&w=1200",
      title: "Professional Delivery Drones",
      subtitle: "Revolutionary cargo solutions for modern logistics",
      highlight: "Transport"
    },
    {
      image: "https://images.pexels.com/photos/442587/pexels-photo-442587.jpeg?auto=compress&cs=tinysrgb&w=1200",
      title: "Advanced Surveillance Systems",
      subtitle: "Cutting-edge aerial security and monitoring",
      highlight: "Security"
    },
    {
      image: "https://images.pexels.com/photos/1034650/pexels-photo-1034650.jpeg?auto=compress&cs=tinysrgb&w=1200",
      title: "Precision Agriculture Drones",
      subtitle: "Smart farming with AI-powered crop analysis",
      highlight: "Agriculture"
    }
  ];

  const droneApplications = [
    {
      image: "https://images.pexels.com/photos/8566473/pexels-photo-8566473.jpeg?auto=compress&cs=tinysrgb&w=600",
      name: "CargoLift Pro",
      application: "Transport & Delivery",
      description: "Industrial-grade delivery drone with 50kg payload capacity, GPS navigation, and weather-resistant design. Perfect for logistics, emergency medical supply delivery, and industrial transport.",
      features: "50kg payload | 25km range | Auto landing"
    },
    {
      image: "https://images.pexels.com/photos/2876511/pexels-photo-2876511.jpeg?auto=compress&cs=tinysrgb&w=600",
      name: "GuardianEye Security",
      application: "Surveillance & Security",
      description: "Advanced security drone equipped with night vision, motion detection, and real-time monitoring. Ideal for perimeter surveillance, property protection, and emergency response operations.",
      features: "Night vision | 5km range | Live streaming"
    },
    {
      image: "https://images.pexels.com/photos/1034650/pexels-photo-1034650.jpeg?auto=compress&cs=tinysrgb&w=600",
      name: "AgroSpray Elite",
      application: "Agriculture & Farming",
      description: "Precision agriculture drone for crop monitoring, field spraying, and smart farming integration. Features advanced sensors for yield analysis and pest detection across acres of land.",
      features: "Precision spraying | 12km range | Crop analysis"
    },
    {
      image: "https://images.pexels.com/photos/442587/pexels-photo-442587.jpeg?auto=compress&cs=tinysrgb&w=600",
      name: "AeroVision Pro 4K",
      application: "Photography & Media",
      description: "Professional aerial photography drone with 4K HDR camera, advanced gimbal stabilization, and follow-me mode. Perfect for cinematography, real estate, and content creation.",
      features: "4K HDR | 8km range | 34min flight time"
    },
    {
      image: "https://images.pexels.com/photos/724921/pexels-photo-724921.jpeg?auto=compress&cs=tinysrgb&w=600",
      name: "SurveyMaster Pro",
      application: "Mapping & Survey",
      description: "Professional surveying drone with LiDAR sensor, thermal imaging, and RTK GPS. Essential for construction planning, land surveying, and infrastructure inspection projects.",
      features: "LiDAR sensor | 15km range | Thermal imaging"
    },
    {
      image: "https://images.pexels.com/photos/1034662/pexels-photo-1034662.jpeg?auto=compress&cs=tinysrgb&w=600",
      name: "RaceFly Speed X",
      application: "Racing & Entertainment",
      description: "High-performance racing drone built for speed and agility with carbon fiber frame and FPV camera. Perfect for competitive racing events and aerial entertainment shows.",
      features: "Carbon frame | 2km range | 12min flight time"
    }
  ];

  const nextGalleryItem = () => {
    setCurrentGalleryIndex((prev) => (prev + 1) % galleryItems.length);
  };

  const prevGalleryItem = () => {
    setCurrentGalleryIndex((prev) => (prev - 1 + galleryItems.length) % galleryItems.length);
  };

  const [hoveredDroneId, setHoveredDroneId] = useState(null);

  return (
    <div className="min-h-screen bg-gray-900 text-white overflow-hidden">
      {/* Navigation */}
      <nav className="fixed top-0 w-full z-50 bg-gray-900/95 backdrop-blur-sm border-b border-gray-800">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <div className="flex items-center space-x-2">
              <div className="w-10 h-10 bg-gradient-to-r from-blue-500 to-cyan-600 rounded-lg flex items-center justify-center font-bold">
                A
              </div>
              <span className="text-xl font-bold bg-gradient-to-r from-blue-400 to-cyan-400 bg-clip-text text-transparent">
                Allytic Labs
              </span>
            </div>
            
            <div className="hidden md:flex items-center space-x-8">
              <a href="/" className="text-gray-300 hover:text-white transition-colors">Home</a>
              <a href="/robots" className="text-gray-300 hover:text-white transition-colors">Robots</a>
              <a href="/drones" className="text-blue-400 hover:text-blue-300 transition-colors">Drones</a>
              <a href="/solarpanels" className="text-gray-300 hover:text-white transition-colors">Solar</a>
              <a href="/contact" className="bg-gradient-to-r from-blue-500 to-cyan-600 px-4 py-2 rounded-lg hover:shadow-lg transition-all">
                Contact Us
              </a>
            </div>
          </div>
        </div>
      </nav>

      {/* Hero Gallery Section */}
      <section className="relative h-screen pt-16 flex items-center justify-center overflow-hidden">
        <div className="relative w-full h-full">
          <img
            key={currentGalleryIndex}
            src={galleryItems[currentGalleryIndex].image}
            alt={galleryItems[currentGalleryIndex].title}
            className="w-full h-full object-cover"
          />
          
          <div className="absolute inset-0 bg-gradient-to-t from-gray-900 via-gray-900/50 to-transparent"></div>
          
          <button
            onClick={prevGalleryItem}
            className="absolute left-8 top-1/2 -translate-y-1/2 z-20 w-14 h-14 bg-white/10 backdrop-blur-md border border-white/20 rounded-full flex items-center justify-center hover:bg-white/20 transition-all duration-300 group"
            aria-label="Previous"
          >
            <ChevronLeft className="w-8 h-8 text-white group-hover:scale-110 transition-transform" />
          </button>
          
          <button
            onClick={nextGalleryItem}
            className="absolute right-8 top-1/2 -translate-y-1/2 z-20 w-14 h-14 bg-white/10 backdrop-blur-md border border-white/20 rounded-full flex items-center justify-center hover:bg-white/20 transition-all duration-300 group"
            aria-label="Next"
          >
            <ChevronRight className="w-8 h-8 text-white group-hover:scale-110 transition-transform" />
          </button>
          
          <div className="absolute bottom-20 left-0 right-0 z-10">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
              <div className="max-w-3xl">
                <div className="inline-block px-4 py-2 bg-white/10 backdrop-blur-md rounded-full border border-white/20 mb-6">
                  <p className="text-sm font-medium text-blue-200">{galleryItems[currentGalleryIndex].highlight}</p>
                </div>
                
                <h1 className="text-6xl md:text-7xl font-bold mb-4 text-white leading-tight">
                  {galleryItems[currentGalleryIndex].title}
                </h1>
                
                <p className="text-xl md:text-2xl text-gray-200 mb-8">
                  {galleryItems[currentGalleryIndex].subtitle}
                </p>
                
                <div className="flex flex-col sm:flex-row gap-4">
                  <button className="px-8 py-4 bg-gradient-to-r from-blue-600 to-cyan-600 rounded-lg font-semibold hover:shadow-2xl transform hover:scale-105 transition-all duration-300 flex items-center justify-center">
                    Order Now
                    <ArrowRight className="ml-2 w-5 h-5" />
                  </button>
                  <button className="px-8 py-4 border-2 border-white/30 bg-white/10 backdrop-blur-sm rounded-lg font-semibold hover:border-white hover:bg-white/20 transition-all duration-300">
                    Learn More
                  </button>
                </div>
              </div>
            </div>
          </div>
          
          <div className="absolute bottom-8 left-1/2 -translate-x-1/2 flex space-x-3 z-10">
            {galleryItems.map((_, index) => (
              <button
                key={index}
                className={`transition-all rounded-full ${
                  index === currentGalleryIndex
                    ? 'w-12 h-3 bg-gradient-to-r from-blue-500 to-cyan-600'
                    : 'w-3 h-3 bg-gray-600 hover:bg-gray-500'
                }`}
                onClick={() => setCurrentGalleryIndex(index)}
                aria-label={`Go to slide ${index + 1}`}
              />
            ))}
          </div>
        </div>
      </section>

      {/* Drone Applications Section */}
      <section className="py-24 bg-gradient-to-b from-gray-900 via-gray-800 to-gray-900">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-20">
            <h2 className="text-5xl md:text-6xl font-bold mb-6 text-white">Drone Solutions for Every Industry</h2>
            <p className="text-xl text-gray-400 max-w-3xl mx-auto">
              Discover our comprehensive range of professional drones designed for surveillance, transport, agriculture, mapping, media, and entertainment
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            {droneApplications.map((drone, index) => (
              <div
                key={index}
                onMouseEnter={() => setHoveredDroneId(index)}
                onMouseLeave={() => setHoveredDroneId(null)}
                className="relative overflow-hidden rounded-2xl border border-gray-700 hover:border-blue-500 transition-all duration-500 h-96 group"
              >
                <img
                  src={drone.image}
                  alt={drone.name}
                  className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-700"
                />
                
                <div className="absolute inset-0 bg-gradient-to-t from-gray-900 via-gray-900/60 to-transparent opacity-80 group-hover:opacity-70 transition-opacity duration-500"></div>
                
                <div className={`absolute inset-0 flex flex-col justify-between p-8 transition-all duration-500 ${
                  hoveredDroneId === index ? 'opacity-100' : 'opacity-0'
                }`}>
                  <div className="inline-block px-3 py-1 bg-blue-600/80 rounded-full w-fit">
                    <span className="text-sm font-semibold text-white">{drone.application}</span>
                  </div>
                  
                  <div>
                    <h3 className="text-2xl font-bold text-white mb-2">{drone.name}</h3>
                    <p className="text-gray-200 text-sm mb-4 line-clamp-3">{drone.description}</p>
                    
                    <div className="mb-4 space-y-2">
                      <p className="text-xs text-gray-300 font-semibold uppercase tracking-wide">Key Features:</p>
                      <p className="text-sm text-blue-300">{drone.features}</p>
                    </div>
                    
                    <button className="w-full py-2 bg-gradient-to-r from-blue-600 to-cyan-600 text-white font-semibold rounded-lg hover:shadow-lg transition-all duration-300 text-sm">
                      Learn More
                    </button>
                  </div>
                </div>

                <div className={`absolute inset-0 flex flex-col justify-between p-8 transition-all duration-500 ${
                  hoveredDroneId === index ? 'opacity-0' : 'opacity-100'
                }`}>
                  <div className="inline-block px-3 py-1 bg-blue-600/80 rounded-full w-fit">
                    <span className="text-sm font-semibold text-white">{drone.application}</span>
                  </div>
                  
                  <div>
                    <h3 className="text-3xl font-bold text-white">{drone.name}</h3>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Latest Innovations Section */}
      <section className="relative h-screen flex items-center justify-center overflow-hidden">
        <img
          src="https://images.pexels.com/photos/8566473/pexels-photo-8566473.jpeg?auto=compress&cs=tinysrgb&w=1400"
          alt="Latest Drone Innovation"
          className="absolute inset-0 w-full h-full object-cover"
        />
        
        <div className="absolute inset-0 bg-gradient-to-r from-gray-900/80 via-gray-900/40 to-gray-900/80"></div>

        <div className="relative z-10 max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <div className="inline-block px-4 py-2 bg-white/10 backdrop-blur-md rounded-full border border-white/20 mb-8">
            <p className="text-sm font-medium text-blue-200">Next Generation Technology</p>
          </div>
          
          <h2 className="text-6xl md:text-7xl font-bold mb-8 text-white leading-tight">
            Latest Innovations in Drone Technology
          </h2>
          
          <p className="text-2xl text-gray-200 mb-12 leading-relaxed">
            Experience breakthrough advancements in flight performance, AI-powered autonomy, extended range capabilities, 
            and industrial-grade reliability. Our latest drone lineup combines cutting-edge hardware with intelligent software 
            to deliver unprecedented solutions for every application.
          </p>
          
          <div className="flex flex-col sm:flex-row gap-6 justify-center">
            <button className="px-10 py-5 bg-gradient-to-r from-blue-600 to-cyan-600 rounded-lg font-bold text-lg hover:shadow-2xl transform hover:scale-105 transition-all duration-300 flex items-center justify-center mx-auto sm:mx-0">
              Explore Latest Models
              <ArrowRight className="ml-3 w-6 h-6" />
            </button>
            <button className="px-10 py-5 border-2 border-white/30 bg-white/10 backdrop-blur-sm rounded-lg font-bold text-lg hover:border-white hover:bg-white/20 transition-all duration-300">
              Watch Technology Demo
            </button>
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
};

export default Drones;