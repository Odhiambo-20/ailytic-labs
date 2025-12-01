import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { CreditCard, Lock, Package, Truck, ArrowLeft, Check } from 'lucide-react';

function Order() {
  const location = useLocation();
  const navigate = useNavigate();
  const [robot, setRobot] = useState(null);
  const [orderConfirmed, setOrderConfirmed] = useState(false);

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
    const orderData = location.state?.robot;
    if (orderData) {
      setRobot(orderData);
    } else {
      navigate('/robots');
    }
  }, [location, navigate]);

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
      <div className="min-h-screen bg-gradient-to-br from-slate-900 via-blue-900 to-slate-900 flex items-center justify-center">
        <div className="text-white text-xl">Loading order details...</div>
      </div>
    );
  }

  if (orderConfirmed) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-slate-900 via-blue-900 to-slate-900 flex items-center justify-center">
        <div className="bg-white/10 backdrop-blur-lg rounded-3xl p-12 max-w-md mx-auto text-center border border-white/20">
          <div className="w-20 h-20 bg-green-500 rounded-full flex items-center justify-center mx-auto mb-6">
            <Check className="w-10 h-10 text-white" />
          </div>
          <h2 className="text-3xl font-bold text-white mb-4">Order Confirmed!</h2>
          <p className="text-blue-100 mb-6">
            Your {robot.name} has been ordered successfully. Check your email for confirmation details.
          </p>
          <button
            onClick={() => navigate('/robots')}
            className="px-8 py-3 bg-gradient-to-r from-blue-600 to-purple-600 text-white font-semibold rounded-full hover:from-blue-700 hover:to-purple-700 transition-all duration-300"
          >
            Back to Robots
          </button>
        </div>
      </div>
    );
  }

  const totals = calculateTotal();

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-blue-900 to-slate-900 py-12 px-4">
      <div className="max-w-7xl mx-auto">
        <button
          onClick={() => navigate(-1)}
          className="flex items-center gap-2 text-blue-300 hover:text-white mb-8 transition-colors"
        >
          <ArrowLeft className="w-5 h-5" />
          Back to Robots
        </button>

        <h1 className="text-4xl md:text-5xl font-bold text-white mb-12 text-center">
          Complete Your Order
        </h1>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          <div className="space-y-6">
            <div className="bg-white/10 backdrop-blur-lg rounded-3xl p-8 border border-white/20">
              <h2 className="text-2xl font-bold text-white mb-6 flex items-center gap-3">
                <Package className="w-6 h-6 text-blue-400" />
                Order Details
              </h2>

              <div className="flex gap-6 mb-6">
                <img
                  src={robot.image || "https://images.pexels.com/photos/8566473/pexels-photo-8566473.jpeg?auto=compress&cs=tinysrgb&w=600"}
                  alt={robot.name}
                  className="w-32 h-32 object-cover rounded-2xl"
                  onError={(e) => {
                    e.currentTarget.src = "https://images.pexels.com/photos/8566473/pexels-photo-8566473.jpeg?auto=compress&cs=tinysrgb&w=600";
                  }}
                />
                <div className="flex-1">
                  <h3 className="text-xl font-bold text-white mb-2">{robot.name}</h3>
                  <p className="text-blue-300 text-sm mb-2">{robot.type}</p>
                  <p className="text-blue-100 text-sm line-clamp-2">{robot.description}</p>
                  <p className="text-2xl font-bold text-blue-400 mt-3">{robot.price}</p>
                </div>
              </div>

              <div className="border-t border-white/20 pt-6">
                <h3 className="text-lg font-semibold text-white mb-4 flex items-center gap-2">
                  <Truck className="w-5 h-5 text-blue-400" />
                  Shipping Method
                </h3>
                <div className="space-y-3">
                  <label className="flex items-center justify-between p-4 bg-white/5 rounded-xl cursor-pointer hover:bg-white/10 transition-all border border-white/10">
                    <div className="flex items-center gap-3">
                      <input
                        type="radio"
                        name="shippingMethod"
                        value="standard"
                        checked={formData.shippingMethod === 'standard'}
                        onChange={handleInputChange}
                        className="w-4 h-4 text-blue-600"
                      />
                      <div>
                        <p className="text-white font-semibold">Standard Shipping</p>
                        <p className="text-blue-300 text-sm">5-7 business days</p>
                      </div>
                    </div>
                    <span className="text-green-400 font-semibold">FREE</span>
                  </label>

                  <label className="flex items-center justify-between p-4 bg-white/5 rounded-xl cursor-pointer hover:bg-white/10 transition-all border border-white/10">
                    <div className="flex items-center gap-3">
                      <input
                        type="radio"
                        name="shippingMethod"
                        value="express"
                        checked={formData.shippingMethod === 'express'}
                        onChange={handleInputChange}
                        className="w-4 h-4 text-blue-600"
                      />
                      <div>
                        <p className="text-white font-semibold">Express Shipping</p>
                        <p className="text-blue-300 text-sm">2-3 business days</p>
                      </div>
                    </div>
                    <span className="text-blue-400 font-semibold">$49.99</span>
                  </label>

                  <label className="flex items-center justify-between p-4 bg-white/5 rounded-xl cursor-pointer hover:bg-white/10 transition-all border border-white/10">
                    <div className="flex items-center gap-3">
                      <input
                        type="radio"
                        name="shippingMethod"
                        value="overnight"
                        checked={formData.shippingMethod === 'overnight'}
                        onChange={handleInputChange}
                        className="w-4 h-4 text-blue-600"
                      />
                      <div>
                        <p className="text-white font-semibold">Overnight Shipping</p>
                        <p className="text-blue-300 text-sm">Next business day</p>
                      </div>
                    </div>
                    <span className="text-purple-400 font-semibold">$99.99</span>
                  </label>
                </div>
              </div>

              <div className="border-t border-white/20 mt-6 pt-6">
                <div className="space-y-3">
                  <div className="flex justify-between text-blue-100">
                    <span>Subtotal</span>
                    <span className="font-semibold">${totals.subtotal}</span>
                  </div>
                  <div className="flex justify-between text-blue-100">
                    <span>Shipping</span>
                    <span className="font-semibold">${totals.shipping}</span>
                  </div>
                  <div className="flex justify-between text-blue-100">
                    <span>Tax (8%)</span>
                    <span className="font-semibold">${totals.tax}</span>
                  </div>
                  <div className="border-t border-white/20 pt-3 flex justify-between text-white text-xl font-bold">
                    <span>Total</span>
                    <span className="text-blue-400">${totals.total}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div className="space-y-6">
            <form onSubmit={handleSubmit} className="space-y-6">
              <div className="bg-white/10 backdrop-blur-lg rounded-3xl p-8 border border-white/20">
                <h2 className="text-2xl font-bold text-white mb-6">Shipping Information</h2>

                <div className="space-y-4">
                  <div>
                    <label className="block text-blue-200 text-sm font-semibold mb-2">Full Name</label>
                    <input
                      type="text"
                      name="fullName"
                      required
                      value={formData.fullName}
                      onChange={handleInputChange}
                      className="w-full px-4 py-3 bg-white/5 border border-white/20 rounded-xl text-white placeholder-blue-300/50 focus:outline-none focus:border-blue-400 transition-all"
                      placeholder="John Doe"
                    />
                  </div>

                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                      <label className="block text-blue-200 text-sm font-semibold mb-2">Email</label>
                      <input
                        type="email"
                        name="email"
                        required
                        value={formData.email}
                        onChange={handleInputChange}
                        className="w-full px-4 py-3 bg-white/5 border border-white/20 rounded-xl text-white placeholder-blue-300/50 focus:outline-none focus:border-blue-400 transition-all"
                        placeholder="john@example.com"
                      />
                    </div>
                    <div>
                      <label className="block text-blue-200 text-sm font-semibold mb-2">Phone</label>
                      <input
                        type="tel"
                        name="phone"
                        required
                        value={formData.phone}
                        onChange={handleInputChange}
                        className="w-full px-4 py-3 bg-white/5 border border-white/20 rounded-xl text-white placeholder-blue-300/50 focus:outline-none focus:border-blue-400 transition-all"
                        placeholder="+1 (555) 123-4567"
                      />
                    </div>
                  </div>

                  <div>
                    <label className="block text-blue-200 text-sm font-semibold mb-2">Address</label>
                    <input
                      type="text"
                      name="address"
                      required
                      value={formData.address}
                      onChange={handleInputChange}
                      className="w-full px-4 py-3 bg-white/5 border border-white/20 rounded-xl text-white placeholder-blue-300/50 focus:outline-none focus:border-blue-400 transition-all"
                      placeholder="123 Main Street"
                    />
                  </div>

                  <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
                    <div>
                      <label className="block text-blue-200 text-sm font-semibold mb-2">City</label>
                      <input
                        type="text"
                        name="city"
                        required
                        value={formData.city}
                        onChange={handleInputChange}
                        className="w-full px-4 py-3 bg-white/5 border border-white/20 rounded-xl text-white placeholder-blue-300/50 focus:outline-none focus:border-blue-400 transition-all"
                        placeholder="New York"
                      />
                    </div>
                    <div>
                      <label className="block text-blue-200 text-sm font-semibold mb-2">State</label>
                      <input
                        type="text"
                        name="state"
                        required
                        value={formData.state}
                        onChange={handleInputChange}
                        className="w-full px-4 py-3 bg-white/5 border border-white/20 rounded-xl text-white placeholder-blue-300/50 focus:outline-none focus:border-blue-400 transition-all"
                        placeholder="NY"
                      />
                    </div>
                    <div>
                      <label className="block text-blue-200 text-sm font-semibold mb-2">ZIP Code</label>
                      <input
                        type="text"
                        name="zipCode"
                        required
                        value={formData.zipCode}
                        onChange={handleInputChange}
                        className="w-full px-4 py-3 bg-white/5 border border-white/20 rounded-xl text-white placeholder-blue-300/50 focus:outline-none focus:border-blue-400 transition-all"
                        placeholder="10001"
                      />
                    </div>
                  </div>
                </div>
              </div>

              <div className="bg-gradient-to-br from-blue-600/20 to-purple-600/20 backdrop-blur-lg rounded-3xl p-8 border border-blue-400/30">
                <h2 className="text-2xl font-bold text-white mb-6 flex items-center gap-3">
                  <CreditCard className="w-6 h-6 text-blue-400" />
                  Payment Information
                </h2>

                <div className="space-y-6">
                  <div className="relative">
                    <div className="absolute inset-0 bg-gradient-to-r from-blue-500 via-purple-500 to-pink-500 rounded-2xl opacity-80"></div>
                    <div className="relative bg-gradient-to-br from-slate-800 to-slate-900 rounded-2xl p-6 shadow-2xl">
                      <div className="flex justify-between items-start mb-8">
                        <div className="w-12 h-12 bg-gradient-to-r from-yellow-400 to-yellow-600 rounded-lg flex items-center justify-center">
                          <div className="w-8 h-8 bg-gradient-to-br from-yellow-300 to-orange-400 rounded"></div>
                        </div>
                        <div className="text-right">
                          <p className="text-xs text-blue-300 mb-1">CREDIT CARD</p>
                          <Lock className="w-5 h-5 text-blue-400 ml-auto" />
                        </div>
                      </div>

                      <div className="mb-6">
                        <p className="text-2xl text-white font-mono tracking-wider mb-1">
                          {formData.cardNumber || '•••• •••• •••• ••••'}
                        </p>
                      </div>

                      <div className="flex justify-between items-end">
                        <div>
                          <p className="text-xs text-blue-300 mb-1">CARD HOLDER</p>
                          <p className="text-white font-semibold uppercase">
                            {formData.cardName || 'YOUR NAME'}
                          </p>
                        </div>
                        <div className="text-right">
                          <p className="text-xs text-blue-300 mb-1">EXPIRES</p>
                          <p className="text-white font-semibold">
                            {formData.expiryDate || 'MM/YY'}
                          </p>
                        </div>
                      </div>
                    </div>
                  </div>

                  <div className="space-y-4">
                    <div>
                      <label className="block text-blue-200 text-sm font-semibold mb-2">Card Number</label>
                      <input
                        type="text"
                        name="cardNumber"
                        required
                        maxLength={19}
                        value={formData.cardNumber}
                        onChange={handleInputChange}
                        className="w-full px-4 py-3 bg-white/5 border border-white/20 rounded-xl text-white placeholder-blue-300/50 focus:outline-none focus:border-blue-400 transition-all font-mono"
                        placeholder="1234 5678 9012 3456"
                      />
                    </div>

                    <div>
                      <label className="block text-blue-200 text-sm font-semibold mb-2">Cardholder Name</label>
                      <input
                        type="text"
                        name="cardName"
                        required
                        value={formData.cardName}
                        onChange={handleInputChange}
                        className="w-full px-4 py-3 bg-white/5 border border-white/20 rounded-xl text-white placeholder-blue-300/50 focus:outline-none focus:border-blue-400 transition-all uppercase"
                        placeholder="JOHN DOE"
                      />
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                      <div>
                        <label className="block text-blue-200 text-sm font-semibold mb-2">Expiry Date</label>
                        <input
                          type="text"
                          name="expiryDate"
                          required
                          maxLength={5}
                          value={formData.expiryDate}
                          onChange={handleInputChange}
                          className="w-full px-4 py-3 bg-white/5 border border-white/20 rounded-xl text-white placeholder-blue-300/50 focus:outline-none focus:border-blue-400 transition-all font-mono"
                          placeholder="MM/YY"
                        />
                      </div>
                      <div>
                        <label className="block text-blue-200 text-sm font-semibold mb-2">CVV</label>
                        <input
                          type="text"
                          name="cvv"
                          required
                          maxLength={4}
                          value={formData.cvv}
                          onChange={handleInputChange}
                          className="w-full px-4 py-3 bg-white/5 border border-white/20 rounded-xl text-white placeholder-blue-300/50 focus:outline-none focus:border-blue-400 transition-all font-mono"
                          placeholder="123"
                        />
                      </div>
                    </div>
                  </div>

                  <div className="flex items-center gap-3 p-4 bg-green-500/10 border border-green-500/30 rounded-xl">
                    <Lock className="w-5 h-5 text-green-400" />
                    <p className="text-sm text-green-300">
                      Your payment information is encrypted and secure
                    </p>
                  </div>
                </div>
              </div>

              <button
                type="submit"
                className="w-full py-4 bg-gradient-to-r from-blue-600 to-purple-600 text-white text-lg font-bold rounded-full hover:from-blue-700 hover:to-purple-700 transition-all duration-300 transform hover:scale-105 shadow-2xl"
              >
                Complete Order - ${totals.total}
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Order;