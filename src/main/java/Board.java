import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface Board {
    Optional<Token> get(int row, int col);

    default Optional<Token> get(Position position) {
        return get(position.row(), position.col());
    }

    default Board flipRow() {
        return (row, col) -> get(2 - row, col);
    }

    default Board flipCol() {
        return (row, col) -> get(row, 2 - col);
    }

    default Board swapRowCol() {
        return (row, col) -> get(col, row);
    }

    default List<Board> isomorphisms() {
        return List.of(this,
                       flipRow(),
                       flipCol(),
                       swapRowCol(),
                       flipRow().flipCol(),
                       flipRow().swapRowCol(),
                       flipCol().swapRowCol(),
                       flipRow().flipCol().swapRowCol());
    }

    default Board withCell(int row, int col, Token value) {
        return (reqRow, reqCol) -> {
            if (reqRow == row &&
                reqCol == col) {
                return Optional.of(value);
            } else {
                return get(reqRow, reqCol);
            }
        };
    }

    default Board withCell(Position position, Token value) {
        return withCell(position.row(), position.col(), value);
    }

    default Board withEmptyCell(int row, int col) {
        return (reqRow, reqCol) -> {
            if (reqRow == row &&
                reqCol == col) {
                return Optional.empty();
            } else {
                return get(reqRow, reqCol);
            }
        };
    }

    default boolean isWinningAt(int row, int col) {
        return get(row, col).isPresent() &&
               (get(0, col).equals(get(1, col)) &&
                get(1, col).equals(get(2, col)) ||
                get(row, 0).equals(get(row, 1)) &&
                get(row, 1).equals(get(row, 2)) ||
                row == col &&
                get(0, 0).equals(get(1, 1)) &&
                get(1, 1).equals(get(2, 2)) ||
                row == 2 - col &&
                get(0, 2).equals(get(1, 1)) &&
                get(1, 1).equals(get(2, 0)));
    }

    default boolean winningAt(Position position) {
        return isWinningAt(position.row(), position.col());
    }

    static Board emptyBoard() {
        return (row, col) -> Optional.empty();
    }

    default boolean equals(Board board) {
        return get(0, 0).equals(board.get(0, 0)) &&
               get(0, 1).equals(board.get(0, 1)) &&
               get(0, 2).equals(board.get(0, 2)) &&
               get(1, 0).equals(board.get(1, 0)) &&
               get(1, 1).equals(board.get(1, 1)) &&
               get(1, 2).equals(board.get(1, 2)) &&
               get(2, 0).equals(board.get(2, 0)) &&
               get(2, 1).equals(board.get(2, 1)) &&
               get(2, 2).equals(board.get(2, 2));
    }

    default Stream<Position> empties() {
        return Stream.of(0, 1, 2)
                     .flatMap(row -> Stream.of(0, 1, 2)
                                           .map(col -> new Position(row, col)))
                     .filter(position -> get(position).isEmpty());
    }
}
