import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { ArrowLeft, Zap } from 'lucide-react';
import BrandLogo from '../components/BrandLogo';
import robotVideo from '../assets/robot.mp4';
import robot1Video from '../assets/robot1.mp4';
import solarpanelsVideo from '../assets/solar panels.mp4';
import AdvancedRobotics from '../assets/advanced robotics.mp4';
import SolarEnergy from '../assets/solar energy.mp4';

const Demo = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [activeDemo, setActiveDemo] = useState('robot');

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const demo = params.get('type');
    if (demo && ['robot', 'drone', 'solar'].includes(demo)) {
      setActiveDemo(demo);
    }
  }, [location]);

  const demoContent = {
    robot: {
      title: "Advanced Robotics Demo",
      subtitle: "Experience precision automation with AI-powered intelligence",
      heroVideo: robotVideo,
      demoVideo: AdvancedRobotics,
    },
    drone: {
      title: "Professional Drone Demo",
      subtitle: "Aerial innovation with autonomous capabilities",
      heroVideo: robot1Video,
      demoVideo: robot1Video,
    },
    solar: {
      title: "Solar Energy Systems Demo",
      subtitle: "Sustainable power solutions for a cleaner future",
      heroVideo: solarpanelsVideo,
      demoVideo: SolarEnergy,
    }
  };

  const currentDemo = demoContent[activeDemo];

  return (
    <div className="min-h-screen bg-gray-900 text-white">
      {/* Hero Section with Video */}
      <section className="relative h-screen flex items-center justify-center overflow-hidden pt-16">
        <div className="absolute inset-0 w-full h-full">
          <video
            className="w-full h-full object-cover"
            autoPlay
            muted
            loop
            playsInline
          >
            <source src={currentDemo.heroVideo} type="video/mp4" />
          </video>
          <div className="absolute inset-0 bg-gradient-to-b from-gray-900/70 via-gray-900/50 to-gray-900"></div>
        </div>

        <div className="relative z-10 text-center max-w-6xl mx-auto px-4">
          <button
            onClick={() => navigate(-1)}
            className="inline-flex items-center mb-8 px-6 py-3 bg-white/10 backdrop-blur-md rounded-full border border-white/20 hover:bg-white/20 transition-all"
          >
            <ArrowLeft className="w-5 h-5 mr-2" />
            Back
          </button>

          <h1 className="text-6xl md:text-8xl font-bold mb-6 bg-gradient-to-r from-white via-blue-200 to-purple-200 bg-clip-text text-transparent leading-tight">
            {currentDemo.title}
          </h1>

          <p className="text-xl md:text-2xl text-gray-200 max-w-3xl mx-auto">
            {currentDemo.subtitle}
          </p>
        </div>
      </section>

      {/* Live Demonstration - Edge to Edge */}
      <section className="py-24 bg-gray-900">
        <div className="max-w-7xl mx-auto px-4 mb-12">
          <div className="text-center">
            <h2 className="text-4xl md:text-5xl font-bold mb-6">Live Demonstration</h2>
            <p className="text-xl text-gray-400">Watch our technology in action</p>
          </div>
        </div>

        <div className="w-full">
          <video
            className="w-full h-auto"
            autoPlay
            muted
            loop
            playsInline
            controls
          >
            <source src={currentDemo.demoVideo} type="video/mp4" />
          </video>
        </div>
      </section>

      {/* Footer */}
      <footer className="bg-gray-950 border-t border-gray-800 py-16">
        <div className="max-w-7xl mx-auto px-4">
          <div className="grid md:grid-cols-4 gap-8 mb-12">
            <div>
              <BrandLogo className="mb-4" />
              <p className="text-gray-400">Pioneering the future of robotics, drones, and renewable energy.</p>
            </div>
            <div>
              <h4 className="font-semibold mb-4">Solutions</h4>
              <ul className="space-y-2 text-gray-400">
                <li><button onClick={() => navigate('/robots')} className="hover:text-white transition-colors">Robotics</button></li>
                <li><button onClick={() => navigate('/drones')} className="hover:text-white transition-colors">Drones</button></li>
                <li><button onClick={() => navigate('/solarpanels')} className="hover:text-white transition-colors">Solar</button></li>
              </ul>
            </div>
            <div>
              <h4 className="font-semibold mb-4">Company</h4>
              <ul className="space-y-2 text-gray-400">
                <li><button onClick={() => navigate('/about')} className="hover:text-white transition-colors">About</button></li>
                <li><button onClick={() => navigate('/careers')} className="hover:text-white transition-colors">Careers</button></li>
                <li><button onClick={() => navigate('/news')} className="hover:text-white transition-colors">News</button></li>
              </ul>
            </div>
            <div>
              <h4 className="font-semibold mb-4">Connect</h4>
              <ul className="space-y-2 text-gray-400">
                <li><button onClick={() => navigate('/contact')} className="hover:text-white transition-colors">Contact</button></li>
                <li><button onClick={() => navigate('/support')} className="hover:text-white transition-colors">Support</button></li>
                <li><button onClick={() => navigate('/partners')} className="hover:text-white transition-colors">Partners</button></li>
              </ul>
            </div>
          </div>

          <div className="border-t border-gray-800 pt-8 text-center text-gray-400">
            <p>&copy; 2025 Bella Technologies. All rights reserved.</p>
          </div>
        </div>
      </footer>
    </div>
  );
};

export default Demo;
