export default function CompletenessBar({ percent }) {
  const p = Math.min(100, Math.max(0, percent || 0));
  const color = p < 40 ? '#d32f2f' : p < 70 ? '#f9a825' : '#2e7d32';
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
      <div style={{
        flex: 1,
        height: 8,
        background: '#e0e0e0',
        borderRadius: 4,
        overflow: 'hidden',
        minWidth: 60,
      }}>
        <div style={{
          width: `${p}%`,
          height: '100%',
          background: color,
          borderRadius: 4,
          transition: 'width 0.3s',
        }} />
      </div>
      <span style={{ fontSize: 12, fontWeight: 600, color, minWidth: 36 }}>{p}%</span>
    </div>
  );
}
