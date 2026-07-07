import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';

export default function DashboardPage() {
  const [stats, setStats] = useState<any>(null);
  const [suppliers, setSuppliers] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([
      axios.get('/api/suppliers/stats').then(r => r.data),
      axios.get('/api/suppliers/top').then(r => r.data),
    ]).then(([s, top]) => {
      setStats(s);
      setSuppliers(top.slice(0, 10));
      setLoading(false);
    });
  }, []);

  const chartData = suppliers.map((s: any) => ({ name: s.name.substring(0, 12), spend: s.annualSpend }));

  if (loading) return (
    <div className="flex items-center justify-center h-64">
      <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-500"></div>
    </div>
  );

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-white">Procurement Dashboard</h1>
        <p className="text-gray-400 text-sm mt-1">Real-time overview of your procurement data</p>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        {[
          { title: 'Total Suppliers', value: stats.total, icon: '🏢', color: 'from-indigo-500 to-purple-600' },
          { title: 'Total Spend', value: `$${(stats.totalSpend / 1000000).toFixed(1)}M`, icon: '💰', color: 'from-emerald-500 to-teal-600' },
          { title: 'High Risk', value: stats.highRisk, icon: '⚠️', color: 'from-red-500 to-pink-600' },
          { title: 'Categories', value: stats.categories, icon: '📊', color: 'from-amber-500 to-orange-600' },
        ].map(card => (
          <div key={card.title} className="bg-gray-900 rounded-xl border border-white/10 p-5">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-400">{card.title}</p>
                <p className="text-2xl font-bold text-white mt-1">{card.value}</p>
              </div>
              <div className={`w-12 h-12 rounded-xl bg-gradient-to-br ${card.color} flex items-center justify-center text-xl shadow-lg`}>
                {card.icon}
              </div>
            </div>
          </div>
        ))}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Top Suppliers Chart */}
        <div className="bg-gray-900 rounded-xl border border-white/10 p-6">
          <h2 className="text-lg font-semibold text-white mb-4">Top Suppliers by Spend</h2>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={chartData}>
              <CartesianGrid strokeDasharray="3 3" stroke="#ffffff10" />
              <XAxis dataKey="name" stroke="#6b7280" tick={{ fontSize: 11 }} />
              <YAxis stroke="#6b7280" tick={{ fontSize: 11 }} tickFormatter={(v) => `$${(v/1000000).toFixed(0)}M`} />
              <Tooltip
                contentStyle={{ background: '#1f2937', border: '1px solid #ffffff20', borderRadius: '8px', color: '#fff' }}
                formatter={(v: number) => [`$${v.toLocaleString()}`, 'Spend']}
              />
              <Bar dataKey="spend" fill="#818cf8" radius={[4, 4, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>

        {/* AI Assistant Card */}
        <div className="bg-gray-900 rounded-xl border border-white/10 p-6 flex flex-col">
          <h2 className="text-lg font-semibold text-white mb-4">AI Assistant</h2>
          <div className="flex-1 bg-gradient-to-br from-indigo-500/10 to-purple-500/10 rounded-xl p-5 border border-indigo-500/20">
            <p className="text-2xl mb-3">🤖</p>
            <p className="text-gray-300 text-sm mb-4">
              Your AI procurement assistant is ready. Ask about suppliers, risks, contracts, and more.
            </p>
            <div className="space-y-2 mb-4">
              {[
                "📊 'Show me top suppliers'",
                "⚠️ 'Which suppliers are high risk?'",
                "📄 'Find expiring contracts'",
                "💰 'What's the total spend?'"
              ].map((s, i) => (
                <p key={i} className="text-sm text-gray-400">{s}</p>
              ))}
            </div>
            <Link to="/chat"
              className="inline-flex items-center gap-2 bg-gradient-to-r from-indigo-600 to-purple-600 text-white px-5 py-2.5 rounded-xl hover:from-indigo-500 hover:to-purple-500 transition-all shadow-lg shadow-indigo-500/25 font-medium text-sm">
              Open AI Chat →
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}
