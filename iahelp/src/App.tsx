import React from 'react'
import './App.css'
import { StudyPlannerProvider } from './context/StudyPlannerContext'
import Chatbot from './components/Chatbot'
import ProgressView from './components/ProgressView'

function App() {
  return (
    <StudyPlannerProvider>
      <div className="appRoot">
        <header>
          <h1>StudyPlanner — Assistant d'étude</h1>
          <p>Utilise le chatbot pour ajouter des tâches et voir ton avancement.</p>
        </header>

        <div className="layoutWrapper">
          <main className="layout">
            <section className="left">
              <Chatbot />
            </section>
            <aside className="right">
              <ProgressView />
            </aside>
          </main>
        </div>
      </div>
    </StudyPlannerProvider>
  )
}

export default App
