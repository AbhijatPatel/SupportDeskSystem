import axios from 'axios';

const API_BASE = '/api';

const apiClient = axios.create({
  baseURL: API_BASE,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
});

export const api = {
  // Tickets
  getTickets: async (params = {}) => {
    // Filter out undefined/empty params
    const cleanParams = {};
    Object.keys(params).forEach((key) => {
      if (params[key] !== '' && params[key] !== undefined && params[key] !== null && params[key] !== 'ALL') {
        cleanParams[key] = params[key];
      }
    });
    const response = await apiClient.get('/tickets', { params: cleanParams });
    return response.data;
  },

  getTicketById: async (id) => {
    const response = await apiClient.get(`/tickets/${id}`);
    return response.data;
  },

  createTicket: async (ticketData) => {
    const response = await apiClient.post('/tickets', ticketData);
    return response.data;
  },

  updateTicket: async (id, ticketData) => {
    const response = await apiClient.put(`/tickets/${id}`, ticketData);
    return response.data;
  },

  addPublicReply: async (id, replyData) => {
    const response = await apiClient.post(`/tickets/${id}/replies`, replyData);
    return response.data;
  },

  addInternalNote: async (id, noteData) => {
    const response = await apiClient.post(`/tickets/${id}/notes`, noteData);
    return response.data;
  },

  getTicketEntries: async (id) => {
    const response = await apiClient.get(`/tickets/${id}/entries`);
    return response.data;
  },

  getTicketHistory: async (id) => {
    const response = await apiClient.get(`/tickets/${id}/history`);
    return response.data;
  },

  // Agents
  getAgents: async () => {
    const response = await apiClient.get('/agents');
    return response.data;
  },

  // Dashboard
  getDashboardStats: async () => {
    const response = await apiClient.get('/dashboard');
    return response.data;
  },
};

export default api;
