const express = require('express')
const cors = require('cors')
const app = express()
app.use(cors())
app.use(express.json())

// Simple mock /v1/chat endpoint returning a canned response
app.post('/v1/chat', (req, res) => {
  const { input } = req.body || {}
  const reply = input ? `VoxMate (mock): I heard: "${input}". This is a demo response.` : 'VoxMate (mock): No input received.'
  return res.json({ id: 'mock-1', text: reply, source: 'mock' })
})

const port = process.env.PORT || 5000
app.listen(port, () => console.log(`Mock server listening on http://localhost:${port}`))
