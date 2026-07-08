import React, { useEffect, useState } from 'react';
import { AlertCircle, Loader } from 'lucide-react';

const TOKEN_KEYS = ['accessToken', 'refreshToken', 'token', 'jwt'];
const USER_KEYS = ['userId', 'email', 'username'];

const getOAuthParams = () => {
  const queryParams = new URLSearchParams(window.location.search);
  const hashParams = new URLSearchParams(window.location.hash.replace(/^#/, ''));

  return {
    get: (key) => queryParams.get(key) || hashParams.get(key),
  };
};

const storeOAuthSession = (params) => {
  const accessToken = params.get('accessToken') || params.get('token') || params.get('jwt');
  const refreshToken = params.get('refreshToken');

  if (!accessToken) {
    return false;
  }

  localStorage.setItem('accessToken', accessToken);

  if (refreshToken) {
    localStorage.setItem('refreshToken', refreshToken);
  }

  USER_KEYS.forEach((key) => {
    const value = params.get(key);
    if (value) {
      localStorage.setItem(key === 'email' ? 'userEmail' : key, value);
    }
  });

  const user = {
    userId: params.get('userId') || localStorage.getItem('userId'),
    email: params.get('email') || localStorage.getItem('userEmail'),
    username: params.get('username') || localStorage.getItem('username'),
  };

  localStorage.setItem('user', JSON.stringify(user));

  return true;
};

function OAuthRedirect() {
  const [error, setError] = useState('');

  useEffect(() => {
    const params = getOAuthParams();
    const oauthError = params.get('error') || params.get('message');

    if (oauthError) {
      setError(oauthError);
      return;
    }

    const hasKnownTokenParam = TOKEN_KEYS.some((key) => params.get(key));

    if (!hasKnownTokenParam || !storeOAuthSession(params)) {
      setError('Google sign in completed, but no login token was returned.');
      return;
    }

    const returnTo = params.get('state') || localStorage.getItem('returnTo') || '/';
    localStorage.removeItem('returnTo');
    window.location.replace(returnTo);
  }, []);

  if (error) {
    return (
      <main className="flex min-h-screen items-center justify-center bg-white px-4">
        <section className="w-full max-w-md rounded-lg border border-red-200 bg-red-50 p-6 text-red-900">
          <div className="flex items-start gap-3">
            <AlertCircle className="mt-0.5 h-5 w-5 shrink-0" />
            <div>
              <h1 className="text-lg font-semibold">Google sign in failed</h1>
              <p className="mt-2 text-sm">{error}</p>
              <button
                type="button"
                onClick={() => window.location.replace('/login')}
                className="mt-5 rounded-md bg-red-700 px-4 py-2 text-sm font-semibold text-white hover:bg-red-800"
              >
                Back to sign in
              </button>
            </div>
          </div>
        </section>
      </main>
    );
  }

  return (
    <main className="flex min-h-screen items-center justify-center bg-white px-4">
      <div className="flex items-center gap-3 text-gray-800">
        <Loader className="h-5 w-5 animate-spin" />
        <span className="font-medium">Completing Google sign in...</span>
      </div>
    </main>
  );
}

export default OAuthRedirect;
