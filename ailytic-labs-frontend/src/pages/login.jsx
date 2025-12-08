import React, { useState } from 'react';

const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [step, setStep] = useState(1);
  const [errors, setErrors] = useState({});

  const validateEmail = (email) => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  };

  const handleEmailSubmit = (e) => {
    e.preventDefault();
    
    if (!email.trim()) {
      setErrors({ email: 'Email is required' });
      return;
    }
    
    if (!validateEmail(email)) {
      setErrors({ email: 'Please enter a valid email address' });
      return;
    }
    
    setErrors({});
    setStep(2);
  };

  const handlePasswordSubmit = (e) => {
    e.preventDefault();
    
    if (!password.trim()) {
      setErrors({ password: 'Password is required' });
      return;
    }
    
    if (password.length < 6) {
      setErrors({ password: 'Password must be at least 6 characters' });
      return;
    }
    
    setErrors({});
    
    console.log('Login with:', email, password);
    
    // Navigate to home page after successful login
    alert('Login successful! Redirecting to home page...');
    window.location.href = '/';
  };

  const handleCreateAccount = () => {
    window.location.href = '/signup';
  };

  const handleBack = () => {
    setStep(1);
    setPassword('');
    setErrors({});
  };

  return (
    <div className="min-h-screen bg-white flex flex-col">
      <header className="py-6 px-8">
        <div className="max-w-md mx-auto">
          <div className="text-2xl font-bold text-gray-900">Allytic Labs</div>
        </div>
      </header>

      <main className="flex-1 flex items-start justify-center pt-20 px-4">
        <div className="w-full max-w-md">
          <h1 className="text-4xl font-medium mb-8 text-center">Sign In</h1>

          {step === 1 ? (
            <div>
              <div className="mb-6">
                <label className="block text-sm font-medium mb-2">
                  Email
                </label>
                <input
                  type="email"
                  value={email}
                  onChange={(e) => {
                    setEmail(e.target.value);
                    if (errors.email) {
                      setErrors({});
                    }
                  }}
                  onKeyPress={(e) => {
                    if (e.key === 'Enter') {
                      handleEmailSubmit(e);
                    }
                  }}
                  className={`w-full px-4 py-3 border ${
                    errors.email ? 'border-red-500' : 'border-gray-300'
                  } rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent`}
                  placeholder="Enter your email"
                />
                {errors.email && (
                  <p className="text-red-500 text-xs mt-1">{errors.email}</p>
                )}
              </div>

              <button
                onClick={handleEmailSubmit}
                className="w-full bg-blue-500 hover:bg-blue-600 text-white font-medium py-3 rounded-md transition-colors mb-4"
              >
                Next
              </button>

              <div className="text-center">
                <button
                  onClick={() => alert('Password reset feature coming soon')}
                  className="text-sm text-gray-600 hover:underline"
                >
                  Trouble Signing In?
                </button>
              </div>

              <div className="relative my-8">
                <div className="absolute inset-0 flex items-center">
                  <div className="w-full border-t border-gray-300"></div>
                </div>
                <div className="relative flex justify-center text-sm">
                  <span className="px-4 bg-white text-gray-500">Or</span>
                </div>
              </div>

              <button
                onClick={handleCreateAccount}
                className="w-full bg-gray-100 hover:bg-gray-200 text-gray-800 font-medium py-3 rounded-md transition-colors"
              >
                Create Account
              </button>
            </div>
          ) : (
            <div>
              <div className="mb-4">
                <button
                  onClick={handleBack}
                  className="text-sm text-blue-600 hover:underline flex items-center"
                >
                  <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
                  </svg>
                  Back
                </button>
              </div>
              
              <div className="mb-6">
                <p className="text-sm text-gray-600 mb-4">
                  Signing in as: <span className="font-medium">{email}</span>
                </p>
              </div>

              <div className="mb-6">
                <label className="block text-sm font-medium mb-2">
                  Password
                </label>
                <input
                  type="password"
                  value={password}
                  onChange={(e) => {
                    setPassword(e.target.value);
                    if (errors.password) {
                      setErrors({});
                    }
                  }}
                  onKeyPress={(e) => {
                    if (e.key === 'Enter') {
                      handlePasswordSubmit(e);
                    }
                  }}
                  className={`w-full px-4 py-3 border ${
                    errors.password ? 'border-red-500' : 'border-gray-300'
                  } rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent`}
                  placeholder="Enter your password"
                  autoFocus
                />
                {errors.password && (
                  <p className="text-red-500 text-xs mt-1">{errors.password}</p>
                )}
              </div>

              <button
                onClick={handlePasswordSubmit}
                className="w-full bg-blue-500 hover:bg-blue-600 text-white font-medium py-3 rounded-md transition-colors mb-4"
              >
                Sign In
              </button>

              <div className="text-center">
                <button
                  onClick={() => alert('Password reset feature coming soon')}
                  className="text-sm text-gray-600 hover:underline"
                >
                  Forgot Password?
                </button>
              </div>
            </div>
          )}
        </div>
      </main>

      <footer className="py-6 px-8 border-t border-gray-200">
        <div className="max-w-md mx-auto flex justify-center space-x-6 text-sm text-gray-600">
          <span>Allytic Labs © 2025</span>
          <button onClick={() => alert('Privacy page coming soon')} className="hover:text-gray-900 transition-colors">
            Privacy & Legal
          </button>
          <button onClick={() => alert('Contact page coming soon')} className="hover:text-gray-900 transition-colors">
            Contact
          </button>
        </div>
      </footer>
    </div>
  );
};

export default Login;