const BASE = '/api';

async function request(path, options = {}) {
  const res = await fetch(`${BASE}${path}`, {
    headers: { 'Content-Type': 'application/json', ...options.headers },
    ...options,
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({ error: res.statusText }));
    throw new Error(err.error || `Request failed: ${res.status}`);
  }
  if (res.status === 204) return null;
  return res.json();
}

export const api = {
  // Dashboard
  getStats: () => request('/dashboard/stats'),

  // Plants
  getPlants: (search = '', page = 0, size = 20) => {
    const params = new URLSearchParams({ page, size });
    if (search) params.set('search', search);
    return request(`/plants?${params}`);
  },
  getPlant: (id) => request(`/plants/${id}`),
  createPlant: (data) => request('/plants', { method: 'POST', body: JSON.stringify(data) }),
  updatePlant: (id, data) => request(`/plants/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
  deletePlant: (id) => request(`/plants/${id}`, { method: 'DELETE' }),

  // Attributes
  getAttributeTree: () => request('/attributes/tree'),

  // Queue
  getQueue: (status, sourceId, page = 0, size = 20) => {
    const params = new URLSearchParams({ page, size });
    if (status) params.set('status', status);
    if (sourceId) params.set('sourceId', sourceId);
    return request(`/queue?${params}`);
  },
  getQueueEntry: (id) => request(`/queue/${id}`),
  updateQueueEntry: (id, data) => request(`/queue/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
  changeQueueStatus: (id, status) => request(`/queue/${id}/status`, { method: 'PUT', body: JSON.stringify({ status }) }),
  approveEntry: (id, reviewedBy = 'admin') => request(`/queue/${id}/approve`, { method: 'POST', body: JSON.stringify({ reviewedBy }) }),
  rejectEntry: (id, adminNotes, reviewedBy = 'admin') => request(`/queue/${id}/reject`, { method: 'POST', body: JSON.stringify({ adminNotes, reviewedBy }) }),

  // Sources
  getSources: () => request('/sources'),

  // Import
  simulateImport: (sourceId, count) => request('/import/simulate', { method: 'POST', body: JSON.stringify({ sourceId, count }) }),

  // Claude AI
  setClaudeApiKey: (sessionId, apiKey) => request('/claude/api-key', { method: 'POST', body: JSON.stringify({ sessionId, apiKey }) }),
  getClaudeKeyStatus: (sessionId) => request(`/claude/api-key/status?sessionId=${encodeURIComponent(sessionId)}`),
  askClaude: (queueEntryId, sessionId) => request(`/queue/${queueEntryId}/ask-claude`, { method: 'POST', body: JSON.stringify({ sessionId }) }),
};
