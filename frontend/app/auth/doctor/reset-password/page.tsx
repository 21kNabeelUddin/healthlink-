'use client';

import { useState, Suspense } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import { useForm } from 'react-hook-form';
import { authApi, getUserFriendlyError } from '@/lib/api';
import { toast } from 'react-hot-toast';
import Input from '@/components/ui/Input';
import Button from '@/components/ui/Button';
import AuthLayout from '@/components/auth/AuthLayout';

interface ResetPasswordForm {
  email: string;
  otp: string;
  newPassword: string;
  confirmPassword: string;
}

function DoctorResetPasswordContent() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const emailFromQuery = searchParams.get('email') ?? '';
  const [isLoading, setIsLoading] = useState(false);
  const { register, handleSubmit, watch, formState: { errors } } = useForm<ResetPasswordForm>({
    defaultValues: { email: emailFromQuery },
  });

  const onSubmit = async (data: ResetPasswordForm) => {
    if (data.newPassword !== data.confirmPassword) {
      toast.error('Passwords do not match.');
      return;
    }

    setIsLoading(true);
    try {
      await authApi.resetPassword(data.email, data.otp, data.newPassword);
      toast.success('Password reset successfully. You can now log in.');
      router.replace('/auth/doctor/login');
    } catch (error: any) {
      toast.error(error.userMessage || getUserFriendlyError(error, 'Could not reset password.'));
    } finally {
      setIsLoading(false);
    }
  };

  const newPassword = watch('newPassword');

  return (
    <AuthLayout
      role="DOCTOR"
      title="Reset Password"
      subtitle="Enter the code from your email and choose a new password."
    >
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        <Input
          label="Email"
          type="email"
          autoComplete="email"
          {...register('email', { required: 'Email is required' })}
          error={errors.email?.message}
        />

        <Input
          label="Reset code (OTP)"
          type="text"
          maxLength={6}
          {...register('otp', { required: 'Reset code is required' })}
          error={errors.otp?.message}
        />

        <Input
          label="New password"
          type="password"
          autoComplete="new-password"
          {...register('newPassword', { required: 'New password is required' })}
          error={errors.newPassword?.message}
        />

        <Input
          label="Confirm new password"
          type="password"
          autoComplete="new-password"
          {...register('confirmPassword', {
            required: 'Please confirm your new password',
            validate: (value) => value === newPassword || 'Passwords do not match',
          })}
          error={errors.confirmPassword?.message}
        />

        <Button
          type="submit"
          isLoading={isLoading}
          className="w-full bg-gradient-to-r from-blue-500 to-cyan-500 hover:from-blue-600 hover:to-cyan-600"
        >
          Reset password
        </Button>
      </form>
    </AuthLayout>
  );
}

export default function DoctorResetPassword() {
  return (
    <Suspense fallback={
      <AuthLayout role="DOCTOR" title="Reset Password" subtitle="Loading...">
        <div className="text-center py-8">Loading...</div>
      </AuthLayout>
    }>
      <DoctorResetPasswordContent />
    </Suspense>
  );
}


