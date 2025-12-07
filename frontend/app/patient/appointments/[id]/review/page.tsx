'use client';

import { useState, useEffect } from 'react';
import { useRouter, useParams } from 'next/navigation';
import { useAuth } from '@/contexts/AuthContext';
import { appointmentsApi, reviewsApi } from '@/lib/api';
import { Appointment } from '@/types';
import { toast } from 'react-hot-toast';
import DashboardLayout from '@/components/layout/DashboardLayout';
import Card from '@/components/ui/Card';
import Button from '@/components/ui/Button';
import { Star, User, Calendar, MessageSquare, CheckCircle2 } from 'lucide-react';
import Link from 'next/link';

export default function ReviewAppointmentPage() {
  const router = useRouter();
  const params = useParams();
  const { user } = useAuth();
  const appointmentId = params?.id as string;

  const [appointment, setAppointment] = useState<Appointment | null>(null);
  const [rating, setRating] = useState<number>(0);
  const [hoveredRating, setHoveredRating] = useState<number>(0);
  const [comments, setComments] = useState<string>('');
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isSubmitted, setIsSubmitted] = useState(false);

  useEffect(() => {
    if (appointmentId && user?.id) {
      loadAppointment();
    }
  }, [appointmentId, user?.id]);

  const loadAppointment = async () => {
    if (!appointmentId) return;
    setIsLoading(true);
    try {
      const apt = await appointmentsApi.getById(appointmentId);
      
      // Verify appointment belongs to patient and is completed
      if (apt.patientId.toString() !== user?.id) {
        toast.error('You can only review your own appointments');
        router.push('/patient/appointments');
        return;
      }

      if (apt.status !== 'COMPLETED') {
        toast.error('You can only review completed appointments');
        router.push('/patient/appointments');
        return;
      }

      setAppointment(apt);
    } catch (error: any) {
      toast.error('Failed to load appointment details');
      console.error('Appointment load error:', error);
      router.push('/patient/appointments');
    } finally {
      setIsLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (rating === 0) {
      toast.error('Please select a rating');
      return;
    }

    if (!appointment) return;

    setIsSubmitting(true);
    try {
      await reviewsApi.create({
        appointmentId: appointment.id.toString(),
        doctorId: appointment.doctorId.toString(),
        rating,
        comments: comments.trim() || undefined,
      });

      setIsSubmitted(true);
      toast.success('Thank you for your review!');
      
      // Redirect after 3 seconds
      setTimeout(() => {
        router.push('/patient/appointments');
      }, 3000);
    } catch (error: any) {
      if (error.response?.status === 400 && error.response?.data?.message?.includes('already reviewed')) {
        toast.error('You have already reviewed this appointment');
        router.push('/patient/appointments');
      } else {
        toast.error(error.response?.data?.message || 'Failed to submit review');
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  if (isLoading) {
    return (
      <DashboardLayout requiredUserType="PATIENT">
        <div className="min-h-screen bg-gradient-to-b from-blue-50 to-white">
          <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            <div className="text-center py-20">
              <div className="inline-block animate-spin rounded-full h-12 w-12 border-4 border-teal-500 border-t-transparent"></div>
              <p className="mt-4 text-slate-600">Loading appointment details...</p>
            </div>
          </div>
        </div>
      </DashboardLayout>
    );
  }

  if (!appointment) {
    return null;
  }

  if (isSubmitted) {
    return (
      <DashboardLayout requiredUserType="PATIENT">
        <div className="min-h-screen bg-gradient-to-b from-blue-50 to-white">
          <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            <Card className="text-center py-12">
              <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <CheckCircle2 className="w-8 h-8 text-green-600" />
              </div>
              <h2 className="text-2xl font-bold text-slate-900 mb-2">Thank You!</h2>
              <p className="text-slate-600 mb-6">Your review has been submitted successfully.</p>
              <p className="text-sm text-slate-500">Redirecting to appointments...</p>
            </Card>
          </div>
        </div>
      </DashboardLayout>
    );
  }

  return (
    <DashboardLayout requiredUserType="PATIENT">
      <div className="min-h-screen bg-gradient-to-b from-blue-50 via-white to-blue-50">
        <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
          {/* Header */}
          <div className="mb-6">
            <Link
              href="/patient/appointments"
              className="inline-flex items-center gap-2 text-teal-600 hover:text-teal-700 text-sm font-medium mb-4"
            >
              ← Back to Appointments
            </Link>
            <div className="flex items-center gap-3 mb-2">
              <div className="w-12 h-12 bg-gradient-to-br from-teal-500 to-violet-600 rounded-xl flex items-center justify-center">
                <Star className="w-6 h-6 text-white" />
              </div>
              <div>
                <h1 className="text-3xl font-bold text-slate-900">Rate Your Experience</h1>
                <p className="text-slate-600 text-sm mt-1">Help us improve by sharing your feedback</p>
              </div>
            </div>
          </div>

          {/* Appointment Info Card */}
          <Card className="mb-6 bg-gradient-to-br from-teal-50 to-violet-50 border-teal-200">
            <div className="space-y-3">
              <div className="flex items-start gap-4">
                <div className="w-10 h-10 bg-teal-500 rounded-lg flex items-center justify-center flex-shrink-0">
                  <User className="w-5 h-5 text-white" />
                </div>
                <div className="flex-1">
                  <h3 className="font-semibold text-slate-900 mb-1">Dr. {appointment.doctorName}</h3>
                  {appointment.doctorSpecialization && (
                    <p className="text-sm text-slate-600 mb-2">{appointment.doctorSpecialization}</p>
                  )}
                  <div className="flex items-center gap-4 text-sm text-slate-600">
                    <div className="flex items-center gap-1">
                      <Calendar className="w-4 h-4" />
                      <span>{new Date(appointment.appointmentDateTime).toLocaleDateString()}</span>
                    </div>
                    {appointment.appointmentType && (
                      <span className="px-2 py-1 bg-slate-100 rounded text-xs">
                        {appointment.appointmentType === 'ONLINE' ? 'Online' : 'On-site'}
                      </span>
                    )}
                  </div>
                </div>
              </div>
            </div>
          </Card>

          <Card className="shadow-lg">
            <form onSubmit={handleSubmit} className="space-y-6">
              {/* Rating */}
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-4">
                  How would you rate your experience? *
                </label>
                <div className="flex items-center gap-2 justify-center">
                  {[1, 2, 3, 4, 5].map((star) => (
                    <button
                      key={star}
                      type="button"
                      onClick={() => setRating(star)}
                      onMouseEnter={() => setHoveredRating(star)}
                      onMouseLeave={() => setHoveredRating(0)}
                      className="focus:outline-none transition-transform hover:scale-110"
                    >
                      <Star
                        className={`w-12 h-12 ${
                          star <= (hoveredRating || rating)
                            ? 'fill-yellow-400 text-yellow-400'
                            : 'text-slate-300'
                        }`}
                      />
                    </button>
                  ))}
                </div>
                <div className="text-center mt-2">
                  <p className="text-sm text-slate-600">
                    {rating === 0 && 'Select a rating'}
                    {rating === 1 && 'Poor'}
                    {rating === 2 && 'Fair'}
                    {rating === 3 && 'Good'}
                    {rating === 4 && 'Very Good'}
                    {rating === 5 && 'Excellent'}
                  </p>
                </div>
              </div>

              {/* Comments */}
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-2">
                  <MessageSquare className="w-4 h-4 inline mr-2" />
                  Additional Comments (Optional)
                </label>
                <textarea
                  value={comments}
                  onChange={(e) => setComments(e.target.value)}
                  className="w-full px-4 py-3 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-teal-500 focus:border-teal-500 resize-none"
                  rows={6}
                  placeholder="Share your experience, suggestions, or feedback..."
                  maxLength={2000}
                />
                <p className="mt-1 text-xs text-slate-500 text-right">
                  {comments.length}/2000 characters
                </p>
              </div>

              {/* Action Buttons */}
              <div className="flex gap-4 pt-6 border-t border-slate-200">
                <Button
                  type="button"
                  variant="secondary"
                  onClick={() => router.push('/patient/appointments')}
                  className="flex-1"
                >
                  Skip for Now
                </Button>
                <Button
                  type="submit"
                  isLoading={isSubmitting}
                  disabled={rating === 0}
                  className="flex-1 bg-gradient-to-r from-teal-500 to-violet-600 hover:from-teal-600 hover:to-violet-700 text-white"
                >
                  Submit Review
                </Button>
              </div>
            </form>
          </Card>
        </div>
      </div>
    </DashboardLayout>
  );
}

