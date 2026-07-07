import { useState, useEffect } from 'react';
import axios from 'axios';

export default function SuppliersPage() {
  const [suppliers, setSuppliers] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');

  useEffect(() => {
    axios.get('/api/suppliers').then(r => { setSuppliers(r.data); setLoading(false); });
  }, []);

  const seed = async () => {
    await axios.post('/api/suppliers/seed?count=50');
    const r = await axios.get('/api/suppliers');
    setSuppliers(r.data);
  };

  const filtered = suppliers.filter((s: any) =>
    !search || s.name?.toLowerCase().includes(search.toLowerCase()) || s.category?.toLowerCase().includes(search.toLowerCase())
  );

  if (loading) return (
    <div className="flex items-center justify-center h-64">
      <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-500"></div>
    </div>
  );

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-white">Suppliers</h1>
          <p className="text-gray-400 text-sm">{suppliers.length} total suppliers</p>
        </div>
        <div className="flex gap-3">
          <input type="text" placeholder="Search..." className="bg-gray-800 border border-white/10 rounded-xl px-4 py-2 text-white placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-indigo-500 w-64"
            value={search} onChange={e => setSearch(e.target.value)} />
          <button onClick={seed} className="bg-indigo-600 text-white px-4 py-2 rounded-xl hover:bg-indigo-500 text-sm font-medium">
            + Seed 50
          </button>
        </div>
      </div>

      <div className="bg-gray-900 rounded-xl border border-white/10 overflow-hidden">
        <table className="w-full">
          <thead>
            <tr className="border-b border-white/10 bg-white/5">
              <th className="text-left p-4 text-sm text-gray-400 font-medium">Name</th>
              <th className="text-left p-4 text-sm text-gray-400 font-medium">Category</th>
              <th className="text-right p-4 text-sm text-gray-400 font-medium">Annual Spend</th>
              <th className="text-center p-4 text-sm text-gray-400 font-medium">Risk</th>
              <th className="text-right p-4 text-sm text-gray-400 font-medium">Contact</th>
            </tr>
          </thead>
          <tbody>
            {filtered.map((s: any) => (
              <tr key={s.id} className="border-b border-white/5 hover:bg-white/5 transition-colors">
                <td className="p-4 text-white font-medium">{s.name}</td>
                <td className="p-4">
                  <span className="bg-indigo-500/10 text-indigo-400 px-2 py-1 rounded-lg text-xs">{s.category}</span>
                </td>
                <td className="p-4 text-right text-white font-mono">${s.annualSpend?.toLocaleString()}</td>
                <td className="p-4 text-center">
                  <span className={`px-3 py-1 rounded-full text-xs font-medium ${
                    s.riskScore >= 70 ? 'bg-red-500/20 text-red-400' :
                    s.riskScore >= 40 ? 'bg-yellow-500/20 text-yellow-400' :
                    'bg-emerald-500/20 text-emerald-400'
                  }`}>{s.riskScore ?? 'N/A'}</span>
                </td>
                <td className="p-4 text-right text-gray-400 text-sm">{s.contactEmail || '-'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
