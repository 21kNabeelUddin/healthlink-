import axios from 'axios';
import type {
  ApiResponse,
  User,
  LoginRequest,
  LoginResponse,
  SignupRequest,
  OtpVerificationRequest,
  Doctor,
  Appointment,
  AppointmentRequest,
  MedicalHistory,
  MedicalHistoryRequest,
  Clinic,
  ClinicRequest,
  ZoomMeeting,
  PatientProfileUpdateRequest,
} from '@/types';

const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add auth token if available
api.interceptors.request.use((config) => {
  if (typeof window !== 'undefined') {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
  }
  return config;
});

// ==================== PATIENT API ====================

export const patientApi = {
  signup: async (data: SignupRequest): Promise<ApiResponse<string>> => {
    const response = await api.post('/api/patient/signup', data);
    return response.data;
  },

  verifyOtp: async (data: OtpVerificationRequest): Promise<ApiResponse<User>> => {
    const response = await api.post('/api/patient/verify-otp', data);
    return response.data;
  },

  login: async (data: LoginRequest): Promise<ApiResponse<LoginResponse>> => {
    const response = await api.post('/api/patient/login', data);
    return response.data;
  },

  getDoctors: async (specialization?: string): Promise<ApiResponse<Doctor[]>> => {
    const params = specialization ? { specialization } : {};
    const response = await api.get('/api/patient/doctors', { params });
    return response.data;
  },

  getDoctorById: async (doctorId: number): Promise<ApiResponse<Doctor>> => {
    const response = await api.get(`/api/patient/doctors/${doctorId}`);
    return response.data;
  },

  // Medical History
  getMedicalHistories: async (patientId: number, status?: string): Promise<ApiResponse<MedicalHistory[]>> => {
    const params = status ? { status } : {};
    const response = await api.get(`/api/patient/${patientId}/medical-history`, { params });
    return response.data;
  },

  getMedicalHistoryById: async (patientId: number, historyId: number): Promise<ApiResponse<MedicalHistory>> => {
    const response = await api.get(`/api/patient/${patientId}/medical-history/${historyId}`);
    return response.data;
  },

  createMedicalHistory: async (patientId: number, data: MedicalHistoryRequest): Promise<ApiResponse<MedicalHistory>> => {
    const response = await api.post(`/api/patient/${patientId}/medical-history`, data);
    return response.data;
  },

  updateMedicalHistory: async (patientId: number, historyId: number, data: MedicalHistoryRequest): Promise<ApiResponse<MedicalHistory>> => {
    const response = await api.put(`/api/patient/${patientId}/medical-history/${historyId}`, data);
    return response.data;
  },

  deleteMedicalHistory: async (patientId: number, historyId: number): Promise<ApiResponse<null>> => {
    const response = await api.delete(`/api/patient/${patientId}/medical-history/${historyId}`);
    return response.data;
  },

  // Appointments
  getAppointments: async (patientId: number, status?: string): Promise<ApiResponse<Appointment[]>> => {
    const params = status ? { status } : {};
    const response = await api.get(`/api/patient/${patientId}/appointments`, { params });
    return response.data;
  },

  getAppointmentById: async (patientId: number, appointmentId: number): Promise<ApiResponse<Appointment>> => {
    const response = await api.get(`/api/patient/${patientId}/appointments/${appointmentId}`);
    return response.data;
  },

  createAppointment: async (patientId: number, data: AppointmentRequest): Promise<ApiResponse<Appointment>> => {
    const response = await api.post(`/api/patient/${patientId}/appointments`, data);
    return response.data;
  },

  updateAppointment: async (patientId: number, appointmentId: number, data: AppointmentRequest): Promise<ApiResponse<Appointment>> => {
    const response = await api.put(`/api/patient/${patientId}/appointments/${appointmentId}`, data);
    return response.data;
  },

  cancelAppointment: async (patientId: number, appointmentId: number): Promise<ApiResponse<null>> => {
    const response = await api.post(`/api/patient/${patientId}/appointments/${appointmentId}/cancel`);
    return response.data;
  },

  getZoomMeeting: async (patientId: number, appointmentId: number): Promise<ApiResponse<ZoomMeeting>> => {
    const response = await api.get(`/api/patient/${patientId}/appointments/${appointmentId}/zoom-meeting`);
    return response.data;
  },

  updateProfile: async (
    patientId: number,
    data: PatientProfileUpdateRequest,
  ): Promise<ApiResponse<User>> => {
    const response = await api.put(`/api/patient/${patientId}/profile`, data);
    return response.data;
  },
};

