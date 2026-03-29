import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../api/client';

export default function Dashboard() {
  const [stats, setStats] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    api.getStats().then(setStats).catch(e => setError(e.message));
  }, []);

  if (error) return <div className="error">{error}</div>;
  if (!stats) return <div className="loading">Loading dashboard...</div>;

  const qc = stats.queueCounts || {};

  return (
    <div>
      <div className="page-header"><h1>Dashboard</h1></div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: 16, marginBottom: 24 }}>
        <StatCard label="Total Plants" value={stats.totalPlants} color="#2e7d32" />
        <StatCard label="Sources" value={stats.totalSources} color="#1565c0" />
        <StatCard label="Attributes" value={stats.totalAttributes} color="#6a1b9a" />
      </div>

      <h2 style={{ fontSize: 18, marginBottom: 12 }}>Review Queue</h2>
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(150px, 1fr))', gap: 16, marginBottom: 24 }}>
        <QueueCard label="New" count={qc.NEW || 0} color="#1565c0" />
        <QueueCard label="In Progress" count={qc.WIP || 0} color="#e65100" />
        <QueueCard label="Approved" count={qc.APPROVED || 0} color="#2e7d32" />
        <QueueCard label="Rejected" count={qc.REJECTED || 0} color="#c62828" />
      </div>

      <div className="card">
        <h2 style={{ fontSize: 16, marginBottom: 12 }}>Recent Activity</h2>
        {stats.recentActivity?.length === 0 && <p style={{ color: '#999' }}>No recent activity</p>}
        {stats.recentActivity?.map(a => (
          <div key={a.id} style={{ padding: '8px 0', borderBottom: '1px solid #eee', fontSize: 13 }}>
            <strong>{a.changeType}</strong> on{' '}
            <Link to={`/plants/${a.plantId}`}>{a.plantName || a.plantId}</Link>{' '}
            &mdash; {a.field} {a.changedBy && <span style={{ color: '#999' }}>by {a.changedBy}</span>}
          </div>
        ))}
      </div>
    </div>
  );
}

function StatCard({ label, value, color }) {
  return (
    <div className="card" style={{ textAlign: 'center' }}>
      <div style={{ fontSize: 32, fontWeight: 700, color }}>{value}</div>
      <div style={{ fontSize: 13, color: '#666', marginTop: 4 }}>{label}</div>
    </div>
  );
}

function QueueCard({ label, count, color }) {
  return (
    <Link to={`/queue?status=${label === 'In Progress' ? 'WIP' : label.toUpperCase()}`} style={{ textDecoration: 'none' }}>
      <div className="card" style={{ textAlign: 'center', borderLeft: `4px solid ${color}` }}>
        <div style={{ fontSize: 28, fontWeight: 700, color }}>{count}</div>
        <div style={{ fontSize: 13, color: '#666', marginTop: 4 }}>{label}</div>
      </div>
    </Link>
  );
}
