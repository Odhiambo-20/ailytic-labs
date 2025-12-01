import { useState, useEffect } from 'react';
import { ChevronRight } from 'lucide-react';

function RobotsCatalog() {
  const [robots, setRobots] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchRobots = async () => {
      try {
        setLoading(true);

        const mockRobots = [
          {
            id: '1',
            name: 'AgroBot Pro X1',
            type: 'Agricultural',
            description: 'Advanced autonomous farming robot with AI-powered crop monitoring, precision planting, and smart harvesting capabilities. Increases yield by 40% while reducing water usage.',
            image: 'https://images.pexels.com/photos/2085831/pexels-photo-2085831.jpeg?auto=compress&cs=tinysrgb&w=800',
            capabilities: ['Autonomous Navigation', 'Crop Analysis', 'Precision Planting', 'Smart Harvesting'],
            price: '$45,000',
            rating: 4.8,
            reviews: 124,
            specifications: {
              weight: '350 kg',
              height: '1.8 m',
              batteryLife: '12 hours',
              speed: '5 km/h'
            }
          },
          {
            id: '2',
            name: 'SafeTest 3000',
            type: 'Food Testing',
            description: 'Revolutionary food safety testing robot with 99.9% accuracy in detecting contaminants, measuring nutritional content, and ensuring compliance with food safety standards.',
            image: 'https://images.pexels.com/photos/2085832/pexels-photo-2085832.jpeg?auto=compress&cs=tinysrgb&w=800',
            capabilities: ['Contaminant Detection', 'Nutritional Analysis', 'Quality Control', 'Compliance Reporting'],
            price: '$78,000',
            rating: 4.9,
            reviews: 89,
            specifications: {
              weight: '120 kg',
              height: '1.5 m',
              batteryLife: '8 hours',
              speed: 'Stationary'
            }
          },
          {
            id: '3',
            name: 'IndustrialArm MAX',
            type: 'Industrial',
            description: 'High-precision industrial robotic arm designed for manufacturing, assembly, and heavy-duty operations. Delivers 24/7 productivity with zero downtime.',
            image: 'https://images.pexels.com/photos/1108101/pexels-photo-1108101.jpeg?auto=compress&cs=tinysrgb&w=800',
            capabilities: ['Precision Assembly', 'Heavy Lifting', 'Quality Inspection', 'Welding'],
            price: '$125,000',
            rating: 4.7,
            reviews: 203,
            specifications: {
              weight: '580 kg',
              height: '2.4 m',
              batteryLife: 'AC Powered',
              speed: '15 ops/min'
            }
          },
          {
            id: '4',
            name: 'RoboDog Alpha',
            type: 'Companion',
            description: 'Advanced quadruped robot with AI-powered mobility and interaction. Perfect for security, inspection, and companionship applications.',
            image: 'https://images.pexels.com/photos/8566473/pexels-photo-8566473.jpeg?auto=compress&cs=tinysrgb&w=800',
            capabilities: ['Terrain Navigation', 'Object Recognition', 'Voice Commands', 'Security Patrol'],
            price: '$32,000',
            rating: 4.6,
            reviews: 156,
            specifications: {
              weight: '25 kg',
              height: '0.6 m',
              batteryLife: '4 hours',
              speed: '12 km/h'
            }
          },
          {
            id: '5',
            name: 'MediBot Care+',
            type: 'Medical',
            description: 'Healthcare assistance robot designed to support medical staff with patient monitoring, medication delivery, and vital sign tracking.',
            image: 'https://images.pexels.com/photos/8566470/pexels-photo-8566470.jpeg?auto=compress&cs=tinysrgb&w=800',
            capabilities: ['Patient Monitoring', 'Medication Delivery', 'Vital Sign Tracking', 'Emergency Response'],
            price: '$95,000',
            rating: 4.9,
            reviews: 78,
            specifications: {
              weight: '180 kg',
              height: '1.6 m',
              batteryLife: '10 hours',
              speed: '3 km/h'
            }
          },
          {
            id: '6',
            name: 'CleanBot Pro',
            type: 'Service',
            description: 'Intelligent commercial cleaning robot with advanced navigation and multi-surface cleaning capabilities for offices and public spaces.',
            image: 'https://images.pexels.com/photos/8386440/pexels-photo-8386440.jpeg?auto=compress&cs=tinysrgb&w=800',
            capabilities: ['Auto Mapping', 'Multi-Surface Cleaning', 'Obstacle Avoidance', 'Schedule Management'],
            price: '$18,500',
            rating: 4.5,
            reviews: 312,
            specifications: {
              weight: '45 kg',
              height: '0.9 m',
              batteryLife: '6 hours',
              speed: '4 km/h'
            }
          },
          {
            id: '7',
            name: 'LogisticsPro X500',
            type: 'Warehouse',
            description: 'Automated warehouse robot for inventory management, picking, packing, and transportation of goods with 99.5% accuracy.',
            image: 'https://images.pexels.com/photos/1108099/pexels-photo-1108099.jpeg?auto=compress&cs=tinysrgb&w=800',
            capabilities: ['Inventory Tracking', 'Automated Picking', 'Load Transport', 'Route Optimization'],
            price: '$65,000',
            rating: 4.8,
            reviews: 145,
            specifications: {
              weight: '320 kg',
              height: '1.4 m',
              batteryLife: '14 hours',
              speed: '8 km/h'
            }
          },
          {
            id: '8',
            name: 'EduBot Scholar',
            type: 'Educational',
            description: 'Interactive educational robot designed to teach coding, robotics, and STEM subjects to students of all ages with engaging AI-powered lessons.',
            image: 'https://images.pexels.com/photos/8438979/pexels-photo-8438979.jpeg?auto=compress&cs=tinysrgb&w=800',
            capabilities: ['Interactive Learning', 'Coding Tutorials', 'STEM Education', 'Progress Tracking'],
            price: '$8,900',
            rating: 4.7,
            reviews: 567,
            specifications: {
              weight: '12 kg',
              height: '0.5 m',
              batteryLife: '5 hours',
              speed: '2 km/h'
            }
          },
          {
            id: '9',
            name: 'SecurityBot Guardian',
            type: 'Security',
            description: 'Advanced security and surveillance robot with thermal imaging, facial recognition, and autonomous patrol capabilities for 24/7 protection.',
            image: 'https://images.pexels.com/photos/2599244/pexels-photo-2599244.jpeg?auto=compress&cs=tinysrgb&w=800',
            capabilities: ['Thermal Imaging', 'Facial Recognition', 'Autonomous Patrol', 'Alert System'],
            price: '$52,000',
            rating: 4.6,
            reviews: 98,
            specifications: {
              weight: '95 kg',
              height: '1.3 m',
              batteryLife: '8 hours',
              speed: '6 km/h'
            }
          }
        ];

        await new Promise(resolve => setTimeout(resolve, 800));
        setRobots(mockRobots);
      } catch (error) {
        console.error('Failed to fetch robots:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchRobots();
  }, []);

  const groupedRobots = robots.reduce((acc, robot) => {
    const type = robot.type;
    if (!acc[type]) {
      acc[type] = [];
    }
    acc[type].push(robot);
    return acc;
  }, {});

  const robotCategories = Object.entries(groupedRobots);

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-blue-900 to-slate-900">
      <div className="relative overflow-hidden bg-gradient-to-r from-blue-900/80 to-purple-900/80 py-24">
        <div className="absolute inset-0 bg-[url('https://images.pexels.com/photos/2599244/pexels-photo-2599244.jpeg?auto=compress&cs=tinysrgb&w=1920')] bg-cover bg-center opacity-20"></div>
        <div className="absolute inset-0 bg-gradient-to-r from-blue-900/90 to-purple-900/90"></div>

        <div className="relative z-10 max-w-7xl mx-auto px-6">
          <div className="text-center">
            <h1 className="text-5xl md:text-6xl font-bold text-white mb-6">
              Our Robot Collection
            </h1>
            <p className="text-xl text-blue-100 max-w-3xl mx-auto leading-relaxed">
              Discover our complete lineup of intelligent robots powered by advanced AI and cutting-edge technology.
              Each robot is designed to transform industries and enhance productivity.
            </p>
          </div>
        </div>
      </div>

      {loading ? (
        <div className="text-center py-40">
          <div className="inline-block animate-spin rounded-full h-16 w-16 border-t-4 border-b-4 border-blue-500 mb-4"></div>
          <p className="text-xl text-blue-100">Loading our amazing robots...</p>
        </div>
      ) : (
        <div className="space-y-20 py-16">
          {robotCategories.map(([category, categoryRobots]) => (
            <div key={category} className="max-w-7xl mx-auto px-6">
              <div className="mb-10">
                <h2 className="text-4xl font-bold text-white mb-2">{category}</h2>
                <div className="w-20 h-1 bg-gradient-to-r from-blue-600 to-purple-600 rounded-full"></div>
              </div>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                {categoryRobots.slice(0, 2).map((robot) => (
              <div
                key={robot.id}
                className="bg-gradient-to-br from-slate-800/90 to-blue-900/90 backdrop-blur-sm rounded-2xl overflow-hidden border border-blue-500/20 hover:border-blue-500/50 transition-all duration-300 transform hover:scale-105 hover:shadow-2xl"
              >
                <div className="relative h-64 overflow-hidden">
                  <img
                    src={robot.image || "https://images.pexels.com/photos/8566473/pexels-photo-8566473.jpeg?auto=compress&cs=tinysrgb&w=600"}
                    alt={robot.name}
                    className="w-full h-full object-cover transition-transform duration-500 hover:scale-110"
                    onError={(e) => {
                      e.target.src = "https://images.pexels.com/photos/8566473/pexels-photo-8566473.jpeg?auto=compress&cs=tinysrgb&w=600";
                    }}
                  />
                  <div className="absolute top-4 right-4 bg-blue-600/90 backdrop-blur-sm px-4 py-2 rounded-full shadow-lg">
                    <span className="text-white text-sm font-semibold">{robot.type}</span>
                  </div>
                  {robot.rating && (
                    <div className="absolute top-4 left-4 bg-black/60 backdrop-blur-sm px-3 py-1 rounded-full flex items-center gap-1">
                      <span className="text-yellow-400 text-lg">★</span>
                      <span className="text-white font-semibold">{robot.rating}</span>
                    </div>
                  )}
                </div>

                <div className="p-6">
                  <h3 className="text-2xl font-bold text-white mb-3">{robot.name}</h3>
                  <p className="text-blue-100 text-sm mb-4 leading-relaxed line-clamp-3">
                    {robot.description}
                  </p>

                  {robot.capabilities && robot.capabilities.length > 0 && (
                    <div className="mb-4">
                      <p className="text-xs text-blue-300 font-semibold uppercase tracking-wide mb-2">
                        Key Capabilities:
                      </p>
                      <div className="flex flex-wrap gap-2">
                        {robot.capabilities.slice(0, 4).map((capability, index) => (
                          <span
                            key={index}
                            className="text-xs bg-blue-600/30 text-blue-200 px-3 py-1 rounded-full"
                          >
                            {capability}
                          </span>
                        ))}
                      </div>
                    </div>
                  )}

                  {robot.specifications && (
                    <div className="mb-4 p-3 bg-black/20 rounded-lg">
                      <p className="text-xs text-blue-300 font-semibold uppercase tracking-wide mb-2">
                        Specifications:
                      </p>
                      <div className="grid grid-cols-2 gap-2 text-xs">
                        {robot.specifications.weight && (
                          <div className="text-blue-100">
                            <span className="text-gray-400">Weight:</span> {robot.specifications.weight}
                          </div>
                        )}
                        {robot.specifications.batteryLife && (
                          <div className="text-blue-100">
                            <span className="text-gray-400">Battery:</span> {robot.specifications.batteryLife}
                          </div>
                        )}
                        {robot.specifications.height && (
                          <div className="text-blue-100">
                            <span className="text-gray-400">Height:</span> {robot.specifications.height}
                          </div>
                        )}
                        {robot.specifications.speed && (
                          <div className="text-blue-100">
                            <span className="text-gray-400">Speed:</span> {robot.specifications.speed}
                          </div>
                        )}
                      </div>
                    </div>
                  )}

                  <div className="flex justify-between items-center mb-4">
                    {robot.price && (
                      <p className="text-3xl font-bold bg-gradient-to-r from-blue-400 to-purple-400 bg-clip-text text-transparent">
                        {robot.price}
                      </p>
                    )}
                    {robot.reviews && (
                      <span className="text-gray-400 text-sm">({robot.reviews} reviews)</span>
                    )}
                  </div>

                  <button className="w-full py-3 bg-gradient-to-r from-blue-600 to-purple-600 text-white font-semibold rounded-full hover:from-blue-700 hover:to-purple-700 transition-all duration-300 transform hover:scale-105 shadow-lg flex items-center justify-center gap-2">
                    View Details
                    <ChevronRight className="w-4 h-4" />
                  </button>
                </div>
              </div>
                ))}
              </div>
            </div>
          ))}
        </div>
      )}

      <div className="bg-gradient-to-r from-blue-900/50 to-purple-900/50 py-16">
        <div className="max-w-7xl mx-auto px-6 text-center">
          <h2 className="text-3xl md:text-4xl font-bold text-white mb-4">
            Need Help Choosing?
          </h2>
          <p className="text-xl text-blue-100 mb-8 max-w-2xl mx-auto">
            Our robotics experts are here to help you find the perfect robot for your needs.
          </p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <button className="px-8 py-4 bg-gradient-to-r from-blue-600 to-purple-600 text-white font-semibold rounded-full hover:from-blue-700 hover:to-purple-700 transition-all duration-300 transform hover:scale-105 shadow-lg">
              Contact Sales
            </button>
            <button className="px-8 py-4 border-2 border-blue-300 text-blue-100 font-semibold rounded-full hover:bg-blue-600 hover:border-blue-600 transition-all duration-300">
              Request a Demo
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default RobotsCatalog;