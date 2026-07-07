import axios from 'axios';

export interface Supplier {
  id: number;
  name: string;
  category: string;
  annualSpend: number;
  contactEmail?: string;
  contactPhone?: string;
  riskScore?: number;
}

export interface ChatResponse {
  answer: string;
  data: any;
  timestamp: string;
}

export interface HealthResponse {
  status: string;
  app: string;
  version: string;
}

const api = axios.create({ baseURL: '/api' });

export const supplierApi = {
  getAll: () => api.get<Supplier[]>('/suppliers').then(r => r.data),
  getById: (id: number) => api.get<Supplier>(`/suppliers/${id}`).then(r => r.data),
  create: (s: Partial<Supplier>) => api.post<Supplier>('/suppliers', s).then(r => r.data),
  update: (id: number, s: Partial<Supplier>) => api.put<Supplier>(`/suppliers/${id}`, s).then(r => r.data),
  delete: (id: number) => api.delete(`/suppliers/${id}`),
  search: (q: string) => api.get<Supplier[]>(`/suppliers/search?q=${q}`).then(r => r.data),
  topBySpend: () => api.get<Supplier[]>('/suppliers/top').then(r => r.data),
  byCategory: (cat: string) => api.get<Supplier[]>(`/suppliers/category/${cat}`).then(r => r.data),
};

export const chatApi = {
  send: (message: string) => api.post<ChatResponse>('/chat', { message }).then(r => r.data),
};

export const healthApi = {
  check: () => api.get<HealthResponse>('/health').then(r => r.data),
};
