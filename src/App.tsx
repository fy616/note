import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { Search, RefreshCw, BookOpen, X, Command, ArrowLeft, FolderOpen } from 'lucide-react';
import { parseNotes, renderMarkdown, formatDateLabel, type Entry, type SubModule } from './notes';

type Theme = 'a' | 'b' | 'c';

function detectTheme(): Theme {
  const path = window.location.pathname;
  if (path.startsWith('/b')) return 'b';
  if (path.startsWith('/c')) return 'c';
  return 'a';
}

const isMac = typeof navigator !== 'undefined' && navigator.platform.includes('Mac');
const MOD = isMac ? '⌘' : 'Ctrl';

export default function App({ moduleSlug, subSlug }: { moduleSlug: string; subSlug: string | null }) {
  const [theme] = useState<Theme>(detectTheme);
  const [content, setContent] = useState('');
  const [loading, setLoading] = useState(true);
  const [query, setQuery] = useState('');
  const [lastUpdate, setLastUpdate] = useState<Date | null>(null);
  const [showHelp, setShowHelp] = useState(false);
  const searchRef = useRef<HTMLInputElement>(null);
  const cardRefs = useRef<Map<string, HTMLElement>>(new Map());

  const load = useCallback(async () => {
    try {
      const res = await fetch(`/api/notes/${encodeURIComponent(moduleSlug)}.json`, { cache: 'no-store' });
      if (!res.ok) {
        if (res.status === 404) {
          setContent('');
        } else {
          throw new Error(`HTTP ${res.status}`);
        }
      } else {
        const data = await res.json();
        setContent(data.content || '');
      }
      setLastUpdate(new Date());
    } catch (err) {
      console.error('Failed to load notes:', err);
    } finally {
      setLoading(false);
    }
  }, [moduleSlug]);

  useEffect(() => {
    document.body.className = `theme-${theme}`;
    document.documentElement.classList.remove('dark');
    load();
  }, [theme, load]);

  useEffect(() => {
    const t = setInterval(load, 30000);
    return () => clearInterval(t);
  }, [load]);

  const parsed = useMemo(() => parseNotes(content), [content]);

  // If module has sub-modules and no subSlug chosen → render sub-module overview.
  // Otherwise → render entries (filtered by subSlug if given)
  const showSubOverview = parsed.hasSubModules && !subSlug;

  const entries = useMemo<Entry[]>(() => {
    if (showSubOverview) return [];
    if (subSlug) return parsed.entries.filter((e) => e.subModule === subSlug);
    if (!parsed.hasSubModules) return parsed.entries;
    // sub-modules exist but no subSlug → show root entries
    return parsed.rootEntries;
  }, [parsed, subSlug, showSubOverview]);

  const filtered = useMemo(() => {
    const q = query.trim().toLowerCase();
    if (!q) return entries;
    return entries.filter(
      (e) =>
        e.title?.toLowerCase().includes(q) ||
        e.content.toLowerCase().includes(q) ||
        e.timestamp.toLowerCase().includes(q),
    );
  }, [entries, query]);

  // Group entries by year-month for sticky headers
  const grouped = useMemo(() => groupByMonth(filtered), [filtered]);

  // Keyboard shortcuts
  useEffect(() => {
    const onKey = (e: KeyboardEvent) => {
      // Ignore when typing in inputs
      const tag = (e.target as HTMLElement)?.tagName;
      const isEditing = tag === 'INPUT' || tag === 'TEXTAREA';
      const mod = e.metaKey || e.ctrlKey;

      if (mod && e.key.toLowerCase() === 'k') {
        e.preventDefault();
        searchRef.current?.focus();
        searchRef.current?.select();
        return;
      }
      if (mod && e.key.toLowerCase() === 'r') {
        e.preventDefault();
        load();
        return;
      }
      if (e.key === '?' && !isEditing) {
        e.preventDefault();
        setShowHelp((v) => !v);
        return;
      }
      if (e.key === 'Escape') {
        if (showHelp) setShowHelp(false);
        else if (query) setQuery('');
        else if (document.activeElement === searchRef.current) {
          searchRef.current?.blur();
        }
        return;
      }
      if (e.key === '/' && !isEditing) {
        e.preventDefault();
        searchRef.current?.focus();
        return;
      }
      // j/k navigation
      if (!isEditing && (e.key === 'j' || e.key === 'k')) {
        e.preventDefault();
        const list = filtered;
        if (list.length === 0) return;
        const currentIndex = list.findIndex((entry) => {
          const el = cardRefs.current.get(entry.id);
          return el && isElementInViewport(el);
        });
        const dir = e.key === 'j' ? 1 : -1;
        const nextIndex =
          currentIndex < 0 ? 0 : Math.max(0, Math.min(list.length - 1, currentIndex + dir));
        const target = cardRefs.current.get(list[nextIndex].id);
        target?.scrollIntoView({ behavior: 'smooth', block: 'start' });
        return;
      }
    };
    window.addEventListener('keydown', onKey);
    return () => window.removeEventListener('keydown', onKey);
  }, [filtered, query, showHelp, load]);

  return (
    <div>
      <Header
        theme={theme}
        moduleSlug={moduleSlug}
        subSlug={subSlug}
        subName={subSlug ? (parsed.subModules.find((s) => s.slug === subSlug)?.name ?? null) : null}
        query={query}
        setQuery={setQuery}
        onRefresh={load}
        lastUpdate={lastUpdate}
        total={entries.length}
        shown={filtered.length}
        showSearch={!showSubOverview}
        onHelp={() => setShowHelp(true)}
        searchRef={searchRef}
      />

      <main style={{ maxWidth: showSubOverview ? 1120 : 720, margin: '0 auto', padding: '32px 24px' }}>
        {loading ? (
          <Skeleton theme={theme} />
        ) : showSubOverview ? (
          <SubModuleOverview
            moduleSlug={moduleSlug}
            subModules={parsed.subModules}
            rootCount={parsed.rootEntries.length}
          />
        ) : entries.length === 0 ? (
          <Empty theme={theme} />
        ) : filtered.length === 0 ? (
          <NoMatch theme={theme} query={query} onClear={() => setQuery('')} />
        ) : (
          <div>
            {grouped.map((group) => (
              <section key={group.key} style={{ marginBottom: 32 }}>
                <div
                  style={{
                    position: 'sticky',
                    top: 64,
                    zIndex: 5,
                    background: 'var(--canvas)',
                    padding: '8px 0',
                    marginBottom: 12,
                    fontSize: 11,
                    fontWeight: 600,
                    letterSpacing: 1.2,
                    textTransform: 'uppercase',
                    color: 'var(--muted)',
                    borderBottom: '1px solid var(--hairline)',
                  }}
                >
                  {group.label} <span style={{ opacity: 0.5, marginLeft: 6 }}>· {group.entries.length}</span>
                </div>
                <ul style={{ listStyle: 'none', padding: 0, margin: 0, display: 'flex', flexDirection: 'column', gap: 16 }}>
                  {group.entries.map((entry) => (
                    <li
                      key={entry.id}
                      ref={(el) => {
                        if (el) cardRefs.current.set(entry.id, el);
                        else cardRefs.current.delete(entry.id);
                      }}
                    >
                      <EntryCard entry={entry} query={query} />
                    </li>
                  ))}
                </ul>
              </section>
            ))}
          </div>
        )}
      </main>

      <footer style={{ maxWidth: showSubOverview ? 1120 : 720, margin: '0 auto', padding: '48px 24px', textAlign: 'center' }}>
        <p className="footer-text" style={{ fontSize: 12, margin: 0 }}>
          NoteFlow · 由 ZCode 帮你记录 · 按 <kbd>?</kbd> 查看快捷键
        </p>
        <div style={{ marginTop: 14 }}>
          <a href="/" className="footer-text" style={{ fontSize: 12, textDecoration: 'none', fontWeight: 500 }}>
            ← 返回大厅
          </a>
        </div>
      </footer>

      {showHelp && <HelpModal onClose={() => setShowHelp(false)} />}
    </div>
  );
}

