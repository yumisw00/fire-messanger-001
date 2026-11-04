# NusaChat Backend

Backend chat serverless menggunakan **Cloudflare Workers** + **Durable Objects** dengan enkripsi end-to-end (E2EE).

## Arsitektur

```
Client (Compose) ── WebSocket ──> Cloudflare Workers ──> Durable Object (ChatRoom)
                                              │
                                              └─> SQLite Storage (per conversation)
```

- Setiap conversation ID memiliki satu Durable Object instance terpisah
- Semua pesan disimpan sebagai **ciphertext** (enkripsi dilakukan sisi klien)
- Server tidak pernah melihat plaintext
- Riwayat pesan disimpan di Durable Object SQLite storage untuk replay singkat

## Struktur File

```
backend/
├── wrangler.toml              # Konfigurasi Cloudflare Workers
├── package.json               # Dependensi & skrip
├── tsconfig.json              # Konfigurasi TypeScript
├── README.md                  # Dokumentasi ini
└── src/
    ├── index.ts               # Main Worker - routing WebSocket ke DO
    └── chat-room.ts           # Durable Object ChatRoom
```

## Prasyarat

- Node.js ≥ 18
- Wrangler CLI (`npm install -g wrangler`)
- Akun Cloudflare dengan akses Workers

## Instalasi

```bash
cd backend
npm install
```

## Pengembangan Lokal

```bash
npm run dev
```

Backend akan berjalan di `http://localhost:8787`.

## Deploy ke Produksi

```bash
npx wrangler deploy
```

Pastikan Anda sudah login ke Cloudflare:

```bash
wrangler login
```

## Endpoint WebSocket

```
wss://<your-worker>.workers.dev/ws/<conversation-id>
```

Contoh:
```
wss://nusachat-backend.<subdomain>.workers.dev/ws/conversation-123
```

## Format Pesan (JSON)

**Kirim ke server:**
```json
{
  "ciphertext": "<base64-encoded-encrypted-message>",
  "senderId": "user-abc-123"
}
```

**Terima dari server (history):**
```json
{
  "type": "history",
  "messages": [
    {
      "id": "uuid-1",
      "ciphertext": "<base64>",
      "senderId": "user-abc-123",
      "timestamp": 1730000000000,
      "conversationId": "conversation-123"
    }
  ]
}
```

**Terima dari server (pesan baru):**
```json
{
  "type": "message",
  "id": "uuid-2",
  "ciphertext": "<base64>",
  "senderId": "user-xyz-789",
  "timestamp": 1730000100000,
  "conversationId": "conversation-123"
}
```

## Keamanan

- **Tidak ada plaintext yang disimpan server:** Server hanya menerima dan mentransmisi `ciphertext`
- **Enkripsi dilakukan sisi klien:** Kunci enkripsi tidak pernah dikirim ke server
- **Per-conversation isolation:** Setiap conversation ID memiliki Durable Object terpisah, tidak ada cross-room leakage
- **SQLite storage:** Riwayat pesan disimpan terenkripsi di Durable Object SQLite storage

## Perintah Penting

| Perintah | Deskripsi |
|----------|-----------|
| `npm run dev` | Jalankan development server lokal |
| `npm run deploy` | Deploy ke Cloudflare Workers |
| `npm run deploy:dry-run` | Cek konfigurasi tanpa deploy |
| `wrangler login` | Login ke akun Cloudflare |
| `wrangler whoami` | Cek status login |