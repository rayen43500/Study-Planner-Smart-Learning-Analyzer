// Simple model adapter. If you set VITE_GEMINI_PROXY_URL it will POST { prompt }
// to that URL and expect a JSON { reply: string } response. Otherwise falls
// back to a local heuristic reply. This keeps API keys off the client.

export async function queryModel(prompt: string): Promise<string> {
  const proxy = import.meta.env.VITE_GEMINI_PROXY_URL as string | undefined
  if (proxy) {
    try {
      const res = await fetch(proxy, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ prompt }),
      })
      if (!res.ok) throw new Error(`Proxy error: ${res.status}`)
      const data = await res.json()
      if (data.reply) return String(data.reply)
      return JSON.stringify(data)
    } catch (err) {
      return `Erreur de connexion au proxy: ${(err as Error).message}`
    }
  }

  // Local fallback: simple canned responses and echo.
  const t = prompt.trim().toLowerCase()
  if (t === 'aide' || t === 'help') return 'Commandes: `ajouter <titre>` ; `avancement` ; `liste` ; `aide`'
  if (t.startsWith('ajouter ')) return `Je peux enregistrer cette tâche pour toi. Exemple: ajouter Préparer examen` // client still uses context to add
  if (t.includes('avancement') || t.includes('progress') || t.includes('progrès')) return 'Je ne suis pas connecté à un modèle externe — ton avancement est calculé localement.'
  return `Je suis en mode hors-ligne. Tu as dit: "${prompt}"`
}

export default queryModel
