import { NavLink, Outlet } from 'react-router-dom';
import './AppLayout.css';

export default function AppLayout() {
  return (
    <>
      <aside className="sidebar">
        <div className="sidebar-brand">FireWise Admin</div>
        <nav>
          <NavLink to="/" end>Dashboard</NavLink>
          <NavLink to="/plants">Plants</NavLink>
          <NavLink to="/queue">Review Queue</NavLink>
          <NavLink to="/sources">Sources</NavLink>
          <NavLink to="/import">Import</NavLink>
        </nav>
      </aside>
      <main className="main-content">
        <Outlet />
      </main>
    </>
  );
}
