import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { api } from '../api/client';
import CompletenessBar from '../components/common/CompletenessBar';

export default function PlantDetail() {
  const { id } = useParams();
  const [plant, setPlant] = useState(null);
  const [error, setError] = useState(null);
  const [openGroups, setOpenGroups] = useState({});

  useEffect(() => {
    api.getPlant(id).then(data => {
      setPlant(data);
      const initial = {};
      data.attributeGroups?.forEach(g => {
        if (g.values.length > 0) initial[g.groupId] = true;
      });
      setOpenGroups(initial);
    }).catch(e => setError(e.message));
  }, [id]);

  if (error) return <div className="error">{error}</div>;
  if (!plant) return <div className="loading">Loading plant details...</div>;

  const toggle = (gid) => setOpenGroups(prev => ({ ...prev, [gid]: !prev[gid] }));

  return (
    <div>
      <div style={{ marginBottom: 8 }}><Link to="/plants">&larr; Back to Plants</Link></div>

      <div className="card" style={{ marginBottom: 20 }}>
        <h1 style={{ fontSize: 24, marginBottom: 4 }}>
          <em>{plant.genus} {plant.species}</em>
          {plant.subspeciesVarieties && <span style={{ fontStyle: 'normal', color: '#666' }}> {plant.subspeciesVarieties}</span>}
        </h1>
        {plant.commonName && <div style={{ fontSize: 16, color: '#666', marginBottom: 12 }}>{plant.commonName}</div>}

        <div style={{ display: 'flex', gap: 24, alignItems: 'center', flexWrap: 'wrap' }}>
          <div style={{ width: 200 }}>
            <div style={{ fontSize: 12, color: '#999', marginBottom: 4 }}>Data Completeness</div>
            <CompletenessBar percent={plant.completenessPercent} />
          </div>
          <div style={{ fontSize: 13, color: '#666' }}>
            {plant.populatedAttributes} / {plant.totalAttributes} attributes populated
          </div>
        </div>

        {plant.notes && (
          <div style={{ marginTop: 12, padding: 12, background: '#f5f5f5', borderRadius: 6, fontSize: 13 }}>
            {plant.notes}
          </div>
        )}
      </div>

      <h2 style={{ fontSize: 18, marginBottom: 12 }}>Attribute Groups</h2>
      {plant.attributeGroups?.map(group => (
        <div key={group.groupId} className="card" style={{ marginBottom: 8 }}>
          <div
            onClick={() => toggle(group.groupId)}
            style={{ cursor: 'pointer', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}
          >
            <strong>{group.groupName}</strong>
            <span style={{ fontSize: 12, color: '#999' }}>
              {group.values.length} value{group.values.length !== 1 ? 's' : ''} {openGroups[group.groupId] ? '▾' : '▸'}
            </span>
          </div>
          {openGroups[group.groupId] && group.values.length > 0 && (
            <table style={{ marginTop: 8, fontSize: 13 }}>
              <thead>
                <tr><th>Attribute</th><th>Value</th><th>Source Value</th><th>Notes</th></tr>
              </thead>
              <tbody>
                {group.values.map(v => (
                  <tr key={v.valueId}>
                    <td>{v.attributeName}</td>
                    <td><strong>{v.value}</strong></td>
                    <td style={{ color: '#666' }}>{v.sourceValue || '—'}</td>
                    <td style={{ color: '#666', maxWidth: 200, overflow: 'hidden', textOverflow: 'ellipsis' }}>{v.notes || '—'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
          {openGroups[group.groupId] && group.values.length === 0 && (
            <div style={{ padding: '8px 0', color: '#999', fontSize: 13 }}>No values populated</div>
          )}
        </div>
      ))}
    </div>
  );
}
