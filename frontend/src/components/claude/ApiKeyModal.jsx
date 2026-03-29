import { useState, useEffect } from 'react';
import { api } from '../../api/client';

function getSessionId() {
  let id = sessionStorage.getItem('claude_session_id');
  if (!id) {
    id = crypto.randomUUID();
    sessionStorage.setItem('claude_session_id', id);
  }
  return id;
}

export { getSessionId };

export default function ApiKeyModal({ onKeyStored, onCancel }) {
  const [apiKey, setApiKey] = useState('');
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);

  const handleSave = async () => {
    if (!apiKey.trim()) return;
    setSaving(true);
    setError(null);
    try {
      await api.setClaudeApiKey(getSessionId(), apiKey.trim());
      onKeyStored();
    } catch (e) {
      setError(e.message);
    }
    setSaving(false);
  };

  return (
    <div className="card" style={{ marginBottom: 16, border: '2px solid #7c5cbf', background: '#f8f6ff' }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 12 }}>
        <span style={{ fontSize: 18 }}>Claude AI</span>
        <span style={{ fontSize: 13, color: '#666' }}>Enter your Anthropic API key (cached for 10 min only, never persisted)</span>
      </div>

      <div style={{ display: 'flex', gap: 8, alignItems: 'center' }}>
        <input
          type="password"
          placeholder="sk-ant-..."
          value={apiKey}
          onChange={e => setApiKey(e.target.value)}
          onKeyDown={e => e.key === 'Enter' && handleSave()}
          style={{ flex: 1, fontFamily: 'monospace', fontSize: 13 }}
        />
        <button className="btn-primary" onClick={handleSave} disabled={saving || !apiKey.trim()}
          style={{ background: '#7c5cbf', whiteSpace: 'nowrap' }}>
          {saving ? 'Saving...' : 'Save Key'}
        </button>
        <button onClick={onCancel} style={{ background: 'none', border: '1px solid #ccc', padding: '6px 12px', borderRadius: 4, cursor: 'pointer' }}>
          Cancel
        </button>
      </div>

      {error && <div style={{ color: '#c0392b', fontSize: 13, marginTop: 8 }}>{error}</div>}
    </div>
  );
}
