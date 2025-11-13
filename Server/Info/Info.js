const express = require("express");
const fs = require("fs");
const app = express();

// Statische Dateien wie HTML aus dem public-Ordner bereitstellen
app.use(express.static("public"));

// Route für das JSON-Login
app.get("/helfer", (req, res) => {
  fs.readFile("./Server/Info/Helfer.json", "utf8", (err, data) => {
    if (err) {
      return res.status(500).send("Fehler beim Lesen der JSON-Datei");
    }
    res.setHeader("Content-Type", "application/json");
    res.send(data);
  });
});

// Server starten
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => console.log(`✅ Server läuft auf Port ${PORT}`));
