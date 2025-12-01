import React, { useState, useEffect } from 'react';
import { ChevronLeft, ChevronRight, ArrowRight, Instagram, Github, Youtube, Linkedin } from 'lucide-react';
import { useNavigate, Link } from 'react-router-dom';

const droneAPI = {
  getAll: async () => {
    await new Promise(resolve => setTimeout(resolve, 1000));
    return [
      {
        id: 1,
        name: "SkyGuard Pro",
        application: "Surveillance",
        type: "Security",
        description: "Advanced surveillance drone with 4K thermal imaging and 8-hour flight time",
        image: "https://images.pexels.com/photos/442587/pexels-photo-442587.jpeg?auto=compress&cs=tinysrgb&w=1200",
        features: "4K Camera | Thermal Imaging | 8hr Battery",
        price: "$15,000",
        rating: 4.8,
        reviews: 124
      },
      {
        id: 2,
        name: "CargoMax 500",
        application: "Transport",
        type: "Delivery",
        description: "Heavy-lift cargo drone with 50kg payload capacity and autonomous navigation",
        image: "https://images.pexels.com/photos/8566473/pexels-photo-8566473.jpeg?auto=compress&cs=tinysrgb&w=1200",
        features: "50kg Payload | GPS Navigation | Weather Resistant",
        price: "$25,000",
        rating: 4.9,
        reviews: 89
      },
      {
        id: 3,
        name: "AgriScan X1",
        application: "Agriculture",
        type: "Farming",
        description: "Precision agriculture drone with multispectral imaging for crop health monitoring",
        image: "https://images.pexels.com/photos/1034650/pexels-photo-1034650.jpeg?auto=compress&cs=tinysrgb&w=1200",
        features: "Multispectral | AI Analysis | Crop Mapping",
        price: "$18,000",
        rating: 4.7,
        reviews: 156
      },
      {
        id: 4,
        name: "MapMaster Pro",
        application: "Mapping",
        type: "Survey",
        description: "High-precision mapping drone with LiDAR and photogrammetry capabilities",
        image: "https://images.pexels.com/photos/2246476/pexels-photo-2246476.jpeg?auto=compress&cs=tinysrgb&w=1200",
        features: "LiDAR | 3D Mapping | RTK GPS",
        price: "$22,000",
        rating: 4.8,
        reviews: 73
      },
      {
        id: 5,
        name: "CineAir 8K",
        application: "Media",
        type: "Cinematography",
        description: "Professional cinema drone with 8K video recording and gimbal stabilization",
        image: "https://images.pexels.com/photos/3945683/pexels-photo-3945683.jpeg?auto=compress&cs=tinysrgb&w=1200",
        features: "8K Video | 3-Axis Gimbal | RAW Recording",
        price: "$12,000",
        rating: 4.9,
        reviews: 201
      },
      {
        id: 6,
        name: "PartyFlyer LED",
        application: "Entertainment",
        type: "Events",
        description: "Light show drone for events with synchronized LED displays and formations",
        image: "https://images.pexels.com/photos/1730877/pexels-photo-1730877.jpeg?auto=compress&cs=tinysrgb&w=1200",
        features: "LED Display | Swarm Control | Show Programming",
        price: "$8,000",
        rating: 4.6,
        reviews: 94
      }
    ];
  }
};

