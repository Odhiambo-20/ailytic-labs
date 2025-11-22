import React from 'react';
import { ChevronRight, Instagram, Github, Youtube, Linkedin } from 'lucide-react';
import AllyticVideo from '../assets/Allytic.mp4';
import FoodTestingRobot from '../assets/Food Testing Robot.webm';
import AgriculturalRobotVideo from '../assets/Agricultural Robot.webm';
import RoboticDog from '../assets/Robotic Dog.mp4';
import IndustrialRobotVideo from '../assets/Industrial Robot.webm';

function Robots() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-blue-900 to-slate-900">
      <div className="relative overflow-hidden min-h-screen flex items-center pt-20">
        <div className="absolute inset-0 z-0">
          <video
            width="1920"
            height="1080"
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
            <source src={AllyticVideo} type="video/mp4" />
            Your browser does not support the video tag.
          </video>
          <div className="absolute inset-0 bg-black/40"></div>
        </div>

        <div className="relative z-10 max-w-7xl mx-auto px-6 py-16">
          <div className="text-center">
            <h1 className="text-5xl md:text-7xl font-bold text-white mb-6 bg-gradient-to-r from-white to-blue-200 bg-clip-text text-transparent">
              Future Robotics
            </h1>
            <p className="text-xl text-blue-100 mb-8 max-w-2xl mx-auto leading-relaxed">
              Discover the next generation of intelligent robots designed to transform industries, 
              enhance lives, and push the boundaries of what's possible.
            </p>
            <div className="flex flex-col sm:flex-row gap-4 justify-center">
              <button className="px-8 py-4 bg-gradient-to-r from-blue-600 to-purple-600 text-white font-semibold rounded-full hover:from-blue-700 hover:to-purple-700 transition-all duration-300 transform hover:scale-105 shadow-lg">
                Explore Robots
              </button>
              <button className="px-8 py-4 border-2 border-blue-300 text-blue-100 font-semibold rounded-full hover:bg-blue-600 hover:border-blue-600 transition-all duration-300">
                Watch Demo
              </button>
            </div>
          </div>
        </div>
      </div>

      <section className="py-20 bg-slate-900/50">
        <div className="max-w-7xl mx-auto px-6">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-12 items-center">
            <div className="relative group">

             <video
                autoPlay
                muted
                loop
                playsInline
                className="w-full h-[500px] object-cover rounded-3xl shadow-2xl"
              >
                <source src={FoodTestingRobot} type="video/mp4" />
                Your browser does not support the video tag.
              </video>
              <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-black/40 to-transparent rounded-3xl flex flex-col justify-end p-8">
                <h3 className="text-3xl font-bold text-white mb-4">
                  Precision Food Safety Testing
                </h3>
                <p className="text-blue-100 mb-6 text-lg leading-relaxed">
                  Revolutionary AI-powered robot that ensures food quality and safety with 99.9% accuracy. 
                  Detect contaminants, measure nutritional content, and guarantee compliance in seconds.
                </p>
                <button className="w-fit px-8 py-4 bg-gradient-to-r from-blue-600 to-purple-600 text-white font-semibold rounded-full hover:from-blue-700 hover:to-purple-700 transition-all duration-300 transform hover:scale-105 shadow-lg flex items-center gap-2">
                  Order Now
                  <ChevronRight className="w-5 h-5" />
                </button>
              </div>
            </div>

            <div className="relative group">
              <video
                autoPlay
                muted
                loop
                playsInline
                className="w-full h-[500px] object-cover rounded-3xl shadow-2xl"
              >
                <source src={AgriculturalRobotVideo} type="video/mp4" />
                Your browser does not support the video tag.
              </video>
              <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-black/40 to-transparent rounded-3xl flex flex-col justify-end p-8">
                <h3 className="text-3xl font-bold text-white mb-4">
                  Smart Agricultural Innovation
                </h3>
                <p className="text-blue-100 mb-6 text-lg leading-relaxed">
                  Autonomous farming robot with advanced crop monitoring, precision planting, and harvesting capabilities. 
                  Boost yields by 40% while reducing water usage and environmental impact.
                </p>
                <button className="w-fit px-8 py-4 bg-gradient-to-r from-green-600 to-teal-600 text-white font-semibold rounded-full hover:from-green-700 hover:to-teal-700 transition-all duration-300 transform hover:scale-105 shadow-lg flex items-center gap-2">
                  Order Now
                  <ChevronRight className="w-5 h-5" />
                </button>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section className="py-20 bg-gradient-to-r from-blue-900/30 to-purple-900/30">
        <div className="max-w-7xl mx-auto px-6">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-12 items-center">
            <div className="order-2 lg:order-1">
              <h2 className="text-4xl md:text-5xl font-bold text-white mb-6">
                Ready to Welcome the Future?
              </h2>
              <p className="text-xl text-blue-100 mb-8 leading-relaxed">
                Control cutting-edge robotics right from your smartphone. Experience seamless integration, 
                real-time monitoring, and intelligent automation at your fingertips. The future of robotics 
                is mobile, accessible, and incredibly powerful.
              </p>
              <div className="flex flex-col sm:flex-row gap-4">
                <button className="px-8 py-4 bg-gradient-to-r from-blue-600 to-purple-600 text-white font-semibold rounded-full hover:from-blue-700 hover:to-purple-700 transition-all duration-300 transform hover:scale-105 shadow-lg">
                  Get Started Today
                </button>
                <button className="px-8 py-4 border-2 border-blue-300 text-blue-100 font-semibold rounded-full hover:bg-blue-600 hover:border-blue-600 transition-all duration-300">
                  Contact Sales
                </button>
              </div>
            </div>
            <div className="order-1 lg:order-2">
               <video
                autoPlay
                muted
                loop
                playsInline
                className="w-full h-[500px] object-cover rounded-3xl shadow-2xl"
              >
                <source src={RoboticDog} type="video/mp4" />
                Your browser does not support the video tag.
              </video>

            </div>
          </div>
        </div>
      </section>

      <section className="py-20 bg-slate-900/50">
        <div className="max-w-7xl mx-auto px-6">
          <div className="relative group">
            <video
              autoPlay
              muted
              loop
              playsInline
              className="w-full h-[600px] object-cover rounded-3xl shadow-2xl"
            >
              <source src={IndustrialRobotVideo} type="video/mp4" />
              Your browser does not support the video tag.
            </video>
            <div className="absolute inset-0 bg-gradient-to-t from-black/90 via-black/50 to-transparent rounded-3xl flex flex-col justify-end p-12">
              <h2 className="text-4xl md:text-5xl font-bold text-white mb-6">
                Industrial Excellence in Motion
              </h2>
              <p className="text-xl text-blue-100 mb-8 leading-relaxed max-w-3xl">
                Witness the power of advanced industrial robotics. Our precision-engineered robots deliver 
                unmatched performance in manufacturing, assembly, and heavy-duty operations. Experience 
                24/7 productivity with zero downtime and maximum efficiency.
              </p>
              <button className="w-fit px-8 py-4 bg-gradient-to-r from-orange-600 to-red-600 text-white font-semibold rounded-full hover:from-orange-700 hover:to-red-700 transition-all duration-300 transform hover:scale-105 shadow-lg flex items-center gap-2">
                Order Now
                <ChevronRight className="w-5 h-5" />
              </button>
            </div>
          </div>
        </div>
      </section>

      <footer className="py-12 bg-slate-900/80 backdrop-blur-sm">
        <div className="max-w-7xl mx-auto px-6">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
            <div>
              <div className="bg-black text-white px-3 py-2 font-bold inline-block mb-4">
                A
              </div>
              <p className="text-gray-400 text-sm">AIlyticslabs</p>
            </div>

            <div>
              <h4 className="font-semibold text-white mb-4">Solutions</h4>
              <ul className="space-y-2">
                <li><a href="#" className="text-gray-400 hover:text-white text-sm">Autopilots</a></li>
                <li><a href="#" className="text-gray-400 hover:text-white text-sm">Drones</a></li>
                <li><a href="#" className="text-gray-400 hover:text-white text-sm">Robots</a></li>
              </ul>
            </div>

            <div>
              <h4 className="font-semibold text-white mb-4">Applications</h4>
              <ul className="space-y-2">
                <li><a href="#" className="text-gray-400 hover:text-white text-sm">Academic Research</a></li>
                <li><a href="#" className="text-gray-400 hover:text-white text-sm">Agriculture</a></li>
                <li><a href="#" className="text-gray-400 hover:text-white text-sm">Construction</a></li>
                <li><a href="#" className="text-gray-400 hover:text-white text-sm">Industrial</a></li>
              </ul>
            </div>

            <div>
              <h4 className="font-semibold text-white mb-4">Company</h4>
              <ul className="space-y-2 mb-6">
                <li><a href="#" className="text-gray-400 hover:text-white text-sm">Our story</a></li>
                <li><a href="#" className="text-gray-400 hover:text-white text-sm">Careers</a></li>
                <li><a href="#" className="text-gray-400 hover:text-white text-sm">Press</a></li>
                <li><a href="#" className="text-gray-400 hover:text-white text-sm">Meet the Team</a></li>
              </ul>
              
              <div className="text-sm text-gray-400 space-y-1">
                <p><strong>COC</strong> To be provided</p>
                <p><strong>VAT</strong> To be provided</p>
                <p><strong>A</strong> P.O box 00100, Nairobi</p>
                <p><strong>E</strong> info@ailyticslabs.com</p>
                <p><strong>T</strong> +254748630243</p>
              </div>
            </div>
          </div>

          <div className="mt-12 pt-8 border-t border-gray-700">
            <div className="flex flex-col md:flex-row justify-between items-center">
              <div className="mb-4 md:mb-0">
                <div className="text-center mb-4">
                  <h4 className="font-semibold text-white mb-2">Subscribe to our newsletter</h4>
                  <p className="text-gray-400 text-sm mb-4">The latest news, articles, and resources, sent to your inbox monthly</p>
                  <div className="flex max-w-md mx-auto">
                    <input
                      type="email"
                      placeholder="Enter your email"
                      className="flex-1 px-4 py-2 border border-gray-600 bg-slate-800 text-white rounded-l-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    />
                    <button className="bg-blue-500 text-white px-4 py-2 rounded-r-lg hover:bg-blue-600">
                      <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 8l7.89 4.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 002 2z" />
                      </svg>
                    </button>
                  </div>
                </div>
              </div>
              
              <div className="flex items-center space-x-4">
                <div className="text-sm text-gray-400">
                  <p>© 2025 AIlyticslabs. All rights reserved</p>
                  <div className="flex space-x-4 mt-2">
                    <a href="#" className="hover:text-white">Terms and Conditions</a>
                    <span>|</span>
                    <a href="#" className="hover:text-white">EULA</a>
                  </div>
                </div>
                <div className="flex space-x-3">
                  <a href="#" className="text-gray-400 hover:text-white">
                    <Instagram className="h-5 w-5" />
                  </a>
                  <a href="#" className="text-gray-400 hover:text-white">
                    <Github className="h-5 w-5" />
                  </a>
                  <a href="#" className="text-gray-400 hover:text-white">
                    <Youtube className="h-5 w-5" />
                  </a>
                  <a href="#" className="text-gray-400 hover:text-white">
                    <Linkedin className="h-5 w-5" />
                  </a>
                </div>
              </div>
            </div>
          </div>
        </div>
      </footer>
    </div>
  );
}

export default Robots;
