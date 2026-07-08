import React, { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { ChevronRight, Menu, Search, UserCircle, X } from 'lucide-react';
import BrandLogo from './BrandLogo';

function Navbar() {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const location = useLocation();
  const isHomePage = location.pathname === '/';

  return (
    <nav className="fixed top-0 z-50 w-full border-b border-gray-800 bg-gray-950/95 backdrop-blur-md">
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        <div className="flex h-16 items-center justify-between gap-6">
          <div className="flex min-w-0 items-center gap-3">
            <BrandLogo />
            <ChevronRight className="hidden h-4 w-4 shrink-0 text-gray-400 sm:block" />
            <button type="button" className="hidden truncate text-base text-gray-300 transition hover:text-white sm:block">
              Main Menu
            </button>
          </div>

          <div className="hidden items-center gap-5 text-sm text-gray-300 md:flex">
            <Link to="/robots" className="transition hover:text-white">Shop</Link>
            <Link to="/drones" className="transition hover:text-white">Drivers</Link>
            <Link to="/support" className="transition hover:text-white">Support</Link>
            <button type="button" className="inline-flex h-9 w-9 items-center justify-center rounded-full transition hover:bg-white/10 hover:text-white" aria-label="Search">
              <Search className="h-5 w-5" />
            </button>
            <Link to="/login" className="inline-flex h-9 w-9 items-center justify-center rounded-full transition hover:bg-white/10 hover:text-white" aria-label="Sign up or log in">
              <UserCircle className="h-6 w-6" />
            </Link>
          </div>

          {isHomePage && (
            <button
              className="rounded-lg p-2 text-white transition-colors hover:bg-white/10 md:hidden"
              onClick={() => setIsMenuOpen(!isMenuOpen)}
              aria-label={isMenuOpen ? 'Close menu' : 'Open menu'}
              aria-expanded={isMenuOpen}
            >
              {isMenuOpen ? <X className="h-6 w-6" /> : <Menu className="h-6 w-6" />}
            </button>
          )}
        </div>
      </div>

      {isHomePage && isMenuOpen && (
        <div className="border-t border-gray-800 bg-gray-950 md:hidden">
          <div className="mx-auto max-w-7xl space-y-1 px-4 py-3">
            <Link to="/robots" className="block rounded-lg px-3 py-2 text-gray-300 hover:bg-white/10 hover:text-white" onClick={() => setIsMenuOpen(false)}>Shop</Link>
            <Link to="/drones" className="block rounded-lg px-3 py-2 text-gray-300 hover:bg-white/10 hover:text-white" onClick={() => setIsMenuOpen(false)}>Drivers</Link>
            <Link to="/support" className="block rounded-lg px-3 py-2 text-gray-300 hover:bg-white/10 hover:text-white" onClick={() => setIsMenuOpen(false)}>Support</Link>
            <Link to="/login" className="block rounded-lg px-3 py-2 text-gray-300 hover:bg-white/10 hover:text-white" onClick={() => setIsMenuOpen(false)}>Login</Link>
          </div>
        </div>
      )}
    </nav>
  );
}

export default Navbar;
