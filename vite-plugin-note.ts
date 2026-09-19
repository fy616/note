import type { Plugin } from 'vite';
import { readFileSync, existsSync, readdirSync, statSync } from 'node:fs';
import { resolve } from 'node:path';

const __dirname = import.meta.dirname;
const NOTES_DIR = resolve(__dirname, 'notes');

interface ModuleInfo {
  slug: string;
  name: string;
  filename: string;
  size: number;
  updatedAt: number;
  /** Top-level entry count (without sub-modules) */
  entryCount: number;
}

interface SubModuleInfo {
  slug: string;
  name: string;
  count: number;
  latestTimestamp: string | null;
}

function readFileUtf8(path: string): string {
  const buf = readFileSync(path);
  const cleaned =
    buf.length >= 3 && buf[0] === 0xef && buf[1] === 0xbb && buf[2] === 0xbf
      ? buf.subarray(3)
      : buf;
  return cleaned.toString('utf-8');
}

function readEntriesFromRaw(raw: string): { totalCount: number; subModules: SubModuleInfo[] } {
  const dateEntryRe = /^##\s+(\d{4}-\d{2}-\d{2}\s+\d{2}:\d{2}(?::\d{2})?)\s*$/;
  // Sub-module candidate: must be followed by a blockquote description on next non-blank line
  const subModuleCandidateRe = /^##\s+(\S.*?)\s*$/;
  const isChapterHeading = (s: string) => /^[一二三四五六七八九十百\d]+[、.]/.test(s);
  const lines = raw.split(/\r?\n/);
  let totalCount = 0;
  const subMeta = new Map<string, { name: string; count: number; latest: string | null }>();
  let currentSub: string | null = null;
  let hasSub = false;
  let i = 0;
  while (i < lines.length) {
    const line = lines[i];
    const dateMatch = dateEntryRe.exec(line);
    if (dateMatch) {
      const ts = dateMatch[1].includes(':') && dateMatch[1].length === 16 ? dateMatch[1] + ':00' : dateMatch[1];
      totalCount++;
      if (currentSub) {
        const meta = subMeta.get(currentSub)!;
        meta.count++;
        if (!meta.latest || ts.localeCompare(meta.latest) > 0) meta.latest = ts;
      }
      i++;
      // Consume body until next date or module boundary
      while (i < lines.length && !dateEntryRe.test(lines[i])) {
        // Check if the next non-blank line is a "module declaration": non-chapter H2 + blockquote
        if (subModuleCandidateRe.test(lines[i])) {
          const peekName = lines[i].replace(/^##\s+/, '').trim();
          if (
            !/^\d{4}-\d{2}-\d{2}/.test(peekName) &&
            !isChapterHeading(peekName) &&
            hasBlockquoteAfter(lines, i)
          ) {
            break;
          }
        }
        i++;
      }
      continue;
    }

    // Only treat as sub-module if it's at file-level (followed by blockquote)
    const subMatch = subModuleCandidateRe.exec(line);
    if (subMatch) {
      const name = subMatch[1].trim();
      if (/^\d{4}-\d{2}-\d{2}/.test(name)) {
        i++;
        continue;
      }
      if (isChapterHeading(name)) {
        i++;
        continue;
      }
      // Must be followed by a blockquote description (otherwise it's inside an entry's body)
      if (!hasBlockquoteAfter(lines, i)) {
        i++;
        continue;
      }
      hasSub = true;
      currentSub = name;
      if (!subMeta.has(name)) {
        subMeta.set(name, { name, count: 0, latest: null });
      }
      i++;
      // Skip blank + blockquote description lines
      while (i < lines.length && (lines[i].trim() === '' || /^>\s?/.test(lines[i]))) i++;
      continue;
    }
    i++;
  }
  if (!hasSub) return { totalCount, subModules: [] };
  const subModules: SubModuleInfo[] = Array.from(subMeta.values())
    .sort((a, b) => {
      if (a.latest && b.latest) return b.latest.localeCompare(a.latest);
      return a.name.localeCompare(b.name);
    })
    .map((m) => ({ slug: m.name, name: m.name, count: m.count, latestTimestamp: m.latest }));
  return { totalCount, subModules };
}

// Check whether there's a `> ...` blockquote within the next 3 non-blank lines after `lines[i]`
function hasBlockquoteAfter(lines: string[], i: number): boolean {
  let j = i + 1;
  let checked = 0;
  while (j < lines.length && checked < 4) {
    const l = lines[j];
    if (l.trim() === '') {
      j++;
      continue;
    }
    if (/^>\s?/.test(l)) return true;
    // Hitting another H2/H3 means no description
    if (/^#{1,3}\s/.test(l)) return false;
    checked++;
    j++;
  }
  return false;
}

function listModules(): ModuleInfo[] {
  if (!existsSync(NOTES_DIR)) return [];
  const files = readdirSync(NOTES_DIR).filter((f) => f.endsWith('.md'));
  return files
    .map((f) => {
      const fullPath = resolve(NOTES_DIR, f);
      const stat = statSync(fullPath);
      const slug = f.replace(/\.md$/, '');
      const raw = readFileUtf8(fullPath);
      const { totalCount } = readEntriesFromRaw(raw);
      return {
        slug,
        name: slug,
        filename: f,
        size: stat.size,
        updatedAt: stat.mtimeMs,
        entryCount: totalCount,
      };
    })
    .sort((a, b) => b.updatedAt - a.updatedAt);
}

function sendJson(res: any, payload: unknown) {
  res.setHeader('Content-Type', 'application/json; charset=utf-8');
  res.setHeader('Cache-Control', 'no-store');
  res.end(JSON.stringify(payload));
}

export function noteFilePlugin(): Plugin {
  return {
    name: 'noteflow-note-file',
    configureServer(server) {
      server.middlewares.use('/api/modules.json', (_req, res) => {
        try {
          sendJson(res, { modules: listModules() });
        } catch (err) {
          res.statusCode = 500;
          sendJson(res, { error: String(err) });
        }
      });

      server.middlewares.use('/api/notes', (req, res) => {
        try {
          // Connect strips the mount prefix from req.url; originalUrl keeps the full path
          const url = new URL((req as any).originalUrl || req.url || '/', 'http://localhost');
          const pathname = url.pathname;
          // Support both /api/notes/<slug>.json and /api/notes?module=<slug>
          const jsonMatch = pathname.match(/^\/api\/notes\/(.+)\.json$/);
          const moduleSlug = jsonMatch
            ? jsonMatch[1]
            : url.searchParams.get('module');
          if (!moduleSlug) {
            res.statusCode = 400;
            return sendJson(res, { error: 'module param required' });
          }
          const filePath = resolve(NOTES_DIR, `${moduleSlug}.md`);
          if (!existsSync(filePath)) {
            res.statusCode = 404;
            return sendJson(res, { error: 'module not found' });
          }
          const content = readFileUtf8(filePath);
          const { totalCount, subModules } = readEntriesFromRaw(content);
          sendJson(res, { content, module: moduleSlug, totalCount, subModules });
        } catch (err) {
          res.statusCode = 500;
          sendJson(res, { error: String(err) });
        }
      });
    },

    generateBundle() {
      const modules = listModules();
      this.emitFile({
        type: 'asset',
        fileName: 'api/modules.json',
        source: JSON.stringify({ modules }),
      });

      for (const m of modules) {
        const filePath = resolve(NOTES_DIR, `${m.slug}.md`);
        const content = readFileUtf8(filePath);
        const { totalCount, subModules } = readEntriesFromRaw(content);
        this.emitFile({
          type: 'asset',
          fileName: `api/notes/${m.slug}.json`,
          source: JSON.stringify({ content, module: m.slug, totalCount, subModules }),
        });
      }
    },
  };
}