function SubModuleOverview({
  moduleSlug,
  subModules,
  rootCount,
}: {
  moduleSlug: string;
  subModules: SubModule[];
  rootCount: number;
}) {
  return (
    <div>
      <div style={{ marginBottom: 28 }}>
        <h2
          style={{
            fontFamily: 'Inter, sans-serif',
            fontSize: 28,
            fontWeight: 500,
            letterSpacing: -0.8,
            margin: '0 0 8px',
            color: 'var(--ink)',
          }}
        >
          子模块
        </h2>
        <p style={{ fontSize: 14, color: 'var(--muted)', margin: 0 }}>
          {moduleSlug} 共 {subModules.length} 个子模块 · 选择一个查看笔记
        </p>
      </div>

      <div
        style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fill, minmax(260px, 1fr))',
          gap: 14,
        }}
      >
        {subModules.map((sm) => (
          <SubModuleCard
            key={sm.slug}
            moduleSlug={moduleSlug}
            sub={sm}
          />
        ))}
      </div>

      {rootCount > 0 && (
        <div style={{ marginTop: 40 }}>
          <div
            style={{
              fontSize: 11,
              fontWeight: 600,
              letterSpacing: 1.2,
              textTransform: 'uppercase',
              color: 'var(--muted)',
              padding: '8px 0',
              borderBottom: '1px solid var(--hairline)',
              marginBottom: 16,
            }}
          >
            未分类 · {rootCount}
          </div>
          <a
            href={`/m/${encodeURIComponent(moduleSlug)}/all`}
            style={{
              display: 'block',
              padding: 16,
              border: '1px solid var(--hairline)',
              borderRadius: 10,
              color: 'var(--ink)',
              textDecoration: 'none',
              background: 'var(--surface)',
              transition: 'border-color 150ms ease',
            }}
            onMouseEnter={(e) => (e.currentTarget.style.borderColor = 'var(--hairline-strong)')}
            onMouseLeave={(e) => (e.currentTarget.style.borderColor = 'var(--hairline)')}
          >
            <div style={{ fontSize: 14, fontWeight: 500 }}>查看未分类的 {rootCount} 条笔记</div>
            <div style={{ fontSize: 12, color: 'var(--muted)', marginTop: 4 }}>
              这些笔记没有归入任何子模块
            </div>
          </a>
        </div>
      )}
    </div>
  );
}

