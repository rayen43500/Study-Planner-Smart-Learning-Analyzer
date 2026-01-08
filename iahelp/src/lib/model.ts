// Model adapter with three modes, in order of preference:
// 1) VITE_GEMINI_PROXY_URL: call your own backend (recommended for secrets)
// 2) VITE_GEMINI_API_KEY: direct call to Gemini Flash in the browser
// 3) Fallback: local canned replies (offline)

// Default to a widely available model; override with VITE_GEMINI_MODEL if needed.
const MODEL = (import.meta.env.VITE_GEMINI_MODEL as string | undefined) || 'gemini-2.5-flash'
const API_KEY = import.meta.env.VITE_GEMINI_API_KEY as string | undefined

async function callProxy(prompt: string, proxy: string) {
  const res = await fetch(proxy, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ prompt }),
  })
  if (!res.ok) throw new Error(`Proxy error: ${res.status}`)
  const data = await res.json()
  if (data.reply) return String(data.reply)
  return JSON.stringify(data)
}

async function callGemini(prompt: string) {
  if (!API_KEY) throw new Error('Aucune clé Gemini configurée')
  const endpoint = `https://generativelanguage.googleapis.com/v1beta/models/${MODEL}:generateContent?key=${API_KEY}`

  const payload = {
    contents: [{ role: 'user', parts: [{ text: prompt }] }],
    generationConfig: { temperature: 0.35, topP: 0.95, maxOutputTokens: 512 },
  }

  const res = await fetch(endpoint, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  })

  if (!res.ok) {
    const text = await res.text()
    throw new Error(`Gemini: ${res.status} ${text}`)
  }

  const data = await res.json()
  const first = data.candidates?.[0]?.content?.parts?.map((p: { text?: string }) => p.text).join('').trim()
  if (first) return first
  return 'Réponse vide du modèle.'
}

export async function queryModel(prompt: string): Promise<string> {
  const proxy = import.meta.env.VITE_GEMINI_PROXY_URL as string | undefined

  try {
    if (proxy) return await callProxy(prompt, proxy)
    if (API_KEY) return await callGemini(prompt)
  } catch (err) {
    return `Erreur de connexion au modèle: ${(err as Error).message}`
  }

  // Local fallback: simple canned responses and echo.
  const t = prompt.trim().toLowerCase()
  if (t === 'aide' || t === 'help') return 'Commandes: `ajouter <titre>` ; `avancement` ; `liste` ; `aide`'
  if (t.startsWith('ajouter ')) return `Je peux enregistrer cette tâche pour toi. Exemple: ajouter Préparer examen` // client still uses context to add
  if (t.includes('avancement') || t.includes('progress') || t.includes('progrès')) return 'Je ne suis pas connecté à un modèle externe — ton avancement est calculé localement.'
  return `Je suis en mode hors-ligne. Tu as dit: "${prompt}"`
}

export default queryModel
