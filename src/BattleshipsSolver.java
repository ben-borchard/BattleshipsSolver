import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

/**
 * Created by bborchard on 7/20/2015.
 */
public class BattleshipsSolver {

    private int s;

    private Grid grid;
    private ArrayList<Stack<ShipSection>> initShipPossibilities;
    private ArrayList<Stack<ShipSection>> initShipPossibilitiesFresh;
    private int[] cols;
    private int[] rows;

    private Stack<Grid> grids;
    private Stack<ShipSection> guesses;
    private Stack<Integer> coords;
    private int currInitShip;
    private int currRow;
    private int currCol;
    private boolean checkingRows;
    private boolean initShipsPlaced;


    private boolean bigShip;

    public BattleshipsSolver(int[][] initialCellsVals, int[] cols, int[] rows, boolean bigShip) {
        if (!puzzleSquare(cols, rows)) {
            System.err.println("The input puzzle is not a square, please check your input again");
            System.exit(1);
        }

        this.grids = new Stack();
        this.guesses = new Stack();
        this.coords = new Stack();
        this.initShipPossibilitiesFresh = new ArrayList();
        this.initShipPossibilities = new ArrayList();
        this.bigShip = bigShip;
        this.cols = cols;
        this.rows = rows;


        s = rows.length;

        this.grid = new Grid(s, new int[][]{cols, rows}, initialCellsVals, bigShip);
    }

    public void solve() {

        // pre-guess logic
        grid.init();

        createInitialGuessStack(grid.getUnplacedShips());

        while(true){
            break;
        }

        System.out.println(grid);

        currInitShip = 0;
        currRow = 0;
        currCol = 0;
        checkingRows = false;
        initShipsPlaced = initShipPossibilities.isEmpty();
        while (true){
            if (!initShipsPlaced){

                if (initShipPossibilities.get(currInitShip).isEmpty()){
                    grid = grids.pop();
                    guesses.pop();
                    initShipPossibilities.set(currInitShip, initShipPossibilitiesFresh.get(currInitShip));
                    currInitShip--;
                    if (currInitShip < 0) {
                        System.out.println("unsolvable");
                        System.exit(1);
                    }
                    continue;
                }
                ShipSection ship = initShipPossibilities.get(currInitShip).pop();

                System.out.println("guessing init ship length "+ship.getLength()+" at "+ship.getRow()+", "+ship.getCol()+" going "+ship.getDirection());
                grids.push(grid.deepClone());
                guesses.push(ship);
                if (grid.placeShip(ship.getRow(), ship.getCol(),ship.getDirection(), ship.getLength())){
                    System.out.println("good guess");
//                    System.out.println(grid);
                    currInitShip++;
                    if (currInitShip == initShipPossibilities.size()){
                        checkingRows = false;
                        currRow = 0;
                        currCol = 0;
                        initShipsPlaced = true;
                    }

                } else {
                    System.out.println("bad guess, rolling back");
                    while (initShipPossibilities.get(currInitShip).isEmpty()){
                        /* Refresh the guesses since we will pop at the previous indx */
                        initShipPossibilities.set(currInitShip, initShipPossibilitiesFresh.get(currInitShip));
                        currInitShip--;
                        if (currInitShip < 0) {
                            System.out.println("unsolvable");
                            System.exit(1);
                        }
                    }
                    //TODO: fix dis shit
//                    initShipPossibilities.get(currInitShip).pop();
                    grid = grids.pop();
                    guesses.pop();
                }
            } else {
                guessInGrid();
            }
        }

    }