const Drones = () => {
  const navigate = useNavigate();
  const [currentGalleryIndex, setCurrentGalleryIndex] = useState(0);
  const [hoveredDroneId, setHoveredDroneId] = useState(null);
  const [droneApplications, setDroneApplications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchDrones = async () => {
      try {
        setLoading(true);
        const drones = await droneAPI.getAll();
        setDroneApplications(drones);
        setError(null);
      } catch (err) {
        console.error('Failed to fetch drones:', err);
        setError('Failed to load drones. Please try again later.');
        setDroneApplications([]);
      } finally {
        setLoading(false);
      }
    };

    fetchDrones();
  }, []);

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

  const nextGalleryItem = () => {
    setCurrentGalleryIndex((prev) => (prev + 1) % galleryItems.length);
  };

  const prevGalleryItem = () => {
    setCurrentGalleryIndex((prev) => (prev - 1 + galleryItems.length) % galleryItems.length);
  };

  const handleOrderNow = (drone) => {
    console.log('Navigating to order page with drone:', drone);
    navigate('/order', {
      state: { robot: drone },
      replace: false
    });
  };

  return (
    <div className="min-h-screen bg-gray-900 text-white overflow-hidden">
      <nav className="fixed top-0 w-full z-50 bg-gray-900/95 backdrop-blur-sm border-b border-gray-800">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <Link to="/" className="flex items-center space-x-2">
              <div className="w-10 h-10 bg-gradient-to-r from-blue-500 to-cyan-600 rounded-lg flex items-center justify-center font-bold">
                A
              </div>
              <span className="text-xl font-bold bg-gradient-to-r from-blue-400 to-cyan-400 bg-clip-text text-transparent">
                Allytic Labs
              </span>
            </Link>

            <div className="hidden md:flex items-center space-x-8">
              <Link to="/" className="text-gray-300 hover:text-white transition-colors">Home</Link>
              <Link to="/robots" className="text-gray-300 hover:text-white transition-colors">Robots</Link>
              <Link to="/drones" className="text-blue-400 hover:text-blue-300 transition-colors">Drones</Link>
              <Link to="/solarpanels" className="text-gray-300 hover:text-white transition-colors">Solar</Link>
              <Link to="/contact" className="bg-gradient-to-r from-blue-500 to-cyan-600 px-4 py-2 rounded-lg hover:shadow-lg transition-all">
                Contact Us
              </Link>
            </div>
          </div>
        </div>
      </nav>

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
                  <button
                    onClick={() => handleOrderNow({
                      id: `gallery-${currentGalleryIndex}`,
                      name: galleryItems[currentGalleryIndex].title,
                      type: galleryItems[currentGalleryIndex].highlight,
                      description: galleryItems[currentGalleryIndex].subtitle,
                      image: galleryItems[currentGalleryIndex].image,
                      price: '$25,000'
                    })}
                    className="px-8 py-4 bg-gradient-to-r from-blue-600 to-cyan-600 rounded-lg font-semibold hover:shadow-2xl transform hover:scale-105 transition-all duration-300 flex items-center justify-center"
                  >
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

      <section className="py-24 bg-gradient-to-b from-gray-900 via-gray-800 to-gray-900">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-20">
            <h2 className="text-5xl md:text-6xl font-bold mb-6 text-white">Drone Solutions for Every Industry</h2>
            <p className="text-xl text-gray-400 max-w-3xl mx-auto">
              Discover our comprehensive range of professional drones designed for surveillance, transport, agriculture, mapping, media, and entertainment
            </p>
          </div>

          {loading && (
            <div className="text-center py-20">
              <div className="inline-block animate-spin rounded-full h-16 w-16 border-t-4 border-b-4 border-blue-500 mb-4"></div>
              <p className="text-xl text-gray-400">Loading drones from database...</p>
            </div>
          )}

          {error && !loading && (
            <div className="text-center py-20">
              <div className="max-w-md mx-auto bg-red-900/20 border border-red-500/50 rounded-lg p-8">
                <p className="text-red-400 text-lg mb-4">{error}</p>
                <button
                  onClick={() => window.location.reload()}
                  className="px-6 py-3 bg-gradient-to-r from-blue-600 to-cyan-600 rounded-lg hover:shadow-lg transition-all duration-300 font-semibold"
                >
                  Retry Loading
                </button>
              </div>
            </div>
          )}

          {!loading && !error && droneApplications.length > 0 && (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
              {droneApplications.map((drone, index) => (
                <div
                  key={drone.id || index}
                  onMouseEnter={() => setHoveredDroneId(index)}
                  onMouseLeave={() => setHoveredDroneId(null)}
                  className="relative overflow-hidden rounded-2xl border border-gray-700 hover:border-blue-500 transition-all duration-500 h-96 group"
                >
                  <img
                    src={drone.image || "https://images.pexels.com/photos/442587/pexels-photo-442587.jpeg?auto=compress&cs=tinysrgb&w=600"}
                    alt={drone.name}
                    className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-700"
                    onError={(e) => {
                      e.target.src = "https://images.pexels.com/photos/442587/pexels-photo-442587.jpeg?auto=compress&cs=tinysrgb&w=600";
                    }}
                  />

                  <div className="absolute inset-0 bg-gradient-to-t from-gray-900 via-gray-900/60 to-transparent opacity-80 group-hover:opacity-70 transition-opacity duration-500"></div>

                  <div className={`absolute inset-0 flex flex-col justify-between p-8 transition-all duration-500 ${
                    hoveredDroneId === index ? 'opacity-100' : 'opacity-0'
                  }`}>
                    <div className="inline-block px-3 py-1 bg-blue-600/80 rounded-full w-fit">
                      <span className="text-sm font-semibold text-white">
                        {drone.application || drone.type || 'Professional Drone'}
                      </span>
                    </div>

                    <div>
                      <h3 className="text-2xl font-bold text-white mb-2">{drone.name}</h3>
                      <p className="text-gray-200 text-sm mb-4 line-clamp-3">
                        {drone.description || 'Advanced drone technology for professional applications'}
                      </p>

                      <div className="mb-4 space-y-2">
                        <p className="text-xs text-gray-300 font-semibold uppercase tracking-wide">Key Features:</p>
                        <p className="text-sm text-blue-300">
                          {drone.features || drone.capabilities?.join(' | ') || 'Advanced technology'}
                        </p>
                      </div>

                      {drone.price && (
                        <p className="text-lg font-bold text-blue-400 mb-2">{drone.price}</p>
                      )}

                      {drone.rating && (
                        <div className="flex items-center gap-2 mb-3">
                          <div className="flex items-center">
                            <span className="text-yellow-400 text-sm">★</span>
                            <span className="text-white text-sm ml-1">{drone.rating}</span>
                          </div>
                          {drone.reviews && (
                            <span className="text-gray-400 text-xs">({drone.reviews} reviews)</span>
                          )}
                        </div>
                      )}

                      <button
                        onClick={(e) => {
                          e.preventDefault();
                          e.stopPropagation();
                          handleOrderNow(drone);
                        }}
                        className="w-full py-2 bg-gradient-to-r from-blue-600 to-cyan-600 text-white font-semibold rounded-lg hover:shadow-lg transition-all duration-300 text-sm"
                      >
                        Order Now
                      </button>
                    </div>
                  </div>

                  <div className={`absolute inset-0 flex flex-col justify-between p-8 transition-all duration-500 ${
                    hoveredDroneId === index ? 'opacity-0' : 'opacity-100'
                  }`}>
                    <div className="inline-block px-3 py-1 bg-blue-600/80 rounded-full w-fit">
                      <span className="text-sm font-semibold text-white">
                        {drone.application || drone.type || 'Professional Drone'}
                      </span>
                    </div>

                    <div>
                      <h3 className="text-3xl font-bold text-white">{drone.name}</h3>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}

          {!loading && !error && droneApplications.length === 0 && (
            <div className="text-center py-20">
              <div className="max-w-md mx-auto bg-gray-800/50 border border-gray-700 rounded-lg p-8">
                <p className="text-gray-400 text-lg mb-4">No drones available at the moment.</p>
                <p className="text-gray-500 text-sm">Please check back later or contact support.</p>
              </div>
            </div>
          )}
        </div>
      </section>

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
            <button
              onClick={() => navigate('/latest-models')}
              className="px-10 py-5 bg-gradient-to-r from-blue-600 to-cyan-600 rounded-lg font-bold text-lg hover:shadow-2xl transform hover:scale-105 transition-all duration-300 flex items-center justify-center mx-auto sm:mx-0"
            >
              Explore Latest Models
              <ArrowRight className="ml-3 w-6 h-6" />
            </button>
            <button className="px-10 py-5 border-2 border-white/30 bg-white/10 backdrop-blur-sm rounded-lg font-bold text-lg hover:border-white hover:bg-white/20 transition-all duration-300">
              Watch Technology Demo
            </button>
          </div>
        </div>
      </section>

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
                <li><Link to="/robots" className="hover:text-white transition-colors">Robotics</Link></li>
                <li><Link to="/drones" className="hover:text-white transition-colors">Drones</Link></li>
                <li><Link to="/solarpanels" className="hover:text-white transition-colors">Solar</Link></li>
              </ul>
            </div>

            <div>
              <h4 className="font-semibold text-white mb-4">Company</h4>
              <ul className="space-y-2 text-gray-400 text-sm">
                <li><Link to="#" className="hover:text-white transition-colors">About</Link></li>
                <li><Link to="#" className="hover:text-white transition-colors">Careers</Link></li>
                <li><Link to="#" className="hover:text-white transition-colors">News</Link></li>
              </ul>
            </div>

            <div>
              <h4 className="font-semibold text-white mb-4">Contact</h4>
              <ul className="space-y-2 text-gray-400 text-sm">
                <li><Link to="/contact" className="hover:text-white transition-colors">Get in Touch</Link></li>
                <li><Link to="#" className="hover:text-white transition-colors">Support</Link></li>
                <li><Link to="#" className="hover:text-white transition-colors">Partnerships</Link></li>
              </ul>
            </div>
          </div>

          <div className="border-t border-gray-800 pt-8">
            <div className="flex flex-col md:flex-row justify-between items-center gap-8">
              <div className="text-sm text-gray-400">
                <p>&copy; 2025 Allytic Labs. All rights reserved.</p>
                <div className="flex space-x-4 mt-4">
                  <Link to="#" className="hover:text-white transition-colors">Terms and Conditions</Link>
                  <span>|</span>
                  <Link to="#" className="hover:text-white transition-colors">Privacy Policy</Link>
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