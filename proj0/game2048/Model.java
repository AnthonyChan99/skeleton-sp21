package game2048;

import java.util.Formatter;
import java.util.Observable;


/** The state of a game of 2048.
 *  @author Anthony Chan
 */
public class Model extends Observable {
    /** Current contents of the board. */
    private Board board;
    /** Current score. */
    private int score;
    /** Maximum score so far.  Updated when game ends. */
    private int maxScore;
    /** True iff game is ended. */
    private boolean gameOver;

    /* Coordinate System: column C, row R of the board (where row 0,
     * column 0 is the lower-left corner of the board) will correspond
     * to board.tile(c, r).  Be careful! It works like (x, y) coordinates.
     */

    /** Largest piece value. */
    public static final int MAX_PIECE = 2048;

    /** A new 2048 game on a board of size SIZE with no pieces
     *  and score 0. */
    public Model(int size) {
        board = new Board(size);
        score = maxScore = 0;
        gameOver = false;
    }

    /** A new 2048 game where RAWVALUES contain the values of the tiles
     * (0 if null). VALUES is indexed by (row, col) with (0, 0) corresponding
     * to the bottom-left corner. Used for testing purposes. */
    public Model(int[][] rawValues, int score, int maxScore, boolean gameOver) {
        int size = rawValues.length;
        board = new Board(rawValues, score);
        this.score = score;
        this.maxScore = maxScore;
        this.gameOver = gameOver;
    }

    /** Return the current Tile at (COL, ROW), where 0 <= ROW < size(),
     *  0 <= COL < size(). Returns null if there is no tile there.
     *  Used for testing. Should be deprecated and removed.
     *  */
    public Tile tile(int col, int row) {
        return board.tile(col, row);
    }

    /** Return the number of squares on one side of the board.
     *  Used for testing. Should be deprecated and removed. */
    public int size() {
        return board.size();
    }

    /** Return true iff the game is over (there are no moves, or
     *  there is a tile with value 2048 on the board). */
    public boolean gameOver() {
        checkGameOver();
        if (gameOver) {
            maxScore = Math.max(score, maxScore);
        }
        return gameOver;
    }

    /** Return the current score. */
    public int score() {
        return score;
    }

    /** Return the current maximum game score (updated at end of game). */
    public int maxScore() {
        return maxScore;
    }

    /** Clear the board to empty and reset the score. */
    public void clear() {
        score = 0;
        gameOver = false;
        board.clear();
        setChanged();
    }

    /** Add TILE to the board. There must be no Tile currently at the
     *  same position. */
    public void addTile(Tile tile) {
        board.addTile(tile);
        checkGameOver();
        setChanged();
    }

    /** Tilt the board toward SIDE. Return true iff this changes the board.
     *
     * 1. If two Tile objects are adjacent in the direction of motion and have
     *    the same value, they are merged into one Tile of twice the original
     *    value and that new value is added to the score instance variable
     * 2. A tile that is the result of a merge will not merge again on that
     *    tilt. So each move, every tile will only ever be part of at most one
     *    merge (perhaps zero).
     * 3. When three adjacent tiles in the direction of motion have the same
     *    value, then the leading two tiles in the direction of motion merge,
     *    and the trailing tile does not.
     * */
    public boolean tilt(Side side) {
        boolean changed;
        changed = false;

        // TODO: Modify this.board (and perhaps this.score) to account
        // for the tilt to the Side SIDE. If the board changed, set the
        // changed local variable to true.
        board.setViewingPerspective(side);
        for (int i = 0; i < board.size(); i++) {
            int[] tiltRes = tiltOneCol(side,i);
            boolean anyChange = false;
            for (int j = 0; j < board.size(); j++) {
                if (tiltRes[j] != -1 && tiltRes[j] != j) anyChange = true;
            }
            if (anyChange) {
                tiltOneColMove(side, i, tiltRes);
                changed = true;
            }
        }
        board.setViewingPerspective(Side.NORTH);
        checkGameOver();
        if (changed) {
            setChanged();
        }
        return changed;
    }


    private int[] updateTiltIndexRes(int[] currentTiltIndexRes, int[] tiltIndexRes) {
        for (int i = 0; i < board.size(); i++) {
            if (tiltIndexRes[i] != -1 && currentTiltIndexRes[tiltIndexRes[i]] != tiltIndexRes[i]) tiltIndexRes[i] = currentTiltIndexRes[tiltIndexRes[i]];
        }
        return tiltIndexRes;
    }

    private int[] slide(int[] tiltValue) {
        int[] currentTiltIndexRes = new int[board.size()];
        int p = 0;
        for (int i = 0; i < board.size(); i++) {
            currentTiltIndexRes[i] = i;
        }

        for (int i = 0; i < board.size(); i++) {
            if (tiltValue[i] != -1) {
                tiltValue[p] = tiltValue[i];
                if (p != i) tiltValue[i] = -1;
                currentTiltIndexRes[i] = p;
                p += 1;
            }
        }
        return currentTiltIndexRes;
    }


    private int[] merge(int[] tiltValue) {
        int[] currentTiltIndexRes = new int[board.size()];
        for (int i = 0; i < board.size(); i++) {
            currentTiltIndexRes[i] = i;
        }
        int mergeIndex = 0;
        while (mergeIndex + 1 < board.size() && tiltValue[mergeIndex] != -1) {
            if(tiltValue[mergeIndex] == tiltValue[mergeIndex+1]) {
                tiltValue[mergeIndex] *= 2;
                tiltValue[mergeIndex + 1] = -1;
                currentTiltIndexRes[mergeIndex + 1] = mergeIndex;
                mergeIndex += 2;
            }
            else mergeIndex += 1;
        }
        return currentTiltIndexRes;
    }

