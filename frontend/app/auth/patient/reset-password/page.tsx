'use client';

import { useState } from 'react';
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

export default function PatientResetPassword() {
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
      router.replace('/auth/patient/login');
    } catch (error: any) {
      toast.error(error.userMessage || getUserFriendlyError(error, 'Could not reset password.'));
    } finally {
      setIsLoading(false);
    }
  };

  const newPassword = watch('newPassword');

  return (
    <AuthLayout
      role="PATIENT"
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
          className="w-full bg-gradient-to-r from-teal-500 to-violet-600 hover:from-teal-600 hover:to-violet-700"
        >
          Reset password
        </Button>
      </form>
    </AuthLayout>
  );
}


