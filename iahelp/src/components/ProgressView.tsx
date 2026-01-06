import React from 'react'
import { useStudyPlanner } from '../context/StudyPlannerContext'

const ProgressView: React.FC = () => {
  const { tasks, toggleComplete, progress } = useStudyPlanner()

  return (
    <div className="progressView">
      <h2>Avancement</h2>
      <div className="progressBar">
        <div className="progressFill" style={{ width: `${progress}%` }} />
      </div>
      <p>{progress}% terminé</p>

      <h3>Liste des tâches</h3>
      <ul className="taskList">
        {tasks.map((t) => (
          <li key={t.id} className={t.completed ? 'done' : ''}>
            <label>
              <input type="checkbox" checked={t.completed} onChange={() => toggleComplete(t.id)} />
              <span className="title">{t.title}</span>
            </label>
          </li>
        ))}
        {tasks.length === 0 && <li>Aucune tâche</li>}
      </ul>
    </div>
  )
}

export default ProgressView
