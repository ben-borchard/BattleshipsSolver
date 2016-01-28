import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Created by bborchard on 7/21/2015.
 */
public class Grid {

    private Cell[][] cells;



    public enum dir {
        UP,
        RIGHT,
        DOWN,
        LEFT,
        SINGLE,
        UPDOWN,
        LEFTRIGHT,
        UNDETERMINED
    }

    private boolean bigShip;
    private int[] shipsPlaced;
    private int[][] puzzleStatus;
    private int[][] initialValues;
    private ArrayList<ShipSection> unplacedShips;

    private final int[] shipNums = {4,3,2,1,1};

//    int[] blockOffsets = new int[] { 2, 6, 8, 9, 10, 11 };
// value == 3 ? new int[] {0,1,2,3,5,6,7,8,11} :
    /* All (x,y) offsets we will need to use */
    private final int[][] all = {
            {0,1},  //0
            {1,1},  //1
            {1,0},  //2
            {1,-1}, //3
            {0,-1}, //4
            {-1,-1},//5
            {-1,0}, //6
            {-1, 1},//7
            {1,2},  //8
            {1,-2}, //9
            {-1,-2},//10
            {-1,2}, //11
            {2,1},  //12
            {2,-1}, //13
            {-2,1}, //14
            {-2,-1} //15
    };



    public Grid(int s, int[][] puzzle, int[][] initialValues, boolean bigShip){
        cells = new Cell[s][s];
        for (int i=0;i<s;i++)
            for(int j=0;j<s;j++)
                cells[i][j] = new Cell(i,j);

        this.shipsPlaced = bigShip ? new int[5] : new int[4];
        this.puzzleStatus = puzzle;
        this.initialValues = initialValues;
        this.unplacedShips = new ArrayList<>();
        this.bigShip = bigShip;
    }

    public Grid(Cell[][] cells, int[][] puzzle){
        this.cells = cells;
        this.puzzleStatus = puzzle;
        this.unplacedShips = new ArrayList<>();
    }

    public void setCellNot(int row, int col){
        cells[row][col].notShip();
    }

    public boolean placeShip(ShipSection ship){
        return this.placeShip(ship.getRow(), ship.getCol(), ship.getDirection(), ship.getLength());
    }

