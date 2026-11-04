import { ChatRoom } from "./chat-room";

export interface Env {
  CHAT_ROOM: DurableObjectNamespace;
}

export default {
  async fetch(request: Request, env: Env, ctx: ExecutionContext): Promise<Response> {
    const url = new URL(request.url);

    // Hanya terima WebSocket upgrade requests
    if (request.headers.get("upgrade") !== "websocket") {
      return new Response("Expected WebSocket upgrade", { status: 400 });
    }

    // Ekstrak conversation ID dari path: /ws/{conversationId}
    const pathParts = url.pathname.split("/");
    const conversationId = pathParts[2];

    if (!conversationId) {
      return new Response("Missing conversation ID in path", { status: 400 });
    }

    // Ambil atau buat Durable Object untuk conversation ini
    const id = env.CHAT_ROOM.idFromName(conversationId);
    const stub = env.CHAT_ROOM.get(id);

    // Forward WebSocket request ke Durable Object
    return stub.fetch(request);
  },
} satisfies ExportedHandler<Env>;