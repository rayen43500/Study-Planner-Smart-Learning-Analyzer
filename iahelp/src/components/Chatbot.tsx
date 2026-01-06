import React, { useEffect, useRef, useState } from 'react'
import { useStudyPlanner } from '../context/StudyPlannerContext'
import { queryModel } from '../lib/model'

type Msg = { from: 'user' | 'bot'; text: string }

const Chatbot: React.FC = () => {
  const { addTask, progress, tasks } = useStudyPlanner()
  const [messages, setMessages] = useState<Msg[]>([
    { from: 'bot', text: "Bonjour ! Je suis ton assistant d'étude. Tape `aide` pour voir les commandes." },
  ])
  const [value, setValue] = useState('')

  const push = (m: Msg) => setMessages((s) => [...s, m])
  const msgsRef = useRef<HTMLDivElement | null>(null)

  useEffect(() => {
    // Scroll to bottom when messages update
    const el = msgsRef.current
    if (!el) return
    requestAnimationFrame(() => {
      el.scrollTop = el.scrollHeight
    })
  }, [messages])

  const process = (text: string) => {
    const t = text.trim().toLowerCase()
    if (t === 'aide' || t === 'help') {
      push({ from: 'bot', text: 'Commandes: `ajouter <titre>` ; `avancement` ; `liste` ; `aide`' })
      return
    }

    if (t.startsWith('ajouter ') || t.startsWith('add ')) {
      const title = text.slice(text.indexOf(' ') + 1).trim()
      if (!title) {
        push({ from: 'bot', text: "Donne un titre : `ajouter Préparer examen`" })
        return
      }
      addTask(title)
      push({ from: 'bot', text: `Tâche ajoutée : "${title}"` })
      return
    }

    if (t.includes('avancement') || t.includes('progress') || t.includes('progrès')) {
      push({ from: 'bot', text: `Ton avancement est de ${progress}% (${tasks.filter((x) => x.completed).length}/${tasks.length})` })
      return
    }

    if (t.includes('liste') || t.includes('tâche') || t.includes('tache')) {
      if (tasks.length === 0) {
        push({ from: 'bot', text: 'Aucune tâche pour le moment. Ajoute-en avec `ajouter <titre>`.' })
        return
      }
      const list = tasks.map((s, i) => `${s.completed ? '✅' : '⬜'} ${s.title}`).join('\n')
      push({ from: 'bot', text: `Voici tes tâches:\n${list}` })
      return
    }

    push({ from: 'bot', text: "Je n'ai pas compris. Tape `aide` pour les commandes." })
  }

  const onSubmit = async (e?: React.FormEvent) => {
    e?.preventDefault()
    if (!value.trim()) return
    const userText = value
    push({ from: 'user', text: userText })
    setValue('')

    // show typing indicator
    push({ from: 'bot', text: '...' })

    // handle built-in commands locally for immediate updates
    const low = userText.trim().toLowerCase()
    if (low === 'aide' || low === 'help' || low.startsWith('ajouter ') || low.startsWith('add ') || low.includes('liste') || low.includes('avancement') || low.includes('progress')) {
      process(userText)
      // remove typing indicator
      setMessages((msgs) => msgs.filter((m) => m.text !== '...'))
      return
    }

    try {
      const reply = await queryModel(userText)
      // remove typing indicator
      setMessages((msgs) => msgs.filter((m) => m.text !== '...'))
      push({ from: 'bot', text: reply })
    } catch (err) {
      setMessages((msgs) => msgs.filter((m) => m.text !== '...'))
      push({ from: 'bot', text: `Erreur: ${(err as Error).message}` })
    }
  }

  return (
    <div className="chatbot">
      <div className="messages" ref={msgsRef}>
        {messages.map((m, i) => (
          <div key={i} className={`msg ${m.from}`}>
            {m.from === 'bot' ? <div className="avatar" aria-hidden>AI</div> : <div className="avatar" aria-hidden>TU</div>}
            <div className="bubble">{m.text.split('\n').map((ln, idx) => <div key={idx}>{ln}</div>)}</div>
          </div>
        ))}
      </div>
      <form onSubmit={onSubmit} className="inputRow">
        <input value={value} onChange={(e) => setValue(e.target.value)} placeholder="Écris un message (ex: ajouter Devoir math)" />
        <button type="submit">Envoyer</button>
      </form>
    </div>
  )
}

export default Chatbot
