import React, { useState } from 'react';
import { Eye, EyeOff, AlertCircle } from 'lucide-react';

// UPDATED: API Configuration for AWS Backend
const API_BASE_URL = import.meta.env.VITE_API_URL || 
                     process.env.REACT_APP_API_URL || 
                     'http://allytic-labs-prod.eba-pukad2pd.us-east-1.elasticbeanstalk.com';

console.log('Login - Using API Base URL:', API_BASE_URL);

const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [step, setStep] = useState(1);
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);

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

  const handlePasswordSubmit = async (e) => {
    e.preventDefault();
    
    if (!password.trim()) {
      setErrors({ password: 'Password is required' });
      return;
    }
    
    if (password.length < 8) {
      setErrors({ password: 'Password must be at least 8 characters' });
      return;
    }
    
    setErrors({});
    setLoading(true);

    try {
      console.log('Sending login request to:', `${API_BASE_URL}/api/v1/auth/login`);
      console.log('Login payload:', { username: email, password: '***' });

      const response = await fetch(`${API_BASE_URL}/api/v1/auth/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          username: email, // Backend expects 'username' field
          password: password
        }),
        credentials: 'include'
      });

      console.log('Response status:', response.status);
      console.log('Response headers:', Object.fromEntries(response.headers.entries()));

      const contentType = response.headers.get('content-type');
      
      // Check if response is JSON
      if (!contentType || !contentType.includes('application/json')) {
        const text = await response.text();
        console.error('Non-JSON response received:', text);
        throw new Error('Server returned invalid response. Please try again.');
      }

      const data = await response.json();
      console.log('Login response:', data);

      if (response.ok && data.status === 'success') {
        // UPDATED: Store all authentication data properly
        if (data.accessToken) {
          localStorage.setItem('accessToken', data.accessToken);
          console.log('Access token stored');
        }
        if (data.refreshToken) {
          localStorage.setItem('refreshToken', data.refreshToken);
          console.log('Refresh token stored');
        }
        
        // Store user identification data
        if (data.userId) {
          localStorage.setItem('userId', data.userId);
          console.log('User ID stored:', data.userId);
        }
        if (data.email || email) {
          localStorage.setItem('userEmail', data.email || email);
          console.log('User email stored');
        }
        if (data.username || email) {
          localStorage.setItem('username', data.username || email);
          console.log('Username stored');
        }
        
        // Store complete user object for other components
        localStorage.setItem('user', JSON.stringify({
          userId: data.userId,
          email: data.email || email,
          username: data.username || email,
          roles: data.roles || []
        }));

        console.log('Login successful. Stored data:', {
          hasToken: !!data.accessToken,
          hasUserId: !!data.userId,
          email: data.email || email
        });
        
        // Redirect to return URL or home
        const returnTo = localStorage.getItem('returnTo') || '/';
        localStorage.removeItem('returnTo');
        
        console.log('Redirecting to:', returnTo);
        window.location.href = returnTo;
      } else {
        // Handle error response
        console.error('Login failed:', data);
        setErrors({ 
          password: data.message || data.error || 'Invalid email or password' 
        });
      }
    } catch (error) {
      console.error('Login error:', error);
      
      if (error.message.includes('Failed to fetch') || error.message.includes('Network')) {
        setErrors({ 
          password: 'Unable to connect to server. Please check your internet connection.' 
        });
      } else {
        setErrors({ 
          password: error.message || 'Login failed. Please try again.' 
        });
      }
    } finally {
      setLoading(false);
    }
  };

  const handleGoogleLogin = () => {
    // Store return URL before OAuth redirect
    const returnTo = localStorage.getItem('returnTo') || '/';
    
    // Redirect to backend OAuth2 endpoint
    const redirectUri = encodeURIComponent(`${window.location.origin}/oauth2/redirect`);
    window.location.href = `${API_BASE_URL}/oauth2/authorize/google?redirect_uri=${redirectUri}&state=${encodeURIComponent(returnTo)}`;
  };

  const handleCreateAccount = () => {
    window.location.href = '/signup';
  };

  const handleBack = () => {
    setStep(1);
    setPassword('');
    setErrors({});
  };

  const handleForgotPassword = () => {
    alert('Password reset feature will be available soon. Please contact support if you need immediate assistance.');
  };

  return (
    <div className="min-h-screen bg-white flex flex-col">
      {/* Header */}
      <header className="py-6 px-8">
        <div className="max-w-md mx-auto">
          <div className="text-2xl font-bold text-gray-900">Allytic Labs</div>
        </div>
      </header>

      {/* Main Content */}
      <main className="flex-1 flex items-start justify-center pt-20 px-4">
        <div className="w-full max-w-md">
          <h1 className="text-4xl font-medium mb-8 text-center">Sign In</h1>

          {step === 1 ? (
            // Step 1: Email Input
            <div>
              <div className="mb-6">
                <label htmlFor="email" className="flex items-center text-sm font-medium text-gray-700 mb-2">
                  Email
                  {errors.email && (
                    <AlertCircle className="w-4 h-4 text-red-500 ml-1" />
                  )}
                </label>
                <input
                  type="email"
                  id="email"
                  value={email}
                  onChange={(e) => {
                    setEmail(e.target.value);
                    if (errors.email) setErrors({});
                  }}
                  onKeyPress={(e) => {
                    if (e.key === 'Enter') handleEmailSubmit(e);
                  }}
                  className={`w-full px-4 py-3 border ${
                    errors.email ? 'border-red-500' : 'border-gray-300'
                  } rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all`}
                  placeholder="Enter your email"
                  autoComplete="email"
                />
                {errors.email && (
                  <p className="text-red-500 text-xs mt-1 flex items-center">
                    {errors.email}
                  </p>
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
                  onClick={handleForgotPassword}
                  className="text-sm text-gray-600 hover:text-gray-900 hover:underline transition-colors"
                >
                  Trouble Signing In?
                </button>
              </div>

              {/* Divider */}
              <div className="relative my-8">
                <div className="absolute inset-0 flex items-center">
                  <div className="w-full border-t border-gray-300"></div>
                </div>
                <div className="relative flex justify-center text-sm">
                  <span className="px-4 bg-white text-gray-500">Or</span>
                </div>
              </div>

              {/* Google OAuth Button */}
              <button
                onClick={handleGoogleLogin}
                className="w-full bg-white hover:bg-gray-50 text-gray-800 font-medium py-3 rounded-md border border-gray-300 transition-colors mb-4 flex items-center justify-center shadow-sm"
              >
                <svg className="w-5 h-5 mr-2" viewBox="0 0 24 24">
                  <path 
                    fill="#4285F4" 
                    d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
                  />
                  <path 
                    fill="#34A853" 
                    d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
                  />
                  <path 
                    fill="#FBBC05" 
                    d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"
                  />
                  <path 
                    fill="#EA4335" 
                    d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"
                  />
                </svg>
                Continue with Google
              </button>

              {/* Create Account Button */}
              <button
                onClick={handleCreateAccount}
                className="w-full bg-gray-100 hover:bg-gray-200 text-gray-800 font-medium py-3 rounded-md transition-colors"
              >
                Create Account
              </button>
            </div>
          ) : (
            // Step 2: Password Input
            <div>
              <div className="mb-4">
                <button
                  onClick={handleBack}
                  className="text-sm text-blue-600 hover:text-blue-700 hover:underline flex items-center transition-colors"
                >
                  <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
                  </svg>
                  Back
                </button>
              </div>
              
              <div className="mb-6">
                <p className="text-sm text-gray-600 mb-4">
                  Signing in as: <span className="font-medium text-gray-900">{email}</span>
                </p>
              </div>

              <div className="mb-6">
                <label htmlFor="password" className="flex items-center text-sm font-medium text-gray-700 mb-2">
                  Password
                  {errors.password && (
                    <AlertCircle className="w-4 h-4 text-red-500 ml-1" />
                  )}
                </label>
                <div className="relative">
                  <input
                    type={showPassword ? 'text' : 'password'}
                    id="password"
                    value={password}
                    onChange={(e) => {
                      setPassword(e.target.value);
                      if (errors.password) setErrors({});
                    }}
                    onKeyPress={(e) => {
                      if (e.key === 'Enter') handlePasswordSubmit(e);
                    }}
                    className={`w-full px-4 py-3 pr-12 border ${
                      errors.password ? 'border-red-500' : 'border-gray-300'
                    } rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all`}
                    placeholder="Enter your password"
                    autoFocus
                    disabled={loading}
                    autoComplete="current-password"
                  />
                  <button
                    type="button"
                    onClick={() => setShowPassword(!showPassword)}
                    className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-700 transition-colors"
                    disabled={loading}
                  >
                    {showPassword ? (
                      <EyeOff className="w-5 h-5" />
                    ) : (
                      <Eye className="w-5 h-5" />
                    )}
                  </button>
                </div>
                {errors.password && (
                  <p className="text-red-500 text-xs mt-1 flex items-center">
                    {errors.password}
                  </p>
                )}
              </div>

              <button
                onClick={handlePasswordSubmit}
                disabled={loading}
                className="w-full bg-blue-500 hover:bg-blue-600 text-white font-medium py-3 rounded-md transition-colors mb-4 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center"
              >
                {loading ? (
                  <>
                    <svg className="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                      <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                      <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                    </svg>
                    Signing In...
                  </>
                ) : (
                  'Sign In'
                )}
              </button>

              <div className="text-center">
                <button
                  onClick={handleForgotPassword}
                  className="text-sm text-gray-600 hover:text-gray-900 hover:underline transition-colors"
                  disabled={loading}
                >
                  Forgot Password?
                </button>
              </div>
            </div>
          )}
        </div>
      </main>

      {/* Footer */}
      <footer className="py-6 px-8 border-t border-gray-200">
        <div className="max-w-md mx-auto flex justify-center space-x-6 text-sm text-gray-600">
          <span>Allytic Labs © 2025</span>
          <button 
            onClick={() => window.location.href = '/privacy'} 
            className="hover:text-gray-900 transition-colors"
          >
            Privacy & Legal
          </button>
          <button 
            onClick={() => window.location.href = '/contact'} 
            className="hover:text-gray-900 transition-colors"
          >
            Contact
          </button>
        </div>
      </footer>
    </div>
  );
};

export default Login;
