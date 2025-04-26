const fs = require("fs");
const https = require("https");
const path = require("path");

const targetDir = path.join(__dirname, "../static/code-snippets");
const fileUrl = "https://raw.githubusercontent.com/Collektive/collektive-examples/master/simulation/src/main/kotlin/it/unibo/collektive/examples/neighbors/NeighborCounter.kt";
const outputPath = path.join(targetDir, "NeighborCounter.kt");

fs.mkdirSync(targetDir, { recursive: true });

https.get(fileUrl, (res) => {
  const file = fs.createWriteStream(outputPath);
  res.pipe(file);
  file.on("finish", () => {
    file.close();
    console.log("code-snippets: ✅");
  });
}).on("error", (err) => {
  console.error("Error during download:", err.message);
  process.exit(1);
});