    private int[] tiltOneCol(Side side, int col) {
        int[] tiltValue = new int[board.size()];
        for (int i = 0; i < board.size(); i++) {
            int[] actualCoordinates = getActualCoordinates(side,col, board.size()-1-i);
            if (tile(actualCoordinates[0],actualCoordinates[1]) != null) tiltValue[i] = tile(actualCoordinates[0],actualCoordinates[1]).value();
            else tiltValue[i] = -1;
        }
        int[] tiltIndexRes = new int[board.size()];
        for (int i = 0; i < board.size(); i++) {
            if (tiltValue[i] != -1) {
                tiltIndexRes[i] = i;
            }
            else tiltIndexRes[i] = -1;
        }

        int[] currentTiltIndexRes = slide(tiltValue);

        tiltIndexRes = updateTiltIndexRes(currentTiltIndexRes,tiltIndexRes);

        currentTiltIndexRes = merge(tiltValue);

        tiltIndexRes = updateTiltIndexRes(currentTiltIndexRes,tiltIndexRes);

        currentTiltIndexRes = slide(tiltValue);

        tiltIndexRes = updateTiltIndexRes(currentTiltIndexRes,tiltIndexRes);

        return tiltIndexRes;
    }

    private void tiltOneColMove(Side side, int col, int[] tiltRes) {
        for (int i = 0; i < board.size(); i++) {
            if (tiltRes[i] != -1 && tiltRes[i] != i) {
                int[] actualFromCoordinates = getActualCoordinates(side,col, board.size()-1-i);
                int[] actualToCoordinates = getActualCoordinates(side,col, board.size()-1-tiltRes[i]);
                if (tile(actualToCoordinates[0],actualToCoordinates[1]) != null && tile(actualFromCoordinates[0], actualFromCoordinates[1]).value() == tile(actualToCoordinates[0], actualToCoordinates[1]).value()) {
                    score += board.tile(actualToCoordinates[0],actualToCoordinates[1]).value() * 2;
                }
                board.move(actualToCoordinates[0],actualToCoordinates[1],tile(actualFromCoordinates[0],actualFromCoordinates[1]));
            }
        }
    }

    private int[] getActualCoordinates(Side side, int col, int row){
        int[] actualCoordinates = new int[2];
        actualCoordinates[0] = col;
        actualCoordinates[1] = row;
        return actualCoordinates;
    }
    /** Checks if the game is over and sets the gameOver variable
     *  appropriately.
     */
    private void checkGameOver() {
        gameOver = checkGameOver(board);
    }

    /** Determine whether game is over. */
    private static boolean checkGameOver(Board b) {
        return maxTileExists(b) || !atLeastOneMoveExists(b);
    }

    /** Returns true if at least one space on the Board is empty.
     *  Empty spaces are stored as null.
     * */
    public static boolean emptySpaceExists(Board b) {
        // TODO: Fill in this function.
        int boardSize = b.size();
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (b.tile(i,j) == null) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if any tile is equal to the maximum valid value.
     * Maximum valid value is given by MAX_PIECE. Note that
     * given a Tile object t, we get its value with t.value().
     */
    public static boolean maxTileExists(Board b) {
        // TODO: Fill in this function.
        int boardSize = b.size();
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (b.tile(i,j)!= null && b.tile(i,j).value() == MAX_PIECE) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if there are any valid moves on the board.
     * There are two ways that there can be valid moves:
     * 1. There is at least one empty space on the board.
     * 2. There are two adjacent tiles with the same value.
     */
    public static boolean atLeastOneMoveExists(Board b) {
        // TODO: Fill in this function.
        if (Model.emptySpaceExists(b)) return true;
        for (int i = 0; i < b.size(); i++) {
            for (int j = 0; j < b.size(); j++) {
                if (j - 1 >= 0 && b.tile(i, j).value() == b.tile(i, j - 1).value()) return true;
                if (j + 1 < b.size() && b.tile(i, j).value() == b.tile(i, j + 1).value()) return true;
                if (i - 1 >= 0 && b.tile(i, j).value() == b.tile(i - 1, j).value()) return true;
                if (i + 1 < b.size() && b.tile(i, j).value() == b.tile(i + 1, j).value()) return true;
            }
        }
        return false;
    }


    @Override
     /** Returns the model as a string, used for debugging. */
    public String toString() {
        Formatter out = new Formatter();
        out.format("%n[%n");
        for (int row = size() - 1; row >= 0; row -= 1) {
            for (int col = 0; col < size(); col += 1) {
                if (tile(col, row) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(col, row).value());
                }
            }
            out.format("|%n");
        }
        String over = gameOver() ? "over" : "not over";
        out.format("] %d (max: %d) (game is %s) %n", score(), maxScore(), over);
        return out.toString();
    }

    @Override
    /** Returns whether two models are equal. */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (getClass() != o.getClass()) {
            return false;
        } else {
            return toString().equals(o.toString());
        }
    }

    @Override
    /** Returns hash code of Model’s string. */
    public int hashCode() {
        return toString().hashCode();
    }
}
