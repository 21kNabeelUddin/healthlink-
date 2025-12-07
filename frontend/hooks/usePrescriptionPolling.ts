import { useState, useEffect, useRef } from 'react';
import { prescriptionsApi } from '@/lib/api';
import { toast } from 'react-hot-toast';

interface Prescription {
  id: string;
  appointmentId: string;
  patientId: string;
  doctorId: string;
  title: string;
  body: string;
  medications: string[];
  interactionWarnings: string[];
  createdAt: string;
}

interface UsePrescriptionPollingOptions {
  appointmentId: string | null;
  enabled?: boolean;
  pollInterval?: number; // in milliseconds, default 5000 (5 seconds)
  onPrescriptionFound?: (prescription: Prescription) => void;
}

/**
 * Hook to poll for prescriptions during an active appointment
 * Automatically checks for new prescriptions and notifies when found
 */
export function usePrescriptionPolling({
  appointmentId,
  enabled = true,
  pollInterval = 5000,
  onPrescriptionFound,
}: UsePrescriptionPollingOptions) {
  const [prescription, setPrescription] = useState<Prescription | null>(null);
  const [isPolling, setIsPolling] = useState(false);
  const [hasNotified, setHasNotified] = useState(false);
  const intervalRef = useRef<NodeJS.Timeout | null>(null);
  const lastCheckedRef = useRef<string | null>(null);

  useEffect(() => {
    if (!enabled || !appointmentId) {
      setIsPolling(false);
      if (intervalRef.current) {
        clearInterval(intervalRef.current);
        intervalRef.current = null;
      }
      return;
    }

    setIsPolling(true);

    const checkPrescription = async () => {
      try {
        const result = await prescriptionsApi.getByAppointmentId(appointmentId);
        
        if (result && result.id !== lastCheckedRef.current) {
          // New prescription found
          setPrescription(result);
          lastCheckedRef.current = result.id;
          
          // Only notify once per prescription
          if (!hasNotified) {
            setHasNotified(true);
            if (onPrescriptionFound) {
              onPrescriptionFound(result);
            }
          }
        } else if (result) {
          // Prescription exists but we've already seen it
          setPrescription(result);
        }
      } catch (error: any) {
        // 404 is expected when no prescription exists yet
        if (error?.response?.status !== 404) {
          console.error('Error checking for prescription:', error);
        }
      }
    };

    // Check immediately
    checkPrescription();

    // Then poll at intervals
    intervalRef.current = setInterval(checkPrescription, pollInterval);

    return () => {
      if (intervalRef.current) {
        clearInterval(intervalRef.current);
        intervalRef.current = null;
      }
    };
  }, [appointmentId, enabled, pollInterval, onPrescriptionFound, hasNotified]);

  // Reset notification state when appointment changes
  useEffect(() => {
    setHasNotified(false);
    lastCheckedRef.current = null;
  }, [appointmentId]);

  return {
    prescription,
    isPolling,
    hasPrescription: prescription !== null,
  };
}

