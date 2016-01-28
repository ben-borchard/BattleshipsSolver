/**
 * Created by bborchard on 7/21/2015.
 */
public class Cell {

    public enum ship {
        YES,
        NO,
        NOT_SURE
    };

    private ship shipStatus;
    private int row, col;

    public Cell(int row, int col){
        this(row, col, ship.NOT_SURE);
    }

    public Cell(int row, int col, ship shipStatus){
        this.row = row;
        this.col = col;
        this.shipStatus = shipStatus;
    }

    /**
     * Denote that their is a piece of ship in this cell
     */
    public void setShip(){
        shipStatus = ship.YES;
    }

    /**
     * Removes the possibility that this cell has a piece of a ship in it
     */
    public void notShip(){

        shipStatus = ship.NO;
    }

    /**
     * Check if this ship has a piece in it or not
     * @return
     */
    public ship getShipStatus(){
        return shipStatus;
    }

    public String toString() {
        String shipStr = shipStatus == ship.NOT_SURE ? "   " :
                shipStatus == ship.NO ? " . " : " O ";

        String str = "| ";

        str += shipStr + " ";
        return str + "|";
    }

    public Cell clone(){
        return new Cell(row, col, shipStatus);
    }


}
