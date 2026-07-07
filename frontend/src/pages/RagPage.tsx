import { useState, useRef } from 'react';
import axios from 'axios';

export default function RagPage() {
  const [file, setFile] = useState<File | null>(null);
  const [question, setQuestion] = useState('');
  const [answer, setAnswer] = useState('');
  const [loading, setLoading] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [uploadResult, setUploadResult] = useState('');
  const fileRef = useRef<HTMLInputElement>(null);

  const handleUpload = async () => {
    if (!file) return;
    setUploading(true);
    const formData = new FormData();
    formData.append('file', file);
    try {
      const res = await axios.post('/api/rag/ingest', formData);
      setUploadResult(`✅ ${res.data.filename} ingested`);
      setFile(null);
    } catch (e) {
      setUploadResult('❌ Upload failed');
    }
    setUploading(false);
  };

  const handleAsk = async () => {
    if (!question.trim()) return;
    setLoading(true);
    try {
      const res = await axios.post('/api/rag/ask', { question });
      setAnswer(res.data.response);
    } catch (e) {
      setAnswer('❌ Error asking question');
    }
    setLoading(false);
  };

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-white">📄 Document Q&A</h1>
        <p className="text-gray-400 text-sm">Upload contracts, RFPs, or procurement documents and ask questions about them</p>
      </div>

      <div className="bg-gray-900 rounded-xl border border-white/10 p-6">
        <h2 className="text-lg font-semibold text-white mb-4">Upload Document</h2>
        <div className="flex gap-3">
          <input ref={fileRef} type="file" onChange={e => setFile(e.target.files?.[0] || null)}
            className="flex-1 bg-gray-800 border border-white/10 rounded-xl px-4 py-2 text-white file:mr-4 file:py-1 file:px-3 file:rounded-lg file:border-0 file:bg-indigo-600 file:text-white file:text-sm hover:file:bg-indigo-500" />
          <button onClick={handleUpload} disabled={!file || uploading}
            className="bg-indigo-600 text-white px-5 py-2 rounded-xl hover:bg-indigo-500 disabled:opacity-50 font-medium">
            {uploading ? 'Uploading...' : 'Upload'}
          </button>
        </div>
        {uploadResult && <p className="text-sm mt-2 text-emerald-400">{uploadResult}</p>}
      </div>

      <div className="bg-gray-900 rounded-xl border border-white/10 p-6">
        <h2 className="text-lg font-semibold text-white mb-4">Ask a Question</h2>
        <div className="flex gap-3 mb-4">
          <input type="text" value={question} onChange={e => setQuestion(e.target.value)}
            placeholder="What are the payment terms in this contract?"
            className="flex-1 bg-gray-800 border border-white/10 rounded-xl px-4 py-3 text-white placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-indigo-500"
            onKeyDown={e => e.key === 'Enter' && handleAsk()} />
          <button onClick={handleAsk} disabled={loading || !question.trim()}
            className="bg-gradient-to-r from-indigo-600 to-purple-600 text-white px-6 py-3 rounded-xl hover:from-indigo-500 hover:to-purple-500 disabled:opacity-50 font-medium shadow-lg shadow-indigo-500/25">
            {loading ? 'Thinking...' : 'Ask'}
          </button>
        </div>

        {answer && (
          <div className="bg-gray-800/50 rounded-xl p-4 border border-white/5 mt-3">
            <div className="flex items-start gap-3">
              <span className="text-lg">🤖</span>
              <p className="text-gray-200 text-sm whitespace-pre-wrap">{answer}</p>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
