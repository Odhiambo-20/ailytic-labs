import React, { useState, useEffect } from 'react';
import { Sun, Zap, TrendingUp, Shield, Leaf, Globe, ArrowRight, Play, CheckCircle, Star, MapPin, Phone, Mail, Calendar, Users, Award, BarChart3 } from 'lucide-react';
import { solarPanelAPI } from '../services/api';
import solarVideo from '../assets/solar.mp4';
import solar from '../assets/solar.webm';
// Import all local images at the top
import solarFarmImage from '../assets/solar panels.jpg';
import cuttingEdgeImage from '../assets/cutting-edge solar panels.jpg';
import solarPanels1Image from '../assets/solar panels 1.jpg';
import RoofTopSolar from '../assets/roof-top solar.jpg';

const SolarPanels = () => {
  const [currentSlide, setCurrentSlide] = useState(0);
  const [solarPanels, setSolarPanels] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedType, setSelectedType] = useState('all');

  // Fetch solar panels from backend
  useEffect(() => {
    const fetchSolarPanels = async () => {
      try {
        setLoading(true);
        const panelsData = await solarPanelAPI.getAll();
        setSolarPanels(panelsData);
        setError(null);
      } catch (err) {
        console.error('Failed to fetch solar panels:', err);
        setError('Failed to load solar panels. Please try again later.');
        setSolarPanels([]);
      } finally {
        setLoading(false);
      }
    };

    fetchSolarPanels();
  }, []);

  // Testimonials auto-slide
  useEffect(() => {
    const interval = setInterval(() => {
      setCurrentSlide(prev => (prev + 1) % testimonials.length);
    }, 5000);
    return () => clearInterval(interval);
  }, []);

  const testimonials = [
    {
      name: 'Sarah Johnson',
      company: 'Green Home Solutions',
      rating: 5,
      text: 'Allytic Labs solar panels have exceeded our expectations. The efficiency is outstanding and the smart monitoring system is incredibly useful.',
      location: 'Nairobi, Kenya'
    },
    {
      name: 'Michael Chen',
      company: 'EcoTech Industries',
      rating: 5,
      text: 'We installed their commercial solar system last year and have seen a 40% reduction in energy costs. Excellent ROI and reliable performance.',
      location: 'Lagos, Nigeria'
    },
    {
      name: 'Dr. Amara Okafor',
      company: 'Solar Research Institute',
      rating: 5,
      text: 'The technology and innovation behind these panels is impressive. Perfect for our research facility\'s energy needs.',
      location: 'Accra, Ghana'
    }
  ];

  const benefits = [
    {
      icon: Leaf,
      title: 'Environmental Impact',
      description: 'Reduce carbon footprint by up to 80% with clean renewable energy',
      color: 'bg-green-500/20 border-green-400/30'
    },
    {
      icon: TrendingUp,
      title: 'Cost Savings',
      description: 'Save 60-90% on electricity bills with efficient solar solutions',
      color: 'bg-blue-500/20 border-blue-400/30'
    },
    {
      icon: Shield,
      title: 'Reliability',
      description: '25-30 year warranty with 99.9% uptime guarantee',
      color: 'bg-purple-500/20 border-purple-400/30'
    },
    {
      icon: BarChart3,
      title: 'Smart Monitoring',
      description: 'Real-time performance tracking and predictive maintenance',
      color: 'bg-orange-500/20 border-orange-400/30'
    }
  ];

  // Filter solar panels by type
  const filteredPanels = selectedType === 'all' 
    ? solarPanels 
    : solarPanels.filter(panel => panel.type === selectedType);

  // Get unique panel types for filtering
  const panelTypes = ['all', ...new Set(solarPanels.map(panel => panel.type))];

  return (
    <div className="min-h-screen bg-white">
      {/* Hero Section with Fullscreen Video */}
      <div className="relative overflow-hidden h-screen flex items-center pt-20">
        {/* Fullscreen Video Background */}
        <div className="absolute inset-0 z-0">
          <video
            autoPlay
            muted
            loop
            playsInline
            className="w-full h-full object-cover"
          >
            <source src={solarVideo} type="video/mp4" />
            Your browser does not support the video tag.
          </video>
          <div className="absolute inset-0 bg-gradient-to-r from-black/60 via-black/40 to-transparent"></div>
        </div>

        {/* Hero Content */}
        <div className="relative z-10 max-w-7xl mx-auto px-6 w-full">
          <div className="max-w-2xl">
            <h1 className="text-6xl md:text-7xl font-bold text-white mb-6">
              Advanced Solar Panel Technology
            </h1>
            <p className="text-2xl text-gray-100 mb-8">
              Transforming energy consumption with intelligent solar solutions that deliver exceptional performance and sustainability.
            </p>
            <button className="flex items-center gap-3 px-8 py-4 bg-gradient-to-r from-orange-600 to-yellow-600 hover:from-orange-700 hover:to-yellow-700 rounded-lg text-white font-bold text-lg transition-all">
              <Calendar className="w-5 h-5" />
              Get Started Today
            </button>
          </div>
        </div>
      </div>

      {/* Rooftop Solar Section */}
      <div className="py-20 bg-gradient-to-br from-slate-50 to-blue-50">
        <div className="max-w-7xl mx-auto px-6">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-12 items-center">
            <div className="order-2 lg:order-1">
              <img
                src={RoofTopSolar}
                alt="Rooftop Solar"
                className="w-full h-96 md:h-[500px] object-cover rounded-3xl shadow-2xl"
              />
            </div>
            <div className="order-1 lg:order-2">
              <h2 className="text-5xl font-bold text-slate-900 mb-6">
                Transform Your Home with Rooftop Solar
              </h2>
              <p className="text-xl text-slate-700 mb-6">
                Our advanced rooftop solar solutions bring clean, renewable energy directly to your home. Experience unmatched efficiency with premium monocrystalline panels that maximize every ray of sunlight.
              </p>
              <ul className="space-y-4 mb-8">
                <li className="flex items-center gap-3">
                  <CheckCircle className="w-6 h-6 text-green-600 flex-shrink-0" />
                  <span className="text-lg text-slate-700">Up to 22% energy efficiency</span>
                </li>
                <li className="flex items-center gap-3">
                  <CheckCircle className="w-6 h-6 text-green-600 flex-shrink-0" />
                  <span className="text-lg text-slate-700">Weather-resistant installation</span>
                </li>
                <li className="flex items-center gap-3">
                  <CheckCircle className="w-6 h-6 text-green-600 flex-shrink-0" />
                  <span className="text-lg text-slate-700">Smart monitoring system included</span>
                </li>
                <li className="flex items-center gap-3">
                  <CheckCircle className="w-6 h-6 text-green-600 flex-shrink-0" />
                  <span className="text-lg text-slate-700">25-year performance warranty</span>
                </li>
              </ul>
              <button className="flex items-center gap-2 px-8 py-4 bg-gradient-to-r from-orange-600 to-yellow-600 hover:from-orange-700 hover:to-yellow-700 rounded-lg text-white font-bold transition-all">
                Learn More
                <ArrowRight className="w-5 h-5" />
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* Solar Farm Section */}
      <div className="py-20 bg-white">
        <div className="max-w-7xl mx-auto px-6">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-12 items-center">
            <div>
              <h2 className="text-5xl font-bold text-slate-900 mb-6">
                Large-Scale Solar Farm Solutions
              </h2>
              <p className="text-xl text-slate-700 mb-6">
                Our solar farm technology harnesses the power of thousands of panels to generate massive amounts of clean energy. Perfect for agricultural applications and utility-scale projects across Africa.
              </p>
              <div className="space-y-4 mb-8">
                <div className="bg-gradient-to-r from-orange-50 to-yellow-50 p-4 rounded-lg border border-orange-200">
                  <h3 className="font-bold text-slate-900 mb-2">Energy Generation</h3>
                  <p className="text-slate-700">Generate 500+ kWh annually per installed system</p>
                </div>
                <div className="bg-gradient-to-r from-blue-50 to-cyan-50 p-4 rounded-lg border border-blue-200">
                  <h3 className="font-bold text-slate-900 mb-2">Cost Efficiency</h3>
                  <p className="text-slate-700">Reduce operational costs by up to 70%</p>
                </div>
                <div className="bg-gradient-to-r from-green-50 to-emerald-50 p-4 rounded-lg border border-green-200">
                  <h3 className="font-bold text-slate-900 mb-2">Environmental Impact</h3>
                  <p className="text-slate-700">Offset 2000+ tons of carbon annually</p>
                </div>
              </div>
              <button className="flex items-center gap-2 px-8 py-4 border-2 border-orange-600 text-orange-600 hover:bg-orange-50 font-bold rounded-lg transition-all">
                Explore Farm Solutions
                <ArrowRight className="w-5 h-5" />
              </button>
            </div>
            <div>
              <img
                src={solarFarmImage}
                alt="Solar Farm"
                className="w-full h-96 md:h-[500px] object-cover rounded-3xl shadow-2xl"
              />
            </div>
          </div>
        </div>
      </div>

      {/* Commercial Solutions Section */}
      <div className="py-20 bg-gradient-to-br from-slate-50 to-orange-50">
        <div className="max-w-7xl mx-auto px-6">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-12 items-center">
            <div className="order-2 lg:order-1">
              <img
                src={cuttingEdgeImage}
                alt="Commercial Solar"
                className="w-full h-96 md:h-[500px] object-cover rounded-3xl shadow-2xl"
              />
            </div>
            <div className="order-1 lg:order-2">
              <h2 className="text-5xl font-bold text-slate-900 mb-6">
                Enterprise-Grade Commercial Solar
              </h2>
              <p className="text-xl text-slate-700 mb-6">
                Designed for businesses that demand maximum performance and reliability. Our commercial systems integrate seamlessly with your existing infrastructure while delivering impressive ROI.
              </p>
              <div className="space-y-3 mb-8">
                <div className="flex items-start gap-4">
                  <div className="w-12 h-12 bg-gradient-to-r from-orange-500 to-yellow-500 rounded-lg flex items-center justify-center flex-shrink-0">
                    <TrendingUp className="w-6 h-6 text-white" />
                  </div>
                  <div>
                    <h4 className="font-bold text-slate-900 mb-1">500+ kW Capacity</h4>
                    <p className="text-slate-700">Scale to meet any business energy demands</p>
                  </div>
                </div>
                <div className="flex items-start gap-4">
                  <div className="w-12 h-12 bg-gradient-to-r from-blue-500 to-cyan-500 rounded-lg flex items-center justify-center flex-shrink-0">
                    <Award className="w-6 h-6 text-white" />
                  </div>
                  <div>
                    <h4 className="font-bold text-slate-900 mb-1">Industry Leading</h4>
                    <p className="text-slate-700">Certified and trusted by Fortune 500 companies</p>
                  </div>
                </div>
                <div className="flex items-start gap-4">
                  <div className="w-12 h-12 bg-gradient-to-r from-green-500 to-emerald-500 rounded-lg flex items-center justify-center flex-shrink-0">
                    <Zap className="w-6 h-6 text-white" />
                  </div>
                  <div>
                    <h4 className="font-bold text-slate-900 mb-1">24/7 Monitoring</h4>
                    <p className="text-slate-700">Real-time analytics and performance tracking</p>
                  </div>
                </div>
              </div>
              <button className="flex items-center gap-2 px-8 py-4 bg-gradient-to-r from-orange-600 to-yellow-600 hover:from-orange-700 hover:to-yellow-700 rounded-lg text-white font-bold transition-all">
                Request Commercial Quote
                <ArrowRight className="w-5 h-5" />
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* API-Driven Solar Panels Catalog Section */}
      <div className="py-20 bg-gradient-to-br from-orange-50 to-yellow-50">
        <div className="max-w-7xl mx-auto px-6">
          <div className="text-center mb-12">
            <h2 className="text-4xl md:text-5xl font-bold text-slate-900 mb-6">
              Our Solar Panel Collection
            </h2>
            <p className="text-xl text-slate-700 mb-8 max-w-3xl mx-auto">
              Explore our complete range of solar panels designed for residential, commercial, and industrial applications
            </p>

            {/* Filter Buttons */}
            <div className="flex flex-wrap justify-center gap-3 mb-8">
              {panelTypes.map((type) => (
                <button
                  key={type}
                  onClick={() => setSelectedType(type)}
                  className={`px-6 py-2 rounded-full font-semibold transition-all duration-300 ${
                    selectedType === type
                      ? 'bg-gradient-to-r from-orange-600 to-yellow-600 text-white'
                      : 'bg-white text-slate-700 hover:bg-orange-100 border border-orange-200'
                  }`}
                >
                  {type === 'all' ? 'All Panels' : type}
                </button>
              ))}
            </div>
          </div>

          {/* Loading State */}
          {loading && (
            <div className="text-center py-20">
              <div className="inline-block animate-spin rounded-full h-16 w-16 border-t-4 border-b-4 border-orange-500 mb-4"></div>
              <p className="text-xl text-slate-700">Loading solar panels from database...</p>
            </div>
          )}

          {/* Error State */}
          {error && !loading && (
            <div className="text-center py-20">
              <div className="max-w-md mx-auto bg-red-50 border border-red-300 rounded-lg p-8">
                <p className="text-red-600 text-lg mb-4">{error}</p>
                <button 
                  onClick={() => window.location.reload()}
                  className="px-6 py-3 bg-gradient-to-r from-orange-600 to-yellow-600 text-white rounded-lg hover:shadow-lg transition-all duration-300 font-semibold"
                >
                  Retry Loading
                </button>
              </div>
            </div>
          )}

          {/* Solar Panels Grid - API Data */}
          {!loading && !error && filteredPanels.length > 0 && (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
              {filteredPanels.map((panel) => (
                <div
                  key={panel.id}
                  className="bg-white rounded-2xl overflow-hidden border border-orange-200 hover:border-orange-400 hover:shadow-2xl transition-all duration-300 transform hover:scale-105"
                >
                  <div className="relative h-64 overflow-hidden">
                    <img
                      src={panel.image || solarPanels1Image}
                      alt={panel.name}
                      className="w-full h-full object-cover"
                      onError={(e) => {
                        e.target.src = solarPanels1Image;
                      }}
                    />
                    <div className="absolute top-4 right-4 bg-orange-600/90 backdrop-blur-sm px-3 py-1 rounded-full">
                      <span className="text-white text-sm font-semibold">{panel.type}</span>
                    </div>
                  </div>

                  <div className="p-6">
                    <h3 className="text-2xl font-bold text-slate-900 mb-2">{panel.name}</h3>
                    <p className="text-slate-700 text-sm mb-4 line-clamp-3">
                      {panel.description || 'High-efficiency solar panel technology'}
                    </p>

                    {/* Specifications */}
                    <div className="space-y-2 mb-4">
                      {panel.capacity && (
                        <div className="flex justify-between items-center">
                          <span className="text-sm text-slate-600">Capacity:</span>
                          <span className="text-sm font-semibold text-slate-900">{panel.capacity}</span>
                        </div>
                      )}
                      {panel.efficiency && (
                        <div className="flex justify-between items-center">
                          <span className="text-sm text-slate-600">Efficiency:</span>
                          <span className="text-sm font-semibold text-slate-900">{panel.efficiency}</span>
                        </div>
                      )}
                    </div>

                    {/* Price and Rating */}
                    <div className="flex justify-between items-center mb-4 pb-4 border-b border-gray-200">
                      {panel.price && (
                        <p className="text-2xl font-bold text-orange-600">{panel.price}</p>
                      )}
                      {panel.rating && (
                        <div className="flex items-center gap-2">
                          <span className="text-yellow-500">★</span>
                          <span className="text-slate-900 font-semibold">{panel.rating}</span>
                          {panel.reviews && (
                            <span className="text-slate-500 text-sm">({panel.reviews})</span>
                          )}
                        </div>
                      )}
                    </div>

                    <button className="w-full py-3 bg-gradient-to-r from-orange-600 to-yellow-600 text-white font-semibold rounded-lg hover:from-orange-700 hover:to-yellow-700 transition-all duration-300 transform hover:scale-105 shadow-lg flex items-center justify-center gap-2">
                      Get Quote
                      <ArrowRight className="w-4 h-4" />
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}

          {/* Empty State */}
          {!loading && !error && filteredPanels.length === 0 && (
            <div className="text-center py-20">
              <div className="max-w-md mx-auto bg-white border border-orange-200 rounded-lg p-8">
                <Sun className="w-16 h-16 text-orange-400 mx-auto mb-4" />
                <p className="text-slate-700 text-lg mb-4">
                  {selectedType === 'all' 
                    ? 'No solar panels available at the moment.' 
                    : `No ${selectedType} panels found.`}
                </p>
                <p className="text-slate-500 text-sm">
                  {selectedType !== 'all' && 'Try selecting a different category or '}
                  Please check back later for updates.
                </p>
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Innovation Section */}
      <div className="relative overflow-hidden min-h-screen flex items-center">
        {/* Fullscreen Video Background */}
        <div className="absolute inset-0 z-0">
          <video
            autoPlay
            muted
            loop
            playsInline
            className="w-full h-full object-cover"
          >
            <source src={solar} type="video/webm" />
            Your browser does not support the video tag.
          </video>
          <div className="absolute inset-0 bg-gradient-to-r from-black/70 via-black/50 to-black/70"></div>
        </div>

        {/* Content Over Video */}
        <div className="relative z-10 max-w-7xl mx-auto px-6 py-20 w-full">
          <div className="max-w-3xl mx-auto text-center">
            <h2 className="text-5xl md:text-6xl font-bold text-white mb-6">
              Cutting-Edge Solar Innovation
            </h2>
            <p className="text-2xl text-gray-100 mb-8">
              Stay ahead with our latest solar panel technology featuring AI-powered optimization, predictive maintenance, and real-time energy management systems.
            </p>
            <div className="bg-white/10 backdrop-blur-md border border-white/20 rounded-2xl p-8 mb-8">
              <h3 className="text-3xl font-bold text-white mb-6">Next-Generation Features</h3>
              <ul className="space-y-4">
                <li className="flex items-center gap-4">
                  <div className="w-3 h-3 bg-orange-500 rounded-full flex-shrink-0"></div>
                  <span className="text-xl text-gray-100">AI-powered energy optimization</span>
                </li>
                <li className="flex items-center gap-4">
                  <div className="w-3 h-3 bg-orange-500 rounded-full flex-shrink-0"></div>
                  <span className="text-xl text-gray-100">Predictive maintenance alerts</span>
                </li>
                <li className="flex items-center gap-4">
                  <div className="w-3 h-3 bg-orange-500 rounded-full flex-shrink-0"></div>
                  <span className="text-xl text-gray-100">Mobile app control & monitoring</span>
                </li>
                <li className="flex items-center gap-4">
                  <div className="w-3 h-3 bg-orange-500 rounded-full flex-shrink-0"></div>
                  <span className="text-xl text-gray-100">Weather-adaptive performance</span>
                </li>
              </ul>
            </div>
            <button className="flex items-center justify-center gap-2 px-10 py-5 bg-gradient-to-r from-orange-600 to-yellow-600 hover:from-orange-700 hover:to-yellow-700 text-white font-bold text-lg rounded-lg transition-all mx-auto">
              Explore Technology
              <ArrowRight className="w-5 h-5" />
            </button>
          </div>
        </div>
      </div>

      {/* Benefits Section */}
      <div className="bg-black/5 py-20">
        <div className="max-w-7xl mx-auto px-6">
          <div className="text-center mb-16">
            <h2 className="text-4xl font-bold text-slate-900 mb-4">Why Choose Allytic Solar?</h2>
            <p className="text-xl text-slate-700">
              Experience the advantages of our advanced solar technology and comprehensive service.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
            {benefits.map((benefit, index) => {
              const Icon = benefit.icon;
              return (
                <div key={index} className={`${benefit.color} border rounded-2xl p-6 hover:scale-105 transition-transform`}>
                  <Icon className="w-12 h-12 text-slate-900 mb-4" />
                  <h3 className="text-xl font-bold text-slate-900 mb-3">{benefit.title}</h3>
                  <p className="text-slate-700">{benefit.description}</p>
                </div>
              );
            })}
          </div>
        </div>
      </div>

      {/* Testimonials */}
      <div className="max-w-7xl mx-auto px-6 py-20">
        <div className="text-center mb-16">
          <h2 className="text-4xl font-bold text-slate-900 mb-4">What Our Customers Say</h2>
          <p className="text-xl text-slate-700">
            Real feedback from satisfied customers across Africa and beyond.
          </p>
        </div>

        <div className="relative">
          <div className="bg-gradient-to-br from-orange-50 to-yellow-50 rounded-2xl p-8 border border-orange-200 shadow-lg">
            <div className="text-center">
              <div className="flex justify-center mb-4">
                {[...Array(testimonials[currentSlide].rating)].map((_, i) => (
                  <Star key={i} className="w-6 h-6 text-yellow-500 fill-current" />
                ))}
              </div>
              
              <blockquote className="text-2xl text-slate-900 mb-6 italic font-medium">
                "{testimonials[currentSlide].text}"
              </blockquote>
              
              <div className="flex items-center justify-center gap-4">
                <div className="w-12 h-12 bg-gradient-to-r from-orange-500 to-yellow-500 rounded-full flex items-center justify-center text-white font-bold">
                  {testimonials[currentSlide].name.charAt(0)}
                </div>
                <div className="text-left">
                  <div className="text-slate-900 font-medium text-lg">{testimonials[currentSlide].name}</div>
                  <div className="text-slate-700">{testimonials[currentSlide].company}</div>
                  <div className="flex items-center gap-1 text-orange-600 text-sm">
                    <MapPin className="w-3 h-3" />
                    {testimonials[currentSlide].location}
                  </div>
                </div>
              </div>
            </div>
          </div>

          {/* Slide indicators */}
          <div className="flex justify-center mt-6 gap-2">
            {testimonials.map((_, index) => (
              <button
                key={index}
                onClick={() => setCurrentSlide(index)}
                className={`w-3 h-3 rounded-full transition-colors ${
                  currentSlide === index ? 'bg-orange-600' : 'bg-gray-400'
                }`}
              />
            ))}
          </div>
        </div>
      </div>

      {/* CTA Section */}
      <div className="bg-gradient-to-r from-orange-600/95 to-yellow-600/95 py-20">
        <div className="max-w-4xl mx-auto text-center px-6">
          <h2 className="text-4xl font-bold text-white mb-4">Ready to Go Solar?</h2>
          <p className="text-xl text-white/90 mb-8">
            Join thousands of satisfied customers who have made the switch to clean, renewable energy.
            Get a free consultation and custom quote today.
          </p>
          
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <button className="flex items-center justify-center gap-2 px-8 py-4 bg-white text-orange-600 hover:bg-gray-100 rounded-lg font-bold text-lg transition-all">
              <Calendar className="w-5 h-5" />
              Schedule Consultation
            </button>
            <button className="flex items-center justify-center gap-2 px-8 py-4 border-2 border-white text-white hover:bg-white/10 rounded-lg font-bold text-lg transition-all">
              <Phone className="w-5 h-5" />
              Call: +254-700-000-000
            </button>
          </div>
        </div>
      </div>

      {/* Footer */}
      <div className="bg-slate-900 py-12">
        <div className="max-w-7xl mx-auto px-6">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div>
              <div className="flex items-center gap-3 mb-4">
                <div className="w-10 h-10 bg-gradient-to-r from-orange-500 to-yellow-500 rounded-lg flex items-center justify-center">
                  <Sun className="w-5 h-5 text-white" />
                </div>
                <span className="text-xl font-bold text-white">Allytic Labs</span>
              </div>
              <p className="text-gray-400 mb-4">
                Leading provider of solar panel solutions across Africa and beyond.
              </p>
              <div className="flex items-center gap-2 text-gray-400">
                <MapPin className="w-4 h-4" />
                <span>Nairobi, Kenya</span>
              </div>
            </div>
            
            <div>
              <h3 className="text-white font-bold mb-4">Quick Links</h3>
              <ul className="space-y-2 text-gray-400">
                <li><a href="#" className="hover:text-white transition-colors">Products</a></li>
                <li><a href="#" className="hover:text-white transition-colors">Installation</a></li>
                <li><a href="#" className="hover:text-white transition-colors">Maintenance</a></li>
                <li><a href="#" className="hover:text-white transition-colors">Support</a></li>
              </ul>
            </div>
            
            <div>
              <h3 className="text-white font-bold mb-4">Contact Info</h3>
              <div className="space-y-2 text-gray-400">
                <div className="flex items-center gap-2">
                  <Phone className="w-4 h-4" />
                  <span>+254-700-000-000</span>
                </div>
                <div className="flex items-center gap-2">
                  <Mail className="w-4 h-4" />
                  <span>solar@allytic-labs.com</span>
                </div>
              </div>
            </div>
          </div>
          
          <div className="border-t border-gray-700 mt-8 pt-8 text-center text-gray-400">
            <p>&copy; 2025 Allytic Labs. All rights reserved.</p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default SolarPanels;

      