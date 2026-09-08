import { parseNotes } from './notes';

const themes = [
  {
    code: 'A',
    name: 'Cursor 风',
    href: '/a',
    bg: '#f7f7f4',
    ink: '#26251e',
    accent: '#f54e00',
    card: '#ffffff',
    hairline: '#e6e5e0',
    sample: 'Source Serif 4 · 暖奶白底 · Cursor 橙强调',
    font: "'Source Serif 4', serif",
  },
  {
    code: 'B',
    name: 'Linear 风',
    href: '/b',
    bg: '#010102',
    ink: '#f7f8f8',
    accent: '#5e6ad2',
    card: '#0f1011',
    hairline: '#23252a',
    sample: 'Inter · 近黑底 · 紫蓝单色 · 工程师感',
    font: "'Inter', sans-serif",
  },
  {
    code: 'C',
    name: 'Claude 风',
    href: '/c',
    bg: '#faf9f5',
    ink: '#141413',
    accent: '#cc785c',
    card: '#efe9de',
    hairline: '#e6dfd8',
    sample: 'Lora · 奶油底 · 珊瑚橙 · 衬线阅读感',
    font: "'Lora', serif",
  },
];

// Fetch notes once and render preview cards inside each theme tile
async function boot() {
  let entries: ReturnType<typeof parseNotes>['entries'] = [];
  try {
    const r = await fetch('/api/notes');
    const d = await r.json();
    entries = parseNotes(d.content || '').entries;
  } catch (e) {
    console.error(e);
  }

  const root = document.getElementById('root')!;
  root.innerHTML = '';

  // Hero
  const hero = document.createElement('div');
  hero.style.cssText = `
    max-width: 1120px;
    margin: 0 auto;
    padding: 64px 24px 32px;
    text-align: center;
    font-family: 'Inter', system-ui, sans-serif;
    background: #f7f7f4;
    color: #26251e;
  `;
  hero.innerHTML = `
    <div style="display:inline-flex;align-items:center;gap:8px;background:#fff;border:1px solid #e6e5e0;padding:6px 14px;border-radius:999px;font-size:12px;color:#807d72;margin-bottom:24px;">
      <span style="width:6px;height:6px;border-radius:50%;background:#f54e00"></span>
      NoteFlow · 三种风格预览
    </div>
    <h1 style="font-family:'Source Serif 4',serif;font-size:48px;font-weight:400;letter-spacing:-1.5px;margin:0 0 12px;line-height:1.1;">
      选一个最适合你的
    </h1>
    <p style="font-size:16px;color:#5a5852;max-width:560px;margin:0 auto;line-height:1.6;">
      同一个笔记数据，三种完全不同的视觉风格。点开任意一个看完整效果，告诉我你想用哪个，我直接做成最终版。
    </p>
  `;
  root.appendChild(hero);

  // Grid
  const grid = document.createElement('div');
  grid.style.cssText = `
    max-width: 1120px;
    margin: 0 auto;
    padding: 32px 24px 80px;
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(340px, 1fr));
    gap: 24px;
    background: #f7f7f4;
  `;

  for (const theme of themes) {
    const card = document.createElement('div');
    card.style.cssText = `
      background: ${theme.card};
      border: 1px solid ${theme.hairline};
      border-radius: 12px;
      overflow: hidden;
      cursor: pointer;
      transition: transform 200ms ease, box-shadow 200ms ease;
      text-decoration: none;
      color: ${theme.ink};
      display: flex;
      flex-direction: column;
    `;

    // Theme header strip
    const head = document.createElement('div');
    head.style.cssText = `
      padding: 16px 20px;
      background: ${theme.bg};
      color: ${theme.ink};
      border-bottom: 1px solid ${theme.hairline};
      display: flex;
      align-items: center;
      justify-content: space-between;
      font-family: 'Inter', sans-serif;
    `;
    head.innerHTML = `
      <div style="display:flex;align-items:center;gap:10px;">
        <div style="width:28px;height:28px;border-radius:6px;background:${theme.accent};color:#fff;display:flex;align-items:center;justify-content:center;font-weight:600;font-size:13px;">
          ${theme.code}
        </div>
        <div>
          <div style="font-weight:600;font-size:14px;line-height:1.2;">${theme.name}</div>
          <div style="font-size:11px;opacity:0.7;margin-top:2px;">${theme.sample}</div>
        </div>
      </div>
      <div style="font-size:11px;opacity:0.5;font-family:'JetBrains Mono',monospace;">/a · /b · /c</div>
    `;
    card.appendChild(head);

    // Sample notes preview
    const body = document.createElement('div');
    body.style.cssText = `
      padding: 16px 20px;
      background: ${theme.bg};
      flex: 1;
      font-family: ${theme.font};
      font-size: 13px;
      line-height: 1.6;
    `;

    if (entries.length === 0) {
      body.innerHTML = '<p style="opacity:0.5;text-align:center;padding:32px 0;font-style:italic;">还没有笔记，发条消息给我看看效果</p>';
    } else {
      entries.slice(0, 2).forEach((e, idx) => {
        const ec = document.createElement('div');
        ec.style.cssText = `
          padding: 12px;
          margin-bottom: ${idx === entries.length - 1 ? 0 : 12}px;
          background: ${theme.card};
          border: 1px solid ${theme.hairline};
          border-radius: 6px;
          color: ${theme.ink};
        `;
        const ts = e.timestamp.slice(0, 16);
        const firstPara = e.content.split('\n').filter((l) => l.trim())[0] || '(空)';
        ec.innerHTML = `
          <div style="font-family:'JetBrains Mono',monospace;font-size:10px;color:${theme.accent};letter-spacing:0.5px;text-transform:uppercase;margin-bottom:4px;">${e.timestamp.slice(0, 10)}</div>
          <div style="font-size:11px;opacity:0.6;margin-bottom:6px;">${ts}</div>
          <div style="font-size:13px;line-height:1.6;">${escapeHtml(firstPara.slice(0, 80))}${firstPara.length > 80 ? '…' : ''}</div>
        `;
        body.appendChild(ec);
      });
    }
    card.appendChild(body);

    // CTA
    const cta = document.createElement('div');
    cta.style.cssText = `
      padding: 14px 20px;
      background: ${theme.bg};
      border-top: 1px solid ${theme.hairline};
      display: flex;
      align-items: center;
      justify-content: space-between;
      font-family: 'Inter', sans-serif;
      font-size: 13px;
      color: ${theme.accent};
    `;
    cta.innerHTML = `
      <span style="font-weight:500;">打开 Version ${theme.code} →</span>
      <span style="font-family:'JetBrains Mono',monospace;font-size:11px;opacity:0.5;">${theme.href}</span>
    `;
    card.appendChild(cta);

    card.addEventListener('mouseenter', () => {
      card.style.transform = 'translateY(-4px)';
      card.style.boxShadow = `0 12px 32px -8px ${theme.accent}30, 0 4px 12px -4px rgba(0,0,0,0.1)`;
    });
    card.addEventListener('mouseleave', () => {
      card.style.transform = 'translateY(0)';
      card.style.boxShadow = 'none';
    });
    card.addEventListener('click', () => {
      window.location.href = theme.href;
    });

    grid.appendChild(card);
  }

  root.appendChild(grid);

  // Footer
  const footer = document.createElement('footer');
  footer.style.cssText = `
    text-align: center;
    padding: 32px 24px 64px;
    background: #f7f7f4;
    color: #807d72;
    font-size: 12px;
    font-family: 'Inter', sans-serif;
  `;
  footer.innerHTML = `
    直接告诉我用 A / B / C 哪个，我把那个做成你的最终版本
  `;
  root.appendChild(footer);

  document.body.className = 'theme-a';
}

function escapeHtml(s: string): string {
  return s
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;');
}

boot();
