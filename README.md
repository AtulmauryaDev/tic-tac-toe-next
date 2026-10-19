# Tic-Tac-Toe Next — Edition 2.0

A complete browser-first upgrade of the Java Tic-Tac-Toe project. The original Java Swing application is preserved in `java/src/game/TicTacToe.java`; the new web app adds a responsive interface and room-based online play.

## Features

- Browser-based, responsive UI (no frontend build tools or npm dependencies)
- Player vs Computer with Easy, Medium, and Hard (Minimax) difficulty
- Local two-player mode
- Room-code online multiplayer for two players, with server-authoritative turns and board state
- Smooth mark transitions, winning-line highlights, win effects, and reduced-motion option
- Sound effects and optional synthesized background music (no audio files required)
- Session scores, recent match history, all-time stats and win rate saved in browser localStorage
- Dark/light theme, accent color customization, and persistent preferences
- Original Java Swing edition retained

## Run on Windows

**Requirements for the web version:** Node.js 18 or newer. Download from <https://nodejs.org/> if needed.

1. Extract the ZIP.
2. Double-click `start.bat`.
3. Keep the black terminal window open.
4. Open <http://localhost:3000> in Chrome, Edge, or Firefox.

Alternative using Command Prompt in this folder:

```bat
node server.js
```

Then open `http://localhost:3000` in your browser.

## Online multiplayer

1. Start the server and open the site.
2. Select **Online** and click **Create room**.
3. Share the 6-character room code with the other player.
4. The other player opens the same hosted server address, selects **Online**, enters the code and clicks **Join**.

**Important:** both players must be able to reach the same running server. For two devices on the same Wi-Fi, the host can share their computer's local network IP and allow Node.js through Windows Firewall on a trusted private network. For players on different networks, deploy this folder/server to a Node.js hosting provider and share the deployed HTTPS URL. The included server keeps rooms in memory, so rooms reset if the server restarts; this is a starter multiplayer implementation, not a hardened public gaming service.

## Run the preserved Java desktop edition

Requires JDK 8 or newer. From the project root:

```bat
javac -d java\out java\src\game\TicTacToe.java
java -cp java\out game.TicTacToe
```

In IntelliJ IDEA, open the project folder, configure a JDK, then run `main` in `java/src/game/TicTacToe.java`.

## Project structure

```text
tictactoe-next/
├── server.js              # Node HTTP server + multiplayer room API
├── start.bat              # Windows launcher
├── package.json           # No external dependencies
├── README.md
├── web/
│   ├── index.html
│   ├── styles.css
│   └── app.js
└── java/src/game/TicTacToe.java  # Preserved desktop edition
```

## Notes

- Browser match history and settings are stored locally per browser profile.
- Online room state lives in server memory. Use HTTPS and additional authentication/rate limiting before exposing a public production server.
- Audio starts only after the user interacts with the page, as required by browser autoplay policies.
