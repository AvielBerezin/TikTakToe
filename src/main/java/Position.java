public record Position(int row, int col) {
    public Position {
        if (row < 0 || row > 2) {
            throw new IllegalArgumentException("row of value %d is out of range [0,2]".formatted(row));
        }
        if (col < 0 || col > 2) {
            throw new IllegalArgumentException("col of value %d is out of range [0,2]".formatted(col));
        }
    }
}
