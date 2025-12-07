'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { FileText, X, ArrowRight, AlertTriangle } from 'lucide-react';
import { Button } from '@/marketing/ui/button';

interface PrescriptionNotificationProps {
  prescription: {
    id: string;
    title: string;
    medications: string[];
    interactionWarnings?: string[];
  };
  appointmentId: string;
  onDismiss?: () => void;
  autoRedirect?: boolean;
  redirectDelay?: number; // milliseconds
}

/**
 * Notification component that appears when a prescription is created during a call
 * Shows a prominent notification with option to view prescription or auto-redirect
 */
export function PrescriptionNotification({
  prescription,
  appointmentId,
  onDismiss,
  autoRedirect = false,
  redirectDelay = 5000, // 5 seconds default
}: PrescriptionNotificationProps) {
  const router = useRouter();
  const [show, setShow] = useState(true);
  const [countdown, setCountdown] = useState(autoRedirect ? redirectDelay / 1000 : null);

  useEffect(() => {
    if (autoRedirect && countdown !== null && countdown > 0) {
      const timer = setInterval(() => {
        setCountdown((prev) => {
          if (prev === null || prev <= 1) {
            clearInterval(timer);
            return 0;
          }
          return prev - 1;
        });
      }, 1000);

      return () => clearInterval(timer);
    }
  }, [autoRedirect, countdown]);

  useEffect(() => {
    if (autoRedirect && countdown === 0) {
      router.push(`/patient/prescriptions?appointmentId=${appointmentId}`);
      setShow(false);
    }
  }, [autoRedirect, countdown, router, appointmentId]);

  const handleViewPrescription = () => {
    router.push(`/patient/prescriptions?appointmentId=${appointmentId}`);
    setShow(false);
    if (onDismiss) {
      onDismiss();
    }
  };

  const handleDismiss = () => {
    setShow(false);
    if (onDismiss) {
      onDismiss();
    }
  };

  if (!show) {
    return null;
  }

  const hasWarnings = prescription.interactionWarnings && prescription.interactionWarnings.length > 0;

  return (
    <div className="fixed top-4 right-4 z-50 max-w-md animate-in slide-in-from-top-5 duration-300">
      <div className="bg-white rounded-lg shadow-2xl border-2 border-teal-500 p-6 space-y-4">
        {/* Header */}
        <div className="flex items-start justify-between">
          <div className="flex items-center gap-3">
            <div className="p-2 bg-teal-100 rounded-lg">
              <FileText className="w-6 h-6 text-teal-600" />
            </div>
            <div>
              <h3 className="font-semibold text-slate-900">New Prescription Available</h3>
              <p className="text-sm text-slate-600 mt-1">
                Your doctor has created a prescription for this appointment
              </p>
            </div>
          </div>
          <button
            onClick={handleDismiss}
            className="text-slate-400 hover:text-slate-600 transition-colors"
            aria-label="Dismiss"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Prescription Info */}
        <div className="bg-slate-50 rounded-lg p-4 space-y-2">
          <p className="font-medium text-slate-900">{prescription.title}</p>
          <div className="flex items-center gap-2 text-sm text-slate-600">
            <span>{prescription.medications.length} medication(s)</span>
            {hasWarnings && (
              <span className="flex items-center gap-1 text-amber-600">
                <AlertTriangle className="w-4 h-4" />
                {prescription.interactionWarnings?.length} warning(s)
              </span>
            )}
          </div>
        </div>

        {/* Countdown */}
        {autoRedirect && countdown !== null && countdown > 0 && (
          <p className="text-sm text-slate-500 text-center">
            Redirecting to prescription in {countdown} second{countdown !== 1 ? 's' : ''}...
          </p>
        )}

        {/* Actions */}
        <div className="flex gap-3">
          <Button
            onClick={handleViewPrescription}
            className="flex-1 bg-gradient-to-r from-teal-500 to-violet-600 hover:from-teal-600 hover:to-violet-700"
          >
            View Prescription
            <ArrowRight className="w-4 h-4 ml-2" />
          </Button>
          {!autoRedirect && (
            <Button onClick={handleDismiss} variant="outline" className="flex-shrink-0">
              <X className="w-4 h-4" />
            </Button>
          )}
        </div>
      </div>
    </div>
  );
}

