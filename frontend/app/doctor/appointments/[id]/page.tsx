'use client';

import { useEffect, useMemo, useState } from 'react';
import { useParams, useRouter, useSearchParams } from 'next/navigation';
import Link from 'next/link';
import { format } from 'date-fns';
import {
  Calendar,
  Clock,
  Building2,
  User,
  Phone,
  Video,
  ExternalLink,
  AlertCircle,
  CheckCircle2,
  XCircle,
  FileText,
} from 'lucide-react';
import DashboardLayout from '@/components/layout/DashboardLayout';
import Card from '@/components/ui/Card';
import Button from '@/components/ui/Button';
import { Badge } from '@/marketing/ui/badge';
import { appointmentsApi, prescriptionsApi, medicalRecordsApi } from '@/lib/api';
import { Appointment, MedicalHistory } from '@/types';
import { toast } from 'react-hot-toast';

type AppointmentStatus = Appointment['status'];
type Prescription = {
  id: string;
  title?: string;
  body?: string;
  instructions?: string;
  createdAt?: string;
  validUntil?: string;
  doctorName?: string;
  clinicName?: string;
  appointmentId?: string;
  medications?: any[];
};

export default function DoctorAppointmentDetailPage() {
  const params = useParams<{ id: string }>();
  const searchParams = useSearchParams();
  const router = useRouter();
  const appointmentId = params?.id;
  const patientIdFromQuery = searchParams.get('patientId') || '';

  const [appointment, setAppointment] = useState<Appointment | null>(null);
  const [prescription, setPrescription] = useState<Prescription | null>(null);
  const [history, setHistory] = useState<MedicalHistory[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isCompleting, setIsCompleting] = useState(false);

  useEffect(() => {
    if (appointmentId) {
      loadData();
    }
  }, [appointmentId]);

  const loadData = async () => {
    setIsLoading(true);
    try {
      const apt = await appointmentsApi.getById(appointmentId as string);
      setAppointment(apt);

      if (apt?.patientId) {
        const [rxByApt, medicalHistory] = await Promise.all([
          prescriptionsApi.getByAppointmentId(appointmentId), // This now returns null on 404, not an error
          medicalRecordsApi.listForPatient(String(apt.patientId)).catch((err) => {
            console.error('Failed to load medical history:', err);
            return []; // Return empty array on error
          }),
        ]);
        setPrescription(rxByApt);
        setHistory(Array.isArray(medicalHistory) ? medicalHistory : []);
      }
    } catch (error: any) {
      console.error('Appointment detail load error:', error);
      console.error('Error response:', error?.response);
      console.error('Error status:', error?.response?.status);
      console.error('Error data:', error?.response?.data);
      
      let errorMessage = 'Failed to load appointment details';
      
      if (error?.response?.status === 404) {
        errorMessage = 'Appointment not found. It may have been deleted or you may not have permission to view it.';
      } else if (error?.response?.status === 403) {
        errorMessage = 'You do not have permission to view this appointment.';
      } else if (error?.response?.data?.message) {
        errorMessage = error.response.data.message;
      } else if (error?.message) {
        errorMessage = error.message;
      }
      
      toast.error(errorMessage);
      
      // Redirect back to appointments list if appointment not found or unauthorized
      if (error?.response?.status === 404 || error?.response?.status === 403) {
        setTimeout(() => {
          router.push('/doctor/appointments');
        }, 2000);
      }
    } finally {
      setIsLoading(false);
    }
  };

  const canStartMeeting = (appointmentDateTime?: string): boolean => {
    return true; // No time restrictions - doctors can join whenever
  };

  const hasAppointmentStarted = (appointmentDateTime?: string): boolean => {
    if (!appointmentDateTime) return false;
    const appointmentTime = new Date(appointmentDateTime);
    const now = new Date();
    return now >= appointmentTime;
  };

  const isActiveAppointment = (status: AppointmentStatus): boolean => {
    return status === 'IN_PROGRESS';
  };

  const canMarkNoShow = (appointment?: Appointment): boolean => {
    if (!appointment) return false;
    // Use endTime if available, otherwise calculate (30 minutes default duration)
    let endTime: Date;
    if (appointment.endTime) {
      endTime = new Date(appointment.endTime);
    } else {
      const appointmentTime = new Date(appointment.appointmentDateTime);
      endTime = new Date(appointmentTime.getTime() + 30 * 60 * 1000); // 30 minutes default
    }
    const now = new Date();
    return now >= endTime; // Can mark no show only after appointment end time
  };

  const formatDateSafe = (value?: string, pattern = 'MMM dd, yyyy') => {
    if (!value) return 'N/A';
    const d = new Date(value);
    if (isNaN(d.getTime())) return 'N/A';
    return format(d, pattern);
  };

  const getStatusLabel = (status: string) =>
    status
      .split('_')
      .map((p) => p.charAt(0) + p.slice(1).toLowerCase())
      .join(' ');

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'IN_PROGRESS':
        return 'bg-blue-100 text-blue-800 border-blue-200';
      case 'CANCELLED':
        return 'bg-red-100 text-red-800 border-red-200';
      case 'COMPLETED':
        return 'bg-teal-100 text-teal-800 border-teal-200';
      case 'NO_SHOW':
        return 'bg-gray-100 text-gray-800 border-gray-200';
      default:
        return 'bg-gray-100 text-gray-800 border-gray-200';
    }
  };

  const handleComplete = async () => {
    if (!appointment) return;
    
    // Check if prescription exists
    if (!prescription) {
      toast.error(
        (t) => (
          <div className="flex flex-col gap-2 max-w-md">
            <p className="font-semibold text-base">Prescription Required</p>
            <p className="text-sm text-slate-700">
              You must create a prescription before concluding this appointment.
            </p>
            <div className="flex gap-2 mt-2">
              <button
                onClick={() => {
                  toast.dismiss(t.id);
                  router.push(`/doctor/prescriptions/new?appointmentId=${appointment.id}&patientId=${patientId}`);
                }}
                className="px-4 py-2 bg-teal-600 text-white rounded-lg text-sm font-medium hover:bg-teal-700 transition-colors"
              >
                Create Prescription Now
              </button>
              <button
                onClick={() => toast.dismiss(t.id)}
                className="px-4 py-2 bg-slate-200 text-slate-700 rounded-lg text-sm font-medium hover:bg-slate-300 transition-colors"
              >
                Cancel
              </button>
            </div>
          </div>
        ),
        { duration: 10000, icon: '⚠️' }
      );
      return;
    }

    const confirmed = window.confirm(
      `Are you sure you want to conclude this appointment?\n\n` +
      `Patient: ${appointment.patientName}\n` +
      `Date: ${format(new Date(appointment.appointmentDateTime), 'MMM dd, yyyy h:mm a')}\n\n` +
      `This action cannot be undone.`
    );

    if (!confirmed) return;

    setIsCompleting(true);
    try {
      await appointmentsApi.complete(appointment.id.toString());
      toast.success('Appointment completed successfully');
      await loadData();
    } catch (error: any) {
      toast.error(error?.response?.data?.message || 'Failed to conclude appointment');
    } finally {
      setIsCompleting(false);
    }
  };

  const handleCancel = async () => {
    if (!appointment) return;
    
    const reason = window.prompt(
      `Are you sure you want to cancel this appointment?\n\n` +
      `Patient: ${appointment.patientName}\n` +
      `Date: ${format(new Date(appointment.appointmentDateTime), 'MMM dd, yyyy h:mm a')}\n\n` +
      `Please provide a reason for cancellation (optional):`
    );

    if (reason === null) return; // User cancelled

    try {
      await appointmentsApi.cancel(appointment.id.toString(), reason || 'Cancelled by doctor');
      toast.success('Appointment cancelled successfully');
      router.push('/doctor/appointments');
    } catch (error: any) {
      toast.error(error?.response?.data?.message || 'Failed to cancel appointment');
    }
  };

  const handleMarkNoShow = async () => {
    if (!appointment) return;
    
    // Calculate end time (assuming 30 minutes duration if not available)
    const appointmentTime = new Date(appointment.appointmentDateTime);
    const endTime = new Date(appointmentTime.getTime() + 30 * 60 * 1000); // 30 minutes default
    const now = new Date();

    if (now < endTime) {
      toast.error('Cannot mark as no-show: Appointment time has not elapsed yet');
      return;
    }

    const reason = window.prompt(
      `Mark this appointment as No Show?\n\n` +
      `Patient: ${appointment.patientName}\n` +
      `Date: ${format(new Date(appointment.appointmentDateTime), 'MMM dd, yyyy h:mm a')}\n\n` +
      `Please provide a reason (optional):`
    );

    if (reason === null) return; // User cancelled

    try {
      await appointmentsApi.markNoShow(appointment.id.toString(), reason || 'Patient did not show up');
      toast.success('Appointment marked as no-show');
      router.push('/doctor/appointments');
    } catch (error: any) {
      toast.error(error?.response?.data?.message || 'Failed to mark as no-show');
    }
  };

  if (isLoading || !appointment) {
    return (
      <DashboardLayout requiredUserType="DOCTOR">
        <div className="min-h-screen bg-gradient-to-b from-blue-50 to-white flex items-center justify-center text-slate-600">
          Loading appointment...
        </div>
      </DashboardLayout>
    );
  }

  const patientId = appointment.patientId?.toString() || patientIdFromQuery;

  return (
    <DashboardLayout requiredUserType="DOCTOR">
      <div className="min-h-screen bg-gradient-to-b from-blue-50 via-white to-blue-50">
        <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-slate-500 uppercase tracking-wide">Appointment Details</p>
              <h1 className="text-3xl font-bold text-slate-900">
                {appointment.patientName || `Patient ID: ${appointment.patientId}`}
              </h1>
              <div className="mt-2 flex items-center gap-2 flex-wrap">
                <Badge variant="outline" className={`${getStatusColor(appointment.status)} border`}>
                  {getStatusLabel(appointment.status)}
                </Badge>
                <Badge variant="secondary">{appointment.appointmentType}</Badge>
                {appointment.isEmergency && (
                  <Badge variant="destructive" className="text-xs">
                    Emergency
                  </Badge>
                )}
              </div>
            </div>
            <Button variant="secondary" onClick={() => router.push('/doctor/appointments')}>
              Back to Appointments
            </Button>
          </div>

          <Card className="p-5 shadow-sm">
            <div className="grid gap-4 sm:grid-cols-2">
              <div className="space-y-2">
                <div className="flex items-center gap-2 text-slate-700">
                  <Calendar className="w-5 h-5 text-teal-600" />
                  <div>
                    <p className="text-xs uppercase text-slate-500">Date</p>
                    <p className="text-lg font-semibold text-slate-900">
                      {format(new Date(appointment.appointmentDateTime), 'MMM dd, yyyy')}
                    </p>
                  </div>
                </div>
                <div className="flex items-center gap-2 text-slate-700">
                  <Clock className="w-5 h-5 text-teal-600" />
                  <div>
                    <p className="text-xs uppercase text-slate-500">Time</p>
                    <p className="text-lg font-semibold text-slate-900">
                      {format(new Date(appointment.appointmentDateTime), 'h:mm a')}
                    </p>
                  </div>
                </div>
              </div>
              <div className="space-y-2">
                <div className="flex items-center gap-2 text-slate-700">
                  <User className="w-5 h-5 text-slate-500" />
                  <div>
                    <p className="text-xs uppercase text-slate-500">Patient</p>
                    <p className="text-lg font-semibold text-slate-900">
                      {appointment.patientName || `Patient ID: ${appointment.patientId}`}
                    </p>
                  </div>
                </div>
                {appointment.patientEmail && (
                  <div className="flex items-center gap-2 text-slate-700">
                    <Phone className="w-5 h-5 text-slate-500" />
                    <div>
                      <p className="text-xs uppercase text-slate-500">Email</p>
                      <p className="text-sm text-slate-800">{appointment.patientEmail}</p>
                    </div>
                  </div>
                )}
              </div>
            </div>

            <div className="mt-4 grid gap-4 sm:grid-cols-2">
              {appointment.clinicName && (
                <div className="flex items-start gap-2 text-slate-700">
                  <Building2 className="w-5 h-5 text-rose-500" />
                  <div>
                    <p className="text-xs uppercase text-slate-500">Clinic</p>
                    <p className="text-sm text-slate-900">{appointment.clinicName}</p>
                    {appointment.clinicAddress && (
                      <p className="text-xs text-slate-600">{appointment.clinicAddress}</p>
                    )}
                  </div>
                </div>
              )}
              <div className="space-y-2 text-sm text-slate-700">
                {appointment.reason && (
                  <p>
                    <span className="font-semibold text-slate-900">Reason:</span> {appointment.reason}
                  </p>
                )}
                {appointment.notes && (
                  <p>
                    <span className="font-semibold text-slate-900">Notes:</span> {appointment.notes}
                  </p>
                )}
              </div>
            </div>
          </Card>

          <Card className="p-5 shadow-sm">
            <div className="flex flex-wrap gap-3">
              {appointment.appointmentType === 'ONLINE' && (
                appointment.zoomStartUrl ? (
                  <Link href={appointment.zoomStartUrl} target="_blank">
                    <Button
                      variant="primary"
                      className="flex items-center gap-2"
                    >
                      <Video className="w-4 h-4" />
                      Start Zoom Meeting
                      <ExternalLink className="w-3 h-3" />
                    </Button>
                  </Link>
                ) : (
                  <Button variant="primary" disabled className="flex items-center gap-2">
                    <AlertCircle className="w-4 h-4" />
                    Zoom link unavailable
                  </Button>
                )
              )}

              {isActiveAppointment(appointment.status) && (
                <>
                  {hasAppointmentStarted(appointment.appointmentDateTime) ? (
                    <Link href={`/doctor/prescriptions/new?appointmentId=${appointment.id}&patientId=${patientId}`}>
                      <Button variant="outline" className="flex items-center gap-2">
                        <FileText className="w-4 h-4" />
                        {prescription ? 'Edit Prescription' : 'Create Prescription'}
                      </Button>
                    </Link>
                  ) : (
                    <Button variant="secondary" disabled className="flex items-center gap-2">
                      <FileText className="w-4 h-4" />
                      Create Prescription (available after start)
                    </Button>
                  )}

                  <Button
                    variant="primary"
                    className="flex items-center gap-2"
                    onClick={handleComplete}
                    disabled={isCompleting}
                  >
                    <CheckCircle2 className="w-4 h-4" />
                    {isCompleting ? 'Completing...' : 'Conclude Appointment'}
                  </Button>
                </>
              )}

              {/* Cancel and No Show buttons - available for all non-final statuses */}
              {appointment.status !== 'COMPLETED' && 
               appointment.status !== 'CANCELLED' && 
               appointment.status !== 'NO_SHOW' && (
                <>
                  <Button
                    variant="outline"
                    className="flex items-center gap-2 border-red-300 text-red-700 hover:bg-red-50"
                    onClick={handleCancel}
                  >
                    <XCircle className="w-4 h-4" />
                    Cancel Appointment
                  </Button>

                  {/* No Show - only available after appointment end time */}
                  {canMarkNoShow(appointment) && (
                    <Button
                      variant="outline"
                      className="flex items-center gap-2 border-gray-300 text-gray-700 hover:bg-gray-50"
                      onClick={handleMarkNoShow}
                    >
                      <XCircle className="w-4 h-4" />
                      Mark as No Show
                    </Button>
                  )}
                </>
              )}
            </div>
          </Card>

          <div className="grid lg:grid-cols-2 gap-6">
            <Card className="p-5 shadow-sm">
              <div className="flex items-center justify-between mb-3">
                <h3 className="text-lg font-semibold text-slate-900">Patient Medical History</h3>
                {patientId && (
                  <Link
                    href={`/patient/medical-history?patientId=${patientId}`}
                    className="text-xs text-teal-600 hover:underline"
                  >
                    View all
                  </Link>
                )}
              </div>
              {history.length === 0 ? (
                <p className="text-sm text-slate-600">No history found.</p>
              ) : (
                <div className="space-y-3">
                  {history.slice(0, 4).map((h) => (
                    <div key={h.id} className="border border-slate-200 rounded-lg p-3">
                      <div className="flex items-center justify-between text-sm text-slate-700">
                        <span className="font-semibold text-slate-900">{h.condition}</span>
                        <Badge variant="outline">{h.status.replace('_', ' ')}</Badge>
                      </div>
                      <p className="text-xs text-slate-500 mt-1">
                        Diagnosed: {formatDateSafe(h.diagnosisDate)}
                      </p>
                      <p className="text-sm text-slate-700 mt-1 line-clamp-2">{h.description}</p>
                    </div>
                  ))}
                </div>
              )}
            </Card>

            <Card className="p-5 shadow-sm">
              <div className="flex items-center justify-between mb-3">
                <h3 className="text-lg font-semibold text-slate-900">Prescriptions</h3>
                {patientId && (
                  <Link
                    href={`/patient/prescriptions?appointmentId=${appointment.id}`}
                    className="text-xs text-teal-600 hover:underline"
                  >
                    View patient prescriptions
                  </Link>
                )}
              </div>
              {!prescription ? (
                <p className="text-sm text-slate-600">No prescription for this appointment yet.</p>
              ) : (
                <div className="space-y-2">
                  <div className="flex flex-wrap items-center gap-2 text-sm text-slate-700">
                    <Badge variant="outline" className="bg-white text-slate-700 border-slate-200">
                      {formatDateSafe(prescription.createdAt)}
                    </Badge>
                    {prescription.clinicName && (
                      <Badge variant="outline" className="bg-white text-slate-700 border-slate-200">
                        {prescription.clinicName}
                      </Badge>
                    )}
                  </div>
                  {(prescription.instructions || prescription.body) && (
                    <div className="p-3 bg-slate-50 border border-slate-200 rounded-lg text-sm text-slate-700">
                      {prescription.instructions || prescription.body}
                    </div>
                  )}
                  <div className="space-y-2">
                    <p className="text-xs uppercase text-slate-500">Medications</p>
                    {prescription.medications && prescription.medications.length > 0 ? (
                      prescription.medications.map((med: any, idx: number) => (
                        <div key={med.id || idx} className="border border-slate-200 rounded-lg p-3">
                          <p className="font-medium text-slate-900">{med.name || med}</p>
                          {(med.dosage || med.frequency || med.duration) && (
                            <p className="text-sm text-slate-600">
                              {[med.dosage, med.frequency, med.duration].filter(Boolean).join(' • ')}
                            </p>
                          )}
                          {med.instructions && (
                            <p className="text-xs text-slate-500 mt-1">{med.instructions}</p>
                          )}
                        </div>
                      ))
                    ) : (
                      <p className="text-sm text-slate-600">No medications listed.</p>
                    )}
                  </div>
                </div>
              )}
            </Card>
          </div>
        </div>
      </div>
    </DashboardLayout>
  );
}