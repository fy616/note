import { StrictMode, type ReactNode, Component } from 'react'
import { createRoot } from 'react-dom/client'
import App from './App.tsx'
import Lobby from './Lobby.tsx'
import './themes-a.css'

class ErrorBoundary extends Component<{ children: ReactNode }, { err: Error | null }> {
  state = { err: null as Error | null };
  static getDerivedStateFromError(err: Error) { return { err }; }
  componentDidCatch(err: Error, info: { componentStack?: string }) {
    console.error('NoteFlow crashed:', err, info);
  }
  render() {
    if (this.state.err) {
      return (
        <div style={{ padding: 24, fontFamily: 'ui-monospace, monospace' }}>
          <h2 style={{ color: '#b91c1c', margin: 0 }}>页面出错了</h2>
          <pre style={{ whiteSpace: 'pre-wrap', color: '#334155', marginTop: 12 }}>
            {this.state.err.message}
          </pre>
          <button
            onClick={() => location.reload()}
            style={{ marginTop: 16, padding: '8px 16px', border: '1px solid #ccc', borderRadius: 6, cursor: 'pointer' }}
          >刷新页面</button>
        </div>
      );
    }
    return this.props.children;
  }
}

const path = window.location.pathname
const isModuleRoute = path.startsWith('/m/')

const root = createRoot(document.getElementById('root')!)

if (isModuleRoute) {
  const parts = path.replace(/^\/m\//, '').split('/').filter(Boolean).map(decodeURIComponent)
  const moduleSlug = parts[0] || ''
  const subSlug = parts[1] || null
  root.render(
    <StrictMode>
      <ErrorBoundary>
        <App moduleSlug={moduleSlug} subSlug={subSlug} />
      </ErrorBoundary>
    </StrictMode>,
  )
} else {
  root.render(
    <StrictMode>
      <ErrorBoundary>
        <Lobby />
      </ErrorBoundary>
    </StrictMode>,
  )
}
