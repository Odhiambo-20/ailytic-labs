const DEFAULT_API_ORIGIN = 'https://allytic-labs-prod.eba-pukad2pd.us-east-1.elasticbeanstalk.com';

const trimTrailingSlashes = (value) => value.replace(/\/+$/, '');

const normalizeApiOrigin = (value) => {
  const rawValue = trimTrailingSlashes(value || DEFAULT_API_ORIGIN);
  const withoutApiPath = rawValue.replace(/\/api\/v1$/, '').replace(/\/api$/, '');

  if (withoutApiPath.startsWith('http://')) {
    return withoutApiPath.replace(/^http:\/\//, 'https://');
  }

  return withoutApiPath;
};

export const API_ORIGIN = normalizeApiOrigin(
  import.meta.env.VITE_API_ORIGIN || import.meta.env.VITE_API_URL
);

export const API_V1_BASE_URL = `${API_ORIGIN}/api/v1`;

export const buildOAuthAuthorizeUrl = (returnTo = '/') => {
  const redirectUri = `${window.location.origin}/oauth2/redirect`;
  const params = new URLSearchParams({
    redirect_uri: redirectUri,
    state: returnTo,
  });

  return `${API_ORIGIN}/oauth2/authorize/google?${params.toString()}`;
};
