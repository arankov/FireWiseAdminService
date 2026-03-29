export default function Pagination({ page, totalPages, onPageChange }) {
  if (totalPages <= 1) return null;
  return (
    <div style={{ display: 'flex', gap: 8, justifyContent: 'center', marginTop: 16 }}>
      <button disabled={page === 0} onClick={() => onPageChange(page - 1)}>Prev</button>
      <span style={{ padding: '8px 12px', fontSize: 13 }}>
        Page {page + 1} of {totalPages}
      </span>
      <button disabled={page >= totalPages - 1} onClick={() => onPageChange(page + 1)}>Next</button>
    </div>
  );
}
