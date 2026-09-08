import { useEffect, useState } from 'react';
import { BookOpen, ArrowRight, Plus, Grid3x3 } from 'lucide-react';
import Galaxy from './components/Galaxy';

interface Quote {
  text: string;
  author: string;
}

const QUOTES: Quote[] = [
  { text: '不积跬步，无以至千里；不积小流，无以成江海。', author: '《荀子·劝学》' },
  { text: 'The best way to predict the future is to write it down.', author: 'Alan Kay' },
  { text: '路漫漫其修远兮，吾将上下而求索。', author: '屈原《离骚》' },
  { text: 'What we know is a drop, what we don\'t know is an ocean.', author: 'Isaac Newton' },
  { text: '纸上得来终觉浅，绝知此事要躬行。', author: '陆游《冬夜读书示子聿》' },
  { text: 'Writing is thinking made visible.', author: 'Anon' },
  { text: '博观而约取，厚积而薄发。', author: '苏轼《稼说送张琥》' },
  { text: 'If you can\'t explain it simply, you don\'t understand it well enough.', author: 'Albert Einstein' },
  { text: '千里之行，始于足下。', author: '《老子》第六十四章' },
  { text: 'Memory is the residue of thought.', author: 'Dan Roam' },
];

function pickQuote(seed: number): Quote {
  return QUOTES[seed % QUOTES.length];
}

interface ModuleInfo {
  slug: string;
  name: string;
  filename: string;
  size: number;
  updatedAt: number;
  entryCount: number;
}

// Color presets per module — cycle through these
const COLOR_PRESETS = [
  { accent: '#f54e00', bg: '#fff1ea' }, // Cursor orange
  { accent: '#5e6ad2', bg: '#eef0fb' }, // Linear purple
  { accent: '#0d9488', bg: '#e6f7f5' }, // Teal
  { accent: '#cc785c', bg: '#fbeee8' }, // Coral
  { accent: '#1aae39', bg: '#e6f6ea' }, // Green
  { accent: '#7b3ff2', bg: '#f0eafb' }, // Purple
];

function colorFor(slug: string): { accent: string; bg: string } {
  let h = 0;
  for (let i = 0; i < slug.length; i++) h = (h * 31 + slug.charCodeAt(i)) >>> 0;
  return COLOR_PRESETS[h % COLOR_PRESETS.length];
}

function formatDate(ts: number): string {
  const d = new Date(ts);
  const m = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  return `${d.getFullYear()}-${m}-${day}`;
}

