import { useState, useEffect } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { api } from '../api/client';
import StatusBadge from '../components/common/StatusBadge';
import CompletenessBar from '../components/common/CompletenessBar';
import Pagination from '../components/common/Pagination';

const STATUS_TABS = ['ALL', 'NEW', 'WIP', 'APPROVED', 'REJECTED'];

export default function QueueList() {
  const [searchParams, setSearchParams] = useSearchParams();
  const [queue, setQueue] = useState(null);
  const [error, setError] = useState(null);
  const [page, setPage] = useState(0);

  const activeStatus = searchParams.get('status') || 'ALL';

  const load = (status, p) => {
    const s = status === 'ALL' ? null : status;
    api.getQueue(s, null, p, 20).then(setQueue).catch(e => setError(e.message));
  };

  useEffect(() => { load(activeStatus, page); }, [activeStatus, page]);

  const switchTab = (tab) => {
    setPage(0);
    if (tab === 'ALL') {
      setSearchParams({});
    } else {
      setSearchParams({ status: tab });
    }
  };

  if (error) return <div className="error">{error}</div>;

  return (
    <div>
      <div className="page-header"><h1>Review Queue</h1></div>

      <div style={{ display: 'flex', gap: 4, marginBottom: 16 }}>
        {STATUS_TABS.map(tab => (
          <button
            key={tab}
            onClick={() => switchTab(tab)}
            style={{
              background: activeStatus === tab ? 'var(--primary)' : 'var(--bg-card)',
              color: activeStatus === tab ? '#fff' : 'var(--text)',
              fontWeight: activeStatus === tab ? 600 : 400,
              borderRadius: 20,
              padding: '6px 16px',
              fontSize: 13,
            }}
          >
            {tab === 'WIP' ? 'In Progress' : tab.charAt(0) + tab.slice(1).toLowerCase()}
          </button>
        ))}
      </div>

      {!queue ? <div className="loading">Loading queue...</div> : (
        <>
          {queue.content.length === 0 && <div className="card" style={{ textAlign: 'center', color: '#999' }}>No entries found</div>}
          {queue.content.length > 0 && (
            <table>
              <thead>
                <tr>
                  <th>Plant</th>
                  <th>Source</th>
                  <th>Status</th>
                  <th style={{ width: 150 }}>Completeness</th>
                  <th>Created</th>
                </tr>
              </thead>
              <tbody>
                {queue.content.map(entry => (
                  <tr key={entry.id}>
                    <td>
                      <Link to={`/queue/${entry.id}`}>
                        <em>{entry.plantGenus} {entry.plantSpecies}</em>
                      </Link>
                      {entry.plantCommonName && <div style={{ fontSize: 12, color: '#666' }}>{entry.plantCommonName}</div>}
                    </td>
                    <td style={{ fontSize: 13 }}>{entry.sourceName || '—'}</td>
                    <td><StatusBadge status={entry.status} /></td>
                    <td><CompletenessBar percent={entry.completenessPercent} /></td>
                    <td style={{ fontSize: 12, color: '#666' }}>
                      {entry.createdAt ? new Date(entry.createdAt).toLocaleDateString() : '—'}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
          <Pagination page={page} totalPages={queue.totalPages} onPageChange={setPage} />
        </>
      )}
    </div>
  );
}
