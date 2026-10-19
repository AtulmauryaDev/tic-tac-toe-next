package game;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** A polished Swing Tic-Tac-Toe game with AI difficulty levels and two-player mode. */
public final class TicTacToe extends JFrame {
    private static final int EMPTY = 0, X = 1, O = 2;
    private static final Color BG = new Color(17, 24, 39);
    private static final Color PANEL = new Color(31, 41, 55);
    private static final Color ACCENT = new Color(129, 140, 248);
    private static final Color X_COLOR = new Color(96, 165, 250);
    private static final Color O_COLOR = new Color(244, 114, 182);

    private final JButton[] cells = new JButton[9];
    private final JLabel status = new JLabel("Your turn — X starts", SwingConstants.CENTER);
    private final JLabel xScoreLabel = new JLabel("X  0", SwingConstants.CENTER);
    private final JLabel oScoreLabel = new JLabel("O  0", SwingConstants.CENTER);
    private final JLabel drawLabel = new JLabel("Draws  0", SwingConstants.CENTER);
    private final JComboBox<String> modeBox = new JComboBox<>(new String[]{"Player vs Computer", "Two Players"});
    private final JComboBox<String> difficultyBox = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});
    private final Random random = new Random();

    private final int[] board = new int[9];
    private int currentPlayer = X;
    private int xWins, oWins, draws;
    private boolean gameFinished;

    public TicTacToe() {
        super("Tic-Tac-Toe | Classic, Reimagined");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(460, 620));
        setSize(520, 700);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout(0, 16));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBoard(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
        modeBox.addActionListener(e -> {
            difficultyBox.setEnabled(modeBox.getSelectedIndex() == 0);
            newGame();
        });
        difficultyBox.addActionListener(e -> newGame());
        newGame();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setBackground(BG);
        header.setBorder(new EmptyBorder(24, 24, 0, 24));
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("TIC-TAC-TOE");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel subtitle = new JLabel("A timeless game. A smarter challenge.");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(new Color(156, 163, 175));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        status.setForeground(Color.WHITE);
        status.setFont(new Font("SansSerif", Font.BOLD, 15));
        status.setBorder(new EmptyBorder(18, 0, 8, 0));
        header.add(title);
        header.add(Box.createVerticalStrut(5));
        header.add(subtitle);
        header.add(status);
        return header;
    }

    private JPanel buildBoard() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(BG);
        outer.setBorder(new EmptyBorder(0, 24, 0, 24));
        JPanel grid = new JPanel(new GridLayout(3, 3, 10, 10));
        grid.setBackground(BG);
        grid.setPreferredSize(new Dimension(420, 420));
        for (int i = 0; i < 9; i++) {
            final int index = i;
            JButton button = new JButton("");
            button.setFont(new Font("SansSerif", Font.BOLD, 64));
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setOpaque(true);
            button.setBackground(PANEL);
            button.setForeground(Color.WHITE);
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            button.addActionListener(e -> playMove(index));
            cells[i] = button;
            grid.add(button);
        }
        outer.add(grid);
        return outer;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel();
        footer.setBackground(BG);
        footer.setBorder(new EmptyBorder(0, 24, 24, 24));
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));

        JPanel scores = new JPanel(new GridLayout(1, 3, 8, 0));
        scores.setBackground(BG);
        styleScore(xScoreLabel, X_COLOR);
        styleScore(oScoreLabel, O_COLOR);
        styleScore(drawLabel, new Color(209, 213, 219));
        scores.add(xScoreLabel); scores.add(drawLabel); scores.add(oScoreLabel);

        JPanel options = new JPanel(new GridLayout(1, 2, 10, 0));
        options.setBackground(BG);
        styleCombo(modeBox); styleCombo(difficultyBox);
        options.add(modeBox); options.add(difficultyBox);

        JButton newGameButton = new JButton("New Game");
        newGameButton.setFont(new Font("SansSerif", Font.BOLD, 15));
        newGameButton.setForeground(new Color(17, 24, 39));
        newGameButton.setBackground(ACCENT);
        newGameButton.setFocusPainted(false);
        newGameButton.setBorderPainted(false);
        newGameButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        newGameButton.addActionListener(e -> newGame());

        JButton resetButton = new JButton("Reset Scores");
        resetButton.setFont(new Font("SansSerif", Font.PLAIN, 13));
        resetButton.setForeground(new Color(209, 213, 219));
        resetButton.setBackground(PANEL);
        resetButton.setFocusPainted(false);
        resetButton.setBorderPainted(false);
        resetButton.addActionListener(e -> { xWins = oWins = draws = 0; updateScores(); newGame(); });

        JPanel actions = new JPanel(new GridLayout(1, 2, 10, 0));
        actions.setBackground(BG); actions.add(newGameButton); actions.add(resetButton);
        footer.add(scores);
        footer.add(Box.createVerticalStrut(14));
        footer.add(options);
        footer.add(Box.createVerticalStrut(12));
        footer.add(actions);
        return footer;
    }

    private void styleScore(JLabel label, Color color) {
        label.setOpaque(true); label.setBackground(PANEL); label.setForeground(color);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setBorder(new EmptyBorder(12, 4, 12, 4));
    }

    private void styleCombo(JComboBox<String> combo) {
        combo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        combo.setBackground(PANEL); combo.setForeground(Color.WHITE);
    }

    private void playMove(int index) {
        if (gameFinished || board[index] != EMPTY) return;
        if (isComputerTurn()) return;
        makeMove(index, currentPlayer);
        if (finishIfNeeded()) return;
        currentPlayer = other(currentPlayer);
        updateStatus();
        if (isComputerTurn()) {
            // Run the small search off the event queue so the UI remains responsive.
            setBoardEnabled(false);
            Timer timer = new Timer(220, e -> {
                ((Timer) e.getSource()).stop();
                int move = chooseComputerMove();
                if (move >= 0 && !gameFinished) makeMove(move, O);
                if (!finishIfNeeded()) {
                    currentPlayer = X;
                    updateStatus();
                    setBoardEnabled(true);
                }
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

    private boolean isComputerTurn() {
        return modeBox.getSelectedIndex() == 0 && currentPlayer == O;
    }

    private void makeMove(int index, int player) {
        board[index] = player;
        cells[index].setText(player == X ? "X" : "O");
        cells[index].setForeground(player == X ? X_COLOR : O_COLOR);
        cells[index].setBackground(new Color(55, 65, 81));
    }

    private boolean finishIfNeeded() {
        int winner = winner(board);
        if (winner != EMPTY) {
            gameFinished = true;
            if (winner == X) xWins++; else oWins++;
            status.setText((winner == X ? "X" : "O") + " wins! Great game.");
            status.setForeground(winner == X ? X_COLOR : O_COLOR);
            highlightWinningLine(winner);
            updateScores();
            setBoardEnabled(false);
            return true;
        }
        boolean full = true;
        for (int value : board) if (value == EMPTY) { full = false; break; }
        if (full) {
            gameFinished = true; draws++;
            status.setText("It's a draw — try again!");
            status.setForeground(new Color(209, 213, 219));
            updateScores(); setBoardEnabled(false);
            return true;
        }
        return false;
    }

    private void highlightWinningLine(int player) {
        int[][] lines = {{0,1,2},{3,4,5},{6,7,8},{0,3,6},{1,4,7},{2,5,8},{0,4,8},{2,4,6}};
        for (int[] line : lines) {
            if (board[line[0]] == player && board[line[1]] == player && board[line[2]] == player) {
                for (int i : line) cells[i].setBackground(new Color(75, 85, 99));
                return;
            }
        }
    }

    private int chooseComputerMove() {
        String difficulty = (String) difficultyBox.getSelectedItem();
        List<Integer> available = availableMoves(board);
        if (available.isEmpty()) return -1;
        if ("Easy".equals(difficulty)) return available.get(random.nextInt(available.size()));
        if ("Medium".equals(difficulty)) {
            int tactical = findImmediateMove(O);
            if (tactical >= 0) return tactical;
            tactical = findImmediateMove(X);
            if (tactical >= 0) return tactical;
            return available.get(random.nextInt(available.size()));
        }
        int bestScore = Integer.MIN_VALUE, bestMove = available.get(0);
        for (int move : available) {
            board[move] = O;
            int score = minimax(board, false, 0);
            board[move] = EMPTY;
            if (score > bestScore) { bestScore = score; bestMove = move; }
        }
        return bestMove;
    }

    private int findImmediateMove(int player) {
        for (int move : availableMoves(board)) {
            board[move] = player;
            boolean wins = winner(board) == player;
            board[move] = EMPTY;
            if (wins) return move;
        }
        return -1;
    }

    private int minimax(int[] state, boolean maximizing, int depth) {
        int result = winner(state);
        if (result == O) return 10 - depth;
        if (result == X) return depth - 10;
        List<Integer> moves = availableMoves(state);
        if (moves.isEmpty()) return 0;
        if (maximizing) {
            int best = Integer.MIN_VALUE;
            for (int move : moves) { state[move] = O; best = Math.max(best, minimax(state, false, depth + 1)); state[move] = EMPTY; }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (int move : moves) { state[move] = X; best = Math.min(best, minimax(state, true, depth + 1)); state[move] = EMPTY; }
            return best;
        }
    }

    private List<Integer> availableMoves(int[] state) {
        List<Integer> moves = new ArrayList<>();
        for (int i = 0; i < state.length; i++) if (state[i] == EMPTY) moves.add(i);
        return moves;
    }

    private int winner(int[] state) {
        int[][] lines = {{0,1,2},{3,4,5},{6,7,8},{0,3,6},{1,4,7},{2,5,8},{0,4,8},{2,4,6}};
        for (int[] line : lines) {
            int value = state[line[0]];
            if (value != EMPTY && value == state[line[1]] && value == state[line[2]]) return value;
        }
        return EMPTY;
    }

    private int other(int player) { return player == X ? O : X; }

    private void updateStatus() {
        status.setForeground(Color.WHITE);
        if (isComputerTurn()) status.setText("Computer is thinking…");
        else status.setText((currentPlayer == X ? "X" : "O") + "'s turn");
    }

    private void updateScores() {
        xScoreLabel.setText("X  " + xWins);
        oScoreLabel.setText("O  " + oWins);
        drawLabel.setText("Draws  " + draws);
    }

    private void setBoardEnabled(boolean enabled) {
        for (int i = 0; i < cells.length; i++) cells[i].setEnabled(enabled && !gameFinished && board[i] == EMPTY);
    }

    private void newGame() {
        for (int i = 0; i < 9; i++) {
            board[i] = EMPTY;
            cells[i].setText("");
            cells[i].setBackground(PANEL);
            cells[i].setEnabled(true);
        }
        currentPlayer = X;
        gameFinished = false;
        status.setForeground(Color.WHITE);
        status.setText("Your turn — X starts");
        setBoardEnabled(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) { }
            new TicTacToe().setVisible(true);
        });
    }
}
