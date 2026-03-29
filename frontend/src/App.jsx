import { BrowserRouter, Routes, Route } from 'react-router-dom';
import AppLayout from './components/layout/AppLayout';
import Dashboard from './pages/Dashboard';
import PlantList from './pages/PlantList';
import PlantDetail from './pages/PlantDetail';
import QueueList from './pages/QueueList';
import QueueEntryDetail from './pages/QueueEntryDetail';
import SourceList from './pages/SourceList';
import ImportSimulator from './pages/ImportSimulator';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<AppLayout />}>
          <Route path="/" element={<Dashboard />} />
          <Route path="/plants" element={<PlantList />} />
          <Route path="/plants/:id" element={<PlantDetail />} />
          <Route path="/queue" element={<QueueList />} />
          <Route path="/queue/:id" element={<QueueEntryDetail />} />
          <Route path="/sources" element={<SourceList />} />
          <Route path="/import" element={<ImportSimulator />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
