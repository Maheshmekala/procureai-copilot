import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { supplierApi, Supplier } from '../lib/api';

export default function SupplierListPage() {
  const [suppliers, setSuppliers] = useState<Supplier[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [categoryFilter, setCategoryFilter] = useState('');

  useEffect(() => {
    supplierApi.getAll().then(data => { setSuppliers(data); setLoading(false); });
  }, []);

  const categories = [...new Set(suppliers.map(s => s.category))];
  const filtered = suppliers.filter(s => {
    const matchSearch = !search || s.name.toLowerCase().includes(search.toLowerCase()) || s.category.toLowerCase().includes(search.toLowerCase());
    const matchCat = !categoryFilter || s.category === categoryFilter;
    return matchSearch && matchCat;
  });

  if (loading) return (
    <div className="flex items-center justify-center h-64">
      <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
    </div>
  );

  return (
    <div>
      <div className="flex flex-wrap items-center justify-between gap-4 mb-6">
        <h2 className="text-2xl font-bold">Suppliers</h2>
        <div className="flex gap-3">
          <input type="text" placeholder="Search..." className="border rounded-lg px-4 py-2 w-64"
            value={search} onChange={e => setSearch(e.target.value)} />
          <select className="border rounded-lg px-4 py-2" value={categoryFilter}
            onChange={e => setCategoryFilter(e.target.value)}>
            <option value="">All Categories</option>
            {categories.map(c => <option key={c} value={c}>{c}</option>)}
          </select>
        </div>
      </div>
      <div className="bg-white rounded-xl shadow-sm overflow-hidden">
        <table className="w-full">
          <thead className="bg-gray-50">
            <tr>
              <th className="text-left p-4 text-sm font-semibold text-gray-600">Name</th>
              <th className="text-left p-4 text-sm font-semibold text-gray-600">Category</th>
              <th className="text-right p-4 text-sm font-semibold text-gray-600">Annual Spend</th>
              <th className="text-center p-4 text-sm font-semibold text-gray-600">Risk</th>
              <th className="p-4"></th>
            </tr>
          </thead>
          <tbody>
            {filtered.map(s => (
              <tr key={s.id} className="border-t hover:bg-gray-50 transition">
                <td className="p-4 font-medium">{s.name}</td>
                <td className="p-4"><span className="bg-indigo-50 text-indigo-700 px-2 py-1 rounded text-sm">{s.category}</span></td>
                <td className="p-4 text-right font-mono">${s.annualSpend.toLocaleString()}</td>
                <td className="p-4 text-center">
                  <span className={`px-3 py-1 rounded-full text-sm font-medium ${
                    s.riskScore && s.riskScore > 70 ? 'bg-red-100 text-red-700' :
                    s.riskScore && s.riskScore > 40 ? 'bg-yellow-100 text-yellow-700' :
                    'bg-green-100 text-green-700'
                  }`}>{s.riskScore ?? 'N/A'}</span>
                </td>
                <td className="p-4 text-right">
                  <Link to={`/suppliers/${s.id}`} className="text-indigo-600 hover:underline text-sm">View →</Link>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
