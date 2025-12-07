import React, { useState, useEffect } from 'react';
import { ArrowLeft, ChevronRight, Calendar } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import DJIAIR3S from '../assets/DJI Air 3S.avif';
import DJIAVATA2FLYMORECOMBO from '../assets/dji avata 2 fly more combo.jpg';
import DJIMAVIC4PRODRONECOMBO from '../assets/DJI Mavic 4 Pro Drone Combo.png';
import Mavic2 from '../assets/mavic 2.jpg';

const LatestModels = () => {
  const navigate = useNavigate();
  const [drones, setDrones] = useState([]);
  const [loading, setLoading] = useState(true);

  const allDrones = [
    {
      id: 'drone-1',
      name: 'DJI Air 3S',
      tagline: 'AI-Powered Reconnaissance',
      description: 'Next-gen drone with advanced threat detection and 500km range',
      image: DJIAIR3S,
      timeline: 'Q2 2025',
      gradient: 'from-blue-600 to-cyan-600'
    },
    {
      id: 'drone-2',
      name: 'DJI Avata 2 Fly More Combo',
      tagline: 'Heavy-Lift Cargo Master',
      description: 'Autonomous logistics drone with 500kg payload capacity',
      image: DJIAVATA2FLYMORECOMBO,
      timeline: 'Q3 2025',
      gradient: 'from-green-600 to-emerald-600'
    },
    {
      id: 'drone-3',
      name: 'DJI Mavic 4 Pro Drone Combo',
      tagline: 'Precision Agriculture',
      description: 'Hyperspectral imaging for sustainable farming solutions',
      image: DJIMAVIC4PRODRONECOMBO,
      timeline: 'Q2 2025',
      gradient: 'from-purple-600 to-pink-600'
    },
    {
      id: 'drone-4',
      name: 'Mavic 2',
      tagline: 'Professional Cinema',
      description: '8K HDR recording with advanced gimbal stabilization',
      image: Mavic2,
      timeline: 'January 2025',
      gradient: 'from-orange-600 to-red-600'
    }
  ];

  useEffect(() => {
    setLoading(true);
    setTimeout(() => {
      setDrones(allDrones);
      setLoading(false);
    }, 500);
  }, []);

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
            Discover cutting-edge drone technology designed to transform industries and push the boundaries of innovation
          </p>
        </div>

        {loading ? (
          <div className="text-center py-20">
            <div className="inline-block animate-spin rounded-full h-16 w-16 border-t-4 border-b-4 border-blue-500 mb-4"></div>
            <p className="text-xl text-blue-100">Loading latest drone models...</p>
          </div>
        ) : null}
      </div>

      {!loading && (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 px-4 mb-16">
          {drones.map((drone) => (
            <div
              key={drone.id}
              className="group relative overflow-hidden rounded-3xl border border-white/10 hover:border-white/30 transition-all duration-300 hover:shadow-2xl h-[500px]"
            >
              <img
                src={drone.image}
                alt={drone.name}
                className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-700"
                onError={(e) => {
                  e.currentTarget.src = 'https://images.pexels.com/photos/442587/pexels-photo-442587.jpeg?auto=compress&cs=tinysrgb&w=600';
                }}
              />
              
              <div className="absolute inset-0 bg-gradient-to-t from-black/90 via-black/50 to-transparent"></div>

              <div className="absolute top-4 right-4 bg-white/10 backdrop-blur-md px-4 py-2 rounded-full border border-white/20">
                <div className="flex items-center gap-2 text-white text-sm font-semibold">
                  <Calendar className="w-4 h-4" />
                  {drone.timeline}
                </div>
              </div>

              <div className="absolute bottom-0 left-0 right-0 p-8">
                <div className={`inline-block px-4 py-2 bg-gradient-to-r ${drone.gradient} rounded-full mb-4`}>
                  <span className="text-white text-sm font-bold">{drone.tagline}</span>
                </div>
                
                <h3 className="text-4xl font-bold text-white mb-3">{drone.name}</h3>
                <p className="text-blue-100 text-lg mb-6">{drone.description}</p>

                <button className={`px-8 py-3 bg-gradient-to-r ${drone.gradient} text-white font-bold rounded-xl hover:shadow-2xl transform hover:scale-105 transition-all duration-300 flex items-center gap-2`}>
                  Learn More
                  <ChevronRight className="w-5 h-5" />
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      <section className="py-20 bg-gradient-to-r from-blue-900/30 to-purple-900/30 border-t border-white/10">
        <div className="max-w-4xl mx-auto px-4 text-center">
          <h2 className="text-4xl md:text-5xl font-bold text-white mb-6">Stay Ahead of Innovation</h2>
          <p className="text-xl text-blue-100 mb-10">
            Get exclusive updates on new drone launches, early access opportunities, and cutting-edge technology insights
          </p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center max-w-lg mx-auto">
            <input
              type="email"
              placeholder="Enter your email"
              className="flex-1 px-6 py-4 bg-white/10 border border-white/20 rounded-full text-white placeholder-blue-300/60 focus:outline-none focus:border-blue-400 focus:bg-white/15 transition-all backdrop-blur-sm"
            />
            <button className="px-10 py-4 bg-gradient-to-r from-blue-600 to-purple-600 hover:from-blue-700 hover:to-purple-700 text-white font-bold rounded-full shadow-lg hover:shadow-xl transition-all duration-300 transform hover:scale-105">
              Subscribe
            </button>
          </div>
        </div>
      </section>
    </div>
  );
};

export default LatestModels;