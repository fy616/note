// Tiny safe markdown -> HTML (only what we need for read-only view)

const escapeHtml = (s: string) =>
  s
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');

function renderInline(text: string): string {
  let out = escapeHtml(text);
  out = out.replace(/`([^`]+)`/g, '<code>$1</code>');
  out = out.replace(/\*\*([^*]+)\*\*/g, '<strong>$1</strong>');
  out = out.replace(/__([^_]+)__/g, '<strong>$1</strong>');
  out = out.replace(/(^|[^*])\*([^*\n]+)\*/g, '$1<em>$2</em>');
  out = out.replace(/(^|[^_])_([^_\n]+)_/g, '$1<em>$2</em>');
  out = out.replace(
    /\[([^\]]+)\]\(([^)]+)\)/g,
    '<a href="$2" target="_blank" rel="noopener noreferrer">$1</a>',
  );
  return out;
}

export function renderMarkdown(src: string): string {
  if (!src) return '';
  const lines = src.split(/\r?\n/);
  const html: string[] = [];
  let i = 0;
  while (i < lines.length) {
    const line = lines[i];
    if (/^```/.test(line)) {
      const lang = line.replace(/^```/, '').trim();
      const buf: string[] = [];
      i++;
      while (i < lines.length && !/^```/.test(lines[i])) {
        buf.push(lines[i]);
        i++;
      }
      i++;
      const cls = lang ? ` class="language-${escapeHtml(lang)}"` : '';
      html.push(`<pre><code${cls}>${escapeHtml(buf.join('\n'))}</code></pre>`);
      continue;
    }
    const h = /^(#{1,3})\s+(.*)$/.exec(line);
    if (h) {
      const lvl = h[1].length;
      html.push(`<h${lvl}>${renderInline(h[2])}</h${lvl}>`);
      i++;
      continue;
    }
    if (/^---+\s*$/.test(line)) {
      html.push('<hr/>');
      i++;
      continue;
    }
    if (/^>\s?/.test(line)) {
      const buf: string[] = [];
      while (i < lines.length && /^>\s?/.test(lines[i])) {
        buf.push(lines[i].replace(/^>\s?/, ''));
        i++;
      }
      html.push(`<blockquote>${renderInline(buf.join('<br/>'))}</blockquote>`);
      continue;
    }
    if (/^[-*]\s+/.test(line)) {
      const buf: string[] = [];
      while (i < lines.length && /^[-*]\s+/.test(lines[i])) {
        buf.push(lines[i].replace(/^[-*]\s+/, ''));
        i++;
      }
      html.push(`<ul>${buf.map((b) => `<li>${renderInline(b)}</li>`).join('')}</ul>`);
      continue;
    }
    if (/^\d+\.\s+/.test(line)) {
      const buf: string[] = [];
      while (i < lines.length && /^\d+\.\s+/.test(lines[i])) {
        buf.push(lines[i].replace(/^\d+\.\s+/, ''));
        i++;
      }
      html.push(`<ol>${buf.map((b) => `<li>${renderInline(b)}</li>`).join('')}</ol>`);
      continue;
    }
    if (line.trim() === '') {
      i++;
      continue;
    }
    const buf: string[] = [];
    while (
      i < lines.length &&
      lines[i].trim() !== '' &&
      !/^#{1,3}\s+/.test(lines[i]) &&
      !/^```/.test(lines[i]) &&
      !/^>\s?/.test(lines[i]) &&
      !/^[-*]\s+/.test(lines[i]) &&
      !/^\d+\.\s+/.test(lines[i]) &&
      !/^---+\s*$/.test(lines[i])
    ) {
      buf.push(lines[i]);
      i++;
    }
    html.push(`<p>${renderInline(buf.join(' '))}</p>`);
  }
  return html.join('\n');
}

export interface Entry {
  id: string;
  timestamp: string; // ISO YYYY-MM-DD HH:MM[:SS]
  title?: string;
  content: string;
  raw: string;
  /** The sub-module slug this entry belongs to (or null if at module root) */
  subModule: string | null;
}

export interface SubModule {
  slug: string;
  name: string;
  description: string;
  count: number;
  latestTimestamp: string | null;
}

export interface ParsedNotes {
  /** Does the file define any sub-modules via `## 数组` H2 headings? */
  hasSubModules: boolean;
  subModules: SubModule[];
  /** Entries at the module root (no sub-module) — only when hasSubModules=true */
  rootEntries: Entry[];
  /** Flat list of all entries, with subModule populated */
  entries: Entry[];
}

/**
 * Parse notes md into a hierarchical structure:
 *
 *   ## YYYY-MM-DD HH:MM     ← an entry (timestamp line)
 *   ...content...
 *
 *   ## SubModule Name       ← a sub-module declaration (no timestamp)
 *   > optional description
 *
 *   ## YYYY-MM-DD HH:MM     ← entry belongs to the most-recent sub-module
 *   ...content...
 *
 * Entries BEFORE any sub-module header live in `rootEntries`.
 * If the file has NO `## Name` sub-module headers, all entries live flat in `entries`.
 */
export function parseNotes(raw: string): ParsedNotes {
  const result: ParsedNotes = {
    hasSubModules: false,
    subModules: [],
    rootEntries: [],
    entries: [],
  };
  if (!raw.trim()) return result;

  const lines = raw.split(/\r?\n/);
  let i = 0;

  // First pass: scan for sub-module declarations vs entries
  // An entry starts with `## YYYY-MM-DD HH:MM`
  // A sub-module is `## <name>` where <name> is NOT a date AND NOT a chapter heading like `一、二、` or `1.`
  const dateEntryRe = /^##\s+(\d{4}-\d{2}-\d{2}\s+\d{2}:\d{2}(?::\d{2})?)\s*$/;
  const subModuleRe = /^##\s+(\S.*?)\s*$/;
  const isChapterHeading = (s: string) => /^[一二三四五六七八九十百\d]+[、.]/.test(s);

  function hasBlockquoteAfter(startI: number): boolean {
    let j = startI + 1;
    let checked = 0;
    while (j < lines.length && checked < 4) {
      const l = lines[j];
      if (l.trim() === '') { j++; continue; }
      if (/^>\s?/.test(l)) return true;
      if (/^#{1,3}\s/.test(l)) return false;
      checked++; j++;
    }
    return false;
  }

  // Walk through lines, tracking current sub-module context
  let currentSub: string | null = null;
  const subMeta = new Map<string, { name: string; description: string; count: number; latest: string | null }>();

  while (i < lines.length) {
    const line = lines[i];
    const dateMatch = dateEntryRe.exec(line);
    if (dateMatch) {
      const ts = dateMatch[1].includes(':') && dateMatch[1].length === 16 ? dateMatch[1] + ':00' : dateMatch[1];
      const buf: string[] = [];
      i++;
      // Consume body until next date entry OR a top-level sub-module declaration
      while (i < lines.length && !dateEntryRe.test(lines[i])) {
        if (subModuleRe.test(lines[i])) {
          const peekName = lines[i].replace(/^##\s+/, '').trim();
          if (
            !/^\d{4}-\d{2}-\d{2}/.test(peekName) &&
            !isChapterHeading(peekName) &&
            hasBlockquoteAfter(i)
          ) {
            break;
          }
        }
        buf.push(lines[i]);
        i++;
      }
      const body = buf.join('\n').trim();
      const firstLine = body.split('\n', 1)[0] || '';
      const title = /^#\s+(.+)$/.exec(firstLine)?.[1];
      const content = title ? body.split('\n').slice(1).join('\n').trim() : body;

      const entry: Entry = {
        id: ts,
        timestamp: ts,
        title,
        content,
        raw: body,
        subModule: currentSub,
      };
      result.entries.push(entry);
      if (currentSub) {
        const meta = subMeta.get(currentSub)!;
        meta.count++;
        if (!meta.latest || ts.localeCompare(meta.latest) > 0) meta.latest = ts;
      } else {
        result.rootEntries.push(entry);
      }
      continue;
    }

    const subMatch = subModuleRe.exec(line);
    if (subMatch) {
      const name = subMatch[1].trim();
      // Skip if it looks like a date (already handled) — safety
      if (/^\d{4}-\d{2}-\d{2}/.test(name)) {
        i++;
        continue;
      }
      // Skip chapter-style headings like "一、" "1." "1、"
      if (isChapterHeading(name)) {
        i++;
        continue;
      }
      // Must be at module level: followed by a blockquote description.
      // Otherwise it's a heading inside an entry's body.
      if (!hasBlockquoteAfter(i)) {
        i++;
        continue;
      }
      result.hasSubModules = true;
      // Read description from following blockquote (optional)
      let description = '';
      i++;
      // Skip blank lines
      while (i < lines.length && lines[i].trim() === '') i++;
      if (i < lines.length && /^>\s?/.test(lines[i])) {
        const descBuf: string[] = [];
        while (i < lines.length && /^>\s?/.test(lines[i])) {
          descBuf.push(lines[i].replace(/^>\s?/, ''));
          i++;
        }
        description = descBuf.join(' ').trim();
      }
      currentSub = name;
      if (!subMeta.has(name)) {
        subMeta.set(name, { name, description, count: 0, latest: null });
      }
      continue;
    }

    i++;
  }

  // Build subModules list sorted by latest timestamp (newest first)
  result.subModules = Array.from(subMeta.values())
    .sort((a, b) => {
      if (a.latest && b.latest) return b.latest.localeCompare(a.latest);
      if (a.latest) return -1;
      if (b.latest) return 1;
      return a.name.localeCompare(b.name);
    })
    .map((m) => ({
      slug: m.name,
      name: m.name,
      description: m.description,
      count: m.count,
      latestTimestamp: m.latest,
    }));

  // Sort rootEntries + all entries newest first
  const sortByTime = (a: Entry, b: Entry) => b.timestamp.localeCompare(a.timestamp);
  result.entries.sort(sortByTime);
  result.rootEntries.sort(sortByTime);

  return result;
}

export function formatDateLabel(ts: string): string {
  const d = new Date(ts.replace(' ', 'T'));
  if (Number.isNaN(d.getTime())) return ts;
  const m = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  const hh = String(d.getHours()).padStart(2, '0');
  const mm = String(d.getMinutes()).padStart(2, '0');
  const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
  return `${d.getFullYear()}-${m}-${day} ${hh}:${mm} · ${weekdays[d.getDay()]}`;
}
