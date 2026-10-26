// Run from the functions/ folder:  node scripts/check-case.js
// Windows ignores upper/lower case in file names, Linux (Cloud Functions, GitHub Actions) does not.
// This checks that every relative require() matches the file name EXACTLY.
const fs = require("fs");
const path = require("path");

const root = process.cwd();

function walk(dir, out = []) {
    for (const name of fs.readdirSync(dir)) {
        if (name === "node_modules" || name.startsWith(".")) continue;
        const full = path.join(dir, name);
        if (fs.statSync(full).isDirectory()) walk(full, out);
        else if (name.endsWith(".js")) out.push(full);
    }
    return out;
}

// true only if every part of the path exists with the exact same letter case
function existsExact(abs) {
    let cur = root;
    for (const part of path.relative(root, abs).split(path.sep)) {
        if (!fs.readdirSync(cur).includes(part)) return false;
        cur = path.join(cur, part);
    }
    return true;
}

let problems = 0;

for (const file of walk(root)) {
    const src = fs.readFileSync(file, "utf8");
    for (const m of src.matchAll(/require\(\s*["'](\.{1,2}\/[^"']+)["']\s*\)/g)) {
        const base = path.resolve(path.dirname(file), m[1]);
        const found = [base, base + ".js", path.join(base, "index.js")].some(existsExact);
        if (!found) {
            problems++;
            console.log(`MISMATCH  ${path.relative(root, file)}  requires  "${m[1]}"`);
        }
    }
}

console.log(problems === 0 ? "OK: every require() matches its file name exactly" : `\n${problems} problem(s): rename the file to match the require()`);
process.exit(problems === 0 ? 0 : 1);