// ==================== DOCTOR API ====================

export const doctorApi = {
  signup: async (data: SignupRequest): Promise<ApiResponse<string>> => {
    const response = await api.post('/api/doctor/signup', data);
    return response.data;
  },

  verifyOtp: async (data: OtpVerificationRequest): Promise<ApiResponse<User>> => {
    const response = await api.post('/api/doctor/verify-otp', data);
    return response.data;
  },

  login: async (data: LoginRequest): Promise<ApiResponse<LoginResponse>> => {
    const response = await api.post('/api/doctor/login', data);
    return response.data;
  },

  // Clinics
  getClinics: async (doctorId: number, active?: boolean): Promise<ApiResponse<Clinic[]>> => {
    const params = active !== undefined ? { active: active.toString() } : {};
    const response = await api.get(`/api/doctor/${doctorId}/clinic`, { params });
    return response.data;
  },

  getClinicById: async (doctorId: number, clinicId: number): Promise<ApiResponse<Clinic>> => {
    const response = await api.get(`/api/doctor/${doctorId}/clinic/${clinicId}`);
    return response.data;
  },

  createClinic: async (doctorId: number, data: ClinicRequest): Promise<ApiResponse<Clinic>> => {
    const response = await api.post(`/api/doctor/${doctorId}/clinic`, data);
    return response.data;
  },

  updateClinic: async (doctorId: number, clinicId: number, data: ClinicRequest): Promise<ApiResponse<Clinic>> => {
    const response = await api.put(`/api/doctor/${doctorId}/clinic/${clinicId}`, data);
    return response.data;
  },

  deleteClinic: async (doctorId: number, clinicId: number): Promise<ApiResponse<null>> => {
    const response = await api.delete(`/api/doctor/${doctorId}/clinic/${clinicId}`);
    return response.data;
  },

  toggleClinicStatus: async (doctorId: number, clinicId: number): Promise<ApiResponse<Clinic>> => {
    const response = await api.patch(`/api/doctor/${doctorId}/clinic/${clinicId}/toggle-status`);
    return response.data;
  },

  // Appointments
  getAppointments: async (doctorId: number, status?: string): Promise<ApiResponse<Appointment[]>> => {
    const params = status ? { status } : {};
    const response = await api.get(`/api/doctor/${doctorId}/appointments`, { params });
    return response.data;
  },

  confirmAppointment: async (doctorId: number, appointmentId: number): Promise<ApiResponse<Appointment>> => {
    const response = await api.post(`/api/doctor/${doctorId}/appointments/${appointmentId}/confirm`);
    return response.data;
  },

  rejectAppointment: async (doctorId: number, appointmentId: number): Promise<ApiResponse<Appointment>> => {
    const response = await api.post(`/api/doctor/${doctorId}/appointments/${appointmentId}/reject`);
    return response.data;
  },

  completeAppointment: async (doctorId: number, appointmentId: number): Promise<ApiResponse<Appointment>> => {
    const response = await api.post(`/api/doctor/${doctorId}/appointments/${appointmentId}/complete`);
    return response.data;
  },

  getZoomMeeting: async (doctorId: number, appointmentId: number): Promise<ApiResponse<ZoomMeeting>> => {
    const response = await api.get(`/api/doctor/${doctorId}/appointments/${appointmentId}/zoom-meeting`);
    return response.data;
  },
};

// ==================== ADMIN API ====================

export interface AdminDashboard {
  totalPatients: number;
  totalDoctors: number;
  totalAdmins: number;
  totalAppointments: number;
  totalClinics: number;
  totalMedicalHistories: number;
  pendingAppointments: number;
  confirmedAppointments: number;
  completedAppointments: number;
}

