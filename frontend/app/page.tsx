'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/contexts/AuthContext';
import { LandingPage } from '@/marketing/pages/LandingPage';

export default function Home() {
  const router = useRouter();
  const { user, isAuthenticated, isLoading } = useAuth();

  useEffect(() => {
    if (!isLoading && isAuthenticated && user) {
      // Redirect authenticated users to their dashboard
      // Only redirect if we're actually on the home page (not already on a dashboard)
      const currentPath = window.location.pathname;
      if (currentPath === '/' || currentPath === '') {
        switch (user.userType) {
          case 'PATIENT':
            router.replace('/patient/dashboard');
            break;
          case 'DOCTOR':
            router.replace('/doctor/dashboard');
            break;
          case 'ADMIN':
            router.replace('/admin/dashboard');
            break;
        }
      }
    }
  }, [isAuthenticated, isLoading, user, router]);

  // Show loading or nothing while redirecting authenticated users
  if (!isLoading && isAuthenticated && user) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-xl">Redirecting...</div>
      </div>
    );
  }

  return <LandingPage />;
}

