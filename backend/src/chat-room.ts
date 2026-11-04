interface ChatMessage {
  id: string;
  ciphertext: string;
  senderId: string;
  timestamp: number;
  conversationId: string;
}

export class ChatRoom extends DurableObject {
  private connections: WebSocket[] = [];
  private messageHistory: ChatMessage[] = [];
  private readonly MAX_HISTORY = 50;

  constructor(state: DurableObjectState, env: Env) {
    super(state, env);

    // Muat riwayat pesan dari SQLite storage saat inisialisasi
    this.state.blockConcurrencyWhile(async () => {
      const stored = await this.state.storage.get<ChatMessage[]>("messages");
      if (stored) {
        this.messageHistory = stored;
      }
    });
  }

  async fetch(request: Request): Promise<Response> {
    // Buat WebSocket pair
    const webSocketPair = new WebSocketPair();
    const [client, server] = Object.values(webSocketPair) as [WebSocket, WebSocket];

    // Terima WebSocket connection ke Durable Object
    this.state.acceptWebSocket(server);

    // Tambah ke daftar koneksi aktif
    this.connections.push(server);

    // Handler saat menerima pesan dari klien
    server.addEventListener("message", (event: MessageEvent) => {
      const data = event.data as string;

      try {
        const parsed = JSON.parse(data) as {
          ciphertext: string;
          senderId: string;
          type?: string;
        };

        // Buat objek pesan dengan ciphertext dari klien
        const chatMessage: ChatMessage = {
          id: crypto.randomUUID(),
          ciphertext: parsed.ciphertext,
          senderId: parsed.senderId,
          timestamp: Date.now(),
          conversationId: this.state.id.toString(),
        };

        // Simpan ke riwayat di memori
        this.messageHistory.push(chatMessage);
        if (this.messageHistory.length > this.MAX_HISTORY) {
          this.messageHistory = this.messageHistory.slice(-this.MAX_HISTORY);
        }

        // Persist ke SQLite storage Durable Object
        this.state.storage.put("messages", this.messageHistory);

        // Broadcast ciphertext ke semua klien dalam room yang sama (kecuali pengirim)
        const broadcastMsg = JSON.stringify({
          type: "message",
          ...chatMessage,
        });

        for (const conn of this.connections) {
          if (conn !== server && conn.readyState === WebSocket.OPEN) {
            conn.send(broadcastMsg);
          }
        }
      } catch (e) {
        console.error("Invalid message format:", e);
      }
    });

    // Bersihkan koneksi saat ditutup
    server.addEventListener("close", () => {
      this.connections = this.connections.filter((c) => c !== server);
    });

    server.addEventListener("error", () => {
      this.connections = this.connections.filter((c) => c !== server);
    });

    // Kirim riwayat pesan ke klien yang baru terhubung
    const historyMsg = JSON.stringify({
      type: "history",
      messages: this.messageHistory,
    });
    server.send(historyMsg);

    // Kembalikan WebSocket response
    return new Response(null, {
      status: 101,
      webSocket: client,
    });
  }
}