function SubModuleCard({ moduleSlug, sub }: { moduleSlug: string; sub: SubModule }) {
  // Hash-based color from Lobby's preset
  const COLOR_PRESETS = [
    { accent: '#f54e00', bg: '#fff1ea' },
    { accent: '#5e6ad2', bg: '#eef0fb' },
    { accent: '#0d9488', bg: '#e6f7f5' },
    { accent: '#cc785c', bg: '#fbeee8' },
    { accent: '#1aae39', bg: '#e6f6ea' },
    { accent: '#7b3ff2', bg: '#f0eafb' },
  ];
  let h = 0;
  const key = `${moduleSlug}/${sub.slug}`;
  for (let i = 0; i < key.length; i++) h = (h * 31 + key.charCodeAt(i)) >>> 0;
  const { accent, bg } = COLOR_PRESETS[h % COLOR_PRESETS.length];

  return (
    <a
      href={`/m/${encodeURIComponent(moduleSlug)}/${encodeURIComponent(sub.slug)}`}
      style={{
        display: 'block',
        textDecoration: 'none',
        color: 'var(--ink)',
        background: 'var(--surface)',
        border: '1px solid var(--hairline)',
        borderRadius: 12,
        padding: 20,
        transition: 'transform 200ms ease, box-shadow 200ms ease, border-color 200ms ease',
      }}
      onMouseEnter={(e) => {
        e.currentTarget.style.transform = 'translateY(-3px)';
        e.currentTarget.style.boxShadow = `0 10px 24px -8px ${accent}30, 0 2px 8px -2px rgba(0,0,0,0.04)`;
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
          width: 38,
          height: 38,
          borderRadius: 9,
          background: bg,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          marginBottom: 14,
        }}
      >
        <FolderOpen size={18} style={{ color: accent }} />
      </div>
      <h3
        style={{
          fontSize: 16,
          fontWeight: 600,
          margin: '0 0 6px',
          color: 'var(--ink)',
          letterSpacing: -0.3,
        }}
      >
        {sub.name}
      </h3>
      {sub.description && (
        <p
          style={{
            fontSize: 12,
            color: 'var(--muted)',
            margin: '0 0 10px',
            lineHeight: 1.5,
            display: '-webkit-box',
            WebkitLineClamp: 2,
            WebkitBoxOrient: 'vertical',
            overflow: 'hidden',
          }}
        >
          {sub.description}
        </p>
      )}
      <div style={{ fontSize: 12, color: 'var(--muted)' }}>
        {sub.count} 条笔记
      </div>
    </a>
  );
}

