import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { CreditCard, Lock, ArrowLeft, Check, MapPin } from 'lucide-react';

function Order() {
  const location = useLocation();
  const navigate = useNavigate();
  const [robot, setRobot] = useState(null);
  const [orderConfirmed, setOrderConfirmed] = useState(false);
  const [showMap, setShowMap] = useState(false);
  const [mapCoordinates, setMapCoordinates] = useState({ lat: 0, lng: 0 });

  const [formData, setFormData] = useState({
    fullName: '',
    email: '',
    phone: '',
    address: '',
    city: '',
    state: '',
    zipCode: '',
    cardNumber: '',
    cardName: '',
    expiryDate: '',
    cvv: '',
    shippingMethod: 'standard'
  });

  const shippingCosts = {
    standard: 0,
    express: 49.99,
    overnight: 99.99
  };

  useEffect(() => {
    // Get robot data from navigation state
    const orderData = location.state?.robot;
    console.log('Received robot data:', orderData); // Debug log
    
    if (orderData) {
      setRobot(orderData);
    } else {
      // If no robot data, redirect back to robots page
      console.log('No robot data found, redirecting...');
      navigate('/robots');
    }
  }, [location, navigate]);

  // Geocode address when complete address fields are filled
  const geocodeAddress = async () => {
    const { address, city, state, zipCode } = formData;
    
    // Validate that all fields have meaningful content
    if (!address || address.length < 3 || !city || city.length < 2 || !state || !zipCode) {
      console.log('Address fields incomplete:', { address, city, state, zipCode });
      alert('Please enter a complete address with all fields filled in.');
      return;
    }
    
    const fullAddress = `${address}, ${city}, ${state} ${zipCode}`;
    console.log('Attempting to geocode:', fullAddress);
    
    try {
      // Using OpenStreetMap Nominatim API for geocoding (free, no API key needed)
      // Works for any location worldwide
      const response = await fetch(
        `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(fullAddress)}&limit=1`,
        {
          headers: {
            'User-Agent': 'RobotShopApp/1.0'
          }
        }
      );
      const data = await response.json();
      
      console.log('Geocoding response:', data); // Debug log
      
      if (data && data.length > 0) {
        const coords = {
          lat: parseFloat(data[0].lat),
          lng: parseFloat(data[0].lon)
        };
        console.log('Setting coordinates:', coords); // Debug log
        setMapCoordinates(coords);
        setShowMap(true);
      } else {
        console.log('No geocoding results found for:', fullAddress);
        // Fallback: Show a message
        alert('Could not find the exact location for this address. Please verify the address is correct and try again. Make sure to include the city and country if outside the US.');
        return;
      }
    } catch (error) {
      console.error('Geocoding error:', error);
      alert('Error loading map. Please check your internet connection and try again.');
    }
  };
  
  const handleShowMap = () => {
    geocodeAddress();
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;

    if (name === 'cardNumber') {
      const formatted = value.replace(/\s/g, '').replace(/(\d{4})/g, '$1 ').trim();
      setFormData(prev => ({ ...prev, [name]: formatted }));
    } else if (name === 'expiryDate') {
      const formatted = value.replace(/\D/g, '').replace(/(\d{2})(\d)/, '$1/$2').slice(0, 5);
      setFormData(prev => ({ ...prev, [name]: formatted }));
    } else if (name === 'cvv') {
      const formatted = value.replace(/\D/g, '').slice(0, 4);
      setFormData(prev => ({ ...prev, [name]: formatted }));
    } else {
      setFormData(prev => ({ ...prev, [name]: value }));
      
      // Trigger geocoding when address fields change
      if (['address', 'city', 'state', 'zipCode'].includes(name)) {
        // Don't auto-trigger, let user click button instead
        setShowMap(false);
      }
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    setOrderConfirmed(true);
    setTimeout(() => {
      navigate('/robots');
    }, 3000);
  };

  const calculateTotal = () => {
    const robotPrice = parseFloat(robot?.price?.replace(/[^0-9.]/g, '') || '0');
    const shipping = shippingCosts[formData.shippingMethod];
    const subtotal = robotPrice;
    const tax = subtotal * 0.08;
    const total = subtotal + shipping + tax;

    return {
      subtotal: subtotal.toFixed(2),
      shipping: shipping.toFixed(2),
      tax: tax.toFixed(2),
      total: total.toFixed(2)
    };
  };

  if (!robot) {
    return (
      <div className="min-h-screen bg-white flex items-center justify-center">
        <div className="text-gray-900 text-xl">Loading order details...</div>
      </div>
    );
  }

  if (orderConfirmed) {
    return (
      <div className="min-h-screen bg-white flex items-center justify-center">
        <div className="bg-gray-100 rounded-2xl p-12 max-w-md mx-auto text-center border border-gray-200">
          <div className="w-20 h-20 bg-green-500 rounded-full flex items-center justify-center mx-auto mb-6">
            <Check className="w-10 h-10 text-white" />
          </div>
          <h2 className="text-3xl font-bold text-gray-900 mb-4">Order Confirmed!</h2>
          <p className="text-gray-600 mb-6">
            Your {robot.name} has been ordered successfully. Check your email for confirmation details.
          </p>
          <button
            onClick={() => navigate('/robots')}
            className="px-8 py-3 bg-blue-600 text-white font-semibold rounded-lg hover:bg-blue-700 transition-all duration-300"
          >
            Back to Robots
          </button>
        </div>
      </div>
    );
  }

  const totals = calculateTotal();

  return (
    <div className="min-h-screen bg-white">
      {/* Header */}
      <div className="bg-white border-b border-gray-200">
        <div className="max-w-7xl mx-auto px-6 py-4">
          <button
            onClick={() => navigate(-1)}
            className="flex items-center gap-2 text-gray-600 hover:text-gray-900 transition-colors"
          >
            <ArrowLeft className="w-5 h-5" />
            Back
          </button>
        </div>
      </div>

      {/* Main Content */}
      <div className="flex h-[calc(100vh-73px)]">
        {/* Product Display Section - Tesla Style */}
        <div className="flex w-full">
          {/* Left Side - Product Image or Map (3/4 width) */}
          <div className="w-3/4 bg-gray-50 relative">
            {!showMap ? (
              <img
                src={robot.image}
                alt={robot.name}
                className="w-full h-full object-cover"
                onError={(e) => {
                  console.error('Image failed to load:', robot.image);
                  e.currentTarget.src = "https://images.pexels.com/photos/8566473/pexels-photo-8566473.jpeg?auto=compress&cs=tinysrgb&w=600";
                }}
              />
            ) : (
              <div className="w-full h-full relative">
                <iframe
                  width="100%"
                  height="100%"
                  frameBorder="0"
                  scrolling="no"
                  marginHeight="0"
                  marginWidth="0"
                  src={`https://www.openstreetmap.org/export/embed.html?bbox=${mapCoordinates.lng - 0.01},${mapCoordinates.lat - 0.01},${mapCoordinates.lng + 0.01},${mapCoordinates.lat + 0.01}&layer=mapnik&marker=${mapCoordinates.lat},${mapCoordinates.lng}`}
                  style={{ border: 0 }}
                  title="Delivery Location Map"
                />
                <div className="absolute top-6 left-6 bg-white rounded-lg shadow-lg p-4 max-w-md">
                  <div className="flex items-start gap-3">
                    <MapPin className="w-6 h-6 text-blue-600 flex-shrink-0 mt-1" />
                    <div>
                      <h3 className="font-bold text-gray-900 mb-1">Delivery Location</h3>
                      <p className="text-sm text-gray-600">
                        {formData.address}, {formData.city}, {formData.state} {formData.zipCode}
                      </p>
                    </div>
                  </div>
                </div>
                <button
                  onClick={() => setShowMap(false)}
                  className="absolute top-6 right-6 bg-white hover:bg-gray-100 text-gray-700 px-4 py-2 rounded-lg shadow-lg transition-colors font-semibold"
                >
                  View Product
                </button>
              </div>
            )}
          </div>

          {/* Right Side - Product Details and Form (1/4 width) */}
          <div className="w-1/4 overflow-y-auto">
            <div className="p-8 space-y-8">
              {/* Product Info */}
              <div>
                <h1 className="text-4xl font-bold text-gray-900 mb-3">{robot.name}</h1>
                <p className="text-lg text-gray-600 mb-4">{robot.type}</p>
                <p className="text-gray-700 mb-6 leading-relaxed">{robot.description}</p>
                <div className="text-3xl font-bold text-gray-900">{robot.price}</div>
              </div>

              {/* Shipping Method Selection */}
              <div className="border-t border-gray-200 pt-8">
                <h2 className="text-2xl font-bold text-gray-900 mb-6">Shipping Method</h2>
                <div className="space-y-3">
                  <label className="flex items-center justify-between p-5 bg-white border-2 border-gray-200 rounded-xl cursor-pointer hover:border-blue-500 transition-all">
                    <div className="flex items-center gap-4">
                      <input
                        type="radio"
                        name="shippingMethod"
                        value="standard"
                        checked={formData.shippingMethod === 'standard'}
                        onChange={handleInputChange}
                        className="w-5 h-5 text-blue-600"
                      />
                      <div>
                        <p className="text-gray-900 font-semibold text-lg">Standard Shipping</p>
                        <p className="text-gray-500 text-sm">5-7 business days</p>
                      </div>
                    </div>
                    <span className="text-green-600 font-bold text-lg">FREE</span>
                  </label>

                  <label className="flex items-center justify-between p-5 bg-white border-2 border-gray-200 rounded-xl cursor-pointer hover:border-blue-500 transition-all">
                    <div className="flex items-center gap-4">
                      <input
                        type="radio"
                        name="shippingMethod"
                        value="express"
                        checked={formData.shippingMethod === 'express'}
                        onChange={handleInputChange}
                        className="w-5 h-5 text-blue-600"
                      />
                      <div>
                        <p className="text-gray-900 font-semibold text-lg">Express Shipping</p>
                        <p className="text-gray-500 text-sm">2-3 business days</p>
                      </div>
                    </div>
                    <span className="text-gray-900 font-bold text-lg">$49.99</span>
                  </label>

                  <label className="flex items-center justify-between p-5 bg-white border-2 border-gray-200 rounded-xl cursor-pointer hover:border-blue-500 transition-all">
                    <div className="flex items-center gap-4">
                      <input
                        type="radio"
                        name="shippingMethod"
                        value="overnight"
                        checked={formData.shippingMethod === 'overnight'}
                        onChange={handleInputChange}
                        className="w-5 h-5 text-blue-600"
                      />
                      <div>
                        <p className="text-gray-900 font-semibold text-lg">Overnight Shipping</p>
                        <p className="text-gray-500 text-sm">Next business day</p>
                      </div>
                    </div>
                    <span className="text-gray-900 font-bold text-lg">$99.99</span>
                  </label>
                </div>
              </div>

              {/* Order Summary */}
              <div className="bg-gray-50 rounded-2xl p-6 border border-gray-200">
                <h3 className="text-xl font-bold text-gray-900 mb-4">Order Summary</h3>
                <div className="space-y-3">
                  <div className="flex justify-between text-gray-700">
                    <span>Subtotal</span>
                    <span className="font-semibold">${totals.subtotal}</span>
                  </div>
                  <div className="flex justify-between text-gray-700">
                    <span>Shipping</span>
                    <span className="font-semibold">${totals.shipping}</span>
                  </div>
                  <div className="flex justify-between text-gray-700">
                    <span>Tax (8%)</span>
                    <span className="font-semibold">${totals.tax}</span>
                  </div>
                  <div className="border-t border-gray-300 pt-3 flex justify-between text-gray-900 text-xl font-bold">
                    <span>Total</span>
                    <span>${totals.total}</span>
                  </div>
                </div>
              </div>

              {/* Shipping Information Form */}
              <form onSubmit={handleSubmit} className="space-y-8 border-t border-gray-200 pt-8">
                <div>
                  <h2 className="text-2xl font-bold text-gray-900 mb-6">Shipping Information</h2>
                  <div className="space-y-4">
                    <div>
                      <label className="block text-gray-700 text-sm font-semibold mb-2">Full Name</label>
                      <input
                        type="text"
                        name="fullName"
                        required
                        value={formData.fullName}
                        onChange={handleInputChange}
                        className="w-full px-4 py-3 bg-white border border-gray-300 rounded-lg text-gray-900 placeholder-gray-400 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-200 transition-all"
                        placeholder="John Doe"
                      />
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div>
                        <label className="block text-gray-700 text-sm font-semibold mb-2">Email</label>
                        <input
                          type="email"
                          name="email"
                          required
                          value={formData.email}
                          onChange={handleInputChange}
                          className="w-full px-4 py-3 bg-white border border-gray-300 rounded-lg text-gray-900 placeholder-gray-400 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-200 transition-all"
                          placeholder="john@example.com"
                        />
                      </div>
                      <div>
                        <label className="block text-gray-700 text-sm font-semibold mb-2">Phone</label>
                        <input
                          type="tel"
                          name="phone"
                          required
                          value={formData.phone}
                          onChange={handleInputChange}
                          className="w-full px-4 py-3 bg-white border border-gray-300 rounded-lg text-gray-900 placeholder-gray-400 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-200 transition-all"
                          placeholder="+1 (555) 123-4567"
                        />
                      </div>
                    </div>

                    <div>
                      <label className="block text-gray-700 text-sm font-semibold mb-2 flex items-center gap-2">
                        Home Address
                        {showMap && <MapPin className="w-4 h-4 text-blue-600" />}
                      </label>
                      <input
                        type="text"
                        name="address"
                        required
                        value={formData.address}
                        onChange={handleInputChange}
                        className="w-full px-4 py-3 bg-white border border-gray-300 rounded-lg text-gray-900 placeholder-gray-400 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-200 transition-all"
                        placeholder="123 Main Street"
                      />
                    </div>

                    <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
                      <div>
                        <label className="block text-gray-700 text-sm font-semibold mb-2">City</label>
                        <input
                          type="text"
                          name="city"
                          required
                          value={formData.city}
                          onChange={handleInputChange}
                          className="w-full px-4 py-3 bg-white border border-gray-300 rounded-lg text-gray-900 placeholder-gray-400 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-200 transition-all"
                          placeholder="New York"
                        />
                      </div>
                      <div>
                        <label className="block text-gray-700 text-sm font-semibold mb-2">State</label>
                        <input
                          type="text"
                          name="state"
                          required
                          value={formData.state}
                          onChange={handleInputChange}
                          className="w-full px-4 py-3 bg-white border border-gray-300 rounded-lg text-gray-900 placeholder-gray-400 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-200 transition-all"
                          placeholder="NY"
                        />
                      </div>
                      <div>
                        <label className="block text-gray-700 text-sm font-semibold mb-2">ZIP Code</label>
                        <input
                          type="text"
                          name="zipCode"
                          required
                          value={formData.zipCode}
                          onChange={handleInputChange}
                          className="w-full px-4 py-3 bg-white border border-gray-300 rounded-lg text-gray-900 placeholder-gray-400 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-200 transition-all"
                          placeholder="10001"
                        />
                      </div>
                    </div>

                    {formData.address && formData.city && formData.state && formData.zipCode && (
                      <button
                        type="button"
                        onClick={handleShowMap}
                        disabled={formData.address.length < 3 || formData.city.length < 2}
                        className="w-full py-3 bg-blue-600 text-white font-semibold rounded-lg hover:bg-blue-700 transition-all duration-300 flex items-center justify-center gap-2 disabled:bg-gray-400 disabled:cursor-not-allowed"
                      >
                        <MapPin className="w-5 h-5" />
                        Show Delivery Location on Map
                      </button>
                    )}

                    {showMap && (
                      <div className="p-4 bg-blue-50 border border-blue-200 rounded-lg flex items-start gap-3">
                        <MapPin className="w-5 h-5 text-blue-600 flex-shrink-0 mt-0.5" />
                        <div>
                          <p className="text-sm text-blue-900 font-semibold">Delivery location confirmed</p>
                          <p className="text-xs text-blue-700 mt-1">View the map on the left to see your delivery location</p>
                        </div>
                      </div>
                    )}
                  </div>
                </div>

                {/* Payment Information */}
                <div className="border-t border-gray-200 pt-8">
                  <h2 className="text-2xl font-bold text-gray-900 mb-6 flex items-center gap-3">
                    <CreditCard className="w-6 h-6 text-gray-700" />
                    Payment Information
                  </h2>

                  <div className="space-y-6">
                    {/* Credit Card Preview */}
                    <div className="relative">
                      <div className="bg-gradient-to-br from-gray-800 to-gray-900 rounded-2xl p-8 shadow-xl">
                        <div className="flex justify-between items-start mb-8">
                          <div className="w-12 h-10 bg-gradient-to-r from-yellow-400 to-yellow-500 rounded-lg"></div>
                          <Lock className="w-6 h-6 text-gray-400" />
                        </div>

                        <div className="mb-8">
                          <p className="text-2xl text-white font-mono tracking-widest">
                            {formData.cardNumber || '•••• •••• •••• ••••'}
                          </p>
                        </div>

                        <div className="flex justify-between items-end">
                          <div>
                            <p className="text-xs text-gray-400 mb-1 uppercase">Card Holder</p>
                            <p className="text-white font-semibold uppercase text-sm">
                              {formData.cardName || 'YOUR NAME'}
                            </p>
                          </div>
                          <div className="text-right">
                            <p className="text-xs text-gray-400 mb-1 uppercase">Expires</p>
                            <p className="text-white font-semibold text-sm">
                              {formData.expiryDate || 'MM/YY'}
                            </p>
                          </div>
                        </div>
                      </div>
                    </div>

                    {/* Payment Form Fields */}
                    <div className="space-y-4">
                      <div>
                        <label className="block text-gray-700 text-sm font-semibold mb-2">Card Number</label>
                        <input
                          type="text"
                          name="cardNumber"
                          required
                          maxLength={19}
                          value={formData.cardNumber}
                          onChange={handleInputChange}
                          className="w-full px-4 py-3 bg-white border border-gray-300 rounded-lg text-gray-900 placeholder-gray-400 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-200 transition-all font-mono"
                          placeholder="1234 5678 9012 3456"
                        />
                      </div>

                      <div>
                        <label className="block text-gray-700 text-sm font-semibold mb-2">Cardholder Name</label>
                        <input
                          type="text"
                          name="cardName"
                          required
                          value={formData.cardName}
                          onChange={handleInputChange}
                          className="w-full px-4 py-3 bg-white border border-gray-300 rounded-lg text-gray-900 placeholder-gray-400 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-200 transition-all uppercase"
                          placeholder="JOHN DOE"
                        />
                      </div>

                      <div className="grid grid-cols-2 gap-4">
                        <div>
                          <label className="block text-gray-700 text-sm font-semibold mb-2">Expiry Date</label>
                          <input
                            type="text"
                            name="expiryDate"
                            required
                            maxLength={5}
                            value={formData.expiryDate}
                            onChange={handleInputChange}
                            className="w-full px-4 py-3 bg-white border border-gray-300 rounded-lg text-gray-900 placeholder-gray-400 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-200 transition-all font-mono"
                            placeholder="MM/YY"
                          />
                        </div>
                        <div>
                          <label className="block text-gray-700 text-sm font-semibold mb-2">CVV</label>
                          <input
                            type="text"
                            name="cvv"
                            required
                            maxLength={4}
                            value={formData.cvv}
                            onChange={handleInputChange}
                            className="w-full px-4 py-3 bg-white border border-gray-300 rounded-lg text-gray-900 placeholder-gray-400 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-200 transition-all font-mono"
                            placeholder="123"
                          />
                        </div>
                      </div>
                    </div>

                    {/* Security Notice */}
                    <div className="flex items-center gap-3 p-4 bg-green-50 border border-green-200 rounded-lg">
                      <Lock className="w-5 h-5 text-green-600" />
                      <p className="text-sm text-green-700">
                        Your payment information is encrypted and secure
                      </p>
                    </div>
                  </div>
                </div>

                {/* Submit Button */}
                <button
                  type="submit"
                  className="w-full py-4 bg-blue-600 text-white text-lg font-bold rounded-lg hover:bg-blue-700 transition-all duration-300 shadow-lg hover:shadow-xl"
                >
                  Complete Order - ${totals.total}
                </button>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Order;