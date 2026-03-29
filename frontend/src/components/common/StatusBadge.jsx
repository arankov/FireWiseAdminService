const colors = {
  NEW: { bg: '#e3f2fd', color: '#1565c0', label: 'New' },
  WIP: { bg: '#fff3e0', color: '#e65100', label: 'In Progress' },
  APPROVED: { bg: '#e8f5e9', color: '#2e7d32', label: 'Approved' },
  REJECTED: { bg: '#ffebee', color: '#c62828', label: 'Rejected' },
};

export default function StatusBadge({ status }) {
  const s = colors[status] || { bg: '#f5f5f5', color: '#666', label: status };
  return (
    <span style={{
      display: 'inline-block',
      padding: '3px 10px',
      borderRadius: 12,
      fontSize: 12,
      fontWeight: 600,
      background: s.bg,
      color: s.color,
    }}>
      {s.label}
    </span>
  );
}
