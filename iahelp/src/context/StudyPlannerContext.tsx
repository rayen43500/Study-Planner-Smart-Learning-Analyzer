import React, { createContext, useContext, useEffect, useState } from 'react'
import { v4 as uuidv4 } from 'uuid'

export type Task = {
  id: string
  title: string
  due?: string
  completed: boolean
  notes?: string
}

type StudyPlannerContextValue = {
  tasks: Task[]
  addTask: (title: string, due?: string, notes?: string) => Task
  toggleComplete: (id: string) => void
  progress: number
}

const StudyPlannerContext = createContext<StudyPlannerContextValue | null>(null)

export const useStudyPlanner = () => {
  const ctx = useContext(StudyPlannerContext)
  if (!ctx) throw new Error('useStudyPlanner must be used within StudyPlannerProvider')
  return ctx
}

const STORAGE_KEY = 'study_planner_tasks_v1'

export const StudyPlannerProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [tasks, setTasks] = useState<Task[]>(() => {
    try {
      const raw = localStorage.getItem(STORAGE_KEY)
      if (!raw) return []
      return JSON.parse(raw) as Task[]
    } catch {
      return []
    }
  })

  useEffect(() => {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(tasks))
  }, [tasks])

  const addTask = (title: string, due?: string, notes?: string) => {
    const t: Task = { id: uuidv4(), title, due, notes, completed: false }
    setTasks((s) => [t, ...s])
    return t
  }

  const toggleComplete = (id: string) => {
    setTasks((s) => s.map((t) => (t.id === id ? { ...t, completed: !t.completed } : t)))
  }

  const progress = tasks.length === 0 ? 0 : Math.round((tasks.filter((t) => t.completed).length / tasks.length) * 100)

  return (
    <StudyPlannerContext.Provider value={{ tasks, addTask, toggleComplete, progress }}>
      {children}
    </StudyPlannerContext.Provider>
  )
}

export default StudyPlannerContext
