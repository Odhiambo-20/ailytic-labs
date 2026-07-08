import React from 'react';
import { Link } from 'react-router-dom';

const BrandLogo = ({ className = '', colorClass = 'text-white', compact = false }) => (
  <Link
    to="/"
    className={`group flex shrink-0 items-center ${className}`}
    aria-label="Bella home"
  >
    <span className={`inline-flex items-center transition-colors group-hover:text-blue-100 ${colorClass}`}>
      <svg
        className={compact ? 'h-8 w-28' : 'h-14 w-[12.5rem]'}
        viewBox="0 0 360 116"
        role="img"
        aria-labelledby="bellaLogoTitle"
      >
        <title id="bellaLogoTitle">Bella</title>
        <path
          d="M16 12H98C105.2 12 111 17.8 111 25V91C111 98.2 105.2 104 98 104H16V12Z"
          fill="none"
          stroke="currentColor"
          strokeWidth="9"
          strokeLinejoin="round"
        />
        <path
          d="M88 34C78.4 25.8 59.8 23.7 44 31.9C26.9 40.8 21.4 58.6 31.8 70.6C40.7 80.8 59.2 81.2 71.5 71.7C83.9 62.1 81.3 47.2 67 43.4C56.7 40.7 44.9 45.8 38.3 56.4"
          fill="none"
          stroke="currentColor"
          strokeWidth="11"
          strokeLinecap="round"
          strokeLinejoin="round"
        />
        <path
          d="M76.5 36C97.4 48.6 100.6 72.9 84.6 90.5C70.3 106.2 43.8 104.3 25 87.8"
          fill="none"
          stroke="currentColor"
          strokeWidth="9"
          strokeLinecap="round"
          strokeLinejoin="round"
        />
        <path
          d="M130 24V84"
          stroke="currentColor"
          strokeWidth="10"
          strokeLinecap="round"
        />
        <path
          d="M149 24V84"
          stroke="currentColor"
          strokeWidth="10"
          strokeLinecap="round"
        />
        <path
          d="M168 24V84"
          stroke="currentColor"
          strokeWidth="10"
          strokeLinecap="round"
        />
        <path
          d="M194 24V84H238C253.5 84 266 71.6 266 54C266 36.4 253.5 24 238 24H194Z"
          fill="none"
          stroke="currentColor"
          strokeWidth="10"
          strokeLinecap="round"
          strokeLinejoin="round"
        />
        <text
          x="195"
          y="108"
          fill="currentColor"
          fontFamily="Inter, ui-sans-serif, system-ui, sans-serif"
          fontSize="24"
          fontWeight="300"
          letterSpacing="15"
        >
          BELLA
        </text>
      </svg>
      <span className="sr-only">Bella</span>
    </span>
  </Link>
);

export default BrandLogo;