export default function Lobby() {
  const [modules, setModules] = useState<ModuleInfo[]>([]);
  const [loading, setLoading] = useState(true);
  const [counts, setCounts] = useState<Record<string, number>>({});
  const [quote, setQuote] = useState<Quote>(() => pickQuote(Math.floor(Math.random() * QUOTES.length)));

  const load = async () => {
    try {
      const res = await fetch('/api/modules', { cache: 'no-store' });
      const data = await res.json();
      const list: ModuleInfo[] = data.modules || [];
      setModules(list);
      const newCounts: Record<string, number> = {};
      list.forEach((m) => {
        newCounts[m.slug] = m.entryCount;
      });
      setCounts(newCounts);
    } catch (err) {
      console.error('Lobby load failed:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  return (
    <div style={{ minHeight: '100vh', background: 'var(--canvas)' }}>
      {/* Top bar */}
      <header
        style={{
          position: 'sticky',
          top: 0,
          zIndex: 10,
          background: 'rgba(247, 247, 244, 0.82)',
          backdropFilter: 'saturate(180%) blur(12px)',
          WebkitBackdropFilter: 'saturate(180%) blur(12px)',
          borderBottom: '1px solid var(--hairline)',
        }}
      >
        <div
          style={{
            maxWidth: 1120,
            margin: '0 auto',
            padding: '0 32px',
            height: 64,
            display: 'flex',
            alignItems: 'center',
            gap: 12,
          }}
        >
          <div
            style={{
              width: 32,
              height: 32,
              borderRadius: 8,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              background: 'var(--primary)',
              color: 'var(--on-primary)',
            }}
          >
            <Grid3x3 size={16} />
          </div>
          <h1
            style={{
              fontSize: 16,
              fontWeight: 600,
              margin: 0,
              letterSpacing: -0.3,
              color: 'var(--ink)',
            }}
          >
            我的笔记
          </h1>
          <div style={{ flex: 1 }} />
          <span
            style={{
              fontSize: 11,
              fontWeight: 500,
              letterSpacing: 0.5,
              padding: '4px 10px',
              borderRadius: 999,
              background: 'var(--surface-strong)',
              color: 'var(--muted)',
            }}
          >
            共 {modules.length} 个模块
          </span>
        </div>
      </header>

      {/* Hero with Galaxy background */}
      <div
        style={{
          position: 'relative',
          width: '100%',
          height: 460,
          overflow: 'hidden',
          marginBottom: 48,
        }}
      >
        <div
          style={{
            position: 'absolute',
            inset: 0,
            zIndex: 0,
          }}
        >
          <Galaxy
            mouseRepulsion={false}
            mouseInteraction={false}
            density={0.6}
            glowIntensity={0.15}
            saturation={0.4}
            hueShift={25}
            twinkleIntensity={0.5}
            rotationSpeed={0.03}
            starSpeed={0.3}
            transparent
          />
        </div>
        {/* Subtle gradient overlay for readability */}
        <div
          style={{
            position: 'absolute',
            inset: 0,
            background:
              'linear-gradient(180deg, rgba(247,247,244,0.15) 0%, rgba(247,247,244,0.55) 50%, rgba(247,247,244,1.0) 100%)',
            zIndex: 1,
            pointerEvents: 'none',
          }}
        />
        <div
          style={{
            position: 'relative',
            zIndex: 2,
            maxWidth: 1120,
            margin: '0 auto',
            padding: '72px 32px 0',
            textAlign: 'center',
          }}
        >
          <blockquote
            key={quote.text}
            style={{
              fontFamily: 'Source Serif 4, Georgia, serif',
              fontSize: 30,
              fontWeight: 400,
              letterSpacing: -0.5,
              margin: '0 auto 14px',
              color: 'var(--ink)',
              lineHeight: 1.35,
              maxWidth: 720,
              fontStyle: 'normal',
              padding: 0,
              border: 'none',
              animation: 'fadeInUp 600ms ease',
            }}
          >
            {quote.text}
          </blockquote>
          <cite
            style={{
              display: 'block',
              fontSize: 12,
              fontStyle: 'normal',
              color: 'var(--muted)',
              marginBottom: 28,
              letterSpacing: 0.5,
            }}
          >
            — {quote.author}
          </cite>
          <button
            onClick={() => setQuote(pickQuote(Math.floor(Math.random() * QUOTES.length)))}
            className="icon-btn"
            style={{
              fontSize: 12,
              padding: '6px 14px',
              color: 'var(--muted)',
              border: '1px solid var(--hairline)',
              borderRadius: 999,
              background: 'rgba(255, 255, 255, 0.5)',
              backdropFilter: 'blur(6px)',
            }}
            title="换一句"
          >
            换一句 ↻
          </button>
        </div>
      </div>

      {/* Module grid */}
      <main style={{ maxWidth: 1120, margin: '0 auto', padding: '0 32px 32px' }}>

        {/* Module grid */}
        {loading ? (
          <div
            style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))',
              gap: 16,
            }}
          >
            {[0, 1, 2].map((i) => (
              <div
                key={i}
                style={{
                  height: 160,
                  borderRadius: 12,
                  background: 'var(--surface-strong)',
                  opacity: 0.5,
                  animation: 'pulse 1.5s ease-in-out infinite',
                }}
              />
            ))}
            <style>{`@keyframes pulse { 0%,100%{opacity:0.5} 50%{opacity:0.3} }`}</style>
          </div>
        ) : modules.length === 0 ? (
          <EmptyState />
        ) : (
          <div
            style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))',
              gap: 16,
            }}
          >
            {modules.map((m) => (
              <ModuleCard key={m.slug} m={m} count={counts[m.slug] || 0} />
            ))}
          </div>
        )}
      </main>

    </div>
  );
}

