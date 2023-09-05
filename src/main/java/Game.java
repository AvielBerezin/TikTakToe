import java.util.Objects;
import java.util.Optional;

public class Game {
    record GameState(Board board, Optional<Token> winner) {}

    public static Iter<GameState> getPlaying(Player playerX,
                                             Player playerO) {
        record PlayerSwitcher(Player currentPlayer, Token currentToken,
                              Player nextPlayer, Token nextToken) {
            PlayerSwitcher switchPlayer() {
                return new PlayerSwitcher(nextPlayer, nextToken,
                                          currentPlayer, currentToken);
            }

            Position makeTurn(Board board) {
                return currentPlayer.makeTurn(board, currentToken);
            }
        }
        record State(Board board, PlayerSwitcher playerSwitcher, Optional<Token> winner) {
            State next() {
                Position madeTurn = playerSwitcher.makeTurn(board);
                Board nextBoard = board.withCell(madeTurn, playerSwitcher.currentToken);
                return new State(nextBoard,
                                 playerSwitcher.switchPlayer(),
                                 nextBoard.winningAt(madeTurn)
                                 ? Optional.of(playerSwitcher.currentToken)
                                 : winner);
            }
        }
        return Iter.successionFrom(new State(Board.emptyBoard(),
                                             new PlayerSwitcher(playerX, Token.X, playerO, Token.O),
                                             Optional.empty()),
                                   State::next)
                   .map(state -> new GameState(state.board, state.winner))
                   .breakWhen(gameState -> gameState.winner.isPresent())
                   .limit(10);
    }

    public static void main(String[] args) {
        getPlaying(Player.humanPlayer(), Player.bestChoiceBotPlayer())
                .forEach(gameState -> {
                    System.out.printf("%s|%s|%s%n" +
                                      "-----%n" +
                                      "%s|%s|%s%n" +
                                      "-----%n" +
                                      "%s|%s|%s%n",
                                      gameState.board.get(0, 0).map(Objects::toString).orElse(" "),
                                      gameState.board.get(0, 1).map(Objects::toString).orElse(" "),
                                      gameState.board.get(0, 2).map(Objects::toString).orElse(" "),
                                      gameState.board.get(1, 0).map(Objects::toString).orElse(" "),
                                      gameState.board.get(1, 1).map(Objects::toString).orElse(" "),
                                      gameState.board.get(1, 2).map(Objects::toString).orElse(" "),
                                      gameState.board.get(2, 0).map(Objects::toString).orElse(" "),
                                      gameState.board.get(2, 1).map(Objects::toString).orElse(" "),
                                      gameState.board.get(2, 2).map(Objects::toString).orElse(" "));
                    gameState.winner.map("%s won the game!"::formatted)
                                    .ifPresent(System.out::println);
                });
    }
}
