'use client';

import { useEffect, useMemo, useRef, useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import {
  LayoutDashboard,
  Calendar,
  FileText,
  Settings,
  Bell,
  MessageSquare,
  Sparkles,
  Send,
  Loader2,
} from 'lucide-react';

import { useAuth } from '@/contexts/AuthContext';
import { TopNav } from '@/marketing/layout/TopNav';
import { Sidebar } from '@/marketing/layout/Sidebar';
import { Button } from '@/marketing/ui/button';
import { Badge } from '@/marketing/ui/badge';

type ChatRole = 'user' | 'assistant';

interface ChatMessage {
  id: string;
  role: ChatRole;
  content: string;
  timestamp: number;
}

const quickPrompts = [
  'Summarize my upcoming appointments.',
  'Explain how to prepare for a cardiology follow-up.',
  'What lifestyle tips can help with hypertension?',
  'Help me draft a message to my doctor.',
];

const createId = () => crypto.randomUUID?.() ?? Math.random().toString(36).slice(2);

export default function PatientChatbotPage() {
  const router = useRouter();
  const { user, logout, isLoading, isAuthenticated } = useAuth();
  const [input, setInput] = useState('');
  const [messages, setMessages] = useState<ChatMessage[]>([
    {
      id: createId(),
      role: 'assistant',
      content:
        'Hi there! I am the HealthLink+ AI companion. Ask me anything about your appointments, medical history, or healthy habits and I will help.',
      timestamp: Date.now(),
    },
  ]);
  const [isThinking, setIsThinking] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const chatEndRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    if (!isLoading && !isAuthenticated) {
      router.replace('/auth/patient/login');
    }
  }, [isLoading, isAuthenticated, router]);

  useEffect(() => {
    chatEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, isThinking]);

  const sidebarItems = useMemo(
    () => [
      { icon: LayoutDashboard, label: 'Dashboard', href: '/patient/dashboard' },
      { icon: Calendar, label: 'Appointments', href: '/patient/appointments' },
      { icon: FileText, label: 'Medical History', href: '/patient/medical-history' },
      { icon: MessageSquare, label: 'AI Chatbot', href: '/patient/chatbot' },
      { icon: Bell, label: 'Notifications', href: '/patient/dashboard#notifications' },
      { icon: Settings, label: 'Settings', href: '/patient/profile' },
    ],
    [],
  );

  const handleLogout = () => {
    logout();
    router.replace('/');
  };

  const sendMessage = async (prompt?: string) => {
    const content = (prompt ?? input).trim();
    if (!content || isThinking) return;

    const userMessage: ChatMessage = {
      id: createId(),
      role: 'user',
      content,
      timestamp: Date.now(),
    };

    setMessages((prev) => [...prev, userMessage]);
    setInput('');
    setError(null);
    setIsThinking(true);

    try {
      const response = await fetch('/api/chatbot', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          messages: [...messages, userMessage].map((message) => ({
            role: message.role,
            content: message.content,
          })),
        }),
      });

      if (!response.ok) {
        throw new Error('Failed to reach the AI assistant.');
      }

      const data = await response.json();
      const assistantMessage: ChatMessage = {
        id: createId(),
        role: 'assistant',
        content: data.reply ?? 'I am sorry, I was unable to generate a response.',
        timestamp: Date.now(),
      };

      setMessages((prev) => [...prev, assistantMessage]);
    } catch (err: any) {
      console.error(err);
      setError(err.message || 'Something went wrong.');
      setMessages((prev) => [
        ...prev,
        {
          id: createId(),
          role: 'assistant',
          content:
            'I ran into an issue while thinking. Please try again, or check your network connection.',
          timestamp: Date.now(),
        },
      ]);
    } finally {
      setIsThinking(false);
    }
  };

  const handleKeyDown = (event: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      sendMessage();
    }
  };

  if (isLoading || !isAuthenticated || !user) {
    if (!isLoading && !isAuthenticated) {
      return null;
    }
    return null;
  }

  return (
    <div className="min-h-screen bg-gradient-to-b from-blue-50 to-white">
      <TopNav
        userName={`${user.firstName ?? ''} ${user.lastName ?? ''}`.trim() || 'Patient'}
        userRole="Patient"
        showPortalLinks={false}
        onLogout={handleLogout}
      />

      <div className="flex">
        <Sidebar items={sidebarItems} currentPath="/patient/chatbot" />

        <main className="flex-1 p-4 sm:p-6 lg:p-8">
          <div className="max-w-5xl mx-auto space-y-6">
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
              <div>
                <div className="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-white border border-slate-200 shadow-sm mb-3">
                  <Sparkles className="w-4 h-4 text-teal-500" />
                  <span className="text-xs font-semibold text-slate-600 uppercase tracking-widest">
                    HealthLink+ AI Companion
                  </span>
                </div>
                <h1 className="text-3xl text-slate-900 mb-1">AI Chatbot</h1>
                <p className="text-slate-600 max-w-2xl">
                  Ask medical follow-up questions, request summaries, or get lifestyle coaching. This
                  chatbot uses Google Gemini to provide conversational answers. For emergencies,
                  contact your doctor directly.
                </p>
              </div>
            </div>

            <div className="grid lg:grid-cols-[2fr,1fr] gap-6">
              <div className="bg-white/70 backdrop-blur rounded-3xl border border-slate-100 shadow-lg flex flex-col h-[640px]">
                <div className="flex-1 overflow-y-auto px-6 py-6 space-y-6">
                  {messages.map((message) => (
                    <div
                      key={message.id}
                      className={`flex ${message.role === 'user' ? 'justify-end' : 'justify-start'}`}
                    >
                      <div
                        className={`max-w-xl rounded-2xl px-4 py-3 text-sm leading-relaxed shadow ${
                          message.role === 'user'
                            ? 'bg-gradient-to-br from-teal-500 to-violet-600 text-white'
                            : 'bg-white border border-slate-100 text-slate-800'
                        }`}
                      >
                        {message.content}
                      </div>
                    </div>
                  ))}

                  {isThinking && (
                    <div className="flex items-center gap-2 text-sm text-slate-500">
                      <Loader2 className="w-4 h-4 animate-spin" />
                      Thinking...
                    </div>
                  )}
                  <div ref={chatEndRef} />
                </div>

                <div className="border-t border-slate-100 bg-white/80 backdrop-blur rounded-b-3xl px-4 py-4">
                  {error && <p className="text-sm text-red-500 mb-2">{error}</p>}
                  <div className="flex gap-3">
                    <textarea
                      value={input}
                      onChange={(e) => setInput(e.target.value)}
                      onKeyDown={handleKeyDown}
                      placeholder="Ask about appointments, medications, or healthy habits..."
                      className="flex-1 resize-none rounded-2xl border border-slate-200 bg-white px-4 py-3 text-sm text-slate-700 focus:outline-none focus:ring-2 focus:ring-teal-500"
                      rows={2}
                    />
                    <Button
                      onClick={() => sendMessage()}
                      disabled={isThinking || !input.trim()}
                      className="h-14 w-14 rounded-2xl bg-gradient-to-br from-teal-500 to-violet-600 hover:from-teal-600 hover:to-violet-700"
                    >
                      {isThinking ? <Loader2 className="w-4 h-4 animate-spin" /> : <Send className="w-4 h-4" />}
                    </Button>
                  </div>
                </div>
              </div>

              <div className="space-y-4">
                <div className="bg-white rounded-3xl border border-slate-100 shadow p-6">
                  <h2 className="text-lg font-semibold text-slate-900 mb-4">Suggested Prompts</h2>
                  <div className="space-y-3">
                    {quickPrompts.map((prompt) => (
                      <button
                        key={prompt}
                        onClick={() => sendMessage(prompt)}
                        className="w-full text-left px-4 py-3 rounded-2xl border border-slate-200 hover:border-teal-400 hover:bg-teal-50 transition text-sm text-slate-600"
                      >
                        {prompt}
                      </button>
                    ))}
                  </div>
                </div>

                <div className="bg-white rounded-3xl border border-slate-100 shadow p-6 space-y-4">
                  <div className="flex items-center gap-3">
                    <Badge className="bg-slate-900 text-white">Beta</Badge>
                    <p className="text-xs text-slate-500 uppercase tracking-[0.3em]">
                      Responsible AI
                    </p>
                  </div>
                  <p className="text-sm text-slate-600">
                    This chatbot uses Gemini to deliver conversational insights. It is not a
                    replacement for medical professionals. Double-check critical information with
                    your doctor.
                  </p>
                  <Link href="/patient/medical-history" className="text-sm text-teal-600 font-semibold">
                    View my records ➜
                  </Link>
                </div>
              </div>
            </div>
          </div>
        </main>
      </div>
    </div>
  );
}

