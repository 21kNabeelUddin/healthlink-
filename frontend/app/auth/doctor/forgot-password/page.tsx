'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { useForm } from 'react-hook-form';
import { authApi, getUserFriendlyError } from '@/lib/api';
import { toast } from 'react-hot-toast';
import Input from '@/components/ui/Input';
import Button from '@/components/ui/Button';
import AuthLayout from '@/components/auth/AuthLayout';

interface ForgotPasswordForm {
  email: string;
}

export default function DoctorForgotPassword() {
  const router = useRouter();
  const [isLoading, setIsLoading] = useState(false);
  const { register, handleSubmit, formState: { errors } } = useForm<ForgotPasswordForm>();

  const onSubmit = async (data: ForgotPasswordForm) => {
    setIsLoading(true);
    try {
      await authApi.forgotPassword(data.email);
      toast.success('If an account exists for this email, a reset code has been sent.');
      router.push(`/auth/doctor/reset-password?email=${encodeURIComponent(data.email)}`);
    } catch (error: any) {
      toast.error(error.userMessage || getUserFriendlyError(error, 'Could not start password reset.'));
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <AuthLayout
      role="DOCTOR"
      title="Forgot Password"
      subtitle="Enter your email and we’ll send you a 6‑digit code to reset your password."
    >
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        <Input
          label="Email"
          type="email"
          autoComplete="email"
          {...register('email', { required: 'Email is required' })}
          error={errors.email?.message}
        />

        <Button
          type="submit"
          isLoading={isLoading}
          className="w-full bg-gradient-to-r from-blue-500 to-cyan-500 hover:from-blue-600 hover:to-cyan-600"
        >
          Send reset code
        </Button>
      </form>
    </AuthLayout>
  );
}


