import { NextResponse } from 'next/server';
import { GoogleGenerativeAI } from '@google/generative-ai';

const MODEL_NAME = process.env.GEMINI_MODEL_NAME || 'gemini-1.5-flash';

type IncomingMessage = {
  role: 'user' | 'assistant';
  content: string;
};

export async function POST(request: Request) {
  try {
    if (!process.env.GEMINI_API_KEY) {
      return NextResponse.json(
        { error: 'Gemini API key is not configured on the server.' },
        { status: 500 },
      );
    }

    const { messages } = (await request.json()) as { messages: IncomingMessage[] };

    if (!messages || !Array.isArray(messages) || messages.length === 0) {
      return NextResponse.json({ error: 'Messages array is required.' }, { status: 400 });
    }

    const genAI = new GoogleGenerativeAI(process.env.GEMINI_API_KEY);
    const model = genAI.getGenerativeModel({ model: MODEL_NAME });

    const historyMessages = messages
      .slice(0, -1)
      .filter((message, index, arr) => {
        if (message.role === 'assistant') {
          return arr.slice(0, index).some((m) => m.role === 'user');
        }
        return true;
      })
      .map((message) => ({
        role: message.role === 'assistant' ? 'model' : 'user',
        parts: [{ text: message.content }],
      }));

    const chat = model.startChat({ history: historyMessages });

    const latestMessage = messages[messages.length - 1];
    const result = await chat.sendMessage(latestMessage.content);
    const text = result.response.text();

    return NextResponse.json({
      reply: text ?? "I'm sorry, I couldn't generate a response this time.",
    });
  } catch (error) {
    console.error('Chatbot error:', error);
    return NextResponse.json(
      { error: 'Something went wrong while generating a response.' },
      { status: 500 },
    );
  }
}

