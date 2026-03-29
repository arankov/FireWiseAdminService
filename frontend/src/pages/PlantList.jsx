import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../api/client';
import CompletenessBar from '../components/common/CompletenessBar';
import Pagination from '../components/common/Pagination';

export default function PlantList() {
  const [plants, setPlants] = useState(null);
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);
  const [error, setError] = useState(null);

  const load = (s, p) => {
    api.getPlants(s, p, 20).then(setPlants).catch(e => setError(e.message));
  };

  useEffect(() => { load(search, page); }, [page]);

  const handleSearch = (e) => {
    e.preventDefault();
    setPage(0);
    load(search, 0);
  };

  if (error) return <div className="error">{error}</div>;

  return (
    <div>
      <div className="page-header">
        <h1>Plants</h1>
        <span style={{ color: '#666', fontSize: 13 }}>
          {plants ? `${plants.totalElements} total` : ''}
        </span>
      </div>

      <form onSubmit={handleSearch} style={{ display: 'flex', gap: 8, marginBottom: 20 }}>
        <input
          type="text"
          placeholder="Search by genus, species, or common name..."
          value={search}
          onChange={e => setSearch(e.target.value)}
          style={{ maxWidth: 400 }}
        />
        <button type="submit" className="btn-primary">Search</button>
        {search && <button type="button" onClick={() => { setSearch(''); setPage(0); load('', 0); }}>Clear</button>}
      </form>

      {!plants ? <div className="loading">Loading plants...</div> : (
        <>
          <table>
            <thead>
              <tr>
                <th>Genus</th>
                <th>Species</th>
                <th>Common Name</th>
                <th style={{ width: 160 }}>Completeness</th>
              </tr>
            </thead>
            <tbody>
              {plants.content.map(p => (
                <tr key={p.id}>
                  <td><Link to={`/plants/${p.id}`}><em>{p.genus}</em></Link></td>
                  <td><em>{p.species}</em> {p.subspeciesVarieties || ''}</td>
                  <td>{p.commonName || '—'}</td>
                  <td><CompletenessBar percent={p.completenessPercent} /></td>
                </tr>
              ))}
            </tbody>
          </table>
          <Pagination page={page} totalPages={plants.totalPages} onPageChange={setPage} />
        </>
      )}
    </div>
  );
}
