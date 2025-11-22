import React, { useState, useEffect } from 'react';
import { ChevronRight, Zap, ChevronLeft, ArrowRight, Menu, X, ChevronDown } from 'lucide-react';
import robotVideo from '../assets/robot.mp4';
import robot1Video from '../assets/robot1.mp4';
import solarpanelsVideo from '../assets/solar panels.mp4';
import AdvancedRobotics from '../assets/advanced robotics.mp4';
import ProfessionalDrone from '../assets/professional drone.jpg';
import SolarEnergy from '../assets/solar energy.mp4';

const Home = () => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [activeSection, setActiveSection] = useState(0);
  const [scrollPosition, setScrollPosition] = useState(0);
  const [currentGalleryIndex, setCurrentGalleryIndex] = useState(0);

  useEffect(() => {
    const interval = setInterval(() => {
      setActiveSection(prev => (prev + 1) % heroSections.length);
    }, 8000);
    return () => clearInterval(interval);
  }, []);

  useEffect(() => {
    const handleScroll = () => {
      setScrollPosition(window.scrollY);
    };
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  const heroSections = [
    {
      title: "Advanced Robotics Solutions",
      subtitle: "Transforming industries with intelligent automation",
      cta: "Explore Robots",
      gradient: "from-blue-600 to-purple-700",
      videoUrl: robotVideo,
      link: "/robots"
    },
    {
      title: "Professional Drone Services",
      subtitle: "Aerial innovation across all sectors of the economy",
      cta: "Explore Drones",
      gradient: "from-emerald-500 to-teal-600",
      videoUrl: robot1Video,
      link: "/drones"
    },
    {
      title: "Solar Energy Systems",
      subtitle: "Sustainable power solutions for a cleaner future",
      cta: "Explore Solar",
      gradient: "from-orange-500 to-red-600",
      videoUrl: solarpanelsVideo,
      link: "/solarpanels"
    }
  ];

  const innovations = [
    {
      title: "Advanced Robotics",
      description: "Precision automation with AI-powered intelligence for manufacturing and logistics",
      videoUrl: AdvancedRobotics,
      stats: ["25+ payload capacity", "99.9% uptime", "Real-time analytics"],
      type: "video",
      link: "/robots"
    },
    {
      title: "Professional Drones",
      description: "Enterprise-grade aerial solutions with autonomous capabilities",
      image: ProfessionalDrone,
      stats: ["8K camera system", "120-min flight time", "Autonomous mapping"],
      type: "image",
      link: "/drones"
    }
  ];

  const galleryItems = [
    {
      image: ProfessionalDrone,
      title: "Professional Drones",
      description: "Experience unparalleled aerial precision with our enterprise-grade drones. Built for commercial applications, surveying, and autonomous operations with cutting-edge navigation systems.",
      gradient: "from-emerald-500 to-teal-600"
    },
    {
      video: AdvancedRobotics,
      title: "Advanced Robotics",
      description: "Transform your operations with intelligent automation. Our robots deliver precision, efficiency, and reliability across manufacturing, logistics, and industrial applications.",
      gradient: "from-blue-600 to-purple-700"
    },
    {
      video: SolarEnergy,
      title: "Solar Energy Solutions",
      description: "Harness the power of the sun with our high-efficiency solar systems. Sustainable, cost-effective, and designed for maximum energy generation for decades to come.",
      gradient: "from-orange-500 to-red-600"
    }
  ];

  const nextGalleryItem = () => {
    setCurrentGalleryIndex((prev) => (prev + 1) % galleryItems.length);
  };

  const prevGalleryItem = () => {
    setCurrentGalleryIndex((prev) => (prev - 1 + galleryItems.length) % galleryItems.length);
  };

  return (
    <div className="min-h-screen bg-gray-900 text-white overflow-hidden">
      <nav className="fixed top-0 w-full z-50 bg-gray-900/95 backdrop-blur-sm border-b border-gray-800">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <div className="flex items-center space-x-2">
              <div className="w-10 h-10 bg-gradient-to-r from-blue-500 to-purple-600 rounded-lg flex items-center justify-center">
                <Zap className="w-6 h-6 text-white" />
              </div>
              <span className="text-xl font-bold bg-gradient-to-r from-blue-400 to-purple-400 bg-clip-text text-transparent">
                Allytic Labs
              </span>
            </div>
            
            <div className="hidden md:flex items-center space-x-8">
              <a href="#home" className="text-gray-300 hover:text-white transition-colors">Home</a>
              <a href="/robots" className="text-gray-300 hover:text-white transition-colors">Robots</a>
              <a href="/drones" className="text-gray-300 hover:text-white transition-colors">Drones</a>
              <a href="/solarpanels" className="text-gray-300 hover:text-white transition-colors">Solar</a>
              <a href="/contact" className="bg-gradient-to-r from-blue-500 to-purple-600 px-4 py-2 rounded-lg hover:shadow-lg transition-all">
                Contact Us
              </a>
            </div>

            <button 
              className="md:hidden"
              onClick={() => setIsMenuOpen(!isMenuOpen)}
            >
              {isMenuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
            </button>
          </div>
        </div>

        {isMenuOpen && (
          <div className="md:hidden bg-gray-800 border-t border-gray-700">
            <div className="px-4 py-2 space-y-2">
              <a href="#home" className="block py-2 text-gray-300 hover:text-white">Home</a>
              <a href="/robots" className="block py-2 text-gray-300 hover:text-white">Robots</a>
              <a href="/drones" className="block py-2 text-gray-300 hover:text-white">Drones</a>
              <a href="/solarpanels" className="block py-2 text-gray-300 hover:text-white">Solar</a>
              <a href="/contact" className="block py-2 text-blue-400 hover:text-blue-300">Contact Us</a>
            </div>
          </div>
        )}
      </nav>

      <section id="home" className="relative h-screen flex items-center justify-center overflow-hidden pt-16">
        <div className="absolute inset-0 w-full h-full">
          <video
            key={activeSection}
            className="w-full h-full object-cover"
            autoPlay
            muted
            loop
            playsInline
            style={{ filter: 'brightness(0.5)' }}
          >
            <source src={heroSections[activeSection].videoUrl} type="video/mp4" />
          </video>
          <div className="absolute inset-0 bg-gradient-to-br from-gray-900/60 via-gray-800/40 to-gray-900/60"></div>
        </div>
        
        <div className="absolute inset-0 z-5">
          <div className="absolute top-20 left-10 w-72 h-72 bg-blue-500 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-pulse"></div>
          <div className="absolute top-40 right-10 w-72 h-72 bg-purple-500 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-pulse delay-1000"></div>
          <div className="absolute -bottom-8 left-20 w-72 h-72 bg-emerald-500 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-pulse delay-2000"></div>
        </div>

        <div className="relative z-10 text-center max-w-6xl mx-auto px-4">
          <div className="mb-8 flex justify-center">
            <div className="inline-block px-4 py-2 bg-white/10 backdrop-blur-md rounded-full border border-white/20">
              <p className="text-sm font-medium text-blue-200">The Future of Technology</p>
            </div>
          </div>
          
          <h1 className="text-6xl md:text-8xl font-bold mb-6 bg-gradient-to-r from-white via-blue-200 to-purple-200 bg-clip-text text-transparent drop-shadow-2xl leading-tight">
            {heroSections[activeSection].title}
          </h1>
          
          <p className="text-xl md:text-2xl text-gray-200 mb-12 max-w-3xl mx-auto drop-shadow-lg">
            {heroSections[activeSection].subtitle}
          </p>

          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <button className={`px-8 py-4 bg-gradient-to-r ${heroSections[activeSection].gradient} rounded-lg font-semibold hover:shadow-2xl transform hover:scale-105 transition-all duration-300 flex items-center justify-center backdrop-blur-sm`}>
              {heroSections[activeSection].cta}
              <ArrowRight className="ml-2 w-5 h-5" />
            </button>
            <button className="px-8 py-4 border-2 border-gray-300 rounded-lg font-semibold hover:border-white hover:bg-white hover:text-gray-900 transition-all duration-300 backdrop-blur-sm">
              Watch Demo
            </button>
          </div>

          <div className="flex justify-center mt-12 space-x-2">
            {heroSections.map((_, index) => (
              <button
                key={index}
                className={`w-3 h-3 rounded-full transition-all ${
                  index === activeSection ? 'bg-white shadow-lg w-8' : 'bg-gray-500'
                }`}
                onClick={() => setActiveSection(index)}
              />
            ))}
          </div>
        </div>

        <div className="absolute bottom-8 left-1/2 transform -translate-x-1/2 z-20">
          <ChevronDown className="w-6 h-6 animate-bounce" />
        </div>
      </section>

      <section className="py-24 bg-gradient-to-b from-gray-900 via-gray-800 to-gray-900">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:pr-4 lg:pl-0">
          <div className="text-center mb-16 lg:px-4">
            <h2 className="text-5xl md:text-6xl font-bold mb-6">Innovative Solutions</h2>
            <p className="text-xl text-gray-400 max-w-3xl mx-auto">
              Cutting-edge technology powering the next generation of automation and innovation
            </p>
          </div>

          <div className="grid lg:grid-cols-2 gap-12">
            <div className="relative group overflow-hidden rounded-r-3xl lg:rounded-r-3xl border border-gray-700 hover:border-blue-500 transition-all duration-500 h-96 lg:h-full min-h-[500px]">
              <video
                className="w-full h-full object-cover"
                autoPlay
                muted
                loop
                playsInline
              >
                <source src={innovations[0].videoUrl} type="video/mp4" />
              </video>
              
              <div className="absolute inset-0 bg-gradient-to-t from-gray-900 via-transparent to-transparent opacity-80 group-hover:opacity-70 transition-opacity duration-500"></div>
              
              <div className="absolute inset-0 flex flex-col justify-between p-8">
                <div></div>
                <div>
                  <h3 className="text-3xl md:text-4xl font-bold mb-4 text-white">{innovations[0].title}</h3>
                  <p className="text-gray-200 mb-6 max-w-md text-lg">{innovations[0].description}</p>
                  
                  <div className="grid grid-cols-3 gap-4 mb-6">
                    {innovations[0].stats.map((stat, idx) => (
                      <div key={idx} className="bg-white/10 backdrop-blur-sm rounded-lg p-3 border border-white/20">
                        <p className="text-sm text-gray-300">{stat}</p>
                      </div>
                    ))}
                  </div>
                  
                  <a href={innovations[0].link} className="inline-flex items-center text-blue-400 hover:text-blue-300 font-semibold group/link">
                    Learn More <ArrowRight className="ml-2 w-5 h-5 group-hover/link:translate-x-1 transition-transform" />
                  </a>
                </div>
              </div>
            </div>

            <div className="relative group overflow-hidden rounded-2xl border border-gray-700 hover:border-emerald-500 transition-all duration-500 h-96 lg:h-full min-h-[500px]">
              <img
                src={innovations[1].image}
                alt={innovations[1].title}
                className="w-full h-full object-cover transform group-hover:scale-110 transition-transform duration-700"
              />
              
              <div className="absolute inset-0 bg-gradient-to-t from-gray-900 via-transparent to-transparent opacity-80 group-hover:opacity-70 transition-opacity duration-500"></div>
              
              <div className="absolute inset-0 flex flex-col justify-between p-8">
                <div></div>
                <div>
                  <h3 className="text-3xl md:text-4xl font-bold mb-4 text-white">{innovations[1].title}</h3>
                  <p className="text-gray-200 mb-6 max-w-md text-lg">{innovations[1].description}</p>
                  
                  <div className="grid grid-cols-3 gap-4 mb-6">
                    {innovations[1].stats.map((stat, idx) => (
                      <div key={idx} className="bg-white/10 backdrop-blur-sm rounded-lg p-3 border border-white/20">
                        <p className="text-sm text-gray-300">{stat}</p>
                      </div>
                    ))}
                  </div>
                  
                  <a href={innovations[1].link} className="inline-flex items-center text-emerald-400 hover:text-emerald-300 font-semibold group/link">
                    Learn More <ArrowRight className="ml-2 w-5 h-5 group-hover/link:translate-x-1 transition-transform" />
                  </a>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Gallery Section - Edge to Edge */}
      <section className="py-24 bg-gradient-to-b from-gray-800 to-gray-900">
        <div className="w-full">
          <div className="text-center mb-16 px-4">
            <h2 className="text-5xl md:text-6xl font-bold mb-6">Trusted By Industry Leaders</h2>
            <p className="text-xl text-gray-400">Explore our cutting-edge technology solutions</p>
          </div>

          <div className="relative w-full">
            <div className="relative overflow-hidden bg-gray-800/50 backdrop-blur-sm">
              <div className="relative h-[600px]">
                {galleryItems[currentGalleryIndex].video ? (
                  <video
                    key={currentGalleryIndex}
                    className="w-full h-full object-cover"
                    autoPlay
                    muted
                    loop
                    playsInline
                  >
                    <source src={galleryItems[currentGalleryIndex].video} type="video/mp4" />
                  </video>
                ) : (
                  <img
                    src={galleryItems[currentGalleryIndex].image}
                    alt={galleryItems[currentGalleryIndex].title}
                    className="w-full h-full object-cover"
                  />
                )}
                
                <div className="absolute inset-0 bg-gradient-to-t from-gray-900 via-gray-900/60 to-transparent"></div>
                
                <div className="absolute bottom-0 left-0 right-0 p-8 md:p-12 max-w-7xl mx-auto">
                  <h3 className="text-4xl md:text-5xl font-bold mb-4 text-white">
                    {galleryItems[currentGalleryIndex].title}
                  </h3>
                  <p className="text-lg text-gray-200 mb-8 max-w-2xl">
                    {galleryItems[currentGalleryIndex].description}
                  </p>
                  
                  <div className="flex flex-col sm:flex-row gap-4">
                    <button className={`px-8 py-4 bg-gradient-to-r ${galleryItems[currentGalleryIndex].gradient} rounded-lg font-semibold hover:shadow-2xl transform hover:scale-105 transition-all duration-300 flex items-center justify-center`}>
                      Order Now
                      <ArrowRight className="ml-2 w-5 h-5" />
                    </button>
                    <button className="px-8 py-4 border-2 border-white/30 bg-white/10 backdrop-blur-sm rounded-lg font-semibold hover:border-white hover:bg-white/20 transition-all duration-300">
                      Learn More
                    </button>
                  </div>
                </div>
              </div>
              
              <button
                onClick={prevGalleryItem}
                className="absolute left-4 top-1/2 -translate-y-1/2 w-12 h-12 bg-white/10 backdrop-blur-md border border-white/20 rounded-full flex items-center justify-center hover:bg-white/20 transition-all duration-300 group z-10"
                aria-label="Previous item"
              >
                <ChevronLeft className="w-6 h-6 text-white group-hover:scale-110 transition-transform" />
              </button>
              
              <button
                onClick={nextGalleryItem}
                className="absolute right-4 top-1/2 -translate-y-1/2 w-12 h-12 bg-white/10 backdrop-blur-md border border-white/20 rounded-full flex items-center justify-center hover:bg-white/20 transition-all duration-300 group z-10"
                aria-label="Next item"
              >
                <ChevronRight className="w-6 h-6 text-white group-hover:scale-110 transition-transform" />
              </button>
            </div>
            
            <div className="flex justify-center mt-8 space-x-3">
              {galleryItems.map((_, index) => (
                <button
                  key={index}
                  className={`transition-all rounded-full ${
                    index === currentGalleryIndex
                      ? 'w-12 h-3 bg-gradient-to-r from-blue-500 to-purple-600'
                      : 'w-3 h-3 bg-gray-600 hover:bg-gray-500'
                  }`}
                  onClick={() => setCurrentGalleryIndex(index)}
                  aria-label={`Go to slide ${index + 1}`}
                />
              ))}
            </div>
          </div>
        </div>
      </section>

      <section className="relative h-screen flex items-center justify-center overflow-hidden">
        <div className="absolute inset-0 z-0">
          <video
            className="w-full h-full object-cover"
            autoPlay
            muted
            loop
            playsInline
            style={{ filter: 'brightness(0.3)' }}
          >
            <source src={SolarEnergy} type="video/mp4" />
          </video>
          <div className="absolute inset-0 bg-gradient-to-r from-gray-900/80 via-gray-900/40 to-gray-900/80"></div>
        </div>

        <div className="relative z-10 text-center max-w-4xl mx-auto px-4">
          <h2 className="text-6xl md:text-7xl font-bold mb-8 bg-gradient-to-r from-white to-blue-200 bg-clip-text text-transparent">
            Ready to Transform Tomorrow?
          </h2>
          <p className="text-2xl text-gray-200 mb-12">
            Join thousands of companies leveraging our technology to drive innovation and sustainability
          </p>
          <button className="px-10 py-5 bg-gradient-to-r from-blue-600 to-purple-700 rounded-lg font-bold text-lg hover:shadow-2xl transform hover:scale-105 transition-all duration-300 flex items-center justify-center mx-auto">
            Schedule Demo
            <ArrowRight className="ml-3 w-6 h-6" />
          </button>
        </div>
      </section>

      <footer className="bg-gray-950 border-t border-gray-800 py-16">
        <div className="max-w-7xl mx-auto px-4">
          <div className="grid md:grid-cols-4 gap-8 mb-12">
            <div>
              <div className="flex items-center space-x-2 mb-4">
                <div className="w-8 h-8 bg-gradient-to-r from-blue-500 to-purple-600 rounded-lg flex items-center justify-center">
                  <Zap className="w-4 h-4 text-white" />
                </div>
                <span className="text-lg font-bold">Allytic Labs</span>
              </div>
              <p className="text-gray-400">Pioneering the future of robotics, drones, and renewable energy.</p>
            </div>
            <div>
              <h4 className="font-semibold mb-4">Solutions</h4>
              <ul className="space-y-2 text-gray-400">
                <li><a href="/robots" className="hover:text-white transition-colors">Robotics</a></li>
                <li><a href="/drones" className="hover:text-white transition-colors">Drones</a></li>
                <li><a href="/solarpanels" className="hover:text-white transition-colors">Solar</a></li>
              </ul>
            </div>
            <div>
              <h4 className="font-semibold mb-4">Company</h4>
              <ul className="space-y-2 text-gray-400">
                <li><a href="#" className="hover:text-white transition-colors">About</a></li>
                <li><a href="#" className="hover:text-white transition-colors">Careers</a></li>
                <li><a href="#" className="hover:text-white transition-colors">News</a></li>
              </ul>
            </div>
            <div>
              <h4 className="font-semibold mb-4">Connect</h4>
              <ul className="space-y-2 text-gray-400">
                <li><a href="/contact" className="hover:text-white transition-colors">Contact</a></li>
                <li><a href="#" className="hover:text-white transition-colors">Support</a></li>
                <li><a href="#" className="hover:text-white transition-colors">Partners</a></li>
              </ul>
            </div>
          </div>

          <div className="border-t border-gray-800 pt-8 text-center text-gray-400">
            <p>&copy; 2025 Allytic Labs. All rights reserved.</p>
          </div>
        </div>
      </footer>
    </div>
  );
};

export default Home;