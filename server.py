
from fastapi import FastAPI
import math
import time

app = FastAPI()

@app.get("/health")
def health():
    return {"status": "ok"}

@app.get("/radar")
def radar():
    t = time.time()

    objects = [
        {
            "id": 1,
            "type": "player",
            "x": 45 * math.cos(t),
            "y": 45 * math.sin(t)
        },
        {
            "id": 2,
            "type": "player",
            "x": -50,
            "y": 20
        },
        {
            "id": 3,
            "type": "bot",
            "x": 30,
            "y": -40
        },
        {
            "id": 4,
            "type": "loot",
            "x": -20,
            "y": 60
        },
        {
            "id": 5,
            "type": "crate",
            "x": 65,
            "y": 30
        }
    ]

    return {
        "status": "ok",
        "objects": objects
    }
