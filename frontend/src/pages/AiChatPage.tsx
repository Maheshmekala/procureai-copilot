import { useState, useRef, useEffect } from 'react';
import axios from 'axios';
import ReactMarkdown from 'react-markdown';

interface TraceStep {
  type: string;
  content: string;
  timestamp: string;
}

interface Message {
  role: 'user' | 'assistant';
  content: string;
  trace?: TraceStep[];
}

const suggestions = [
  "Show me top suppliers by spend",
  "Which suppliers are high risk?",
  "How many suppliers do we have?",
  "Show spend by category",
  "What's the total spend?",
  "Find expiring contracts",
];

export default function AiChatPage() {
  const [messages, setMessages] = useState<Message[]>([
    { role: 'assistant', content: "👋 Welcome to **ProcureAI Copilot**!\n\nI'm your AI procurement assistant powered by **Groq LLM** with multi-agent orchestration. I can:\n\n- 📊 **Analyze supplier spend** — top suppliers, category breakdowns\n- ⚠️ **Assess risks** — high-risk suppliers, compliance issues\n- 📄 **Check contracts** — expiring contracts, status summaries\n\n**Try asking me anything about your procurement data!**" }
  ]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const [streamingText, setStreamingText] = useState('');
  const [currentTrace, setCurrentTrace] = useState<TraceStep[]>([]);
  const chatRef = useRef<HTMLDivElement>(null);
  const eventSourceRef = useRef<EventSource | null>(null);

  useEffect(() => {
    chatRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, streamingText, currentTrace]);

  const handleSend = async () => {
    if (!input.trim() || loading) return;
    const userMsg = input.trim();
    setInput('');
    setMessages(prev => [...prev, { role: 'user', content: userMsg }]);
    setLoading(true);
    setStreamingText('');
    setCurrentTrace([]);

    try {
      // Use the real-time streaming endpoint
      const res = await axios.post('/api/chat', { message: userMsg });
      const data = res.data;

      // Show the trace steps
      if (data.trace) {
        setCurrentTrace(data.trace.filter((s: TraceStep) => s.type !== 'response'));
      }

      // Simulate word-by-word for the response
      const response = data.response || "I couldn't process that request.";
      const words = response.split(' ');
      setStreamingText('');

      for (let i = 0; i < words.length; i++) {
        await new Promise(r => setTimeout(r, 40));
        setStreamingText(prev => prev + words[i] + ' ');
      }

      setMessages(prev => [...prev, { role: 'assistant', content: response, trace: currentTrace }]);
      setStreamingText('');
      setCurrentTrace([]);
    } catch (err) {
      setMessages(prev => [...prev, {
        role: 'assistant',
        content: '❌ Sorry, I encountered an error. Please make sure the backend is running on port 8080.'
      }]);
    }
    setLoading(false);
  };

  return (
    <div className="flex gap-4 h-[calc(100vh-100px)]">
      {/* Main Chat Area */}
      <div className="flex-1 flex flex-col bg-gray-900 rounded-2xl border border-white/10 overflow-hidden">
        {/* Header */}
        <div className="p-4 border-b border-white/10 flex items-center gap-3">
          <div className="w-10 h-10 rounded-full bg-gradient-to-br from-indigo-500 to-purple-600 flex items-center justify-center text-lg">
            🤖
          </div>
          <div>
            <h2 className="font-semibold text-white">AI Procurement Assistant</h2>
            <p className="text-xs text-gray-400">Powered by Groq Llama 3.3 · Multi-Agent Orchestration</p>
          </div>
          <div className="ml-auto flex items-center gap-2">
            <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse"></span>
            <span className="text-xs text-gray-400">Online</span>
          </div>
        </div>

        {/* Messages */}
        <div className="flex-1 overflow-y-auto p-4 space-y-4">
          {messages.map((msg, i) => (
            <div key={i} className={`chat-enter flex ${msg.role === 'user' ? 'justify-end' : 'justify-start'}`}>
              <div className={`max-w-2xl rounded-2xl p-4 ${
                msg.role === 'user'
                  ? 'bg-indigo-600 text-white rounded-br-sm'
                  : 'bg-gray-800/50 text-gray-100 rounded-bl-sm border border-white/5'
              }`}>
                <div className="flex items-start gap-3">
                  <span className="text-lg mt-0.5">{msg.role === 'user' ? '👤' : '🤖'}</span>
                  <div className="prose prose-invert prose-sm max-w-none">
                    <ReactMarkdown>{msg.content}</ReactMarkdown>
                  </div>
                </div>
              </div>
            </div>
          ))}

          {/* Streaming text */}
          {streamingText && (
            <div className="chat-enter flex justify-start">
              <div className="max-w-2xl rounded-2xl p-4 bg-gray-800/50 rounded-bl-sm border border-white/5">
                <div className="flex items-start gap-3">
                  <span className="text-lg mt-0.5">🤖</span>
                  <div>
                    <div className="prose prose-invert prose-sm max-w-none">
                      <ReactMarkdown>{streamingText}</ReactMarkdown>
                    </div>
                    <span className="inline-block w-2 h-4 bg-indigo-400 animate-pulse ml-1"></span>
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* Typing indicator */}
          {loading && !streamingText && (
            <div className="chat-enter flex justify-start">
              <div className="bg-gray-800/50 rounded-2xl p-4 border border-white/5">
                <div className="flex gap-1.5">
                  <span className="typing-dot w-2.5 h-2.5 bg-indigo-400 rounded-full inline-block"></span>
                  <span className="typing-dot w-2.5 h-2.5 bg-indigo-400 rounded-full inline-block"></span>
                  <span className="typing-dot w-2.5 h-2.5 bg-indigo-400 rounded-full inline-block"></span>
                </div>
              </div>
            </div>
          )}

          {/* Suggestion chips (only at start) */}
          {messages.length === 1 && !loading && (
            <div className="flex flex-wrap gap-2 mt-4">
              {suggestions.map(s => (
                <button key={s} onClick={() => setInput(s)}
                  className="text-sm bg-white/5 hover:bg-white/10 text-gray-300 px-4 py-2 rounded-full border border-white/10 transition-all hover:border-indigo-500/50">
                  {s}
                </button>
              ))}
            </div>
          )}

          <div ref={chatRef} />
        </div>

        {/* Input */}
        <div className="p-4 border-t border-white/10">
          <div className="flex gap-3">
            <input
              type="text" value={input} onChange={e => setInput(e.target.value)}
              placeholder="Ask about suppliers, risks, contracts..."
              className="flex-1 bg-gray-800 border border-white/10 rounded-xl px-4 py-3 text-white placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
              onKeyDown={e => e.key === 'Enter' && !e.shiftKey && handleSend()}
              disabled={loading}
            />
            <button onClick={handleSend} disabled={loading || !input.trim()}
              className="bg-gradient-to-r from-indigo-600 to-purple-600 text-white px-6 py-3 rounded-xl hover:from-indigo-500 hover:to-purple-500 disabled:opacity-50 transition-all font-medium shadow-lg shadow-indigo-500/25">
              {loading ? '...' : '→'}
            </button>
          </div>
          <p className="text-xs text-gray-600 mt-2">Powered by Groq · Llama 3.3 70B · Multi-Agent System</p>
        </div>
      </div>

      {/* Agent Trace Panel */}
      <div className="w-80 bg-gray-900/50 rounded-2xl border border-white/10 p-4 overflow-y-auto hidden lg:block">
        <h3 className="text-sm font-semibold text-gray-400 uppercase tracking-wider mb-4">Agent Trace</h3>
        {currentTrace.length === 0 && !loading && (
          <p className="text-xs text-gray-600">Agent activity will appear here when you send a message...</p>
        )}
        {currentTrace.map((step, i) => (
          <div key={i} className="trace-step flex items-start gap-3 mb-3 p-3 rounded-lg bg-white/5">
            <span className={`text-lg ${
              step.type === 'thought' ? '🧠' :
              step.type === 'tool_call' ? '🔧' :
              step.type === 'tool_result' ? '📊' :
              step.type === 'tool_thought' ? '🤔' :
              step.type === 'response' ? '💬' : '⚡'
            }`}></span>
            <div>
              <p className="text-xs font-medium text-gray-300 capitalize">{step.type.replace('_', ' ')}</p>
              <p className="text-xs text-gray-500 mt-0.5">{step.content.length > 100 ? step.content.substring(0, 100) + '...' : step.content}</p>
            </div>
          </div>
        ))}
        {loading && currentTrace.length === 0 && (
          <div className="flex items-center gap-2 p-3 rounded-lg bg-white/5">
            <div className="flex gap-1">
              <span className="typing-dot w-1.5 h-1.5 bg-indigo-400 rounded-full inline-block"></span>
              <span className="typing-dot w-1.5 h-1.5 bg-indigo-400 rounded-full inline-block"></span>
              <span className="typing-dot w-1.5 h-1.5 bg-indigo-400 rounded-full inline-block"></span>
            </div>
            <span className="text-xs text-gray-500">Processing...</span>
          </div>
        )}
      </div>
    </div>
  );
}
