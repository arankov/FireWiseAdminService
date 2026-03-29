import { useState } from 'react';

const CONFIDENCE_COLORS = {
  HIGH: { bg: '#d4edda', color: '#155724' },
  MEDIUM: { bg: '#fff3cd', color: '#856404' },
  LOW: { bg: '#f8d7da', color: '#721c24' },
};

export default function ClaudeSuggestionsPanel({ suggestions, onAccept, onAcceptAll }) {
  const [editValues, setEditValues] = useState({});
  const [accepted, setAccepted] = useState({});

  const handleEdit = (attrId, value) => {
    setEditValues(prev => ({ ...prev, [attrId]: value }));
  };

  const handleAccept = (suggestion) => {
    const value = editValues[suggestion.attributeId] ?? suggestion.suggestedValue;
    onAccept(suggestion.attributeId, suggestion.attributeName, value);
    setAccepted(prev => ({ ...prev, [suggestion.attributeId]: true }));
  };

  const handleAcceptAll = () => {
    const values = suggestions.map(s => ({
      attributeId: s.attributeId,
      attributeName: s.attributeName,
      value: editValues[s.attributeId] ?? s.suggestedValue,
    }));
    onAcceptAll(values);
    const allAccepted = {};
    suggestions.forEach(s => allAccepted[s.attributeId] = true);
    setAccepted(allAccepted);
  };

  if (!suggestions || suggestions.length === 0) {
    return <div className="card" style={{ color: '#666' }}>No suggestions returned.</div>;
  }

  return (
    <div className="card" style={{ marginBottom: 16, border: '2px solid #7c5cbf' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 12 }}>
        <h3 style={{ margin: 0, fontSize: 15 }}>
          Claude AI Suggestions ({suggestions.length} attributes)
        </h3>
        <button
          className="btn-primary"
          onClick={handleAcceptAll}
          style={{ background: '#7c5cbf', fontSize: 12 }}
        >
          Accept All
        </button>
      </div>

      <table style={{ fontSize: 13 }}>
        <thead>
          <tr>
            <th>Attribute</th>
            <th>Suggested Value</th>
            <th>Confidence</th>
            <th>Reasoning</th>
            <th style={{ width: 80 }}>Action</th>
          </tr>
        </thead>
        <tbody>
          {suggestions.map(s => {
            const conf = CONFIDENCE_COLORS[s.confidence] || CONFIDENCE_COLORS.LOW;
            const isAccepted = accepted[s.attributeId];
            return (
              <tr key={s.attributeId} style={isAccepted ? { opacity: 0.6, background: '#f0fff0' } : {}}>
                <td style={{ fontWeight: 500 }}>{s.attributeName}</td>
                <td>
                  <input
                    type="text"
                    value={editValues[s.attributeId] ?? s.suggestedValue}
                    onChange={e => handleEdit(s.attributeId, e.target.value)}
                    disabled={isAccepted}
                    style={{ width: '100%', fontSize: 12, padding: '3px 6px' }}
                  />
                </td>
                <td>
                  <span style={{
                    padding: '2px 8px', borderRadius: 10, fontSize: 11, fontWeight: 600,
                    background: conf.bg, color: conf.color
                  }}>
                    {s.confidence}
                  </span>
                </td>
                <td style={{ fontSize: 12, color: '#555', maxWidth: 250 }}>{s.reasoning}</td>
                <td>
                  {isAccepted
                    ? <span style={{ color: '#27ae60', fontSize: 12, fontWeight: 600 }}>Accepted</span>
                    : <button onClick={() => handleAccept(s)} style={{
                        background: '#27ae60', color: '#fff', border: 'none', padding: '3px 10px',
                        borderRadius: 3, cursor: 'pointer', fontSize: 12
                      }}>Accept</button>
                  }
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}
