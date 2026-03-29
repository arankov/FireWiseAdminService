import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../api/client';
import StatusBadge from '../components/common/StatusBadge';

export default function ImportSimulator() {
  const [sources, setSources] = useState([]);
  const [selectedSource, setSelectedSource] = useState('');
  const [count, setCount] = useState(5);
  const [results, setResults] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    api.getSources().then(setSources).catch(e => setError(e.message));
  }, []);

  const handleImport = async (e) => {
    e.preventDefault();
    if (!selectedSource) return;
    setLoading(true);
    setError(null);
    setResults(null);
    try {
      const data = await api.simulateImport(selectedSource, count);
      setResults(data);
    } catch (e) {
      setError(e.message);
    }
    setLoading(false);
  };

  return (
    <div>
      <div className="page-header"><h1>Import Simulator</h1></div>

      <div className="card" style={{ marginBottom: 20 }}>
        <p style={{ marginBottom: 12, color: '#666', fontSize: 13 }}>
          Simulate importing plant data from a source database. This creates queue entries with
          partially populated attributes (30-70% complete) to demonstrate the review workflow.
        </p>

        <form onSubmit={handleImport} style={{ display: 'flex', gap: 12, alignItems: 'end', flexWrap: 'wrap' }}>
          <div style={{ flex: 1, minWidth: 200 }}>
            <label style={{ display: 'block', fontSize: 12, fontWeight: 600, marginBottom: 4 }}>Source</label>
            <select value={selectedSource} onChange={e => setSelectedSource(e.target.value)}>
              <option value="">Select a source...</option>
              {sources.map(s => (
                <option key={s.id} value={s.id}>{s.name} {s.region ? `(${s.region})` : ''}</option>
              ))}
            </select>
          </div>
          <div style={{ width: 100 }}>
            <label style={{ display: 'block', fontSize: 12, fontWeight: 600, marginBottom: 4 }}>Count</label>
            <input type="number" min={1} max={50} value={count} onChange={e => setCount(parseInt(e.target.value) || 1)} />
          </div>
          <button type="submit" className="btn-primary" disabled={loading || !selectedSource}>
            {loading ? 'Importing...' : 'Simulate Import'}
          </button>
        </form>
      </div>

      {error && <div className="error">{error}</div>}

      {results && (
        <div>
          <h2 style={{ fontSize: 16, marginBottom: 12 }}>
            Created {results.length} queue entries
          </h2>
          <table>
            <thead>
              <tr><th>Plant</th><th>Status</th><th>Completeness</th><th>Action</th></tr>
            </thead>
            <tbody>
              {results.map(r => (
                <tr key={r.id}>
                  <td>
                    <em>{r.plantGenus} {r.plantSpecies}</em>
                    {r.plantCommonName && <div style={{ fontSize: 12, color: '#666' }}>{r.plantCommonName}</div>}
                  </td>
                  <td><StatusBadge status={r.status} /></td>
                  <td style={{ fontSize: 13 }}>{r.completenessPercent}%</td>
                  <td><Link to={`/queue/${r.id}`}>Review</Link></td>
                </tr>
              ))}
            </tbody>
          </table>
          <div style={{ marginTop: 12 }}>
            <Link to="/queue"><button className="btn-primary">Go to Review Queue</button></Link>
          </div>
        </div>
      )}
    </div>
  );
}