    public boolean placeShip(int row, int col, dir direction, int length){

        boolean horizontal = direction == dir.RIGHT || direction == dir.LEFT;
        boolean downRight = direction == dir.RIGHT || direction == dir.DOWN;
        int interval = downRight ? 1 : -1;
        int startVal = horizontal ? col : row;
        int stopVal = startVal + (downRight ? length : 0-length);

        shipsPlaced[length-1]++;
        for (int i=startVal; (downRight ? i<stopVal : i>stopVal) ;i+=interval){
            int currRowVal = horizontal ? row : i;
            int currColVal = horizontal ? i : col;


            /* Place ship pieces */
            if (direction == dir.SINGLE){
                cells[row][col].setShip();
            } else {
                cells[currRowVal][currColVal].setShip();
            }

            /* Rule out ship pieces */
            int[] startRange = direction == dir.DOWN ? new int[] {2,3,4,5,6} :
                          direction == dir.UP ? new int[] {0,1,2,6,7} :
                          direction == dir.RIGHT ? new int[] {0,4,5,6,7} :
                          direction == dir.LEFT ? new int[] {0,1,2,3,4} : new int[] {0,1,2,3,4,5,6,7};

            int[] endRange = direction == dir.UP ? new int[] {2,3,4,5,6} :
                               direction == dir.DOWN ? new int[] {0,1,2,6,7} :
                               direction == dir.LEFT ? new int[] {0,4,5,6,7} :
                               direction == dir.RIGHT ? new int[] {0,1,2,3,4} : new int[] {0,1,2,3,4,5,6,7};
            int[] midRange = horizontal ? new int[] {0,4} : new int[] {2,6};
            if (direction == dir.SINGLE){
                for (int j : startRange){
                    int rowWithOffset = currRowVal+all[j][1];
                    int colWithOffset = currColVal+all[j][0];
                    if (rowWithOffset >= 0 && rowWithOffset < cells.length && colWithOffset >= 0 && colWithOffset < cells.length )
                        cells[rowWithOffset][colWithOffset].notShip();
                }
            } else if (i == startVal){
                for (int j : startRange){
                    int rowWithOffset = currRowVal+all[j][1];
                    int colWithOffset = currColVal+all[j][0];
                    if (rowWithOffset >= 0 && rowWithOffset < cells.length && colWithOffset >= 0 && colWithOffset < cells.length )
                        cells[rowWithOffset][colWithOffset].notShip();
                }
            } else if(i == (startVal)+((length-1)*interval)) {
                for (int j : endRange){
                    int rowWithOffset = currRowVal+all[j][1];
                    int colWithOffset = currColVal+all[j][0];
                    if (rowWithOffset >= 0 && rowWithOffset < cells.length && colWithOffset >= 0 && colWithOffset < cells.length )
                        cells[rowWithOffset][colWithOffset].notShip();
                }
            } else {
                for (int j : midRange){
                    int rowWithOffset = currRowVal+all[j][1];
                    int colWithOffset = currColVal+all[j][0];
                    if (rowWithOffset >= 0 && rowWithOffset < cells.length && colWithOffset >= 0 && colWithOffset < cells.length )
                        cells[rowWithOffset][colWithOffset].notShip();
                }
            }

            if (horizontal){
                if (checkCol(currColVal) == -1){
                    return false;
                }
            } else {
                if (checkRow(currRowVal) == -1){
                    return false;
                }
            }
        }

        if (horizontal){
            if (checkRow(row) == -1){
                return false;
            }
        } else {
            if (checkCol(col) == -1){
                return false;
            }
        }



        /* Check updated rows and cols */
//        int hstartPos = col + (downRight ? -1 : 1);
//        int vstartPos = row + (downRight ? -1 : 1);
//        int hendPosition = col + (downRight ? 1 : -1) + (horizontal ? length : 1);
//        int vendPosition = row + (downRight ? 1 : -1) + (horizontal ? 1 : length);
//
//        /* Check cols */
//        for (int i=hstartPos;i<hendPosition+1;i++){
//            return checkCol(i) != -1;
//        }
//
//        /* Check rows */
//        for (int i=vstartPos;i<vendPosition+1;i++){
//            return checkRow(i) != -1;
//        }

        return true;

    }

    public int checkRow(int row){
        return checkGroup(cells[row], puzzleStatus[1][row]);
    }

    public int checkCol(int col){
        Cell[] colGroup = new Cell[cells.length];
        for(int i=0;i<cells.length;i++){
            colGroup[i] = cells[i][col];
        }
        return checkGroup(colGroup, puzzleStatus[0][col]);
    }

    public int checkGroup(Cell[] group, int possibleShipPieces){
        int shipPiecesFound = 0;
        int remainingSpots = 0;
        for (int i=0;i<group.length;i++){
            if (group[i].getShipStatus() == Cell.ship.YES){
                shipPiecesFound++;
            } else if (group[i].getShipStatus() == Cell.ship.NOT_SURE){
                remainingSpots++;
            }
        }

        /* Determine if the row is solved, bad, or undetermined */
        if (shipPiecesFound > possibleShipPieces || shipPiecesFound + remainingSpots < possibleShipPieces){
            return -1;
        } else if (shipPiecesFound == possibleShipPieces){
            for (int i=0;i<group.length;i++){
                if (group[i].getShipStatus() == Cell.ship.NOT_SURE){
                    group[i].notShip();
                }
            }
            return 1;
        } else {
            return 0;
        }
    }

    public void init() {

        // All rows with 0
        for (int i = 0; i < puzzleStatus[1].length; i++) {
            if (puzzleStatus[1][i] == 0) {
                for (int j = 0; j < cells.length; j++) {
                    cells[i][j].notShip();
                }
            }
        }

        // All cols with 0
        for (int i = 0; i < puzzleStatus[0].length; i++) {
            if (puzzleStatus[0][i] == 0) {
                for (int j = 0; j < cells.length; j++) {
                    cells[j][i].notShip();
                }
            }
        }

        // Initial ship pieces
        ArrayList<String> coordsToSkip = new ArrayList<>();
        for (int i = 0; i < initialValues.length; i++) {
            for (int j = 0; j < initialValues.length; j++) {
                if (!coordsToSkip.contains(String.valueOf(i)+String.valueOf(j))) {
                    if (initialValues[i][j] != 0) {
                        coordsToSkip.addAll(initShipPiece(initialValues[i][j], i, j).stream().collect(Collectors.toList()));
                    }
                }
            }
        }
    }