    private void guessInGrid(){
        int length = grid.getNextShipLength();
        System.out.println("looking for a ship length "+length+" in the grid");
        if (length == -1){
            System.out.println("solved");
            System.out.println(grid);
            System.exit(0);
        }

        /* you only need to check the rows (not rows and cols) when looking for singles */
        if (length == 1){ checkingRows = true; }
        boolean firstRun = true;
        for (int i=(checkingRows ? currRow : currCol);i<s;i++){
            if ( (checkingRows ? rows[i] : cols[i]) >= length ){
                for (int j = firstRun ? (checkingRows ? currCol : currRow) : 0;j<s;j++ ){
                    firstRun = false;
                    int checkRow = checkingRows ? i : j;
                    int checkCol = checkingRows ? j : i;
                    Grid.dir checkDir = length == 1 ? Grid.dir.SINGLE : checkingRows ? Grid.dir.RIGHT : Grid.dir.DOWN;
                    int space;
                    if ((space = grid.spaceAt(checkRow, checkCol, checkDir)) >= length){

                        System.out.println("guessing ship length "+length+" at "+checkRow+", "+checkCol+" going "+checkDir);
                        grids.push(grid.deepClone());
                        guesses.push(new ShipSection(checkRow,checkCol,length,checkDir, ShipSection.shipType.GRID));
                        if (grid.placeShip(checkRow,checkCol,checkDir,length)){
                            System.out.println("good guess");
//                            System.out.println(grid);
                            if (length != grid.getNextShipLength()){
                                currCol = 0;
                                currRow = 0;
                                checkingRows = length == 1 ? true : false;
                                return;
                            } else {
                                j += length;
                            }

                        } else {
                            System.out.println("bad guess, rolling back");
                            grid = grids.pop();
                            ShipSection lastGuess = guesses.pop();
                            if (lastGuess.getInitShip() == ShipSection.shipType.INIT || lastGuess.getInitShip() == ShipSection.shipType.LAST_INIT) {
                                initShipsPlaced = false;
                                currInitShip--;
                                return;
                            }
                        }
                    } else {
                        j += space;
                    }
                }
            }
        }
        /* You have made it through the grid without making a guess */
        if (checkingRows) {
            System.out.println("cannot place enough ships of length "+length+", rolling back");
            grid = grids.pop();
            ShipSection lastGuess = guesses.pop();
            if (lastGuess.getInitShip() == ShipSection.shipType.INIT || lastGuess.getInitShip() == ShipSection.shipType.LAST_INIT) {
                initShipsPlaced = false;
                currInitShip--;
            } else {
                checkingRows = lastGuess.getDirection() == Grid.dir.RIGHT || lastGuess.getDirection() == Grid.dir.SINGLE;
                currRow = checkingRows ? lastGuess.getRow() : lastGuess.getRow()+1;
                currCol = checkingRows ? lastGuess.getCol()+1 : lastGuess.getCol();

            }
        } else {
            System.out.println("Checking rows");
            checkingRows = true;
            currCol = 0;
            currRow = 0;
        }
    }

