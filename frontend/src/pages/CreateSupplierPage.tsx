import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { supplierApi } from '../lib/api';

export default function CreateSupplierPage() {
  const navigate = useNavigate();
  const [form, setForm] = useState({ name: '', category: '', annualSpend: 0, contactEmail: '', riskScore: 30 });
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      await supplierApi.create(form);
      navigate('/suppliers');
    } catch (err) {
      alert('Failed to create supplier');
      setSubmitting(false);
    }
  };

  return (
    <div className="max-w-lg mx-auto">
      <h2 className="text-2xl font-bold mb-6">Add New Supplier</h2>
      <form onSubmit={handleSubmit} className="bg-white rounded-xl shadow-sm p-6 space-y-5">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Supplier Name *</label>
          <input required className="w-full border rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-indigo-400 outline-none"
            value={form.name} onChange={e => setForm({...form, name: e.target.value})} />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Category *</label>
          <select required className="w-full border rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-indigo-400 outline-none"
            value={form.category} onChange={e => setForm({...form, category: e.target.value})}>
            <option value="">Select category</option>
            <option>Technology</option><option>Manufacturing</option><option>Healthcare</option>
            <option>Food & Beverage</option><option>Construction</option><option>Logistics</option>
          </select>
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Annual Spend *</label>
          <input required type="number" min="0" step="0.01"
            className="w-full border rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-indigo-400 outline-none"
            value={form.annualSpend} onChange={e => setForm({...form, annualSpend: +e.target.value})} />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Contact Email</label>
          <input type="email" className="w-full border rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-indigo-400 outline-none"
            value={form.contactEmail} onChange={e => setForm({...form, contactEmail: e.target.value})} />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Risk Score (0-100)</label>
          <input type="range" min="0" max="100" className="w-full accent-indigo-600" value={form.riskScore}
            onChange={e => setForm({...form, riskScore: +e.target.value})} />
          <span className={`text-sm font-medium ${form.riskScore > 70 ? 'text-red-600' : form.riskScore > 40 ? 'text-yellow-600' : 'text-green-600'}`}>{form.riskScore}</span>
        </div>
        <button type="submit" disabled={submitting}
          className="w-full bg-indigo-600 text-white py-3 rounded-lg hover:bg-indigo-700 disabled:opacity-50 transition font-medium">
          {submitting ? 'Creating...' : 'Create Supplier'}
        </button>
      </form>
    </div>
  );
}
