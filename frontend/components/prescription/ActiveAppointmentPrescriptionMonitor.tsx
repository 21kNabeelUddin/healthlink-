'use client';

import { useMemo } from 'react';
import { usePrescriptionPolling } from '@/hooks/usePrescriptionPolling';
import { PrescriptionNotification } from './PrescriptionNotification';
import { Appointment } from '@/types';

interface ActiveAppointmentPrescriptionMonitorProps {
  appointments: Appointment[];
  autoRedirect?: boolean;
  redirectDelay?: number;
}

/**
 * Component that monitors active appointments and shows prescription notifications
 * when prescriptions are created during online appointments
 */
export function ActiveAppointmentPrescriptionMonitor({
  appointments,
  autoRedirect = true,
  redirectDelay = 5000,
}: ActiveAppointmentPrescriptionMonitorProps) {
  // Find active online appointments (CONFIRMED status, ONLINE type, current or future time)
  const activeAppointments = useMemo(() => {
    const now = new Date();
    return appointments.filter((apt) => {
      const isOnline = apt.appointmentType === 'ONLINE';
      const isConfirmed = apt.status === 'CONFIRMED';
      const appointmentTime = new Date(apt.appointmentDateTime);
      const isActive = appointmentTime <= now && appointmentTime.getTime() + 2 * 60 * 60 * 1000 >= now.getTime(); // Within 2 hours of appointment time
      
      return isOnline && isConfirmed && isActive;
    });
  }, [appointments]);

  // Monitor the first active appointment (or most recent)
  const appointmentToMonitor = activeAppointments.length > 0 
    ? activeAppointments.sort((a, b) => 
        new Date(b.appointmentDateTime).getTime() - new Date(a.appointmentDateTime).getTime()
      )[0]
    : null;

  const { prescription, hasPrescription } = usePrescriptionPolling({
    appointmentId: appointmentToMonitor?.id ? String(appointmentToMonitor.id) : null,
    enabled: !!appointmentToMonitor,
    pollInterval: 5000, // Check every 5 seconds
    onPrescriptionFound: (prescription) => {
      // Optional: Show toast notification
      console.log('Prescription found for appointment:', appointmentToMonitor?.id);
    },
  });

  if (!appointmentToMonitor || !hasPrescription || !prescription) {
    return null;
  }

  return (
    <PrescriptionNotification
      prescription={prescription}
      appointmentId={String(appointmentToMonitor.id)}
      autoRedirect={autoRedirect}
      redirectDelay={redirectDelay}
    />
  );
}

