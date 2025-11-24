'use client';

import { useState, useEffect } from 'react';
import { useRouter, useParams } from 'next/navigation';
import { useAuth } from '@/contexts/AuthContext';
import { useForm } from 'react-hook-form';
import { patientApi, doctorApi } from '@/lib/api';
import { AppointmentRequest, Clinic } from '@/types';
import { toast } from 'react-hot-toast';
import DashboardLayout from '@/components/layout/DashboardLayout';
import Card from '@/components/ui/Card';
import Input from '@/components/ui/Input';
import Button from '@/components/ui/Button';

export default function EditAppointmentPage() {
  const router = useRouter();
  const params = useParams();
  const { user } = useAuth();
  const [isLoading, setIsLoading] = useState(false);
  const [isLoadingData, setIsLoadingData] = useState(true);
  const [clinics, setClinics] = useState<Clinic[]>([]);
  const appointmentId = parseInt(params.id as string);

  const { register, handleSubmit, watch, formState: { errors }, setValue } = useForm<AppointmentRequest & { doctorId: number }>();

  const appointmentType = watch('appointmentType');
  const selectedDoctorId = watch('doctorId');

  useEffect(() => {
    if (user?.id) {
      loadAppointment();
    }
  }, [user, appointmentId]);

  useEffect(() => {
    if (selectedDoctorId) {
      loadClinics(selectedDoctorId);
    }
  }, [selectedDoctorId]);

  const loadAppointment = async () => {
    if (!user?.id) return;
    
    setIsLoadingData(true);
    try {
      const response = await patientApi.getAppointmentById(user.id, appointmentId);
      if (response.success && response.data) {
        const appointment = response.data;
        const dateTime = new Date(appointment.appointmentDateTime);
        const localDateTime = new Date(dateTime.getTime() - dateTime.getTimezoneOffset() * 60000)
          .toISOString()
          .slice(0, 16);
        
        setValue('doctorId', appointment.doctorId);
        setValue('appointmentDateTime', localDateTime);
        setValue('reason', appointment.reason);
        setValue('notes', appointment.notes || '');
        setValue('appointmentType', appointment.appointmentType);
        setValue('clinicId', appointment.clinicId);
      }
    } catch (error: any) {
      toast.error('Failed to load appointment');
      router.push('/patient/appointments');
    } finally {
      setIsLoadingData(false);
    }
  };

  const loadClinics = async (doctorId: number) => {
    try {
      const response = await doctorApi.getClinics(doctorId, true);
      if (response.success && response.data) {
        setClinics(response.data);
      }
    } catch (error: any) {
      // Doctor might not have clinics yet
    }
  };

  const onSubmit = async (data: AppointmentRequest & { doctorId: number }) => {
    if (!user?.id) return;

    setIsLoading(true);
    try {
      const appointmentData: AppointmentRequest = {
        appointmentDateTime: data.appointmentDateTime,
        reason: data.reason,
        notes: data.notes,
        doctorId: data.doctorId,
        clinicId: data.clinicId,
        appointmentType: data.appointmentType,
      };

      const response = await patientApi.updateAppointment(user.id, appointmentId, appointmentData);

      if (response.success) {
        toast.success('Appointment updated successfully!');
        router.push('/patient/appointments');
      } else {
        toast.error(response.message);
      }
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Failed to update appointment');
    } finally {
      setIsLoading(false);
    }
  };

  if (isLoadingData) {
    return (
      <DashboardLayout requiredUserType="PATIENT">
        <div className="text-center">Loading...</div>
      </DashboardLayout>
    );
  }

  return (
    <DashboardLayout requiredUserType="PATIENT">
      <div className="max-w-2xl mx-auto">
        <h1 className="text-4xl font-bold text-gray-800 mb-8">Edit Appointment</h1>

        <Card>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <Input
              label="Date & Time *"
              type="datetime-local"
              {...register('appointmentDateTime', { required: 'Date and time is required' })}
              error={errors.appointmentDateTime?.message}
            />

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Appointment Type *
              </label>
              <select
                {...register('appointmentType', { required: 'Appointment type is required' })}
                className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
              >
                <option value="">Select type</option>
                <option value="ONLINE">Online (Zoom)</option>
                <option value="ONSITE">Onsite</option>
              </select>
              {errors.appointmentType && (
                <p className="mt-1 text-sm text-red-600">{errors.appointmentType.message}</p>
              )}
            </div>

            {appointmentType === 'ONSITE' && selectedDoctorId && (
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Select Clinic
                </label>
                <select
                  {...register('clinicId', { valueAsNumber: true })}
                  className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                >
                  <option value="">Select a clinic (optional)</option>
                  {clinics.map((clinic) => (
                    <option key={clinic.id} value={clinic.id}>
                      {clinic.name} - {clinic.address}
                    </option>
                  ))}
                </select>
              </div>
            )}

            <Input
              label="Reason *"
              {...register('reason', { required: 'Reason is required' })}
              error={errors.reason?.message}
            />

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Notes
              </label>
              <textarea
                {...register('notes')}
                className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                rows={3}
              />
            </div>

            <div className="flex gap-4">
              <Button type="submit" isLoading={isLoading} className="flex-1">
                Update Appointment
              </Button>
              <Button
                type="button"
                variant="secondary"
                onClick={() => router.back()}
                className="flex-1"
              >
                Cancel
              </Button>
            </div>
          </form>
        </Card>
      </div>
    </DashboardLayout>
  );
}