export const adminApi = {
  signup: async (data: SignupRequest): Promise<ApiResponse<string>> => {
    const response = await api.post('/api/admin/signup', data);
    return response.data;
  },

  verifyOtp: async (data: OtpVerificationRequest): Promise<ApiResponse<User>> => {
    const response = await api.post('/api/admin/verify-otp', data);
    return response.data;
  },

  login: async (data: LoginRequest): Promise<ApiResponse<LoginResponse>> => {
    const response = await api.post('/api/admin/login', data);
    return response.data;
  },

  getDashboard: async (): Promise<ApiResponse<AdminDashboard>> => {
    const response = await api.get('/api/admin/dashboard');
    return response.data;
  },

  // Patient Management
  getAllPatients: async (): Promise<ApiResponse<User[]>> => {
    const response = await api.get('/api/admin/patients');
    return response.data;
  },

  getPatientById: async (patientId: number): Promise<ApiResponse<User>> => {
    const response = await api.get(`/api/admin/patients/${patientId}`);
    return response.data;
  },

  deletePatient: async (patientId: number): Promise<ApiResponse<null>> => {
    const response = await api.delete(`/api/admin/patients/${patientId}`);
    return response.data;
  },

  // Doctor Management
  getAllDoctors: async (): Promise<ApiResponse<User[]>> => {
    const response = await api.get('/api/admin/doctors');
    return response.data;
  },

  getDoctorById: async (doctorId: number): Promise<ApiResponse<User>> => {
    const response = await api.get(`/api/admin/doctors/${doctorId}`);
    return response.data;
  },

  deleteDoctor: async (doctorId: number): Promise<ApiResponse<null>> => {
    const response = await api.delete(`/api/admin/doctors/${doctorId}`);
    return response.data;
  },

  // Admin Management
  getAllAdmins: async (): Promise<ApiResponse<User[]>> => {
    const response = await api.get('/api/admin/admins');
    return response.data;
  },

  getAdminById: async (adminId: number): Promise<ApiResponse<User>> => {
    const response = await api.get(`/api/admin/admins/${adminId}`);
    return response.data;
  },

  deleteAdmin: async (adminId: number): Promise<ApiResponse<null>> => {
    const response = await api.delete(`/api/admin/admins/${adminId}`);
    return response.data;
  },

  // Appointment Management
  getAllAppointments: async (status?: string, appointmentType?: string): Promise<ApiResponse<Appointment[]>> => {
    const params: any = {};
    if (status) params.status = status;
    if (appointmentType) params.appointmentType = appointmentType;
    const response = await api.get('/api/admin/appointments', { params });
    return response.data;
  },

  getAppointmentById: async (appointmentId: number): Promise<ApiResponse<Appointment>> => {
    const response = await api.get(`/api/admin/appointments/${appointmentId}`);
    return response.data;
  },

  deleteAppointment: async (appointmentId: number): Promise<ApiResponse<null>> => {
    const response = await api.delete(`/api/admin/appointments/${appointmentId}`);
    return response.data;
  },

  updateAppointmentStatus: async (appointmentId: number, status: string): Promise<ApiResponse<Appointment>> => {
    const response = await api.patch(`/api/admin/appointments/${appointmentId}/status`, null, {
      params: { status },
    });
    return response.data;
  },

  // Clinic Management
  getAllClinics: async (doctorId?: number): Promise<ApiResponse<Clinic[]>> => {
    const params = doctorId ? { doctorId } : {};
    const response = await api.get('/api/admin/clinics', { params });
    return response.data;
  },

  getClinicById: async (clinicId: number): Promise<ApiResponse<Clinic>> => {
    const response = await api.get(`/api/admin/clinics/${clinicId}`);
    return response.data;
  },

  deleteClinic: async (clinicId: number): Promise<ApiResponse<null>> => {
    const response = await api.delete(`/api/admin/clinics/${clinicId}`);
    return response.data;
  },

  // Medical History Management
  getAllMedicalHistories: async (patientId?: number, status?: string): Promise<ApiResponse<MedicalHistory[]>> => {
    const params: any = {};
    if (patientId) params.patientId = patientId;
    if (status) params.status = status;
    const response = await api.get('/api/admin/medical-histories', { params });
    return response.data;
  },

  getMedicalHistoryById: async (historyId: number): Promise<ApiResponse<MedicalHistory>> => {
    const response = await api.get(`/api/admin/medical-histories/${historyId}`);
    return response.data;
  },

  deleteMedicalHistory: async (historyId: number): Promise<ApiResponse<null>> => {
    const response = await api.delete(`/api/admin/medical-histories/${historyId}`);
    return response.data;
  },
};

export default api;