    private ArrayList<String> initShipPiece(int value, int row, int col){
        boolean single = value == 1;
        boolean block = value == 2;
        boolean horizontal = value == 4 || value == 6;
        boolean shipPlaced = false;

        int[] adjacent = new int[] {0,2};

        int[] startRange = value == 5 ? new int[] {1,2,3,4,5,6,7,8,11} :
                           value == 3 ? new int[] {0,1,2,3,5,6,7,9,10} :
                           value == 4 ? new int[] {0,1,3,4,5,6,7,12,13} :
                           value == 6 ? new int[] {0,1,2,3,4,5,7,14,15} :
                           value == 1 ? new int[] {0,1,2,3,4,5,6,7} : new int[] {1,3,5,7};

        int[] blockOffsets = new int[] { 2, 6, 8, 9, 10, 11 };

        /* Look for adjacent initialized ship pieces */
        ArrayList<String> coordsToSkip = new ArrayList<>();
        int pieceLength = 1;
        int dirNumber = initialValues[row][col];
        int n = 0;
        while(true){
            int rowWithOffset = row+all[adjacent[n]][1];
            int colWithOffset = col+all[adjacent[n]][0];
            if (rowWithOffset >= 0 && rowWithOffset < cells.length && colWithOffset >= 0 && colWithOffset < cells.length ) {
                if (initialValues[rowWithOffset][colWithOffset] != 0) {
                    coordsToSkip.add(String.valueOf(rowWithOffset)+String.valueOf(colWithOffset));
                    if (initialValues[rowWithOffset][colWithOffset] == 2 && initialValues[row][col] == 2) {
                        dirNumber = n == 0 ? 7 : 8;
                    } else {
                        dirNumber = initialValues[row][col] != 2 ? initialValues[row][col] : initialValues[rowWithOffset][colWithOffset];
                    }
                    adjacent = new int[] {adjacent[n]};
                    n = 0;
                    pieceLength++;
                    continue;
                }
            }
            n++;
            if (n == 2) { break; }
        }

        /* Rule out cells around the ship piece depending on its type */
        for(int i : startRange){
            int rowWithOffset = row+all[i][1];
            int colWithOffset = col+all[i][0];
            if (rowWithOffset >= 0 && rowWithOffset < cells.length && colWithOffset >= 0 && colWithOffset < cells.length )
                cells[rowWithOffset][colWithOffset].notShip();
        }

        /* Place ships/perform extra logic based on initial information */
        if (single){
            placeShip(row, col, dir.SINGLE, 1);
            shipPlaced = true;
        } else if (block){
            if (puzzleStatus[0][col] < 3 || spaceAt(row, col, dir.UPDOWN) < 3){
                if (puzzleStatus[1][row] == 3 || spaceAt(row, col, dir.LEFTRIGHT) == 3){
                    placeShip(row, col-1, dir.RIGHT, 3);
                    shipPlaced = true;
                } else {
                    dirNumber = 7;
                    for(int i : blockOffsets){
                        int rowVal = row + all[i][0];
                        int colVal = col + all[i][1];
                        if (rowVal > 0 && rowVal < cells.length && colVal > 0 && colVal < cells.length){
                            cells[rowVal][colVal].notShip();
                        }
                    }
                }
            } else if (puzzleStatus[1][row] < 3 || spaceAt(row, col, dir.LEFTRIGHT) < 3){
                if (puzzleStatus[0][col] == 3 || spaceAt(row, col, dir.UPDOWN) == 3){
                    placeShip(row-1, col, dir.DOWN, 3);
                    shipPlaced = true;
                    
                } else {
                    dirNumber = 8;
                    for(int i : blockOffsets){
                        int rowVal = row + all[i][1];
                        int colVal = col + all[i][0];
                        if (rowVal >= 0 && rowVal < cells.length && colVal >= 0 && colVal < cells.length){
                            cells[rowVal][colVal].notShip();
                        }
                    }
                }
            }
        } else {
            dir direction = value == 3 ? dir.UP : value == 4 ? dir.RIGHT :
                            value == 5 ? dir.DOWN : dir.LEFT;
            int rowStatus = horizontal ? 1 : 0;
            int puzzleIndex = horizontal ? row : col;

            if (puzzleStatus[rowStatus][puzzleIndex] == 2 || spaceAt(row, col, direction) == 2){
                placeShip(row, col, direction, 2);
                shipPlaced = true;
            }
        }

        if (!shipPlaced){

            dir shipPieceDir = dirNumber == 2 ? dir.UNDETERMINED :
                               dirNumber == 3 ? dir.UP :
                               dirNumber == 4 ? dir.RIGHT :
                               dirNumber == 5 ? dir.DOWN :
                               dirNumber == 6 ? dir.LEFT :
                               dirNumber == 7 ? dir.LEFTRIGHT : dir.UPDOWN;
            boolean noEdge = shipPieceDir == dir.LEFTRIGHT || shipPieceDir == dir.UPDOWN;
            int rowOffset = shipPieceDir == dir.LEFTRIGHT ? 0 : -1;
            int colOffset = shipPieceDir == dir.LEFTRIGHT ? -1 : 0;
            dir placeDir = shipPieceDir == dir.LEFTRIGHT ? dir.LEFT : dir.DOWN;
            if (noEdge && (!bigShip ||  spaceAt(row, col, shipPieceDir) > 5) && pieceLength == 2) {
                placeShip(row-rowOffset, col-colOffset, placeDir, 4);
                return coordsToSkip;
            }
            if (noEdge && pieceLength == 3){
                placeShip(row-rowOffset, col-colOffset, placeDir, 5);
                return coordsToSkip;
            }
            ShipSection shipSection = new ShipSection(row, col, pieceLength, shipPieceDir);
            unplacedShips.add(shipSection);
        }

        return coordsToSkip;

    }