function groupByMonth(entries: Entry[]): { key: string; label: string; entries: Entry[] }[] {
  const groups = new Map<string, { label: string; entries: Entry[] }>();
  for (const e of entries) {
    const ym = e.timestamp.slice(0, 7); // YYYY-MM
    const [y, m] = ym.split('-');
    const label = `${y} 年 ${parseInt(m, 10)} 月`;
    if (!groups.has(ym)) groups.set(ym, { label, entries: [] });
    groups.get(ym)!.entries.push(e);
  }
  return Array.from(groups.entries())
    .sort(([a], [b]) => b.localeCompare(a))
    .map(([key, { label, entries: list }]) => ({ key, label, entries: list }));
}

function isElementInViewport(el: HTMLElement): boolean {
  const rect = el.getBoundingClientRect();
  return rect.top >= 0 && rect.bottom <= window.innerHeight;
}

function Header({
  theme: _theme,
  moduleSlug,
  subSlug,
  subName,
  query,
  setQuery,
  onRefresh,
  lastUpdate,
  total,
  shown,
  showSearch,
  onHelp,
  searchRef,
}: {
  theme: Theme;
  moduleSlug: string;
  subSlug: string | null;
  subName: string | null;
  query: string;
  setQuery: (q: string) => void;
  onRefresh: () => void;
  lastUpdate: Date | null;
  total: number;
  shown: number;
  showSearch: boolean;
  onHelp: () => void;
  searchRef: React.RefObject<HTMLInputElement | null>;
}) {
  const showingSearch = query.length > 0;
  return (
    <header className="header-bar" style={{ position: 'sticky', top: 0, zIndex: 10 }}>
      <div
        style={{
          maxWidth: subSlug ? 720 : 1120,
          margin: '0 auto',
          padding: '0 24px',
          height: 64,
          display: 'flex',
          alignItems: 'center',
          gap: 12,
        }}
      >
        <a
          href={subSlug ? `/m/${encodeURIComponent(moduleSlug)}` : '/'}
          className="icon-btn"
          aria-label={subSlug ? '返回模块' : '返回大厅'}
          title={subSlug ? `返回 ${moduleSlug}` : '返回大厅'}
          style={{
            padding: 7,
            color: 'var(--muted)',
            textDecoration: 'none',
          }}
        >
          <ArrowLeft size={16} />
        </a>
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
          <BookOpen size={16} />
        </div>
        <div style={{ flex: 1, minWidth: 0 }}>
          <h1 className="header-title" style={{ fontSize: 16, margin: 0, lineHeight: 1.1, fontWeight: 600 }}>
            {subSlug && subName ? (
              <>
                <span style={{ opacity: 0.55 }}>{moduleSlug} / </span>
                {subName}
              </>
            ) : (
              moduleSlug
            )}
          </h1>
          <p className="header-sub" style={{ fontSize: 11, margin: '2px 0 0' }}>
            {total === 0 && !showSearch
              ? '还没有记录'
              : total === 0
                ? '还没有记录'
                : shown === total
                  ? `共 ${total} 条`
                  : `显示 ${shown} / 共 ${total} 条`}
            {lastUpdate && ` · 更新于 ${lastUpdate.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })}`}
          </p>
        </div>

        {showSearch && <div style={{ position: 'relative', flex: 1, maxWidth: 260 }}>
          <Search
            size={14}
            style={{
              position: 'absolute',
              left: 10,
              top: '50%',
              transform: 'translateY(-50%)',
              color: 'var(--muted)',
              pointerEvents: 'none',
            }}
          />
          <input
            ref={searchRef}
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="搜索..."
            className="search-input"
            style={{
              width: '100%',
              padding: showingSearch ? '8px 32px 8px 32px' : '8px 56px 8px 32px',
              fontSize: 13,
              border: 'none',
              borderRadius: 6,
              outline: 'none',
              color: 'var(--ink)',
            }}
          />
          {!showingSearch && (
            <kbd
              className="text-mono"
              style={{
                position: 'absolute',
                right: 8,
                top: '50%',
                transform: 'translateY(-50%)',
                fontSize: 10,
                padding: '2px 5px',
                borderRadius: 4,
                background: 'var(--surface)',
                color: 'var(--muted)',
                border: '1px solid var(--hairline)',
                pointerEvents: 'none',
              }}
            >
              {MOD} K
            </kbd>
          )}
          {showingSearch && (
            <button
              onClick={() => setQuery('')}
              aria-label="清除搜索"
              style={{
                position: 'absolute',
                right: 6,
                top: '50%',
                transform: 'translateY(-50%)',
                border: 'none',
                background: 'transparent',
                color: 'var(--muted)',
                cursor: 'pointer',
                padding: 4,
                borderRadius: 4,
                display: 'flex',
              }}
            >
              <X size={14} />
            </button>
          )}
        </div>}

        <button
          onClick={onRefresh}
          className="icon-btn"
          aria-label="刷新"
          title="刷新 (⌘R)"
        >
          <RefreshCw size={16} />
        </button>
        <button
          onClick={onHelp}
          className="icon-btn"
          aria-label="快捷键"
          title="快捷键 (?)"
        >
          <Command size={16} />
        </button>
      </div>
    </header>
  );
}

