import React from 'react';
import { Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import Home from './pages/Home';
import Robots from './pages/robots';
import Drones from './pages/drones';
import Company from './pages/company';
import Contact from './pages/contact';
import SandboxLab from './pages/sandboxlab';
import Solarpanels from './pages/solarpanels';  


export default function App() {
  return (
    <div>
      <Navbar />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/robots" element={<Robots/>} />
        <Route path="/drones" element={<Drones />} />
        <Route path="/company" element={<Company />} />
        <Route path="/contact" element={<Contact />} />
        <Route path="/sandbox-lab" element={<SandboxLab />} /> 
        <Route path="/solarpanels" element={<Solarpanels />} />  
      </Routes>
    </div>
  );
}