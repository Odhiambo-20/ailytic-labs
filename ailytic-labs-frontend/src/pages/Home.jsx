import React, { useState, useEffect, useRef } from 'react';
import { ChevronRight, Zap, Cpu, Plane, Sun, Users, Target, Globe, ArrowRight, Menu, X, Play, Pause } from 'lucide-react';
// Fixed import paths - removed the extra "./src/" prefix
import robotVideo from '../assets/robot.mp4';
import robot1Video from '../assets/robot1.mp4';
import solarpanelsVideo from '../assets/solar panels.mp4';

const Home = () => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [activeSection, setActiveSection] = useState(0);
  const [isVideoPlaying, setIsVideoPlaying] = useState(true);

  // Auto-rotate hero sections
  useEffect(() => {
    const interval = setInterval(() => {
      setActiveSection(prev => (prev + 1) % 3);
    }, 5000);
    return () => clearInterval(interval);
  }, []);

  const heroSections = [
    {
      title: "Advanced Robotics Solutions",
      subtitle: "Transforming industries with intelligent automation",
      gradient: "from-blue-600 to-purple-700",
      videoUrl: robotVideo
    },
    {
      title: "Professional Drone Services",
      subtitle: "Aerial innovation across all sectors of the economy",
      gradient: "from-emerald-500 to-teal-600",
      videoUrl: robot1Video
    },
    {
      title: "Solar Energy Systems",
      subtitle: "Sustainable power solutions for a cleaner future",
      gradient: "from-orange-500 to-red-600",
      videoUrl: solarpanelsVideo
    }
  ];

  const services = [
    {
      icon: <Cpu className="w-12 h-12" />,
      title: "Industrial Robotics",
      description: "Cutting-edge robotic systems for manufacturing, logistics, and automation across industries.",
      color: "from-blue-500 to-purple-600",
      videoUrl: robotVideo
    },
    {
      icon: <Plane className="w-12 h-12" />,
      title: "Drone Technology",
      description: "Professional UAV solutions for surveillance, mapping, delivery, and specialized applications.",
      color: "from-emerald-500 to-teal-600",
      videoUrl: robot1Video
    },
    {
      icon: <Sun className="w-12 h-12" />,
      title: "Solar Solutions",
      description: "High-efficiency solar panel installations and renewable energy system integration.",
      color: "from-orange-500 to-red-600",  
      videoUrl: solarpanelsVideo
    }
  ];

  // Added missing stats array
  const stats = [
    {
      number: "500+",
      label: "Projects Completed"
    },
    {
      number: "99%",
      label: "Client Satisfaction"
    },
    {
      number: "24/7",
      label: "Technical Support"
    },
    {
      number: "15+",
      label: "Years Experience"
    }
  ];

  // Custom AnimatedNumber component
  const AnimatedNumber = ({ value, duration = 2000, className = "", style = {} }) => {
    const [displayValue, setDisplayValue] = useState(0);
    const [isVisible, setIsVisible] = useState(false);
    const elementRef = useRef(null);
    const animationRef = useRef(null);

    // Extract numeric value from string (handles "500+", "99%", "24/7")
    const extractNumber = (val) => {
      if (typeof val === 'number') return val;
      const match = val.toString().match(/(\d+)/);
      return match ? parseInt(match[1]) : 0;
    };

    // Format the display value back to original format
    const formatValue = (current, original) => {
      if (original.includes('%')) return Math.round(current) + '%';
      if (original.includes('+')) return Math.round(current) + '+';
      if (original.includes('/')) {
        const parts = original.split('/');
        return Math.round(current) + '/' + parts[1];
      }
      return Math.round(current).toLocaleString();
    };

    useEffect(() => {
      const observer = new IntersectionObserver(
        ([entry]) => {
          if (entry.isIntersecting && !isVisible) {
            setIsVisible(true);
          }
        },
        { threshold: 0.1 }
      );

      if (elementRef.current) {
        observer.observe(elementRef.current);
      }

      return () => observer.disconnect();
    }, [isVisible]);

    useEffect(() => {
      if (!isVisible) return;

      const targetValue = extractNumber(value);
      const startTime = Date.now();
      const startValue = 0;

      const animate = () => {
        const elapsed = Date.now() - startTime;
        const progress = Math.min(elapsed / duration, 1);
        
        // Easing function for smooth animation
        const easeOutQuart = 1 - Math.pow(1 - progress, 4);
        const currentValue = startValue + (targetValue - startValue) * easeOutQuart;
        
        setDisplayValue(currentValue);

        if (progress < 1) {
          animationRef.current = requestAnimationFrame(animate);
        }
      };

      animationRef.current = requestAnimationFrame(animate);

      return () => {
        if (animationRef.current) {
          cancelAnimationFrame(animationRef.current);
        }
      };
    }, [isVisible, value, duration]);

    return (
      <span ref={elementRef} className={className} style={style}>
        {formatValue(displayValue, value)}
      </span>
    );
  };

  const toggleVideo = () => {
    setIsVideoPlaying(!isVideoPlaying);
  };

  return (
    <div className="min-h-screen bg-gray-900 text-white overflow-hidden">
      {/* Navigation */}
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
            
            {/* Desktop Navigation */}
            <div className="hidden md:flex items-center space-x-8">
              <a href="#home" className="text-gray-300 hover:text-white transition-colors">Home</a>
              <a href="/robots" className="text-gray-300 hover:text-white transition-colors">Robots</a>
              <a href="/drones" className="text-gray-300 hover:text-white transition-colors">Drones</a>
              <a href="/solarpanels" className="text-gray-300 hover:text-white transition-colors">Solarpanels</a>
              <a href="/sandbox-lab" className="text-gray-300 hover:text-white transition-colors">Sandboxlab</a>
              <a href="/contact" className="bg-gradient-to-r from-blue-500 to-purple-600 px-4 py-2 rounded-lg hover:shadow-lg transition-all">
                Contact Us
              </a>
            </div>

            {/* Mobile menu button */}
            <button 
              className="md:hidden"
              onClick={() => setIsMenuOpen(!isMenuOpen)}
            >
              {isMenuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
            </button>
          </div>
        </div>

        {/* Mobile Navigation */}
        {isMenuOpen && (
          <div className="md:hidden bg-gray-800 border-t border-gray-700">
            <div className="px-4 py-2 space-y-2">
              <a href="#home" className="block py-2 text-gray-300 hover:text-white">Home</a>
              <a href="/robots" className="block py-2 text-gray-300 hover:text-white">Robots</a>
              <a href="/drones" className="block py-2 text-gray-300 hover:text-white">Drones</a>
              <a href="/solarpanels" className="text-gray-300 hover:text-white transition-colors">Solarpanels</a>
              <a href="/sandbox-lab" className="text-gray-300 hover:text-white transition-colors">Sandboxlab</a>
              <a href="/contact" className="block py-2 text-blue-400 hover:text-blue-300">Contact Us</a>
            </div>
          </div>
        )}
      </nav>

      {/* Hero Section with Video Background */}
      <section id="home" className="relative h-screen flex items-center justify-center overflow-hidden">
        {/* Video Background */}
        <div className="absolute inset-0 w-full h-full">
          <video
            key={activeSection}
            className="w-full h-full object-cover"
            autoPlay
            muted
            loop
            playsInline
            style={{ filter: 'brightness(0.4)' }}
          >
            <source src={heroSections[activeSection].videoUrl} type="video/mp4" />
            {/* Fallback gradient background */}
          </video>
          <div className="absolute inset-0 bg-gradient-to-br from-gray-900/50 via-gray-800/50 to-gray-900/50"></div>
        </div>
        
        {/* Video Control Button */}
        <button
          onClick={toggleVideo}
          className="absolute top-24 right-8 z-20 bg-black/50 hover:bg-black/70 rounded-full p-3 transition-all duration-300 backdrop-blur-sm"
        >
          {isVideoPlaying ? <Pause className="w-5 h-5" /> : <Play className="w-5 h-5" />}
        </button>

        {/* Animated Background Elements (reduced opacity to work with video) */}
        <div className="absolute inset-0 z-5">
          <div className="absolute top-20 left-10 w-72 h-72 bg-blue-500 rounded-full mix-blend-multiply filter blur-xl opacity-10 animate-pulse"></div>
          <div className="absolute top-40 right-10 w-72 h-72 bg-purple-500 rounded-full mix-blend-multiply filter blur-xl opacity-10 animate-pulse delay-1000"></div>
          <div className="absolute -bottom-8 left-20 w-72 h-72 bg-emerald-500 rounded-full mix-blend-multiply filter blur-xl opacity-10 animate-pulse delay-2000"></div>
        </div>

        <div className="relative z-10 text-center max-w-6xl mx-auto px-4">
          <div className={`mb-8 flex justify-center text-${heroSections[activeSection].gradient.split(' ')[1].replace('to-', '')} drop-shadow-2xl`}>
            {heroSections[activeSection].icon}
          </div>
          
          <h1 className="text-5xl md:text-7xl font-bold mb-6 bg-gradient-to-r from-white to-gray-300 bg-clip-text text-transparent drop-shadow-2xl">
            {heroSections[activeSection].title}
          </h1>
          
          <p className="text-xl md:text-2xl text-gray-200 mb-12 max-w-3xl mx-auto drop-shadow-lg">
            {heroSections[activeSection].subtitle}
          </p>

          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <button className={`px-8 py-4 bg-gradient-to-r ${heroSections[activeSection].gradient} rounded-lg font-semibold hover:shadow-2xl transform hover:scale-105 transition-all duration-300 flex items-center justify-center backdrop-blur-sm`}>
              Explore Solutions
              <ArrowRight className="ml-2 w-5 h-5" />
            </button>
            <button className="px-8 py-4 border-2 border-gray-600 rounded-lg font-semibold hover:border-white hover:bg-white hover:text-gray-900 transition-all duration-300 backdrop-blur-sm">
              Learn More
            </button>
          </div>

          {/* Section Indicators */}
          <div className="flex justify-center mt-12 space-x-2">
            {heroSections.map((_, index) => (
              <button
                key={index}
                className={`w-3 h-3 rounded-full transition-all ${
                  index === activeSection ? 'bg-white shadow-lg' : 'bg-gray-600'
                }`}
                onClick={() => setActiveSection(index)}
              />
            ))}
          </div>
        </div>
      </section>

      {/* Services Section with Enhanced Video Cards */}
      <section className="py-24 bg-gradient-to-b from-gray-900 to-gray-800">
        <div className="max-w-7xl mx-auto px-4">
          <div className="text-center mb-16">
            <h2 className="text-4xl md:text-5xl font-bold mb-6">Our Core Technologies</h2>
            <p className="text-xl text-gray-400 max-w-3xl mx-auto">
              Leading innovation across robotics, aerospace, and renewable energy sectors
            </p>
          </div>

          <div className="grid md:grid-cols-3 gap-8">
            {services.map((service, index) => (
              <div key={index} className="group relative">
                <div className="bg-gray-800 rounded-2xl overflow-hidden h-full hover:bg-gray-750 transition-all duration-300 transform hover:scale-105 hover:shadow-2xl border border-gray-700 hover:border-gray-600">
                  {/* Video Background for Service Cards */}
                  <div className="relative h-48 overflow-hidden">
                    <video
                      className="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110"
                      autoPlay
                      muted
                      loop
                      playsInline
                      style={{ filter: 'brightness(0.7)' }}
                    >
                      <source src={service.videoUrl} type="video/mp4" />
                    </video>
                    <div className={`absolute inset-0 bg-gradient-to-t ${service.color} opacity-60`}></div>
                    <div className="absolute top-4 left-4 w-12 h-12 bg-black/30 backdrop-blur-sm rounded-xl flex items-center justify-center">
                      {service.icon}
                    </div>
                  </div>
                  
                  <div className="p-8">
                    <h3 className="text-2xl font-bold mb-4">{service.title}</h3>
                    <p className="text-gray-400 mb-6">{service.description}</p>
                    <div className="flex items-center text-blue-400 group-hover:text-blue-300 transition-colors">
                      <span className="font-semibold">Learn More</span>
                      <ChevronRight className="ml-1 w-4 h-4 group-hover:translate-x-1 transition-transform" />
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Stats Section */}
      <section className="py-16 bg-gradient-to-r from-blue-600 to-purple-700">
        <div className="max-w-7xl mx-auto px-4">
          <div className="grid grid-cols-2 md:grid-cols-4 gap-8 text-center">
            {stats.map((stat, index) => (
              <div key={index} className="group">
                <div className="text-4xl md:text-5xl font-bold mb-2 group-hover:scale-110 transition-transform">
                  <AnimatedNumber value={stat.number} />
                </div>
                <div className="text-blue-100 font-medium">{stat.label}</div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Why Choose Us Section */}
      <section className="py-24 bg-gray-800">
        <div className="max-w-7xl mx-auto px-4">
          <div className="grid lg:grid-cols-2 gap-16 items-center">
            <div>
              <h2 className="text-4xl md:text-5xl font-bold mb-8">
                Why Choose Allytic Labs?
              </h2>
              <div className="space-y-6">
                <div className="flex items-start space-x-4">
                  <div className="w-8 h-8 bg-gradient-to-r from-blue-500 to-purple-600 rounded-full flex items-center justify-center flex-shrink-0 mt-1">
                    <Target className="w-4 h-4" />
                  </div>
                  <div>
                    <h3 className="text-xl font-semibold mb-2">Industry Expertise</h3>
                    <p className="text-gray-400">Decades of combined experience across robotics, aerospace, and renewable energy sectors.</p>
                  </div>
                </div>
                <div className="flex items-start space-x-4">
                  <div className="w-8 h-8 bg-gradient-to-r from-emerald-500 to-teal-600 rounded-full flex items-center justify-center flex-shrink-0 mt-1">
                    <Users className="w-4 h-4" />
                  </div>
                  <div>
                    <h3 className="text-xl font-semibold mb-2">Dedicated Team</h3>
                    <p className="text-gray-400">World-class engineers and technicians committed to delivering exceptional results.</p>
                  </div>
                </div>
                <div className="flex items-start space-x-4">
                  <div className="w-8 h-8 bg-gradient-to-r from-orange-500 to-red-600 rounded-full flex items-center justify-center flex-shrink-0 mt-1">
                    <Globe className="w-4 h-4" />
                  </div>
                  <div>
                    <h3 className="text-xl font-semibold mb-2">Global Reach</h3>
                    <p className="text-gray-400">Serving clients worldwide with localized support and international standards.</p>
                  </div>
                </div>
              </div>
            </div>
            <div className="relative">
              <div className="bg-gradient-to-br from-blue-600 to-purple-700 rounded-2xl p-8 transform rotate-3 hover:rotate-0 transition-transform duration-500">
                <div className="bg-white rounded-xl p-6 text-gray-900">
                  <h3 className="text-2xl font-bold mb-4">Ready to Transform Your Business?</h3>
                  <p className="mb-6">Let's discuss how our cutting-edge technologies can revolutionize your operations.</p>
                  <a href="/contact" className="inline-flex items-center bg-gradient-to-r from-blue-600 to-purple-700 text-white px-6 py-3 rounded-lg font-semibold hover:shadow-lg transition-all">
                    Get Started Today
                    <ArrowRight className="ml-2 w-4 h-4" />
                  </a>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="bg-gray-900 border-t border-gray-800 py-12">
        <div className="max-w-7xl mx-auto px-4">
          <div className="grid md:grid-cols-4 gap-8">
            <div>
              <div className="flex items-center space-x-2 mb-4">
                <div className="w-8 h-8 bg-gradient-to-r from-blue-500 to-purple-600 rounded-lg flex items-center justify-center">
                  <Zap className="w-4 h-4 text-white" />
                </div>
                <span className="text-lg font-bold">Allytic Labs</span>
              </div>
              <p className="text-gray-400">Leading the future of robotics, drones, and renewable energy solutions.</p>
            </div>
            <div>
              <h4 className="font-semibold mb-4">Services</h4>
              <ul className="space-y-2 text-gray-400">
                <li><a href="/robots" className="hover:text-white transition-colors">Industrial Robotics</a></li>
                <li><a href="/drones" className="hover:text-white transition-colors">Drone Technology</a></li>
                <li><a href="#" className="hover:text-white transition-colors">Solar Solutions</a></li>
              </ul>
            </div>
            <div>
              <h4 className="font-semibold mb-4">Company</h4>
              <ul className="space-y-2 text-gray-400">
                <li><a href="#" className="hover:text-white transition-colors">About Us</a></li>
                <li><a href="#" className="hover:text-white transition-colors">Careers</a></li>
                <li><a href="#" className="hover:text-white transition-colors">News</a></li>
              </ul>
            </div>
            <div>
              <h4 className="font-semibold mb-4">Contact</h4>
              <ul className="space-y-2 text-gray-400">
                <li><a href="/contact" className="hover:text-white transition-colors">Get in Touch</a></li>
                <li><a href="#" className="hover:text-white transition-colors">Support</a></li>
                <li><a href="#" className="hover:text-white transition-colors">Partners</a></li>
              </ul>
            </div>
          </div>
          <div className="border-t border-gray-800 mt-8 pt-8 text-center text-gray-400">
            <p>&copy; 2025 Allytic Labs. All rights reserved.</p>
          </div>
        </div>
      </footer>
    </div>
  );
};

export default Home;