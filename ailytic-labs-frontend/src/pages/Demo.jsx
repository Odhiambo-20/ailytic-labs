import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { ArrowLeft, Zap, Menu, X, Cpu, Battery, Wifi, Camera, Shield, Wrench } from 'lucide-react';
import robotVideo from '../assets/robot.mp4';
import robot1Video from '../assets/robot1.mp4';
import solarpanelsVideo from '../assets/solar panels.mp4';
import AdvancedRobotics from '../assets/advanced robotics.mp4';
import ProfessionalDrone from '../assets/professional drone.jpg';
import SolarEnergy from '../assets/solar energy.mp4';

const Demo = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [isMenuOpen, setIsMenuOpen] = useState(false);
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
      images: [
        { url: ProfessionalDrone, caption: "Full Robot Assembly" },
        { url: ProfessionalDrone, caption: "Control Interface" },
        { url: ProfessionalDrone, caption: "Sensor Array System" },
        { url: ProfessionalDrone, caption: "Actuator Mechanism" }
      ],
      structure: {
        title: "Robot Architecture & Components",
        description: "Our advanced robotics platform is built with modular components that work seamlessly together to deliver unmatched performance and reliability.",
        components: [
          {
            icon: Cpu,
            name: "AI Processing Core",
            description: "Powered by advanced neural processors capable of 50 trillion operations per second, enabling real-time decision making and adaptive learning.",
            specs: ["Neural Engine", "Edge AI", "Real-time Processing"]
          },
          {
            icon: Battery,
            name: "Power Management",
            description: "High-density lithium polymer battery system with intelligent power distribution, providing 12+ hours of continuous operation.",
            specs: ["Smart Charging", "Power Optimization", "99.9% Uptime"]
          },
          {
            icon: Wifi,
            name: "Connectivity Hub",
            description: "Multi-protocol communication system supporting 5G, WiFi 6, Bluetooth 5.0, and proprietary mesh networking for seamless integration.",
            specs: ["5G Compatible", "Mesh Network", "Secure Protocol"]
          },
          {
            icon: Shield,
            name: "Safety Systems",
            description: "Redundant safety mechanisms including collision avoidance, emergency stop protocols, and fail-safe operational modes.",
            specs: ["Collision Detection", "Emergency Stop", "Fail-Safe Mode"]
          }
        ]
      },
      buildingProcess: {
        title: "How We Build Our Robots",
        description: "Every robot undergoes a rigorous 12-stage manufacturing and testing process to ensure exceptional quality and performance.",
        stages: [
          {
            step: "01",
            title: "Design & Engineering",
            description: "CAD modeling and simulation testing with advanced finite element analysis to optimize every component for strength, efficiency, and durability."
          },
          {
            step: "02",
            title: "Precision Manufacturing",
            description: "CNC machining and 3D printing of aerospace-grade aluminum and carbon fiber components with tolerances down to 0.01mm."
          },
          {
            step: "03",
            title: "Electronics Integration",
            description: "Custom PCB assembly with premium components, followed by automated optical inspection and electrical testing of every circuit."
          },
          {
            step: "04",
            title: "AI Training & Calibration",
            description: "Machine learning model training with thousands of operational scenarios, followed by sensor calibration and neural network optimization."
          },
          {
            step: "05",
            title: "Quality Assurance",
            description: "Comprehensive stress testing including 1000+ hour burn-in tests, environmental chamber testing, and real-world simulation validation."
          },
          {
            step: "06",
            title: "Final Deployment",
            description: "Individual performance tuning, firmware updates, and certification before delivery with lifetime technical support."
          }
        ]
      }
    },
    drone: {
      title: "Professional Drone Demo",
      subtitle: "Aerial innovation with autonomous capabilities",
      heroVideo: robot1Video,
      demoVideo: robot1Video,
      images: [
        { url: ProfessionalDrone, caption: "Drone Flight System" },
        { url: ProfessionalDrone, caption: "8K Camera Module" },
        { url: ProfessionalDrone, caption: "Navigation System" },
        { url: ProfessionalDrone, caption: "Battery Configuration" }
      ],
      structure: {
        title: "Drone Architecture & Components",
        description: "Our professional-grade drones combine cutting-edge aerodynamics with intelligent flight systems for unparalleled aerial performance.",
        components: [
          {
            icon: Camera,
            name: "8K Camera System",
            description: "Professional-grade 8K camera with 3-axis gimbal stabilization, 20MP sensor, and advanced HDR processing for crystal-clear aerial imagery.",
            specs: ["8K Video", "20MP Photos", "3-Axis Gimbal"]
          },
          {
            icon: Cpu,
            name: "Flight Controller",
            description: "Military-grade flight computer with AI-powered obstacle avoidance, autonomous path planning, and precision GPS navigation.",
            specs: ["AI Navigation", "Obstacle Avoidance", "Auto-Pilot"]
          },
          {
            icon: Battery,
            name: "Extended Battery",
            description: "High-capacity battery system delivering 120 minutes of flight time with hot-swappable modules for continuous operation.",
            specs: ["120min Flight", "Hot-Swap", "Fast Charge"]
          },
          {
            icon: Wifi,
            name: "Communication Link",
            description: "Long-range transmission system with 10km range, encrypted video streaming, and redundant control channels for reliable operation.",
            specs: ["10km Range", "Encrypted", "HD Streaming"]
          }
        ]
      },
      buildingProcess: {
        title: "How We Build Our Drones",
        description: "Each drone is meticulously crafted through an advanced manufacturing process combining precision engineering with aerospace standards.",
        stages: [
          {
            step: "01",
            title: "Aerodynamic Design",
            description: "Computational fluid dynamics simulation and wind tunnel testing to optimize propeller design, frame geometry, and airflow efficiency."
          },
          {
            step: "02",
            title: "Carbon Fiber Fabrication",
            description: "Aerospace-grade carbon fiber layup with autoclave curing for maximum strength-to-weight ratio and vibration dampening."
          },
          {
            step: "03",
            title: "Motor & ESC Assembly",
            description: "Precision-balanced brushless motors paired with custom electronic speed controllers, tested for thermal performance and efficiency."
          },
          {
            step: "04",
            title: "Sensor Integration",
            description: "Installation of GPS modules, IMU sensors, ultrasonic arrays, and visual positioning systems with multi-point calibration."
          },
          {
            step: "05",
            title: "Flight Testing",
            description: "Extensive flight envelope testing including stability checks, emergency procedures, autonomous mode validation, and endurance runs."
          },
          {
            step: "06",
            title: "Camera Calibration",
            description: "Professional color grading, lens calibration, gimbal tuning, and image processing optimization for broadcast-quality footage."
          }
        ]
      }
    },
    solar: {
      title: "Solar Energy Systems Demo",
      subtitle: "Sustainable power solutions for a cleaner future",
      heroVideo: solarpanelsVideo,
      demoVideo: SolarEnergy,
      images: [
        { url: ProfessionalDrone, caption: "Solar Panel Array" },
        { url: ProfessionalDrone, caption: "Photovoltaic Cells" },
        { url: ProfessionalDrone, caption: "Inverter System" },
        { url: ProfessionalDrone, caption: "Monitoring Dashboard" }
      ],
      structure: {
        title: "Solar System Architecture & Components",
        description: "Our solar energy systems are engineered for maximum efficiency, featuring premium components that deliver reliable clean energy for decades.",
        components: [
          {
            icon: Zap,
            name: "High-Efficiency Panels",
            description: "Monocrystalline silicon cells with 22% efficiency rating, anti-reflective coating, and 25-year performance warranty.",
            specs: ["22% Efficiency", "25-Year Warranty", "Weather Resistant"]
          },
          {
            icon: Battery,
            name: "Energy Storage",
            description: "Lithium-ion battery banks with 95% round-trip efficiency, intelligent charge management, and 10-year cycle life.",
            specs: ["10kWh Capacity", "95% Efficiency", "Smart Management"]
          },
          {
            icon: Cpu,
            name: "Smart Inverter",
            description: "Grid-tied inverter with MPPT technology, real-time monitoring, and seamless utility integration for optimal energy conversion.",
            specs: ["MPPT Technology", "Grid Integration", "99% Conversion"]
          },
          {
            icon: Wifi,
            name: "Monitoring System",
            description: "Cloud-based monitoring platform with mobile app, real-time analytics, predictive maintenance alerts, and energy optimization.",
            specs: ["Real-Time Data", "Mobile App", "AI Optimization"]
          }
        ]
      },
      buildingProcess: {
        title: "How We Build Our Solar Systems",
        description: "From raw silicon to complete installation, our solar systems undergo rigorous quality control at every manufacturing stage.",
        stages: [
          {
            step: "01",
            title: "Silicon Purification",
            description: "Ultra-pure silicon ingot creation through the Czochralski process, achieving 99.9999% purity for maximum photovoltaic efficiency."
          },
          {
            step: "02",
            title: "Cell Manufacturing",
            description: "Precision wafer slicing, phosphorus doping, and anti-reflective coating application in ISO-certified clean room facilities."
          },
          {
            step: "03",
            title: "Panel Assembly",
            description: "Automated cell stringing, EVA encapsulation, and tempered glass lamination with laser-guided precision alignment."
          },
          {
            step: "04",
            title: "Electrical Testing",
            description: "Flash testing under standard test conditions, IV curve analysis, and power output verification to exceed rated specifications."
          },
          {
            step: "05",
            title: "Weatherproofing",
            description: "Junction box sealing, frame anodization, and accelerated aging tests simulating 25 years of environmental exposure."
          },
          {
            step: "06",
            title: "System Integration",
            description: "On-site installation with structural engineering, electrical code compliance, utility interconnection, and performance validation."
          }
        ]
      }
    }
  };

  const currentDemo = demoContent[activeDemo];

  return (
    <div className="min-h-screen bg-gray-900 text-white">
      <nav className="fixed top-0 w-full z-50 bg-gray-900/95 backdrop-blur-sm border-b border-gray-800">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <div className="flex items-center space-x-2 cursor-pointer" onClick={() => navigate('/')}>
              <div className="w-10 h-10 bg-gradient-to-r from-blue-500 to-purple-600 rounded-lg flex items-center justify-center">
                <Zap className="w-6 h-6 text-white" />
              </div>
              <span className="text-xl font-bold bg-gradient-to-r from-blue-400 to-purple-400 bg-clip-text text-transparent">
                Allytic Labs
              </span>
            </div>

            <div className="hidden md:flex items-center space-x-8">
              <button onClick={() => navigate('/')} className="text-gray-300 hover:text-white transition-colors">Home</button>
              <button onClick={() => navigate('/robots')} className="text-gray-300 hover:text-white transition-colors">Robots</button>
              <button onClick={() => navigate('/drones')} className="text-gray-300 hover:text-white transition-colors">Drones</button>
              <button onClick={() => navigate('/solarpanels')} className="text-gray-300 hover:text-white transition-colors">Solar</button>
              <button onClick={() => navigate('/contact')} className="bg-gradient-to-r from-blue-500 to-purple-600 px-4 py-2 rounded-lg hover:shadow-lg transition-all">
                Contact Us
              </button>
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
              <button onClick={() => { navigate('/'); setIsMenuOpen(false); }} className="block w-full text-left py-2 text-gray-300 hover:text-white">Home</button>
              <button onClick={() => { navigate('/robots'); setIsMenuOpen(false); }} className="block w-full text-left py-2 text-gray-300 hover:text-white">Robots</button>
              <button onClick={() => { navigate('/drones'); setIsMenuOpen(false); }} className="block w-full text-left py-2 text-gray-300 hover:text-white">Drones</button>
              <button onClick={() => { navigate('/solarpanels'); setIsMenuOpen(false); }} className="block w-full text-left py-2 text-gray-300 hover:text-white">Solar</button>
              <button onClick={() => { navigate('/contact'); setIsMenuOpen(false); }} className="block w-full text-left py-2 text-blue-400 hover:text-blue-300">Contact Us</button>
            </div>
          </div>
        )}
      </nav>

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

          <p className="text-xl md:text-2xl text-gray-200 mb-12 max-w-3xl mx-auto">
            {currentDemo.subtitle}
          </p>

          <div className="flex justify-center space-x-4">
            <button
              onClick={() => setActiveDemo('robot')}
              className={`px-6 py-3 rounded-lg font-semibold transition-all ${
                activeDemo === 'robot'
                  ? 'bg-gradient-to-r from-blue-600 to-purple-700'
                  : 'bg-white/10 backdrop-blur-md border border-white/20 hover:bg-white/20'
              }`}
            >
              Robot
            </button>
            <button
              onClick={() => setActiveDemo('drone')}
              className={`px-6 py-3 rounded-lg font-semibold transition-all ${
                activeDemo === 'drone'
                  ? 'bg-gradient-to-r from-emerald-500 to-teal-600'
                  : 'bg-white/10 backdrop-blur-md border border-white/20 hover:bg-white/20'
              }`}
            >
              Drone
            </button>
            <button
              onClick={() => setActiveDemo('solar')}
              className={`px-6 py-3 rounded-lg font-semibold transition-all ${
                activeDemo === 'solar'
                  ? 'bg-gradient-to-r from-orange-500 to-red-600'
                  : 'bg-white/10 backdrop-blur-md border border-white/20 hover:bg-white/20'
              }`}
            >
              Solar
            </button>
          </div>
        </div>
      </section>

      <section className="py-24 bg-gray-900">
        <div className="max-w-7xl mx-auto px-4">
          <div className="text-center mb-16">
            <h2 className="text-4xl md:text-5xl font-bold mb-6">Live Demonstration</h2>
            <p className="text-xl text-gray-400">Watch our technology in action</p>
          </div>

          <div className="relative rounded-2xl overflow-hidden mb-12 max-w-5xl mx-auto">
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
        </div>
      </section>

      <section className="py-24 bg-gradient-to-b from-gray-900 to-gray-800">
        <div className="max-w-7xl mx-auto px-4">
          <div className="text-center mb-16">
            <h2 className="text-4xl md:text-5xl font-bold mb-6">Product Gallery</h2>
            <p className="text-xl text-gray-400">Detailed views of our technology</p>
          </div>

          <div className="grid md:grid-cols-2 gap-6">
            {currentDemo.images.map((image, index) => (
              <div key={index} className="relative group overflow-hidden rounded-2xl border border-gray-700 hover:border-blue-500 transition-all duration-300">
                <img
                  src={image.url}
                  alt={image.caption}
                  className="w-full h-80 object-cover transform group-hover:scale-110 transition-transform duration-500"
                />
                <div className="absolute inset-0 bg-gradient-to-t from-gray-900 via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300">
                  <div className="absolute bottom-0 left-0 right-0 p-6">
                    <p className="text-xl font-semibold text-white">{image.caption}</p>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      <section className="py-24 bg-gray-800">
        <div className="max-w-7xl mx-auto px-4">
          <div className="text-center mb-16">
            <h2 className="text-4xl md:text-5xl font-bold mb-6">{currentDemo.structure.title}</h2>
            <p className="text-xl text-gray-400 max-w-3xl mx-auto">{currentDemo.structure.description}</p>
          </div>

          <div className="grid md:grid-cols-2 gap-8">
            {currentDemo.structure.components.map((component, index) => {
              const IconComponent = component.icon;
              return (
                <div key={index} className="bg-gray-900 rounded-2xl p-8 border border-gray-700 hover:border-blue-500 transition-all duration-300">
                  <div className="flex items-start space-x-4 mb-4">
                    <div className="w-14 h-14 bg-gradient-to-r from-blue-500 to-purple-600 rounded-xl flex items-center justify-center flex-shrink-0">
                      <IconComponent className="w-7 h-7 text-white" />
                    </div>
                    <div>
                      <h3 className="text-2xl font-bold mb-2">{component.name}</h3>
                      <p className="text-gray-400 leading-relaxed">{component.description}</p>
                    </div>
                  </div>
                  <div className="flex flex-wrap gap-2 mt-4">
                    {component.specs.map((spec, idx) => (
                      <span key={idx} className="px-3 py-1 bg-blue-500/10 text-blue-400 rounded-full text-sm border border-blue-500/20">
                        {spec}
                      </span>
                    ))}
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      </section>

      <section className="py-24 bg-gradient-to-b from-gray-800 to-gray-900">
        <div className="max-w-7xl mx-auto px-4">
          <div className="text-center mb-16">
            <h2 className="text-4xl md:text-5xl font-bold mb-6">{currentDemo.buildingProcess.title}</h2>
            <p className="text-xl text-gray-400 max-w-3xl mx-auto">{currentDemo.buildingProcess.description}</p>
          </div>

          <div className="space-y-8">
            {currentDemo.buildingProcess.stages.map((stage, index) => (
              <div key={index} className="bg-gray-800 rounded-2xl p-8 border border-gray-700 hover:border-blue-500 transition-all duration-300">
                <div className="flex flex-col md:flex-row md:items-start md:space-x-8">
                  <div className="flex-shrink-0 mb-4 md:mb-0">
                    <div className="w-20 h-20 bg-gradient-to-r from-blue-500 to-purple-600 rounded-2xl flex items-center justify-center">
                      <span className="text-3xl font-bold">{stage.step}</span>
                    </div>
                  </div>
                  <div className="flex-grow">
                    <h3 className="text-2xl md:text-3xl font-bold mb-4">{stage.title}</h3>
                    <p className="text-gray-400 text-lg leading-relaxed">{stage.description}</p>
                  </div>
                  <div className="flex-shrink-0 mt-4 md:mt-0">
                    <Wrench className="w-8 h-8 text-blue-400" />
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      <section className="py-24 bg-gray-900">
        <div className="max-w-4xl mx-auto px-4 text-center">
          <h2 className="text-4xl md:text-5xl font-bold mb-6">Ready to Experience It Yourself?</h2>
          <p className="text-xl text-gray-400 mb-12">Schedule a personalized demonstration with our team</p>
          <button
            onClick={() => navigate('/contact')}
            className="px-10 py-5 bg-gradient-to-r from-blue-600 to-purple-700 rounded-lg font-bold text-lg hover:shadow-2xl transform hover:scale-105 transition-all duration-300"
          >
            Schedule Your Demo
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
            <p>&copy; 2025 Allytic Labs. All rights reserved.</p>
          </div>
        </div>
      </footer>
    </div>
  );
};

export default Demo;