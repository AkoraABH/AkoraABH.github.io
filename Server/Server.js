// server.js (ESM)
import express from "express";
import fs from "fs/promises";
import path from "path";
import cors from "cors";

const app = express();
app.use(cors());
app.use(express.json());

// Statische Dateien aus dem Server-Ordner liefern (HTML/CSS/JS)
// Passe den Pfad an, falls deine statischen Dateien woanders liegen.
const STATIC_ROOT = path.resolve("./"); // im Server-Ordner ausgeführt
app.use(express.static(STATIC_ROOT));

// Hilfsfunktion: Helfer.json laden
async function loadHelfer() {
  const p = path.join(STATIC_ROOT, "Server", "Info", "Helfer.json");
  const raw = await fs.readFile(p, "utf8");
  return JSON.parse(raw);
}

// GET /helfer  -> Rohdaten (nur zu Debug/Entwicklung)
app.get("/helfer", async (req, res) => {
  try {
    const daten = await loadHelfer();
    res.json(daten);
  } catch (err) {
    console.error("Fehler beim Laden von Helfer.json:", err);
    res.status(500).json({ error: "Fehler beim Laden der Daten" });
  }
});

// POST /api/login  -> erwartet { username, password }
app.post("/api/login", async (req, res) => {
  try {
    const { username, password } = req.body;
    if (!username || !password) {
      return res.status(400).json({ success: false, message: "Username und Passwort erforderlich" });
    }

    const daten = await loadHelfer();
    const user = daten.find(u =>
      u.name && u.password &&
      u.name.toLowerCase() === String(username).toLowerCase() &&
      u.password === String(password)
    );

    if (!user) {
      return res.json({ success: false, message: "Falscher Name oder Passwort." });
    }

    // Achtung: in Produktion niemals Passwörter im Klartext speichern — siehe Hinweis weiter unten.
    res.json({
      success: true,
      name: user.name,
      rang: user.rang || "Mitglied",
      // du kannst hier noch ein token o.Ä. senden
    });
  } catch (err) {
    console.error("Login-Fehler:", err);
    res.status(500).json({ success: false, message: "Serverfehler" });
  }
});

// Server starten
const PORT = process.env.PO