function EntryCard({ entry, query }: { entry: Entry; query: string }) {
  let html = renderMarkdown(entry.content);
  if (query) {
    // Highlight in the rendered HTML (works on escaped text + tags)
    const escaped = query.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
    html = html.replace(
      new RegExp(escaped, 'gi'),
      (m) => `<mark>${m}</mark>`,
    );
  }
  return (
    <article className="entry-card">
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 14 }}>
        <time className="text-mono text-muted" style={{ fontSize: 12 }}>
          {entry.timestamp.slice(11)}
        </time>
        <span className="text-muted" style={{ fontSize: 12 }}>
          {formatDateLabel(entry.timestamp)}
        </span>
      </div>
      <div
        className="entry-prose"
        dangerouslySetInnerHTML={{ __html: html || '<p style="color:var(--muted)">（空）</p>' }}
      />
    </article>
  );
}

function HelpModal({ onClose }: { onClose: () => void }) {
  useEffect(() => {
    const onKey = (e: KeyboardEvent) => {
      if (e.key === 'Escape') onClose();
    };
    window.addEventListener('keydown', onKey);
    return () => window.removeEventListener('keydown', onKey);
  }, [onClose]);

  const shortcuts: { keys: string[]; desc: string }[] = [
    { keys: [MOD, 'K'], desc: '聚焦搜索框' },
    { keys: [MOD, 'R'], desc: '刷新笔记' },
    { keys: ['/'], desc: '聚焦搜索框' },
    { keys: ['j'], desc: '下一条笔记' },
    { keys: ['k'], desc: '上一条笔记' },
    { keys: ['Esc'], desc: '清除搜索 / 关闭弹窗' },
    { keys: ['?'], desc: '显示/隐藏此帮助' },
  ];

  return (
    <div
      onClick={onClose}
      style={{
        position: 'fixed',
        inset: 0,
        background: 'rgba(0, 0, 0, 0.35)',
        backdropFilter: 'blur(4px)',
        zIndex: 50,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        animation: 'fadeIn 150ms ease',
      }}
    >
      <div
        onClick={(e) => e.stopPropagation()}
        style={{
          background: 'var(--surface)',
          border: '1px solid var(--hairline)',
          borderRadius: 12,
          padding: 28,
          maxWidth: 420,
          width: '90%',
          boxShadow: '0 24px 60px -20px rgba(0,0,0,0.3)',
          animation: 'fadeIn 200ms ease',
        }}
      >
        <h2 style={{ fontSize: 18, fontWeight: 600, margin: '0 0 16px', letterSpacing: -0.3 }}>
          键盘快捷键
        </h2>
        <ul style={{ listStyle: 'none', padding: 0, margin: 0, display: 'flex', flexDirection: 'column', gap: 10 }}>
          {shortcuts.map((s) => (
            <li key={s.desc} style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: 12 }}>
              <span style={{ fontSize: 13, color: 'var(--body)' }}>{s.desc}</span>
              <span style={{ display: 'inline-flex', gap: 4 }}>
                {s.keys.map((k) => (
                  <kbd
                    key={k}
                    className="text-mono"
                    style={{
                      fontSize: 11,
                      padding: '3px 7px',
                      borderRadius: 5,
                      background: 'var(--surface-strong)',
                      color: 'var(--ink)',
                      border: '1px solid var(--hairline)',
                      minWidth: 22,
                      textAlign: 'center',
                    }}
                  >
                    {k}
                  </kbd>
                ))}
              </span>
            </li>
          ))}
        </ul>
        <div style={{ marginTop: 20, paddingTop: 16, borderTop: '1px solid var(--hairline)', display: 'flex', justifyContent: 'flex-end' }}>
          <button
            onClick={onClose}
            className="icon-btn"
            style={{
              padding: '6px 14px',
              fontSize: 13,
              fontWeight: 500,
              color: 'var(--ink)',
              background: 'var(--surface-strong)',
              borderRadius: 6,
            }}
          >
            关闭
          </button>
        </div>
      </div>
    </div>
  );
}

