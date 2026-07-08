import React from 'react';
import { Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import Home from './pages/Home';
import Robots from './pages/robots';
import Drones from './pages/drones';
import About from './pages/About';
import Partnerships from './pages/Partnerships';
import Contact from './pages/contact';
import RobotsCatalog from './pages/RobotsCatalog';
import Order from './pages/Order';
import Demo from './pages/Demo';
import Login from './pages/login';
import Signup from './pages/signup';
import OAuthRedirect from './pages/OAuthRedirect';
import Careers from './pages/Careers';
import News from './pages/News';
import Support from './pages/Support';

import LatestModels from './pages/LatestModels';

import Solarpanels from './pages/solarpanels';  


export default function App() {
  return (
    <div>
      <Navbar />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/robots" element={<Robots/>} />
        <Route path="/drones" element={<Drones />} />
        <Route path="/about" element={<About />} />
        <Route path="/company" element={<About />} />
        <Route path="/partners" element={<Partnerships />} />
        <Route path="/partnerships" element={<Partnerships />} />
        <Route path="/careers" element={<Careers />} />
        <Route path="/news" element={<News />} />
        <Route path="/support" element={<Support />} />
        <Route path="/contact" element={<Contact />} />
    
        <Route path="/solarpanels" element={<Solarpanels />} /> 
        <Route path="/robots/catalog" element={<RobotsCatalog />} /> 
        <Route path="/latest-models" element={<LatestModels />} />
        <Route path="/order" element={<Order />} />
        <Route path="/demo" element={<Demo />} />
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />
        <Route path="/oauth2/redirect" element={<OAuthRedirect />} />
      </Routes>
    </div>
  );
}
