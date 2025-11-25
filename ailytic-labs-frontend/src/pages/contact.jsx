import React, { useState } from 'react';
import { Send, Mail, Phone, MapPin, Instagram, Github, Youtube, Linkedin, CheckCircle, AlertCircle } from 'lucide-react';
import { contactAPI } from '../services/api';

const Contact = () => {  
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    helpType: 'Product Information',
    message: ''
  });

  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submitStatus, setSubmitStatus] = useState(null); // 'success' or 'error'
  const [errorMessage, setErrorMessage] = useState('');

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    // Clear status when user starts typing again
    if (submitStatus) {
      setSubmitStatus(null);
      setErrorMessage('');
    }
  };

  const handleSubmit = async (e) => { 
    e.preventDefault();
    setIsSubmitting(true);
    setSubmitStatus(null);
    setErrorMessage('');

    try {
      // Submit to backend API
      await contactAPI.submit(formData);
      
      // Success
      setSubmitStatus('success');
      
      // Reset form after successful submission
      setFormData({
        firstName: '',
        lastName: '',
        email: '',
        helpType: 'Product Information',
        message: ''
      });

      // Auto-hide success message after 5 seconds
      setTimeout(() => {
        setSubmitStatus(null);
      }, 5000);

    } catch (error) {
      console.error('Failed to submit contact form:', error);
      setSubmitStatus('error');
      setErrorMessage('Failed to send message. Please try again or contact us directly via email.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-slate-100">
      {/* Hero Section */}
      <div className="relative overflow-hidden bg-gradient-to-r from-slate-900 via-blue-900 to-slate-800 text-white py-24">
        <div className="absolute inset-0 opacity-10">
          <div className="absolute inset-0" style={{
            backgroundImage: `url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='1'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E")`,
          }}></div>
        </div>
        <div className="relative max-w-7xl mx-auto px-6 text-center">
          <h1 className="text-5xl md:text-6xl font-bold mb-4 tracking-tight">Get in Touch</h1>
          <p className="text-xl text-blue-200 max-w-2xl mx-auto">
            We'd love to hear from you. Send us a message and we'll respond as soon as possible.
          </p>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-6 py-16 -mt-12">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Contact Form */}
          <div className="lg:col-span-2">
            <div className="bg-white rounded-2xl shadow-xl p-8 md:p-12 border border-slate-200">
              <h2 className="text-3xl font-bold text-slate-900 mb-2">Send us a message</h2>
              <p className="text-slate-600 mb-8">Fill out the form below and our team will get back to you within 24 hours.</p>

              {/* Success Alert */}
              {submitStatus === 'success' && (
                <div className="mb-6 p-4 bg-green-50 border border-green-200 rounded-xl flex items-start gap-3 animate-fade-in">
                  <CheckCircle className="w-6 h-6 text-green-600 flex-shrink-0 mt-0.5" />
                  <div>
                    <h4 className="font-semibold text-green-900 mb-1">Message sent successfully!</h4>
                    <p className="text-sm text-green-700">Thank you for contacting us. We'll get back to you within 24 hours.</p>
                  </div>
                </div>
              )}

              {/* Error Alert */}
              {submitStatus === 'error' && (
                <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-xl flex items-start gap-3 animate-fade-in">
                  <AlertCircle className="w-6 h-6 text-red-600 flex-shrink-0 mt-0.5" />
                  <div>
                    <h4 className="font-semibold text-red-900 mb-1">Failed to send message</h4>
                    <p className="text-sm text-red-700">{errorMessage}</p>
                  </div>
                </div>
              )}

              <form onSubmit={handleSubmit} className="space-y-6">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div>
                    <label htmlFor="firstName" className="block text-sm font-semibold text-slate-700 mb-2">
                      First name <span className="text-red-500">*</span>
                    </label>
                    <input
                      type="text"
                      id="firstName"
                      name="firstName"
                      value={formData.firstName}
                      onChange={handleInputChange}
                      required
                      className="w-full px-4 py-3 bg-slate-50 border-2 border-slate-200 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all outline-none"
                      placeholder="John"
                    />
                  </div>
                  <div>
                    <label htmlFor="lastName" className="block text-sm font-semibold text-slate-700 mb-2">
                      Last name
                    </label>
                    <input
                      type="text"
                      id="lastName"
                      name="lastName"
                      value={formData.lastName}
                      onChange={handleInputChange}
                      className="w-full px-4 py-3 bg-slate-50 border-2 border-slate-200 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all outline-none"
                      placeholder="Doe"
                    />
                  </div>
                </div>

                <div>
                  <label htmlFor="email" className="block text-sm font-semibold text-slate-700 mb-2">
                    Email address <span className="text-red-500">*</span>
                  </label>
                  <input
                    type="email"
                    id="email"
                    name="email"
                    value={formData.email}
                    onChange={handleInputChange}
                    required
                    className="w-full px-4 py-3 bg-slate-50 border-2 border-slate-200 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all outline-none"
                    placeholder="john@example.com"
                  />
                </div>

                <div>
                  <label htmlFor="helpType" className="block text-sm font-semibold text-slate-700 mb-2">
                    How can we help you?
                  </label>
                  <select
                    id="helpType"
                    name="helpType"
                    value={formData.helpType}
                    onChange={handleInputChange}
                    className="w-full px-4 py-3 bg-slate-50 border-2 border-slate-200 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all outline-none appearance-none cursor-pointer"
                  >
                    <option value="Product Information">Product Information</option>
                    <option value="Technical Support">Technical Support</option>
                    <option value="Partnership Inquiry">Partnership Inquiry</option>
                    <option value="Media Inquiry">Media Inquiry</option>
                    <option value="Sales Inquiry">Sales Inquiry</option>
                    <option value="Other">Other</option>
                  </select>
                </div>

                <div>
                  <label htmlFor="message" className="block text-sm font-semibold text-slate-700 mb-2">
                    Your message
                  </label>
                  <textarea
                    id="message"
                    name="message"
                    value={formData.message}
                    onChange={handleInputChange}
                    rows={6}
                    className="w-full px-4 py-3 bg-slate-50 border-2 border-slate-200 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all outline-none resize-none"
                    placeholder="Tell us more about your inquiry..."
                  />
                </div>

                <button
                  type="submit"
                  disabled={isSubmitting}
                  className="w-full md:w-auto bg-gradient-to-r from-blue-600 to-blue-700 text-white px-8 py-4 rounded-xl hover:from-blue-700 hover:to-blue-800 transition-all font-semibold shadow-lg hover:shadow-xl disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
                >
                  {isSubmitting ? (
                    <>
                      <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                      Sending...
                    </>
                  ) : (
                    <>
                      <Send className="h-5 w-5" />
                      Send Message
                    </>
                  )}
                </button>
              </form>

              <div className="mt-8 p-4 bg-blue-50 border border-blue-200 rounded-xl">
                <p className="text-sm text-slate-700">
                  <strong>Note:</strong> We do not process job applications via this form. For career opportunities, email{' '}
                  <a href="mailto:recruitment@ailyticslabs.com" className="text-blue-600 hover:text-blue-700 font-semibold">
                    recruitment@ailyticslabs.com
                  </a>
                </p>
              </div>
            </div>
          </div>

          {/* Contact Info Sidebar */}
          <div className="space-y-6">
            <div className="bg-white rounded-2xl shadow-xl p-8 border border-slate-200">
              <h3 className="text-2xl font-bold text-slate-900 mb-6">Contact Info</h3>
              <div className="space-y-5">
                <div className="flex items-start gap-4">
                  <div className="w-12 h-12 bg-blue-100 rounded-xl flex items-center justify-center flex-shrink-0">
                    <Mail className="h-6 w-6 text-blue-600" />
                  </div>
                  <div>
                    <h4 className="font-semibold text-slate-900 mb-1">Email</h4>
                    <a href="mailto:info@ailyticslabs.com" className="text-slate-600 hover:text-blue-600 transition-colors">
                      info@ailyticslabs.com
                    </a>
                    <br />
                    <a href="mailto:business@ailyticslabs.com" className="text-slate-600 hover:text-blue-600 transition-colors">
                      business@ailyticslabs.com
                    </a>
                  </div>
                </div>

                <div className="flex items-start gap-4">
                  <div className="w-12 h-12 bg-green-100 rounded-xl flex items-center justify-center flex-shrink-0">
                    <Phone className="h-6 w-6 text-green-600" />
                  </div>
                  <div>
                    <h4 className="font-semibold text-slate-900 mb-1">Phone</h4>
                    <a href="tel:+254748630243" className="text-slate-600 hover:text-blue-600 transition-colors">
                      +254 748 630 243
                    </a>
                  </div>
                </div>

                <div className="flex items-start gap-4">
                  <div className="w-12 h-12 bg-orange-100 rounded-xl flex items-center justify-center flex-shrink-0">
                    <MapPin className="h-6 w-6 text-orange-600" />
                  </div>
                  <div>
                    <h4 className="font-semibold text-slate-900 mb-1">Address</h4>
                    <p className="text-slate-600">
                      P.O Box 00100<br />
                      Nairobi<br />
                      Kenya
                    </p>
                  </div>
                </div>
              </div>
            </div>

            <div className="bg-gradient-to-br from-slate-900 to-blue-900 rounded-2xl shadow-xl p-8 text-white">
              <h3 className="text-2xl font-bold mb-4">Business Hours</h3>
              <div className="space-y-3 text-blue-100">
                <div className="flex justify-between">
                  <span>Monday - Friday</span>
                  <span className="font-semibold">9:00 - 18:00</span>
                </div>
                <div className="flex justify-between">
                  <span>Saturday</span>
                  <span className="font-semibold">10:00 - 16:00</span>
                </div>
                <div className="flex justify-between">
                  <span>Sunday</span>
                  <span className="font-semibold">Closed</span>
                </div>
              </div>
              <div className="mt-6 pt-6 border-t border-blue-700">
                <p className="text-sm text-blue-200 mb-4">Follow us on social media</p>
                <div className="flex gap-3">
                  <a 
                    href="https://www.instagram.com/ailyticslabs" 
                    target="_blank" 
                    rel="noopener noreferrer"
                    className="w-10 h-10 bg-white/10 hover:bg-white/20 rounded-lg flex items-center justify-center transition-all"
                    aria-label="Follow us on Instagram"
                  >
                    <Instagram className="h-5 w-5" />
                  </a>
                  <a 
                    href="https://github.com/ailyticslabs" 
                    target="_blank" 
                    rel="noopener noreferrer"
                    className="w-10 h-10 bg-white/10 hover:bg-white/20 rounded-lg flex items-center justify-center transition-all"
                    aria-label="Follow us on GitHub"
                  >
                    <Github className="h-5 w-5" />
                  </a>
                  <a 
                    href="https://www.youtube.com/@ailyticslabs" 
                    target="_blank" 
                    rel="noopener noreferrer"
                    className="w-10 h-10 bg-white/10 hover:bg-white/20 rounded-lg flex items-center justify-center transition-all"
                    aria-label="Follow us on YouTube"
                  >
                    <Youtube className="h-5 w-5" />
                  </a>
                  <a 
                    href="https://www.linkedin.com/company/ailyticslabs" 
                    target="_blank" 
                    rel="noopener noreferrer"
                    className="w-10 h-10 bg-white/10 hover:bg-white/20 rounded-lg flex items-center justify-center transition-all"
                    aria-label="Follow us on LinkedIn"
                  >
                    <Linkedin className="h-5 w-5" />
                  </a>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Footer */}
      <footer className="bg-slate-900 text-white py-12">
        <div className="max-w-7xl mx-auto px-6">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-8 mb-8">
            <div>
              <div className="flex items-center space-x-2 mb-4">
                <div className="w-8 h-8 bg-gradient-to-r from-blue-500 to-cyan-600 rounded-lg flex items-center justify-center font-bold text-sm">
                  A
                </div>
                <span className="text-lg font-bold text-white">Allytic Labs</span>
              </div>
              <p className="text-slate-400 text-sm">Pioneering the future of robotics, drones, and renewable energy.</p>
            </div>

            <div>
              <h4 className="font-semibold mb-4">Solutions</h4>
              <ul className="space-y-2">
                <li><a href="/robots" className="text-slate-400 hover:text-white text-sm transition-colors">Robotics</a></li>
                <li><a href="/drones" className="text-slate-400 hover:text-white text-sm transition-colors">Drones</a></li>
                <li><a href="/solarpanels" className="text-slate-400 hover:text-white text-sm transition-colors">Solar Panels</a></li>
              </ul>
            </div>

            <div>
              <h4 className="font-semibold mb-4">Applications</h4>
              <ul className="space-y-2">
                <li><a href="#" className="text-slate-400 hover:text-white text-sm transition-colors">Academic Research</a></li>
                <li><a href="#" className="text-slate-400 hover:text-white text-sm transition-colors">Agriculture</a></li>
                <li><a href="#" className="text-slate-400 hover:text-white text-sm transition-colors">Construction</a></li>
                <li><a href="#" className="text-slate-400 hover:text-white text-sm transition-colors">Industrial</a></li>
              </ul>
            </div>

            <div>
              <h4 className="font-semibold mb-4">Company</h4>
              <ul className="space-y-2">
                <li><a href="#" className="text-slate-400 hover:text-white text-sm transition-colors">Our Story</a></li>
                <li><a href="#" className="text-slate-400 hover:text-white text-sm transition-colors">Careers</a></li>
                <li><a href="#" className="text-slate-400 hover:text-white text-sm transition-colors">Press</a></li>
                <li><a href="/contact" className="text-slate-400 hover:text-white text-sm transition-colors">Contact Us</a></li>
              </ul>
            </div>
          </div>

          <div className="pt-8 border-t border-slate-800">
            <div className="flex flex-col md:flex-row justify-between items-center gap-4">
              <p className="text-sm text-slate-400">© 2025 Allytic Labs. All rights reserved</p>
              <div className="flex gap-4 text-sm text-slate-400">
                <a href="#" className="hover:text-white transition-colors">Terms and Conditions</a>
                <span>|</span>
                <a href="#" className="hover:text-white transition-colors">Privacy Policy</a>
              </div>
            </div>
          </div>
        </div>
      </footer>
    </div>
  );
};

export default Contact;