    public int spaceAt(int row, int col, dir direction){
        boolean bothWays = direction == dir.LEFTRIGHT || direction == dir.UPDOWN;
        boolean horizontal = direction == dir.LEFT || direction == dir.RIGHT || direction == dir.LEFTRIGHT;
        boolean downRight = direction == dir.DOWN || direction == dir.RIGHT;

        int increment = downRight ? 1 : -1;
        int i = horizontal ? col : row;

        int space = 0;

        while(true){
            int curRow = horizontal ? row : i;
            int curCol = horizontal ? i : col;
            if ((i >= 0 && i < cells.length) && cells[curRow][curCol].getShipStatus() == Cell.ship.NOT_SURE){
                space++;
            } else if (bothWays && increment == -1){
                increment = 0-increment;
                i = (horizontal ? col : row) + increment;
            } else {
                break;
            }
            i += increment;
        }

        return space;
    }

    private boolean solved(){

        for (int i=0;i<cells.length;i++){
            if (checkRow(i) != 1 && checkCol(i) != 1) {
                return false;
            }
        }

        return true;
    }

    public String toString(){
        String str = "";
        for(Cell[] cs : cells) {
            for (Cell c : cs)
                str += c + " ";
            str += "\n";
        }
        return str;
    }

    public Grid clone(){
        Cell[][] newCells = new Cell[cells.length][cells.length];
        for (int i=0;i<cells.length;i++){
            for (int j=0;j<cells.length;j++){
                newCells[i][j] = cells[i][j].clone();
            }
        }
        return new Grid(newCells, this.puzzleStatus);
    }

    public int getNextShipLength(){

        for (int i=(bigShip ? 4 : 3); i>=0;i--){
            if (shipsPlaced[i] != shipNums[i]) {
                return i+1;
            }
        }
        return -1;
    }

    public Grid deepClone(){
        Grid grid = this.clone();
        grid.setShipsPlaced(this.shipsPlaced.clone());
        grid.setInitialValues(this.initialValues.clone());
        grid.setBigShip(this.bigShip);
        return grid;
    }

    public Cell.ship getShipStatus(int row, int col) {
        if (row >= cells.length || row < 0 || col >= cells.length ||  col < 0){
            return null;
        }
        return cells[row][col].getShipStatus();
    }
    public void setShipsPlaced(int[] shipsPlaced) { this.shipsPlaced = shipsPlaced; }
    public void setInitialValues(int[][] initialValues) { this.initialValues = initialValues; }
    public void setBigShip(boolean bigShip) { this.bigShip = bigShip; }
    public ArrayList<ShipSection> getUnplacedShips() { return unplacedShips; }
}
