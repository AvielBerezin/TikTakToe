import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Scanner;

public interface Player {
    Position makeTurn(Board board, Token playAs);

    static int score(Board board, Token player) {
        Optional<Token> token = board.get(1, 1);
        if (token.isPresent() &&
            board.isWinningAt(1, 1)) {
            return 10 * (player.equals(token.get()) ? 1 : -1);
        }
        token = board.get(0, 0);
        if (token.isPresent() &&
            board.isWinningAt(0, 0)) {
            return 10 * (player.equals(token.get()) ? 1 : -1);
        }
        token = board.get(2, 2);
        if (token.isPresent() &&
            board.isWinningAt(2, 2)) {
            return 10 * (player.equals(token.get()) ? 1 : -1);
        }
        Token otherPlayer = player == Token.X ? Token.O : Token.X;
        Optional<Integer> maxAfterTurn = board.empties()
                                              .map(position -> score(board.withCell(position,
                                                                                    otherPlayer),
                                                                     otherPlayer))
                                              .max(Comparator.comparingInt(x -> x))
                                              .map(x -> -x);
        if (maxAfterTurn.isEmpty() || maxAfterTurn.get() == 0) {
            return 0;
        } else if (maxAfterTurn.get() < 0) {
            return maxAfterTurn.get() + 1;
        } else {
            return maxAfterTurn.get() - 1;
        }
    }

    static Player bestChoiceBotPlayer() {
        record Scored(Position position, int score) {}
        return (board, playAs) -> {
            Optional<Scored> max = board.empties()
                                        .map(position -> new Scored(position, score(board.withCell(position, playAs), playAs)))
                                        .max(Comparator.comparingInt(s -> s.score));
            if (max.isEmpty()) {
                throw new RuntimeException("cannot play on an empty board");
            }
            return max.get().position;
        };
    }

    static Player humanPlayer() {
        return (board, playAs) -> {
            while (true) {
                Position position = requestHumanPosition(playAs);
                if (board.get(position).isEmpty()) {
                    return position;
                } else {
                    System.out.printf("%s is already occupied%n", position);
                }
            }
        };
    }

    private static Position requestHumanPosition(Token playAs) {
        Scanner inputScanner = new Scanner(System.in);
        while (true) {
            System.out.printf("It is the turn of %s%n" +
                              "Please insert a position <row> <col>%n",
                              playAs);
            Scanner inLineScanner;
            try {
                inLineScanner = new Scanner(inputScanner.nextLine());
            } catch (NoSuchElementException exception) {
                System.err.println("stdin appears to be closed");
                throw exception;
            }
            int row;
            try {
                row = inLineScanner.nextInt();
            } catch (NoSuchElementException exception) {
                System.out.println("could not parse input from user");
                System.out.println("row was not of a correct format");
                continue;
            }
            if (row < 0 || row > 2) {
                System.out.println("could not parse input from user");
                System.out.printf("input row %d in not in bounds [0,2]%n", row);
                continue;
            }
            int col;
            try {
                col = inLineScanner.nextInt();
            } catch (NoSuchElementException exception) {
                System.out.println("could not parse input from user");
                System.out.println("col was not of a correct format");
                continue;
            }
            if (col < 0 || col > 2) {
                System.out.println("could not parse input from user");
                System.out.printf("input col %d in not in bounds [0,2]%n", col);
                continue;
            }
            if (inLineScanner.hasNext()) {
                System.out.println("could not parse input from user");
                System.out.println("inputted line contains another token after <row> <col>");
                continue;
            }
            return new Position(row, col);
        }
    }
}
