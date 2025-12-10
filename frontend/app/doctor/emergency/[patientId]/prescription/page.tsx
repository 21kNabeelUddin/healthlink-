'use client';

import { useState, useEffect } from 'react';
import { useRouter, useParams } from 'next/navigation';
import { useAuth } from '@/contexts/AuthContext';
import { useForm } from 'react-hook-form';
import { prescriptionsApi, patientApi } from '@/lib/api';
import { toast } from 'react-hot-toast';
import DashboardLayout from '@/components/layout/DashboardLayout';
import Card from '@/components/ui/Card';
import Input from '@/components/ui/Input';
import Button from '@/components/ui/Button';
import { FileText, Plus, X, AlertTriangle, User, Pill } from 'lucide-react';

interface PrescriptionFormData {
  title: string;
  body: string;
}

export default function EmergencyPrescriptionPage() {
  const router = useRouter();
  const params = useParams();
  const { user } = useAuth();
  const patientId = params.patientId as string;
  
  const [patientInfo, setPatientInfo] = useState<{ name: string; email: string } | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [isLoadingPatient, setIsLoadingPatient] = useState(true);
  const [medications, setMedications] = useState<{ name: string; dosage: string }[]>([
    { name: '', dosage: '' },
  ]);
  const [interactionWarnings, setInteractionWarnings] = useState<string[]>([]);
  const [isCheckingInteractions, setIsCheckingInteractions] = useState(false);

  const { register, handleSubmit, formState: { errors }, setValue } = useForm<PrescriptionFormData>({
    defaultValues: {
      title: '',
      body: '',
    },
  });

  useEffect(() => {
    if (patientId) {
      loadPatientInfo();
    }
  }, [patientId]);

  const loadPatientInfo = async () => {
    if (!patientId) return;
    setIsLoadingPatient(true);
    try {
      // Try to get patient info - if API doesn't exist, we'll use a placeholder
      // For now, we'll set a placeholder since we don't have a direct patient info endpoint
      setPatientInfo({
        name: 'Emergency Patient',
        email: 'patient@example.com',
      });
      setValue('title', `Prescription for Emergency Patient - ${new Date().toLocaleDateString()}`);
    } catch (error: any) {
      console.error('Failed to load patient info:', error);
      // Set defaults even if API fails
      setPatientInfo({
        name: 'Emergency Patient',
        email: 'patient@example.com',
      });
      setValue('title', `Prescription for Emergency Patient - ${new Date().toLocaleDateString()}`);
    } finally {
      setIsLoadingPatient(false);
    }
  };

  const addMedication = () => {
    setMedications([...medications, { name: '', dosage: '' }]);
  };

  const removeMedication = (index: number) => {
    setMedications(medications.filter((_, i) => i !== index));
  };

  const updateMedication = (index: number, field: 'name' | 'dosage', value: string) => {
    const updated = [...medications];
    updated[index] = { ...updated[index], [field]: value };
    setMedications(updated);
  };

  const checkInteractions = async () => {
    const medsToCheck = medications
      .map(m => m.name.trim())
      .filter(Boolean);

    if (medsToCheck.length < 2) {
      setInteractionWarnings([]);
      return;
    }

    setIsCheckingInteractions(true);
    try {
      const response = await prescriptionsApi.checkInteractions({ medications: medsToCheck });
      const warnings = response.warnings || (Array.isArray(response) ? response : []);
      setInteractionWarnings(warnings);
      if (response.warnings && response.warnings.length > 0) {
        toast.error(`Found ${response.warnings.length} potential drug interaction(s)`);
      } else {
        toast.success('No drug interactions detected');
      }
    } catch (error: any) {
      console.error('Interaction check error:', error);
      toast.error('Failed to check drug interactions');
    } finally {
      setIsCheckingInteractions(false);
    }
  };

  const onSubmit = async (data: PrescriptionFormData) => {
    if (!user?.id || !patientId) return;

    setIsLoading(true);
    try {
      const medicationList = medications
        .map(m => {
          const name = m.name.trim();
          const dosage = m.dosage.trim();
          return name ? (dosage ? `${name} - ${dosage}` : name) : null;
        })
        .filter(Boolean) as string[];

      await prescriptionsApi.create({
        patientId: patientId,
        // appointmentId is optional - emergency patients might not have appointment
        title: data.title,
        body: data.body,
        medications: medicationList,
      });

      toast.success('Prescription created successfully!');
      router.push('/doctor/emergency');
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Failed to create prescription');
    } finally {
      setIsLoading(false);
    }
  };

  if (isLoadingPatient) {
    return (
      <DashboardLayout requiredUserType="DOCTOR">
        <div className="text-center py-12">Loading patient information...</div>
      </DashboardLayout>
    );
  }

  return (
    <DashboardLayout requiredUserType="DOCTOR">
      <div className="max-w-4xl mx-auto">
        <div className="mb-6">
          <Button
            variant="outline"
            onClick={() => router.back()}
            className="mb-4"
          >
            ← Back
          </Button>
          <h1 className="text-4xl font-bold text-gray-800 mb-2">Add Prescription</h1>
          <p className="text-gray-600">
            Create a prescription for the emergency patient
          </p>
        </div>

        <Card>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
            {/* Patient Info */}
            <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
              <div className="flex items-center gap-2 mb-2">
                <User className="w-5 h-5 text-blue-600" />
                <h3 className="font-semibold text-blue-900">Patient Information</h3>
              </div>
              <p className="text-sm text-blue-800">
                <strong>Patient ID:</strong> {patientId}
              </p>
            </div>

            {/* Prescription Title */}
            <Input
              label="Prescription Title *"
              {...register('title', { required: 'Title is required' })}
              error={errors.title?.message}
              placeholder="e.g., Prescription for [Patient Name] - [Date]"
            />

            {/* Prescription Body */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Prescription Details * <FileText className="inline w-4 h-4 ml-1" />
              </label>
              <textarea
                {...register('body', { required: 'Prescription details are required' })}
                rows={8}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
                placeholder="Enter prescription details, instructions, diagnosis, follow-up notes, etc."
              />
              {errors.body && (
                <p className="text-red-600 text-sm mt-1">{errors.body.message}</p>
              )}
            </div>

            {/* Medications */}
            <div>
              <div className="flex items-center justify-between mb-3">
                <label className="block text-sm font-medium text-gray-700">
                  Medications <Pill className="inline w-4 h-4 ml-1" />
                </label>
                <div className="flex gap-2">
                  <Button
                    type="button"
                    variant="outline"
                    className="text-sm px-3 py-1.5"
                    onClick={checkInteractions}
                    disabled={isCheckingInteractions || medications.filter(m => m.name.trim()).length < 2}
                  >
                    {isCheckingInteractions ? 'Checking...' : 'Check Interactions'}
                  </Button>
                  <Button
                    type="button"
                    variant="outline"
                    className="text-sm px-3 py-1.5"
                    onClick={addMedication}
                  >
                    <Plus className="w-4 h-4 mr-1" />
                    Add Medication
                  </Button>
                </div>
              </div>

              {medications.map((med, index) => (
                <div key={index} className="flex gap-2 mb-2">
                  <Input
                    placeholder="Medication name (e.g., Paracetamol)"
                    value={med.name}
                    onChange={(e) => updateMedication(index, 'name', e.target.value)}
                    className="flex-1"
                  />
                  <Input
                    placeholder="Dosage (e.g., 500mg)"
                    value={med.dosage}
                    onChange={(e) => updateMedication(index, 'dosage', e.target.value)}
                    className="flex-1"
                  />
                  {medications.length > 1 && (
                    <Button
                      type="button"
                      variant="outline"
                      className="text-sm px-3 py-1.5"
                      onClick={() => removeMedication(index)}
                    >
                      <X className="w-4 h-4" />
                    </Button>
                  )}
                </div>
              ))}

              {interactionWarnings.length > 0 && (
                <div className="mt-4 bg-red-50 border border-red-200 rounded-lg p-4">
                  <div className="flex items-center gap-2 mb-2">
                    <AlertTriangle className="w-5 h-5 text-red-600" />
                    <h4 className="font-semibold text-red-900">Drug Interaction Warnings</h4>
                  </div>
                  <ul className="list-disc list-inside text-sm text-red-800 space-y-1">
                    {interactionWarnings.map((warning, idx) => (
                      <li key={idx}>{warning}</li>
                    ))}
                  </ul>
                </div>
              )}
            </div>

            {/* Submit Buttons */}
            <div className="flex gap-3 pt-4 border-t">
              <Button type="submit" variant="primary" disabled={isLoading} className="flex-1">
                {isLoading ? 'Creating...' : 'Create Prescription'}
              </Button>
              <Button
                type="button"
                variant="outline"
                onClick={() => router.back()}
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

