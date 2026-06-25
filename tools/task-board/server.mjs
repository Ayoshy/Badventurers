import http from "node:http";
import { readFile, writeFile } from "node:fs/promises";
import path from "node:path";
import { fileURLToPath } from "node:url";

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const repoRoot = path.resolve(__dirname, "..", "..");
const boardPath = path.join(repoRoot, "docs", "work", "tasks.json");
const indexPath = path.join(__dirname, "index.html");
const port = Number(process.env.PORT || process.argv[2] || 4173);

function send(res, status, body, type = "application/json; charset=utf-8") {
  res.writeHead(status, {
    "content-type": type,
    "cache-control": "no-store"
  });
  res.end(body);
}

async function readBoard() {
  return JSON.parse(await readFile(boardPath, "utf8"));
}

async function writeBoard(board) {
  board.updated = new Date().toISOString().slice(0, 10);
  await writeFile(boardPath, `${JSON.stringify(board, null, 2)}\n`, "utf8");
  return board;
}

async function readJson(req) {
  let body = "";
  for await (const chunk of req) {
    body += chunk;
    if (body.length > 1024 * 1024) {
      throw new Error("Request body is too large.");
    }
  }
  return body ? JSON.parse(body) : {};
}

function nextTaskId(tasks) {
  const max = tasks.reduce((highest, task) => {
    const match = /^BV-(\d+)$/.exec(task.id || "");
    return match ? Math.max(highest, Number(match[1])) : highest;
  }, 0);
  return `BV-${String(max + 1).padStart(3, "0")}`;
}

function normalizeTask(task) {
  return {
    id: String(task.id || ""),
    title: String(task.title || "Untitled task"),
    status: String(task.status || "ready"),
    priority: String(task.priority || "P2"),
    area: String(task.area || "general"),
    track: String(task.track || "Backlog"),
    summary: String(task.summary || ""),
    acceptance: Array.isArray(task.acceptance) ? task.acceptance.map((item) => ({
      text: String(item.text || ""),
      done: Boolean(item.done)
    })).filter((item) => item.text.trim()) : [],
    refs: Array.isArray(task.refs) ? task.refs.map(String).filter(Boolean) : [],
    notes: Array.isArray(task.notes) ? task.notes.map(String).filter(Boolean) : []
  };
}

async function handleApi(req, res, url) {
  if (url.pathname === "/api/tasks" && req.method === "GET") {
    send(res, 200, JSON.stringify(await readBoard()));
    return;
  }

  if (url.pathname === "/api/tasks" && req.method === "POST") {
    const board = await readBoard();
    const incoming = await readJson(req);
    const task = normalizeTask({ ...incoming, id: incoming.id || nextTaskId(board.tasks) });
    board.tasks.push(task);
    send(res, 201, JSON.stringify(await writeBoard(board)));
    return;
  }

  const patchMatch = /^\/api\/tasks\/([^/]+)$/.exec(url.pathname);
  if (patchMatch && req.method === "PATCH") {
    const board = await readBoard();
    const task = board.tasks.find((item) => item.id === decodeURIComponent(patchMatch[1]));
    if (!task) {
      send(res, 404, JSON.stringify({ error: "Task not found." }));
      return;
    }
    const patch = await readJson(req);
    Object.assign(task, normalizeTask({ ...task, ...patch }));
    send(res, 200, JSON.stringify(await writeBoard(board)));
    return;
  }

  send(res, 404, JSON.stringify({ error: "Not found." }));
}

const server = http.createServer(async (req, res) => {
  try {
    const url = new URL(req.url, `http://${req.headers.host}`);
    if (url.pathname.startsWith("/api/")) {
      await handleApi(req, res, url);
      return;
    }

    if (url.pathname === "/" || url.pathname === "/index.html") {
      send(res, 200, await readFile(indexPath, "utf8"), "text/html; charset=utf-8");
      return;
    }

    send(res, 404, "Not found.", "text/plain; charset=utf-8");
  } catch (error) {
    send(res, 500, JSON.stringify({ error: error.message }));
  }
});

server.listen(port, "127.0.0.1", () => {
  console.log(`Badventurers Workboard: http://127.0.0.1:${port}`);
  console.log(`Data: ${boardPath}`);
});
