import React, { useState } from 'react';

const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [step, setStep] = useState(1); // 1 for email, 2 for password

  const handleEmailSubmit = (e) => {
    e.preventDefault();
    if (email) {
      setStep(2);
    }
  };

  const handlePasswordSubmit = (e) => {
    e.preventDefault();
    // Handle login logic here
    console.log('Login with:', email, password);
  };

  return (
    <div className="min-h-screen bg-white flex flex-col">
      {/* Header */}
      <header className="p-6">
        <div className="flex items-center justify-between max-w-6xl mx-auto">
          <svg className="w-24 h-8" viewBox="0 0 342 35" fill="none">
            <path d="M0 0.5h27.5v27.5H0z" fill="#E82127"/>
            <path d="M50 14.5h15v2.5H50v6h17v2.5H47V7h19.5v2.5H50v5z" fill="#000"/>
            <path d="M85 25.5c-2.8 0-5-0.8-6.5-2.3s-2.3-3.7-2.3-6.5c0-1.8 0.3-3.4 1-4.7 0.7-1.3 1.7-2.3 3-3s2.8-1.1 4.5-1.1c2.5 0 4.5 0.8 5.9 2.3 1.4 1.5 2.1 3.6 2.1 6.2v1.1H79.2c0.1 1.9 0.7 3.4 1.7 4.4s2.4 1.5 4.1 1.5c1.2 0 2.2-0.2 3.2-0.5 0.9-0.3 1.8-0.8 2.6-1.4v3c-0.8 0.6-1.6 1-2.5 1.3-0.9 0.3-2 0.4-3.3 0.4zm-0.5-15.4c-1.3 0-2.3 0.4-3.1 1.3-0.8 0.9-1.3 2.1-1.4 3.6h8.5c0-1.5-0.4-2.7-1.1-3.6-0.8-0.9-1.8-1.3-3-1.3z" fill="#000"/>
            <path d="M104 25.5c-1.5 0-2.8-0.3-3.9-0.8s-1.9-1.3-2.4-2.2c-0.6-0.9-0.9-2-0.9-3.2 0-1.9 0.7-3.4 2-4.4s3.3-1.5 5.8-1.5h3.9v-1.3c0-1.2-0.3-2.1-1-2.7-0.6-0.6-1.6-0.9-2.9-0.9-0.9 0-1.8 0.1-2.7 0.4-0.9 0.3-1.7 0.6-2.5 1.1l-1.1-2.3c0.9-0.5 1.9-1 3-1.3 1.1-0.3 2.2-0.5 3.3-0.5 2.2 0 3.9 0.5 5 1.5s1.7 2.5 1.7 4.5V25h-2.4l-0.4-2h-0.1c-0.6 0.7-1.4 1.3-2.3 1.7-0.9 0.5-2 0.8-3.1 0.8zm0.6-2.4c1.5 0 2.6-0.4 3.5-1.2 0.9-0.8 1.3-1.9 1.3-3.3v-1.7h-3.7c-1.6 0-2.8 0.3-3.6 0.8-0.8 0.6-1.2 1.4-1.2 2.5 0 0.9 0.3 1.6 0.9 2.1 0.6 0.6 1.5 0.8 2.8 0.8z" fill="#000"/>
            <path d="M122 25.5c-1.9 0-3.4-0.4-4.5-1.3-1.1-0.9-1.6-2.1-1.6-3.7 0-1.7 0.6-3 1.9-3.9 1.2-0.9 3-1.3 5.3-1.3h3.4v-1.2c0-1.1-0.3-2-0.9-2.5-0.6-0.6-1.5-0.8-2.7-0.8-0.9 0-1.7 0.1-2.5 0.4-0.8 0.2-1.6 0.6-2.3 1l-1.1-2.3c0.9-0.5 1.8-0.9 2.8-1.2 1-0.3 2.1-0.4 3.1-0.4 2.1 0 3.7 0.5 4.7 1.4 1 0.9 1.6 2.4 1.6 4.3V25h-2.4l-0.3-1.8h-0.1c-0.6 0.7-1.3 1.2-2.2 1.6-0.8 0.4-1.8 0.7-2.9 0.7zm0.6-2.3c1.4 0 2.5-0.4 3.3-1.1 0.8-0.7 1.2-1.8 1.2-3.1v-1.6h-3.3c-1.5 0-2.6 0.3-3.3 0.8-0.7 0.5-1.1 1.2-1.1 2.2 0 0.8 0.3 1.5 0.8 1.9 0.6 0.5 1.4 0.9 2.4 0.9z" fill="#000"/>
          </svg>
          <button className="flex items-center space-x-2 text-sm">
            <span className="w-6 h-6 rounded-full bg-gray-200"></span>
            <span>en-US</span>
          </button>
        </div>
      </header>

      {/* Main Content */}
      <main className="flex-1 flex items-start justify-center pt-20">
        <div className="w-full max-w-md px-6">
          <h1 className="text-4xl font-medium mb-8 text-center">Sign In</h1>

          {step === 1 ? (
            <div>
              <div className="mb-6">
                <label className="block text-sm font-medium mb-2 flex items-center">
                  Email
                  <svg className="w-4 h-4 ml-1 text-gray-400" fill="currentColor" viewBox="0 0 20 20">
                    <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clipRule="evenodd" />
                  </svg>
                </label>
                <input
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  className="w-full px-4 py-3 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  required
                />
              </div>

              <button
                onClick={handleEmailSubmit}
                className="w-full bg-blue-400 hover:bg-blue-500 text-white font-medium py-3 rounded-md transition-colors mb-4"
              >
                Next
              </button>

              <div className="text-center">
                <a href="#" className="text-sm text-gray-600 hover:underline">
                  Trouble Signing In?
                </a>
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
                onClick={() => console.log('Create account')}
                className="w-full bg-gray-100 hover:bg-gray-200 text-gray-800 font-medium py-3 rounded-md transition-colors"
              >
                Create Account
              </button>
            </div>
          ) : (
            <div>
              <div className="mb-2">
                <button
                  onClick={() => setStep(1)}
                  className="text-sm text-blue-600 hover:underline mb-2"
                >
                  ← Back
                </button>
              </div>
              
              <div className="mb-2">
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
                  onChange={(e) => setPassword(e.target.value)}
                  className="w-full px-4 py-3 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  required
                  autoFocus
                />
              </div>

              <button
                onClick={handlePasswordSubmit}
                className="w-full bg-blue-400 hover:bg-blue-500 text-white font-medium py-3 rounded-md transition-colors mb-4"
              >
                Sign In
              </button>

              <div className="text-center">
                <a href="#" className="text-sm text-gray-600 hover:underline">
                  Forgot Password?
                </a>
              </div>
            </div>
          )}
        </div>
      </main>

      {/* Footer */}
      <footer className="p-6 text-center">
        <div className="flex justify-center space-x-6 text-sm text-gray-600">
          <span>Allytic Labs © 2025</span>
          <a href="#" className="hover:underline">Privacy & Legal</a>
          <a href="#" className="hover:underline">Contact</a>
        </div>
      </footer>
    </div>
  );
};

export default Login;