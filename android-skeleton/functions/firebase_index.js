const functions = require('firebase-functions')
const express = require('express')
const cors = require('cors')

const app = express()
app.use(cors())
app.use(express.json())

// Firebase-hosted mock endpoint for /v1/chat
app.post('/v1/chat', (req, res) => {
  const { input } = req.body || {}
  const reply = input ? `Firebase VoxMate (mock): I heard "${input}"` : 'Firebase VoxMate (mock): No input provided.'
  res.json({ id: 'firebase-mock-1', text: reply, source: 'firebase-mock' })
})

exports.api = functions.https.onRequest(app)
