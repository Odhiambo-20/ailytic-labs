import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { ChevronRight, Zap, Cpu, Plane, Sun, Users, Target, Globe, ArrowRight, Menu, X } from 'lucide-react';

function Navbar() {
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  return (
    <div>
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
              <Link to="/" className="text-gray-300 hover:text-white transition-colors">Home</Link>
              <Link to="/robots" className="text-gray-300 hover:text-white transition-colors">Robots</Link>
              <Link to="/drones" className="text-gray-300 hover:text-white transition-colors">Drones</Link>
              <Link to="/solarpanels" className="text-gray-300 hover:text-white transition-colors">Solarpanels</Link>
              <Link to="/contact" className="bg-gradient-to-r from-blue-500 to-purple-600 px-4 py-2 rounded-lg hover:shadow-lg transition-all text-white">
                Contact Us
              </Link>
            </div>

            {/* Mobile menu button */}
            <button 
              className="md:hidden text-white"
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
              <Link to="/" className="block py-2 text-gray-300 hover:text-white" onClick={() => setIsMenuOpen(false)}>Home</Link>
              <Link to="/robots" className="block py-2 text-gray-300 hover:text-white" onClick={() => setIsMenuOpen(false)}>Robots</Link>
              <Link to="/drones" className="block py-2 text-gray-300 hover:text-white" onClick={() => setIsMenuOpen(false)}>Drones</Link>
              <Link to="/contact" className="block py-2 text-blue-400 hover:text-blue-300" onClick={() => setIsMenuOpen(false)}>Contact Us</Link>
            </div>
          </div>
        )}
      </nav>
    </div>
  );
}

export default Navbar;