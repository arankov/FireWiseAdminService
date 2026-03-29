import { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { api } from '../api/client';
import StatusBadge from '../components/common/StatusBadge';
import CompletenessBar from '../components/common/CompletenessBar';

export default function QueueEntryDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [entry, setEntry] = useState(null);
  const [error, setError] = useState(null);
  const [confirmAction, setConfirmAction] = useState(null);
  const [rejectNotes, setRejectNotes] = useState('');
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    api.getQueueEntry(id).then(setEntry).catch(e => setError(e.message));
  }, [id]);

  const handleStatusChange = async (action) => {
    setSaving(true);
    setError(null);
    try {
      if (action === 'start') {
        const updated = await api.changeQueueStatus(id, 'WIP');
        setEntry(updated);
      } else if (action === 'approve') {
        const updated = await api.approveEntry(id);
        setEntry(updated);
      } else if (action === 'reject') {
        const updated = await api.rejectEntry(id, rejectNotes);
        setEntry(updated);
      } else if (action === 'reopen') {
        const updated = await api.changeQueueStatus(id, 'WIP');
        setEntry(updated);
      }
      setConfirmAction(null);
    } catch (e) {
      setError(e.message);
    }
    setSaving(false);
  };

  if (error) return <div className="error">{error}</div>;
  if (!entry) return <div className="loading">Loading entry...</div>;

  let proposedData = {};
  try { proposedData = JSON.parse(entry.proposedData || '{}'); } catch {}

  const proposedAttrs = proposedData.attributes || {};
  const attrCount = Object.keys(proposedAttrs).length;

  return (
    <div>
      <div style={{ marginBottom: 8 }}><Link to="/queue">&larr; Back to Queue</Link></div>

      <div className="card" style={{ marginBottom: 16 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: 12 }}>
          <div>
            <h1 style={{ fontSize: 22, marginBottom: 4 }}>
              <em>{entry.plantGenus} {entry.plantSpecies}</em>
            </h1>
            {entry.plantCommonName && <div style={{ color: '#666' }}>{entry.plantCommonName}</div>}
            <div style={{ fontSize: 13, color: '#999', marginTop: 4 }}>
              Source: {entry.sourceName || entry.sourceId || '—'}
            </div>
          </div>
          <div style={{ textAlign: 'right' }}>
            <StatusBadge status={entry.status} />
            <div style={{ width: 160, marginTop: 8 }}>
              <CompletenessBar percent={entry.completenessPercent} />
            </div>
          </div>
        </div>

        {entry.adminNotes && (
          <div style={{ marginTop: 12, padding: 10, background: '#fff8e1', borderRadius: 6, fontSize: 13 }}>
            <strong>Admin Notes:</strong> {entry.adminNotes}
          </div>
        )}

        {entry.reviewedBy && (
          <div style={{ fontSize: 12, color: '#999', marginTop: 8 }}>
            Reviewed by: {entry.reviewedBy}
          </div>
        )}
      </div>

      {/* Workflow Buttons */}
      <div style={{ display: 'flex', gap: 8, marginBottom: 20 }}>
        {entry.status === 'NEW' && (
          <button className="btn-accent" onClick={() => handleStatusChange('start')} disabled={saving}>
            Start Review
          </button>
        )}
        {(entry.status === 'NEW' || entry.status === 'WIP') && (
          <>
            <button className="btn-primary" onClick={() => setConfirmAction('approve')} disabled={saving}>
              Approve
            </button>
            <button className="btn-danger" onClick={() => setConfirmAction('reject')} disabled={saving}>
              Reject
            </button>
          </>
        )}
        {entry.status === 'REJECTED' && (
          <button className="btn-accent" onClick={() => handleStatusChange('reopen')} disabled={saving}>
            Reopen for Review
          </button>
        )}
        {entry.plantId && (
          <Link to={`/plants/${entry.plantId}`}>
            <button>View Plant in Database</button>
          </Link>
        )}
      </div>

      {/* Confirm Dialog */}
      {confirmAction && (
        <div className="card" style={{ marginBottom: 20, border: '2px solid var(--primary)' }}>
          <h3 style={{ marginBottom: 8 }}>
            Confirm {confirmAction === 'approve' ? 'Approval' : 'Rejection'}
          </h3>
          {confirmAction === 'reject' && (
            <textarea
              placeholder="Reason for rejection..."
              value={rejectNotes}
              onChange={e => setRejectNotes(e.target.value)}
              rows={3}
              style={{ marginBottom: 8 }}
            />
          )}
          {confirmAction === 'approve' && (
            <p style={{ fontSize: 13, marginBottom: 8 }}>
              This will merge {attrCount} proposed attribute value(s) into the primary plant database.
            </p>
          )}
          <div style={{ display: 'flex', gap: 8 }}>
            <button
              className={confirmAction === 'approve' ? 'btn-primary' : 'btn-danger'}
              onClick={() => handleStatusChange(confirmAction)}
              disabled={saving}
            >
              {saving ? 'Processing...' : `Yes, ${confirmAction}`}
            </button>
            <button onClick={() => setConfirmAction(null)}>Cancel</button>
          </div>
        </div>
      )}

      {/* Proposed Data */}
      <div className="card">
        <h2 style={{ fontSize: 16, marginBottom: 12 }}>Proposed Data</h2>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '8px 24px', fontSize: 13, marginBottom: 16 }}>
          <div><strong>Genus:</strong> {proposedData.genus || '—'}</div>
          <div><strong>Species:</strong> {proposedData.species || '—'}</div>
          <div><strong>Common Name:</strong> {proposedData.commonName || '—'}</div>
          <div><strong>Proposed Attributes:</strong> {attrCount}</div>
        </div>

        {attrCount > 0 && (
          <table style={{ fontSize: 13 }}>
            <thead><tr><th>Attribute ID</th><th>Proposed Value</th></tr></thead>
            <tbody>
              {Object.entries(proposedAttrs).map(([attrId, data]) => (
                <tr key={attrId}>
                  <td style={{ fontFamily: 'monospace', fontSize: 11 }}>{attrId.substring(0, 8)}...</td>
                  <td><strong>{typeof data === 'object' ? data.value : data}</strong></td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