function ModuleCard({ m, count }: { m: ModuleInfo; count: number }) {
  const { accent, bg } = colorFor(m.slug);
  return (
    <a
      href={`/m/${encodeURIComponent(m.slug)}`}
      style={{
        textDecoration: 'none',
        color: 'var(--ink)',
        display: 'block',
        background: 'var(--surface)',
        border: '1px solid var(--hairline)',
        borderRadius: 12,
        padding: 24,
        transition: 'transform 200ms ease, box-shadow 200ms ease, border-color 200ms ease',
        position: 'relative',
        overflow: 'hidden',
      }}
      onMouseEnter={(e) => {
        e.currentTarget.style.transform = 'translateY(-4px)';
        e.currentTarget.style.boxShadow = `0 12px 32px -8px ${accent}30, 0 4px 12px -4px rgba(0,0,0,0.06)`;
        e.currentTarget.style.borderColor = accent;
      }}
      onMouseLeave={(e) => {
        e.currentTarget.style.transform = 'translateY(0)';
        e.currentTarget.style.boxShadow = 'none';
        e.currentTarget.style.borderColor = 'var(--hairline)';
      }}
    >
      <div
        style={{
          width: 44,
          height: 44,
          borderRadius: 10,
          background: bg,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          marginBottom: 16,
        }}
      >
        <BookOpen size={20} style={{ color: accent }} />
      </div>
      <h3
        style={{
          fontSize: 18,
          fontWeight: 600,
          margin: '0 0 6px',
          color: 'var(--ink)',
          letterSpacing: -0.3,
        }}
      >
        {m.name}
      </h3>
      <div
        style={{
          fontSize: 12,
          color: 'var(--muted)',
          display: 'flex',
          alignItems: 'center',
          gap: 10,
        }}
      >
        <span>{count} 条笔记</span>
        <span style={{ opacity: 0.5 }}>·</span>
        <span>更新 {formatDate(m.updatedAt)}</span>
      </div>
      <div
        style={{
          position: 'absolute',
          right: 16,
          top: '50%',
          transform: 'translateY(-50%)',
          opacity: 0,
          color: accent,
          transition: 'opacity 200ms ease, transform 200ms ease',
          display: 'flex',
          alignItems: 'center',
        }}
        className="lobby-arrow"
      >
        <ArrowRight size={18} />
      </div>
      <style>{`.lobby-card:hover .lobby-arrow { opacity: 1; transform: translateY(-50%) translateX(4px); }`}</style>
    </a>
  );
}

function EmptyState() {
  return (
    <div
      style={{
        textAlign: 'center',
        padding: '80px 20px',
        background: 'var(--surface)',
        borderRadius: 12,
        border: '1px dashed var(--hairline-strong)',
      }}
    >
      <div
        style={{
          width: 56,
          height: 56,
          margin: '0 auto 16px',
          borderRadius: 14,
          background: 'var(--surface-strong)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        }}
      >
        <Plus size={24} style={{ color: 'var(--muted)' }} />
      </div>
      <h3 style={{ fontSize: 16, fontWeight: 600, margin: '0 0 6px', color: 'var(--ink)' }}>
        还没有模块
      </h3>
      <p style={{ fontSize: 13, color: 'var(--muted)', margin: 0 }}>
        跟我说「建一个算法模块」「建一个 java 模块」之类的，我帮你创建
      </p>
    </div>
  );
}