function Skeleton({ theme: _theme }: { theme: Theme }) {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
      {[0, 1, 2].map((i) => (
        <div
          key={i}
          className="bg-muted-cell"
          style={{
            height: 120,
            borderRadius: 10,
            opacity: 0.5,
            animation: 'pulse 1.5s ease-in-out infinite',
          }}
        />
      ))}
      <style>{`@keyframes pulse { 0%,100%{opacity:0.5} 50%{opacity:0.3} }`}</style>
    </div>
  );
}

function Empty({ theme: _theme }: { theme: Theme }) {
  return (
    <div style={{ textAlign: 'center', padding: '80px 20px' }}>
      <div
        style={{
          width: 64,
          height: 64,
          margin: '0 auto 16px',
          borderRadius: 16,
          background: 'var(--surface-strong)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        }}
      >
        <BookOpen size={28} style={{ color: 'var(--primary)' }} />
      </div>
      <h2 style={{ fontSize: 20, margin: '0 0 8px', fontWeight: 500 }}>还没有笔记</h2>
      <p className="text-muted" style={{ fontSize: 13, maxWidth: 360, margin: '0 auto' }}>
        把想记录的内容直接发给 ZCode，我会自动追加到这里。打开这个页面就能看到。
      </p>
    </div>
  );
}

function NoMatch({ query, onClear }: { theme: Theme; query: string; onClear: () => void }) {
  return (
    <div style={{ textAlign: 'center', padding: '64px 20px' }}>
      <p className="text-muted" style={{ fontSize: 14, margin: '0 0 12px' }}>
        没有匹配 "<span style={{ color: 'var(--ink)' }}>{query}</span>" 的笔记
      </p>
      <button
        onClick={onClear}
        className="icon-btn"
        style={{
          padding: '6px 14px',
          fontSize: 13,
          fontWeight: 500,
          color: 'var(--primary)',
          background: 'var(--primary-soft)',
          borderRadius: 6,
        }}
      >
        清除搜索
      </button>
    </div>
  );
}
