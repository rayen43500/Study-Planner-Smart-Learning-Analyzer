/**
 * Example Node/Express proxy to call Google Vertex AI (Gemini) server-side.
 *
 * IMPORTANT: Keep credentials on the server only. Do NOT put keys in the frontend.
 *
 * Usage:
 * 1) Create a Google Cloud service account with the ``Vertex AI User`` role and
 *    download the JSON key file. Set the env var GOOGLE_APPLICATION_CREDENTIALS
 *    to point to that file.
 * 2) Set PROJECT_ID and LOCATION (e.g. "us-central1") and MODEL_ID (e.g.
 *    "models/gemini-1.5-flash" or the full resource name provided by Google).
 * 3) Install deps: `npm install express body-parser @google-cloud/aiplatform`
 * 4) Run: `node server/gemini-proxy-example.js`
 *
 * This is a minimal example — consult Vertex AI docs for production use.
 */

const express = require('express')
const bodyParser = require('body-parser')
const {PredictionServiceClient} = require('@google-cloud/aiplatform').v1

const PORT = process.env.PORT || 5178
const PROJECT = process.env.PROJECT_ID || 'YOUR_PROJECT'
const LOCATION = process.env.LOCATION || 'us-central1'
// For Gemini via Vertex AI, model id should NOT include "models/" prefix.
// Example: "gemini-2.5-flash" or "gemini-1.5-flash-002".
const MODEL = process.env.MODEL_NAME || 'gemini-2.5-flash'

const app = express()
app.use(bodyParser.json())

const client = new PredictionServiceClient()

app.post('/chat', async (req, res) => {
  const { prompt } = req.body
  if (!prompt) return res.status(400).json({ error: 'prompt required' })

  try {
    // Vertex AI Gemini predict endpoint path
    const endpoint = `projects/${PROJECT}/locations/${LOCATION}/publishers/google/models/${MODEL}`

    // Request format for Gemini with predict: content schema
    const request = {
      endpoint,
      instances: [
        {
          content: [
            {
              role: 'user',
              parts: [{ text: prompt }],
            },
          ],
        },
      ],
      parameters: {
        temperature: 0.2,
        maxOutputTokens: 512,
        topP: 0.95,
        topK: 40,
      },
    }

    const [response] = await client.predict(request)
    const prediction = response?.predictions?.[0]
    const candidateContent = prediction?.content?.[0]
    const candidateParts = candidateContent?.parts || []
    const text = candidateParts.map((p) => p.text).filter(Boolean).join('').trim()
    const reply = text || JSON.stringify(prediction) || 'No reply'
    return res.json({ reply })
  } catch (err) {
    console.error(err)
    return res.status(500).json({ error: String(err) })
  }
})

app.listen(PORT, () => {
  console.log(`Gemini proxy example listening on http://localhost:${PORT}/chat`)
})
