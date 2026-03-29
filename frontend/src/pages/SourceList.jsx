import { useState, useEffect } from 'react';
import { api } from '../api/client';

export default function SourceList() {
  const [sources, setSources] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    api.getSources().then(setSources).catch(e => setError(e.message));
  }, []);

  if (error) return <div className="error">{error}</div>;
  if (!sources) return <div className="loading">Loading sources...</div>;

  return (
    <div>
      <div className="page-header">
        <h1>Data Sources</h1>
        <span style={{ color: '#666', fontSize: 13 }}>{sources.length} sources</span>
      </div>

      <table>
        <thead>
          <tr>
            <th>Name</th>
            <th>Region</th>
            <th>Target Location</th>
            <th>Topics</th>
            <th>Ref Code</th>
          </tr>
        </thead>
        <tbody>
          {sources.map(s => (
            <tr key={s.id}>
              <td>
                {s.url ? (
                  <a href={s.url} target="_blank" rel="noopener noreferrer">{s.name}</a>
                ) : s.name}
              </td>
              <td>{s.region || '—'}</td>
              <td>{s.targetLocation || '—'}</td>
              <td style={{ fontSize: 12, maxWidth: 200, overflow: 'hidden', textOverflow: 'ellipsis' }}>
                {s.topicsAddressed || '—'}
              </td>
              <td style={{ fontFamily: 'monospace', fontSize: 12 }}>{s.refCode || '—'}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
