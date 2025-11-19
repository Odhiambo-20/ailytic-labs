import React, { useState, useEffect } from 'react';
import { Sun, Zap, TrendingUp, Shield, Leaf, Globe, ArrowRight, Play, CheckCircle, Star, MapPin, Phone, Mail, Calendar, Users, Award, BarChart3 } from 'lucide-react';
import solarVideo from '../assets/solar.mp4';

const SolarPanels = () => {
  const [activeTab, setActiveTab] = useState('residential');
  const [currentSlide, setCurrentSlide] = useState(0);
  const [energyData, setEnergyData] = useState({ current: 0, target: 4200 });
  const [selectedProduct, setSelectedProduct] = useState(null);

  // Animate energy counter
  useEffect(() => {
    const interval = setInterval(() => {
      setEnergyData(prev => ({
        ...prev,
        current: prev.current < prev.target ? prev.current + 50 : prev.target
      }));
    }, 50);

    return () => clearInterval(interval);
  }, []);

  // Auto-slide for testimonials
  useEffect(() => {
    const interval = setInterval(() => {
      setCurrentSlide(prev => (prev + 1) % testimonials.length);
    }, 5000);
    return () => clearInterval(interval);
  }, []);

  const solarProducts = {
    residential: [
      {
        id: 1,
        name: 'SolarMax Home Pro',
        power: '400W',
        efficiency: '22.1%',
        warranty: '25 years',
        price: '$299',
        image: '🏠',
        features: ['Monocrystalline silicon', 'Weather resistant', 'Easy installation', 'Smart monitoring'],
        description: 'Perfect for residential rooftops with high efficiency and reliable performance.'
      },
      {
        id: 2,
        name: 'EcoSolar Residential',
        power: '350W',
        efficiency: '20.5%',
        warranty: '20 years',
        price: '$249',
        image: '🏡',
        features: ['Polycrystalline silicon', 'Cost-effective', 'Durable frame', 'Grid-tie compatible'],
        description: 'Budget-friendly option without compromising on quality and durability.'
      },
      {
        id: 3,
        name: 'FlexSolar Compact',
        power: '300W',
        efficiency: '19.8%',
        warranty: '20 years',
        price: '$199',
        image: '🏘️',
        features: ['Flexible design', 'Lightweight', 'Space-saving', 'Urban optimized'],
        description: 'Ideal for small spaces and urban environments with limited roof area.'
      }
    ],
    commercial: [
      {
        id: 4,
        name: 'PowerGrid Commercial',
        power: '500W',
        efficiency: '23.5%',
        warranty: '25 years',
        price: '$399',
        image: '🏢',
        features: ['High-power output', 'Commercial grade', 'Scalable systems', 'Remote monitoring'],
        description: 'Industrial-grade solar panels designed for large-scale commercial installations.'
      },
      {
        id: 5,
        name: 'MegaSolar Enterprise',
        power: '600W',
        efficiency: '24.2%',
        warranty: '30 years',
        price: '$499',
        image: '🏭',
        features: ['Maximum efficiency', 'Enterprise reliability', 'Weather tracking', 'Performance analytics'],
        description: 'Premium enterprise solution for maximum energy generation and ROI.'
      }
    ],
    industrial: [
      {
        id: 6,
        name: 'IndustrialMax Pro',
        power: '800W',
        efficiency: '25.1%',
        warranty: '30 years',
        price: '$699',
        image: '⚡',
        features: ['Ultra-high power', 'Extreme durability', 'Industrial IoT', 'Predictive maintenance'],
        description: 'Heavy-duty industrial panels for utility-scale solar farms and large installations.'
      }
    ]
  };

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

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-orange-900 to-slate-900">
      {/* Hero Section with Video Background - Added padding-top to account for fixed header */}
      <div className="relative overflow-hidden min-h-screen flex items-center pt-20">
        {/* Video Background */}
        <div className="absolute inset-0 z-0">
          <video
            width="1420"
            height="100"
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
            <source src={solarVideo} type="video/mp4" />
            Your browser does not support the video tag.
          </video>
          {/* Subtle dark overlay to ensure text readability */}
          <div className="absolute inset-0 bg-black/40"></div>
        </div>

        {/* Hero Content */}
        <div className="relative z-10 max-w-7xl mx-auto px-6 py-20">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-12 items-center">
            <div>
              <h1 className="text-5xl font-bold text-white mb-6">
                Advanced Solar Panel Technology
              </h1>
              <p className="text-xl text-gray-200 mb-8">
                Transforming energy consumption with intelligent solar solutions that deliver exceptional performance and sustainability.
              </p>
              
            </div>
          </div>
        </div>
      </div>

      {/* Product Categories */}
      <div className="max-w-7xl mx-auto px-6 py-20">
        <div className="text-center mb-16">
          <h2 className="text-4xl font-bold text-white mb-4">Our Solar Panel Solutions</h2>
          <p className="text-xl text-gray-300 max-w-3xl mx-auto">
            Choose from our comprehensive range of solar panels designed for different applications and energy needs.
          </p>
        </div>

        {/* Category Tabs */}
        <div className="flex justify-center mb-12">
          <div className="bg-black/20 backdrop-blur-sm rounded-2xl p-2 border border-white/10">
            {Object.keys(solarProducts).map((category) => (
              <button
                key={category}
                onClick={() => setActiveTab(category)}
                className={`px-6 py-3 rounded-xl font-medium transition-all capitalize ${
                  activeTab === category
                    ? 'bg-orange-600 text-white'
                    : 'text-gray-300 hover:text-white hover:bg-white/10'
                }`}
              >
                {category}
              </button>
            ))}
          </div>
        </div>

        {/* Product Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          {solarProducts[activeTab].map((product) => (
            <div
              key={product.id}
              className="bg-white/5 backdrop-blur-sm rounded-2xl p-6 border border-white/10 hover:border-orange-400/50 transition-all cursor-pointer group"
              onClick={() => setSelectedProduct(product)}
            >
              <div className="text-center mb-6">
                <div className="text-6xl mb-4">{product.image}</div>
                <h3 className="text-xl font-bold text-white mb-2">{product.name}</h3>
                <p className="text-gray-400 text-sm">{product.description}</p>
              </div>

              <div className="space-y-3 mb-6">
                <div className="flex justify-between items-center">
                  <span className="text-gray-300">Power Output</span>
                  <span className="text-orange-400 font-bold">{product.power}</span>
                </div>
                <div className="flex justify-between items-center">
                  <span className="text-gray-300">Efficiency</span>
                  <span className="text-green-400 font-bold">{product.efficiency}</span>
                </div>
                <div className="flex justify-between items-center">
                  <span className="text-gray-300">Warranty</span>
                  <span className="text-blue-400 font-bold">{product.warranty}</span>
                </div>
              </div>

              <div className="border-t border-white/10 pt-4">
                <div className="flex justify-between items-center mb-4">
                  <span className="text-2xl font-bold text-white">{product.price}</span>
                  <span className="text-gray-400 text-sm">per panel</span>
                </div>
                
                <div className="space-y-2 mb-4">
                  {product.features.slice(0, 2).map((feature, index) => (
                    <div key={index} className="flex items-center gap-2 text-sm text-gray-300">
                      <CheckCircle className="w-4 h-4 text-green-400" />
                      {feature}
                    </div>
                  ))}
                </div>

                <button className="w-full flex items-center justify-center gap-2 px-4 py-2 bg-orange-600 hover:bg-orange-700 rounded-lg text-white font-medium transition-colors group-hover:bg-orange-700">
                  Get Quote
                  <ArrowRight className="w-4 h-4" />
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Benefits Section */}
      <div className="bg-black/20 backdrop-blur-sm py-20">
        <div className="max-w-7xl mx-auto px-6">
          <div className="text-center mb-16">
            <h2 className="text-4xl font-bold text-white mb-4">Why Choose Allytic Solar?</h2>
            <p className="text-xl text-gray-300">
              Experience the advantages of our advanced solar technology and comprehensive service.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
            {benefits.map((benefit, index) => {
              const Icon = benefit.icon;
              return (
                <div key={index} className={`${benefit.color} border rounded-2xl p-6 hover:scale-105 transition-transform`}>
                  <Icon className="w-12 h-12 text-white mb-4" />
                  <h3 className="text-xl font-bold text-white mb-3">{benefit.title}</h3>
                  <p className="text-gray-300">{benefit.description}</p>
                </div>
              );
            })}
          </div>
        </div>
      </div>

      {/* Testimonials */}
      <div className="max-w-7xl mx-auto px-6 py-20">
        <div className="text-center mb-16">
          <h2 className="text-4xl font-bold text-white mb-4">What Our Customers Say</h2>
          <p className="text-xl text-gray-300">
            Real feedback from satisfied customers across Africa and beyond.
          </p>
        </div>

        <div className="relative">
          <div className="bg-white/5 backdrop-blur-sm rounded-2xl p-8 border border-white/10">
            <div className="text-center">
              <div className="flex justify-center mb-4">
                {[...Array(testimonials[currentSlide].rating)].map((_, i) => (
                  <Star key={i} className="w-6 h-6 text-yellow-400 fill-current" />
                ))}
              </div>
              
              <blockquote className="text-xl text-white mb-6 italic">
                "{testimonials[currentSlide].text}"
              </blockquote>
              
              <div className="flex items-center justify-center gap-4">
                <div className="w-12 h-12 bg-gradient-to-r from-orange-500 to-yellow-500 rounded-full flex items-center justify-center text-white font-bold">
                  {testimonials[currentSlide].name.charAt(0)}
                </div>
                <div className="text-left">
                  <div className="text-white font-medium">{testimonials[currentSlide].name}</div>
                  <div className="text-gray-400 text-sm">{testimonials[currentSlide].company}</div>
                  <div className="flex items-center gap-1 text-orange-400 text-sm">
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
                  currentSlide === index ? 'bg-orange-500' : 'bg-gray-600'
                }`}
              />
            ))}
          </div>
        </div>
      </div>

      {/* CTA Section */}
      <div className="bg-gradient-to-r from-orange-600/20 to-yellow-600/20 border-y border-orange-400/30 py-20">
        <div className="max-w-4xl mx-auto text-center px-6">
          <h2 className="text-4xl font-bold text-white mb-4">Ready to Go Solar?</h2>
          <p className="text-xl text-gray-300 mb-8">
            Join thousands of satisfied customers who have made the switch to clean, renewable energy.
            Get a free consultation and custom quote today.
          </p>
          
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <button className="flex items-center gap-2 px-8 py-4 bg-gradient-to-r from-orange-600 to-yellow-600 hover:from-orange-700 hover:to-yellow-700 rounded-lg text-white font-bold text-lg transition-all">
              <Calendar className="w-5 h-5" />
              Schedule Consultation
            </button>
            <button className="flex items-center gap-2 px-8 py-4 border border-orange-400/50 hover:bg-orange-500/20 rounded-lg text-white font-bold text-lg transition-all">
              <Phone className="w-5 h-5" />
              Call Now: +254-700-000-000
            </button>
          </div>
        </div>
      </div>

      {/* Footer */}
      <div className="bg-black/40 backdrop-blur-sm py-12">
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
          
          <div className="border-t border-white/10 mt-8 pt-8 text-center text-gray-400">
            <p>&copy; 2025 Allytic Labs. All rights reserved.</p>
          </div>
        </div>
      </div>

      {/* Product Detail Modal */}
      {selectedProduct && (
        <div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center p-4 z-50" onClick={() => setSelectedProduct(null)}>
          <div className="bg-slate-900 rounded-2xl p-8 max-w-2xl w-full border border-white/10" onClick={e => e.stopPropagation()}>
            <div className="flex justify-between items-start mb-6">
              <div>
                <h3 className="text-2xl font-bold text-white mb-2">{selectedProduct.name}</h3>
                <p className="text-gray-400">{selectedProduct.description}</p>
              </div>
              <button
                onClick={() => setSelectedProduct(null)}
                className="text-gray-400 hover:text-white transition-colors"
              >
                ✕
              </button>
            </div>

            <div className="grid grid-cols-2 gap-8 mb-6">
              <div>
                <h4 className="text-white font-semibold mb-4">Specifications</h4>
                <div className="space-y-3">
                  <div className="flex justify-between">
                    <span className="text-gray-400">Power Output</span>
                    <span className="text-orange-400 font-bold">{selectedProduct.power}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-gray-400">Efficiency</span>
                    <span className="text-green-400 font-bold">{selectedProduct.efficiency}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-gray-400">Warranty</span>
                    <span className="text-blue-400 font-bold">{selectedProduct.warranty}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-gray-400">Price</span>
                    <span className="text-white font-bold text-xl">{selectedProduct.price}</span>
                  </div>
                </div>
              </div>

              <div>
                <h4 className="text-white font-semibold mb-4">Features</h4>
                <div className="space-y-2">
                  {selectedProduct.features.map((feature, index) => (
                    <div key={index} className="flex items-center gap-2 text-gray-300">
                      <CheckCircle className="w-4 h-4 text-green-400" />
                      {feature}
                    </div>
                  ))}
                </div>
              </div>
            </div>

            <div className="flex gap-4">
              <button className="flex-1 px-6 py-3 bg-orange-600 hover:bg-orange-700 rounded-lg text-white font-medium transition-colors">
                Request Quote
              </button>
              <button className="flex-1 px-6 py-3 border border-orange-400/50 hover:bg-orange-500/20 rounded-lg text-white font-medium transition-colors">
                Schedule Demo
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default SolarPanels;