        private void createInitialGuessStack(ArrayList<ShipSection> unplacedShips){
        int i=0;
        for (ShipSection shipSection : unplacedShips){
            Stack<ShipSection> possiblePlacements = new Stack();
            Stack<ShipSection> possiblePlacementsFresh = new Stack();
            boolean horizontal = shipSection.getDirection() == Grid.dir.LEFTRIGHT ||
                                 shipSection.getDirection() == Grid.dir.LEFT ||
                                 shipSection.getDirection() == Grid.dir.RIGHT;
            int uspace = grid.spaceAt(shipSection.getRow(), shipSection.getCol(), Grid.dir.UP);
            int rspace = grid.spaceAt(shipSection.getRow(), shipSection.getCol(), Grid.dir.RIGHT);
            int dspace = grid.spaceAt(shipSection.getRow(), shipSection.getCol(), Grid.dir.LEFT);
            int lspace = grid.spaceAt(shipSection.getRow(), shipSection.getCol(), Grid.dir.DOWN);

            /* Ship section is determined to be either horizontal or vertical */
            if (shipSection.getDirection() == Grid.dir.LEFTRIGHT || shipSection.getDirection() == Grid.dir.UPDOWN) {

                ArrayList<Integer> starts = new ArrayList<>();

                int startOffset = bigShip ? 3 : 2;
                int startValue = horizontal ? shipSection.getCol() : shipSection.getRow();

                for (int j=startValue-startOffset;j!=startValue;j++){
                    if (grid.getShipStatus(horizontal ? shipSection.getRow() : j, horizontal ? j : shipSection.getCol()) == Cell.ship.NOT_SURE){
                        starts.add(j);
                    }
                }
                /*
                offset = 3, possible lengths: [5]              end value: 4
                offset = 2, possible lengths: [4,5], [4]       end value: 3
                offset = 1, possible lengths: [3,4,5], [3,4]   end value: 2
                 */

                for (int start : starts){
                    for(int j = bigShip ? 5 : 4;j!=(startValue - start)+1;j--){
                        if (grid.spaceAt(horizontal ? shipSection.getRow() : start, horizontal ? start : shipSection.getCol(), horizontal ? Grid.dir.RIGHT : Grid.dir.DOWN) >= j){
                            possiblePlacements.push(new ShipSection(horizontal ? shipSection.getRow() : start,
                                                                    horizontal ? start : shipSection.getCol(), j,
                                                                    horizontal ? Grid.dir.RIGHT : Grid.dir.DOWN,
                                                                    ShipSection.shipType.INIT));
                        }
                    }
                }

            } /* Ship section is a single block piece that could be horizontal or not */
            else if (shipSection.getDirection() == Grid.dir.UNDETERMINED) {


                int uoffset = Math.min((bigShip ? 3 : 2), uspace-1);
                int loffset = Math.min((bigShip ? 3 : 2), lspace-1);

                for (int j=shipSection.getRow()-uoffset; j<shipSection.getRow(); j++){
                    for(int k=3; k<=Math.min((bigShip ? 3 : 2), dspace)+(shipSection.getRow()-j); k++){
                        possiblePlacements.push(new ShipSection(j, shipSection.getCol(), k, Grid.dir.DOWN));
                    }
                }

                for (int j=shipSection.getCol()-loffset; j<shipSection.getCol(); j++){
                    for(int k=3; k<=Math.min((bigShip ? 3 : 2), rspace)+(shipSection.getCol()-j); k++){
                        possiblePlacements.push(new ShipSection(shipSection.getRow(), j, k, Grid.dir.LEFT));
                    }
                }
            } /* Ship selection points in a certain direction */
            else {
                int spaceAvailable = shipSection.getDirection() == Grid.dir.UP ? uspace :
                                     shipSection.getDirection() == Grid.dir.RIGHT ? rspace :
                                     shipSection.getDirection() == Grid.dir.LEFT ? dspace : lspace;
                for (int j=Math.max(2, shipSection.getLength()); j<= Math.min(spaceAvailable, (bigShip ? 5 : 4)); j++){
                    if (i == unplacedShips.size()-1) {
                        possiblePlacements.push(new ShipSection(shipSection.getRow(), shipSection.getCol(), j, shipSection.getDirection(), ShipSection.shipType.LAST_INIT));
                        possiblePlacementsFresh.push(new ShipSection(shipSection.getRow(), shipSection.getCol(), j, shipSection.getDirection(), ShipSection.shipType.LAST_INIT));
                    } else {
                        possiblePlacements.push(new ShipSection(shipSection.getRow(), shipSection.getCol(), j, shipSection.getDirection(), ShipSection.shipType.INIT));
                        possiblePlacementsFresh.push(new ShipSection(shipSection.getRow(), shipSection.getCol(), j, shipSection.getDirection(), ShipSection.shipType.INIT));
                    }
                }
            }

            initShipPossibilities.add(possiblePlacements);
            initShipPossibilitiesFresh.add(possiblePlacementsFresh);
            i++;

        }
    }

    private boolean puzzleSquare(int[] cols, int[] rows) {
        // Transitive property :)
        boolean square = cols.length == rows.length;
        return square;
    }
}