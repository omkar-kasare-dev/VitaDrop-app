// A small in-memory Firestore for the offline tests (documents by path, sub-collections,
// transactions that run one after the other like Firestore's conflicting ones).
class Ts { constructor(ms) { this.ms = ms; } toMillis() { return this.ms; } }

const DELETE = { op: "delete" };
const SERVER_TS = { op: "serverTimestamp" };

const FieldValue = {
    serverTimestamp: () => SERVER_TS,
    delete: () => DELETE,
    increment: (n) => ({ op: "increment", n }),
    arrayUnion: (...values) => ({ op: "union", values })
};

const Timestamp = { fromMillis: (ms) => new Ts(ms), now: () => new Ts(Date.now()) };

function apply(doc, update) {
    for (const [key, value] of Object.entries(update)) {
        if (value === DELETE) delete doc[key];
        else if (value === SERVER_TS) doc[key] = new Ts(Date.now());
        else if (value && value.op === "increment") doc[key] = (doc[key] || 0) + value.n;
        else if (value && value.op === "union") doc[key] = [...new Set([...(doc[key] || []), ...value.values])];
        else doc[key] = value;
    }
}

function createFakeDb() {

    const state = { store: {}, txWrites: 0, queue: Promise.resolve() };

    const snapAt = (path) => ({
        id: path.split("/").pop(),
        exists: path in state.store,
        data: () => ({ ...state.store[path] }),
        get: (field) => (state.store[path] ? state.store[path][field] : undefined),
        ref: docRef(path)
    });

    const docRef = (path) => ({
        path,
        id: path.split("/").pop(),
        get: async () => snapAt(path),
        update: async (update) => {
            if (!(path in state.store)) throw new Error("NOT_FOUND");
            apply(state.store[path], update);
        },
        collection: (name) => colRef(`${path}/${name}`)
    });

    const colRef = (path) => ({
        path,
        doc: (id) => docRef(`${path}/${id}`),
        get: async () => {
            const prefix = path + "/";
            const docs = Object.keys(state.store)
                .filter((p) => p.startsWith(prefix) && !p.slice(prefix.length).includes("/"))
                .map(snapAt);
            return { docs, size: docs.length };
        }
    });

    const db = {
        collection: (name) => colRef(name),
        runTransaction(fn) {
            const run = state.queue.then(async () => {
                const writes = [];
                const result = await fn({
                    get: async (r) => r.get(),
                    update: (r, update) => {
                        if (!(r.path in state.store)) throw new Error("NOT_FOUND " + r.path);
                        writes.push([r.path, update]);
                    }
                });
                writes.forEach(([path, update]) => apply(state.store[path], update));   // all or nothing
                state.txWrites += writes.length;
                return result;
            });
            state.queue = run.catch(() => {});
            return run;
        }
    };

    return { db, state, snapAt, Ts, FieldValue, Timestamp };
}

module.exports = { createFakeDb, Ts, FieldValue, Timestamp };