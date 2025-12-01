import React, { useState, useEffect } from 'react';
import { ArrowLeft, Zap, Clock, Lightbulb, ChevronRight, Calendar, Cpu, Wifi, Battery } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

const LatestModels = () => {
  const navigate = useNavigate();
  const [selectedCategory, setSelectedCategory] = useState('all');
  const [drones, setDrones] = useState([]);
  const [loading, setLoading] = useState(true);

  const allDrones = [
    {
      id: 'dev-1',
      name: 'UltraVision X2',
      category: 'development',
      description: 'Next-generation reconnaissance drone with AI-powered threat detection and real-time data processing',
      image: 'https://images.pexels.com/photos/442587/pexels-photo-442587.jpeg?auto=compress&cs=tinysrgb&w=600',
      specs: ['100+ hour flight time', '8K + Thermal imaging', '500km range', 'AI threat detection'],
      timeline: 'Q2 2025',
      innovations: ['Advanced AI Detection System', 'Ultra-long endurance battery', 'Quantum encryption'],
      targetApplications: ['Security', 'Surveillance', 'Border monitoring']
    },
    {
      id: 'dev-2',
      name: 'CargoMax Titan',
      category: 'development',
      description: 'Heavy-lift autonomous cargo drone designed for large-scale industrial logistics and emergency response',
      image: 'https://images.pexels.com/photos/8566473/pexels-photo-8566473.jpeg?auto=compress&cs=tinysrgb&w=600',
      specs: ['500kg payload capacity', 'Autonomous landing system', 'Weather resistant', 'Real-time tracking'],
      timeline: 'Q3 2025',
      innovations: ['Advanced stabilization', 'Autonomous dock charging', 'Swarm capability'],
      targetApplications: ['Logistics', 'Emergency Response', 'Industrial Transport']
    },
    {
      id: 'dev-3',
      name: 'AgriPrecision Pro',
      category: 'development',
      description: 'Advanced agricultural drone with hyper-spectral imaging and precision pesticide application',
      image: 'https://images.pexels.com/photos/1034650/pexels-photo-1034650.jpeg?auto=compress&cs=tinysrgb&w=600',
      specs: ['Hyperspectral imaging', 'Precision spray system', 'AI crop analysis', '1000 hectare/day capacity'],
      timeline: 'Q2 2025',
      innovations: ['Hyperspectral sensors', 'Precision targeting', 'ML crop health analysis'],
      targetApplications: ['Precision farming', 'Sustainable agriculture', 'Crop monitoring']
    },
    {
      id: 'launch-1',
      name: 'SkyFlow HD Pro',
      category: 'launch',
      description: 'Professional cinema drone with 8K HDR recording and advanced gimbal stabilization',
      image: 'https://images.pexels.com/photos/3945683/pexels-photo-3945683.jpeg?auto=compress&cs=tinysrgb&w=600',
      specs: ['8K HDR video', '3-axis gimbal', 'RAW format recording', '40min flight time'],
      timeline: 'January 2025',
      innovations: ['Advanced gimbal AI', 'Wireless video transmission', 'Cloud integration'],
      targetApplications: ['Cinema', 'Broadcasting', 'Commercial production']
    },
    {
      id: 'launch-2',
      name: 'MediDrone Response',
      category: 'launch',
      description: 'Emergency response drone for medical supply delivery and disaster management operations',
      image: 'https://images.pexels.com/photos/1730877/pexels-photo-1730877.jpeg?auto=compress&cs=tinysrgb&w=600',
      specs: ['50km autonomous range', 'Temperature controlled payload', 'GPS + visual navigation', '2hr flight time'],
      timeline: 'February 2025',
      innovations: ['Insulated cargo', 'Autonomous routing', 'Emergency beacon integration'],
      targetApplications: ['Emergency response', 'Medical delivery', 'Disaster management']
    },
    {
      id: 'launch-3',
      name: 'MapperLite RTK',
      category: 'launch',
      description: 'Lightweight surveying drone with RTK positioning for precise mapping and 3D reconstruction',
      image: 'https://images.pexels.com/photos/2246476/pexels-photo-2246476.jpeg?auto=compress&cs=tinysrgb&w=600',
      specs: ['2cm RTK accuracy', 'Lightweight design', 'Cloud processing', 'Real-time orthomosaics'],
      timeline: 'December 2024',
      innovations: ['Compact RTK antenna', 'Cloud photogrammetry', 'Real-time processing'],
      targetApplications: ['Land surveying', 'Construction', '3D mapping']
    },
    {
      id: 'concept-1',
      name: 'NeuralFly Swarm',
      category: 'concept',
      description: 'Revolutionary swarm intelligence drone system with decentralized AI coordination and self-healing formations',
      image: 'https://images.pexels.com/photos/2529148/pexels-photo-2529148.jpeg?auto=compress&cs=tinysrgb&w=600',
      specs: ['100+ drone coordination', 'Decentralized AI', 'Self-healing formations', 'Quantum communication'],
      innovations: ['Quantum entanglement comm', 'Swarm consciousness AI', 'Self-repairing mesh network'],
      targetApplications: ['Military operations', 'Disaster mapping', 'Large-scale surveillance']
    },
    {
      id: 'concept-2',
      name: 'HyperSonic Scout',
      category: 'concept',
      description: 'Ultra-high-speed reconnaissance drone capable of breaking sound barriers with advanced aerodynamics',
      image: 'https://images.pexels.com/photos/3587620/pexels-photo-3587620.jpeg?auto=compress&cs=tinysrgb&w=600',
      specs: ['Mach 2+ speed capability', 'Advanced stealth coating', 'Plasma propulsion', '10,000km range'],
      innovations: ['Hypersonic aerodynamics', 'Plasma jet engines', 'Stealth technology'],
      targetApplications: ['High-speed reconnaissance', 'Research missions', 'Extreme conditions']
    },
    {
      id: 'concept-3',
      name: 'OceanCrawler Deep',
      category: 'concept',
      description: 'Amphibious drone designed for underwater exploration and marine research with autonomous navigation',
      image: 'https://images.pexels.com/photos/3587630/pexels-photo-3587630.jpeg?auto=compress&cs=tinysrgb&w=600',
      specs: ['10,000m depth capability', 'Sonar mapping', 'Underwater propulsion', 'Autonomous navigation'],
      innovations: ['Deep-sea materials', 'Advanced sonar', 'Underwater AI navigation'],
      targetApplications: ['Marine research', 'Deep sea exploration', 'Underwater mining']
    }
  ];

  useEffect(() => {
    setLoading(true);
    setTimeout(() => {
      setDrones(allDrones);
      setLoading(false);
    }, 500);
  }, []);

  const filteredDrones = selectedCategory === 'all'
    ? drones
    : drones.filter(drone => drone.category === selectedCategory);

  const getCategoryColor = (category) => {
    switch (category) {
      case 'development':
        return { bg: 'from-blue-600 to-cyan-600', text: 'bg-blue-500/20 text-blue-200', icon: Zap };
      case 'launch':
        return { bg: 'from-green-600 to-emerald-600', text: 'bg-green-500/20 text-green-200', icon: Clock };
      case 'concept':
        return { bg: 'from-purple-600 to-pink-600', text: 'bg-purple-500/20 text-purple-200', icon: Lightbulb };
      default:
        return { bg: 'from-gray-600 to-gray-700', text: 'bg-gray-500/20 text-gray-200', icon: Zap };
    }
  };

  const getCategoryLabel = (category) => {
    switch (category) {
      case 'development':
        return 'Under Development';
      case 'launch':
        return 'Expected to Launch';
      case 'concept':
        return 'Being Conceptualized';
      default:
        return category;
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-blue-900 to-slate-900">
      <div className="max-w-7xl mx-auto px-4 py-12">
        <button
          onClick={() => navigate(-1)}
          className="flex items-center gap-2 text-blue-300 hover:text-white mb-8 transition-colors"
        >
          <ArrowLeft className="w-5 h-5" />
          Back
        </button>

        <div className="text-center mb-16">
          <h1 className="text-5xl md:text-6xl font-bold text-white mb-6 bg-gradient-to-r from-white to-blue-200 bg-clip-text text-transparent">
            Latest Drone Models
          </h1>
          <p className="text-xl text-blue-100 max-w-3xl mx-auto mb-8">
            Explore the cutting-edge drone technology currently being developed, prepared for launch, and conceptualized by Allytic Labs
          </p>

          <div className="flex flex-wrap justify-center gap-4 mb-12">
            <button
              onClick={() => setSelectedCategory('all')}
              className={`px-6 py-3 rounded-full font-semibold transition-all duration-300 ${
                selectedCategory === 'all'
                  ? 'bg-gradient-to-r from-blue-600 to-purple-600 text-white shadow-lg'
                  : 'bg-white/10 text-blue-200 hover:bg-white/20'
              }`}
            >
              All Models
            </button>
            <button
              onClick={() => setSelectedCategory('development')}
              className={`px-6 py-3 rounded-full font-semibold transition-all duration-300 ${
                selectedCategory === 'development'
                  ? 'bg-gradient-to-r from-blue-600 to-cyan-600 text-white shadow-lg'
                  : 'bg-white/10 text-blue-200 hover:bg-white/20'
              }`}
            >
              Under Development
            </button>
            <button
              onClick={() => setSelectedCategory('launch')}
              className={`px-6 py-3 rounded-full font-semibold transition-all duration-300 ${
                selectedCategory === 'launch'
                  ? 'bg-gradient-to-r from-green-600 to-emerald-600 text-white shadow-lg'
                  : 'bg-white/10 text-blue-200 hover:bg-white/20'
              }`}
            >
              Expected Launches
            </button>
            <button
              onClick={() => setSelectedCategory('concept')}
              className={`px-6 py-3 rounded-full font-semibold transition-all duration-300 ${
                selectedCategory === 'concept'
                  ? 'bg-gradient-to-r from-purple-600 to-pink-600 text-white shadow-lg'
                  : 'bg-white/10 text-blue-200 hover:bg-white/20'
              }`}
            >
              Conceptualized
            </button>
          </div>
        </div>

        {loading ? (
          <div className="text-center py-20">
            <div className="inline-block animate-spin rounded-full h-16 w-16 border-t-4 border-b-4 border-blue-500 mb-4"></div>
            <p className="text-xl text-blue-100">Loading latest drone models...</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
            {filteredDrones.map((drone) => {
              const colors = getCategoryColor(drone.category);
              const IconComponent = colors.icon;

              return (
                <div
                  key={drone.id}
                  className="group relative overflow-hidden rounded-3xl border border-white/10 hover:border-white/30 transition-all duration-300 bg-white/5 backdrop-blur-lg hover:shadow-2xl"
                >
                  <div className="relative h-96 overflow-hidden">
                    <img
                      src={drone.image}
                      alt={drone.name}
                      className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-700"
                      onError={(e) => {
                        e.currentTarget.src = 'https://images.pexels.com/photos/442587/pexels-photo-442587.jpeg?auto=compress&cs=tinysrgb&w=600';
                      }}
                    />
                    <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-black/40 to-transparent"></div>

                    <div className="absolute top-4 left-4">
                      <div className={`${colors.text} px-4 py-2 rounded-full flex items-center gap-2 backdrop-blur-md`}>
                        <IconComponent className="w-4 h-4" />
                        <span className="text-sm font-semibold">{getCategoryLabel(drone.category)}</span>
                      </div>
                    </div>

                    {drone.timeline && (
                      <div className="absolute top-4 right-4 bg-white/10 backdrop-blur-md px-4 py-2 rounded-full border border-white/20">
                        <div className="flex items-center gap-2 text-white text-sm font-semibold">
                          <Calendar className="w-4 h-4" />
                          {drone.timeline}
                        </div>
                      </div>
                    )}
                  </div>

                  <div className="p-8 space-y-6">
                    <div>
                      <h3 className="text-3xl font-bold text-white mb-2">{drone.name}</h3>
                      <p className="text-blue-100 text-lg leading-relaxed">{drone.description}</p>
                    </div>

                    <div>
                      <h4 className="text-sm font-bold text-blue-300 uppercase tracking-wide mb-3 flex items-center gap-2">
                        <Cpu className="w-4 h-4" />
                        Key Specifications
                      </h4>
                      <div className="space-y-2">
                        {drone.specs.map((spec, idx) => (
                          <div key={idx} className="flex items-center gap-3">
                            <div className="w-2 h-2 bg-gradient-to-r from-blue-400 to-cyan-400 rounded-full"></div>
                            <span className="text-blue-200">{spec}</span>
                          </div>
                        ))}
                      </div>
                    </div>

                    <div>
                      <h4 className="text-sm font-bold text-purple-300 uppercase tracking-wide mb-3 flex items-center gap-2">
                        <Zap className="w-4 h-4" />
                        Innovation Highlights
                      </h4>
                      <div className="flex flex-wrap gap-2">
                        {drone.innovations.map((innovation, idx) => (
                          <span
                            key={idx}
                            className="px-3 py-1 bg-gradient-to-r from-purple-600/30 to-pink-600/30 border border-purple-500/50 rounded-full text-xs font-semibold text-purple-200"
                          >
                            {innovation}
                          </span>
                        ))}
                      </div>
                    </div>

                    <div>
                      <h4 className="text-sm font-bold text-green-300 uppercase tracking-wide mb-3 flex items-center gap-2">
                        <Wifi className="w-4 h-4" />
                        Target Applications
                      </h4>
                      <div className="flex flex-wrap gap-2">
                        {drone.targetApplications.map((app, idx) => (
                          <span
                            key={idx}
                            className="px-3 py-1 bg-gradient-to-r from-green-600/30 to-emerald-600/30 border border-green-500/50 rounded-full text-xs font-semibold text-green-200"
                          >
                            {app}
                          </span>
                        ))}
                      </div>
                    </div>

                    <button className={`w-full py-3 bg-gradient-to-r ${colors.bg} text-white font-bold rounded-xl hover:shadow-2xl transform hover:scale-105 transition-all duration-300 flex items-center justify-center gap-2`}>
                      Learn More
                      <ChevronRight className="w-5 h-5" />
                    </button>
                  </div>
                </div>
              );
            })}
          </div>
        )}

        {!loading && filteredDrones.length === 0 && (
          <div className="text-center py-20">
            <div className="max-w-md mx-auto bg-white/10 border border-white/20 rounded-2xl p-8">
              <p className="text-blue-100 text-lg">No models found in this category.</p>
              <p className="text-blue-300 text-sm mt-2">Try selecting a different filter.</p>
            </div>
          </div>
        )}
      </div>

      <section className="py-20 bg-gradient-to-r from-blue-900/20 to-purple-900/20 border-t border-white/10 mt-16">
        <div className="max-w-7xl mx-auto px-4 text-center">
          <h2 className="text-4xl font-bold text-white mb-6">Stay Updated</h2>
          <p className="text-xl text-blue-100 mb-8 max-w-2xl mx-auto">
            Subscribe to receive updates on the latest drone models, launch dates, and exclusive early access opportunities
          </p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center max-w-md mx-auto">
            <input
              type="email"
              placeholder="Enter your email"
              className="flex-1 px-6 py-4 bg-white/10 border border-white/20 rounded-full text-white placeholder-blue-300/50 focus:outline-none focus:border-blue-400 transition-all"
            />
            <button className="px-8 py-4 bg-gradient-to-r from-blue-600 to-purple-600 text-white font-bold rounded-full hover:shadow-lg transition-all duration-300">
              Notify Me
            </button>
          </div>
        </div>
      </section>
    </div>
  );
};

export default LatestModels;