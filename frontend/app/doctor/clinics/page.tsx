'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/contexts/AuthContext';
import { doctorApi } from '@/lib/api';
import { Clinic } from '@/types';
import { toast } from 'react-hot-toast';
import DashboardLayout from '@/components/layout/DashboardLayout';
import Card from '@/components/ui/Card';
import Button from '@/components/ui/Button';
import Link from 'next/link';

export default function ClinicsPage() {
  const { user } = useAuth();
  const router = useRouter();
  const [clinics, setClinics] = useState<Clinic[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    if (user?.id) {
      loadClinics();
    }
  }, [user]);

  const loadClinics = async () => {
    if (!user?.id) return;
    
    setIsLoading(true);
    try {
      const response = await doctorApi.getClinics(user.id);
      if (response.success && response.data) {
        setClinics(response.data);
      }
    } catch (error: any) {
      toast.error('Failed to load clinics');
    } finally {
      setIsLoading(false);
    }
  };

  const handleToggleStatus = async (clinicId: number) => {
    if (!user?.id) return;

    try {
      const response = await doctorApi.toggleClinicStatus(user.id, clinicId);
      if (response.success) {
        toast.success(response.message || 'Clinic status updated');
        loadClinics();
      } else {
        toast.error(response.message);
      }
    } catch (error: any) {
      toast.error('Failed to update clinic status');
    }
  };

  const handleDelete = async (clinicId: number) => {
    if (!user?.id) return;
    if (!confirm('Are you sure you want to delete this clinic?')) return;

    try {
      const response = await doctorApi.deleteClinic(user.id, clinicId);
      if (response.success) {
        toast.success('Clinic deleted');
        loadClinics();
      } else {
        toast.error(response.message);
      }
    } catch (error: any) {
      toast.error('Failed to delete clinic');
    }
  };

  if (isLoading) {
    return (
      <DashboardLayout requiredUserType="DOCTOR">
        <div className="text-center">Loading...</div>
      </DashboardLayout>
    );
  }

  return (
    <DashboardLayout requiredUserType="DOCTOR">
      <div className="mb-8 flex justify-between items-center">
        <div>
          <h1 className="text-4xl font-bold text-gray-800 mb-2">My Clinics</h1>
          <p className="text-gray-600">Manage your clinic information</p>
        </div>
        <Link href="/doctor/clinics/new">
          <Button>Add New Clinic</Button>
        </Link>
      </div>

      <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
        {clinics.length === 0 ? (
          <div className="col-span-full text-center py-8 text-gray-500">
            No clinics found. Create your first clinic!
          </div>
        ) : (
          clinics.map((clinic) => (
            <Card key={clinic.id}>
              <div className="space-y-3">
                <div>
                  <h3 className="text-xl font-semibold text-gray-800">{clinic.name}</h3>
                  <span className={`inline-block mt-2 px-2 py-1 rounded text-xs ${
                    clinic.isActive ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'
                  }`}>
                    {clinic.isActive ? 'Active' : 'Inactive'}
                  </span>
                </div>

                <div className="space-y-1 text-sm text-gray-600">
                  <p>{clinic.address}</p>
                  <p>{clinic.city}, {clinic.state} {clinic.zipCode}</p>
                  <p>Phone: {clinic.phoneNumber}</p>
                  <p>Email: {clinic.email}</p>
                  <p>Hours: {clinic.openingTime} - {clinic.closingTime}</p>
                </div>

                <div className="space-y-2">
                  <Link href={`/doctor/clinics/${clinic.id}/edit`}>
                    <Button variant="outline" className="w-full">Edit</Button>
                  </Link>
                  <Button
                    variant="secondary"
                    className="w-full"
                    onClick={() => handleToggleStatus(clinic.id)}
                  >
                    {clinic.isActive ? 'Deactivate' : 'Activate'}
                  </Button>
                  <Button
                    variant="danger"
                    className="w-full"
                    onClick={() => handleDelete(clinic.id)}
                  >
                    Delete
                  </Button>
                </div>
              </div>
            </Card>
          ))
        )}
      </div>
    </DashboardLayout>
  );
}

