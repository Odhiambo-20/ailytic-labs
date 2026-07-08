import React from 'react';
import { Link } from 'react-router-dom';
import { Github, Instagram, Linkedin, Youtube } from 'lucide-react';
import BrandLogo from './BrandLogo';

function SiteFooter() {
  return (
    <footer className="border-t border-slate-800 bg-slate-950 py-12 text-white">
      <div className="mx-auto grid max-w-7xl gap-8 px-6 md:grid-cols-[1.2fr_0.8fr_0.8fr_0.8fr]">
        <div>
          <BrandLogo />
          <p className="mt-5 max-w-sm text-sm leading-6 text-slate-400">
            Building practical technology across robotics, autonomous systems, and renewable energy.
          </p>
        </div>

        <div>
          <h3 className="font-semibold">Solutions</h3>
          <ul className="mt-4 space-y-3 text-sm text-slate-400">
            <li><Link to="/robots" className="hover:text-white">Robotics</Link></li>
            <li><Link to="/drones" className="hover:text-white">Drones</Link></li>
            <li><Link to="/solarpanels" className="hover:text-white">Solar Panels</Link></li>
          </ul>
        </div>

        <div>
          <h3 className="font-semibold">Company</h3>
          <ul className="mt-4 space-y-3 text-sm text-slate-400">
            <li><Link to="/about" className="hover:text-white">About</Link></li>
            <li><Link to="/careers" className="hover:text-white">Careers</Link></li>
            <li><Link to="/news" className="hover:text-white">News</Link></li>
            <li><Link to="/partners" className="hover:text-white">Partners</Link></li>
          </ul>
        </div>

        <div>
          <h3 className="font-semibold">Connect</h3>
          <ul className="mt-4 space-y-3 text-sm text-slate-400">
            <li><Link to="/contact" className="hover:text-white">Contact</Link></li>
            <li><Link to="/support" className="hover:text-white">Support</Link></li>
          </ul>
          <div className="mt-5 flex gap-3">
            <a href="https://www.instagram.com/ailyticslabs" target="_blank" rel="noopener noreferrer" className="flex h-10 w-10 items-center justify-center rounded-lg bg-white/10 text-white transition hover:bg-white/20" aria-label="Instagram">
              <Instagram className="h-5 w-5" />
            </a>
            <a href="https://github.com/ailyticslabs" target="_blank" rel="noopener noreferrer" className="flex h-10 w-10 items-center justify-center rounded-lg bg-white/10 text-white transition hover:bg-white/20" aria-label="GitHub">
              <Github className="h-5 w-5" />
            </a>
            <a href="https://www.youtube.com/@ailyticslabs" target="_blank" rel="noopener noreferrer" className="flex h-10 w-10 items-center justify-center rounded-lg bg-white/10 text-white transition hover:bg-white/20" aria-label="YouTube">
              <Youtube className="h-5 w-5" />
            </a>
            <a href="https://www.linkedin.com/company/ailyticslabs" target="_blank" rel="noopener noreferrer" className="flex h-10 w-10 items-center justify-center rounded-lg bg-white/10 text-white transition hover:bg-white/20" aria-label="LinkedIn">
              <Linkedin className="h-5 w-5" />
            </a>
          </div>
        </div>
      </div>

      <div className="mx-auto mt-10 max-w-7xl border-t border-slate-800 px-6 pt-6 text-sm text-slate-500">
        <p>&copy; 2026 Bella Technologies. All rights reserved.</p>
      </div>
    </footer>
  );
}

export default SiteFooter;
