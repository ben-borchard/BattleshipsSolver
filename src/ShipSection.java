/**
 * Created by bborchard on 10/8/2015.
 */
public class ShipSection {

    public enum shipType {
        INIT,
        LAST_INIT,
        GRID
    }

    private int row;
    private int col;
    private int length;
    private Grid.dir direction;
    private shipType initShip;

    public ShipSection(int row, int col, int length, Grid.dir direction, shipType initShip){
        this(row,col,length,direction);
        this.initShip = initShip;
    }

    public ShipSection(int row, int col, int length, Grid.dir direction){
        this.row = row;
        this.col = col;
        this.length = length;
        this.direction = direction;
        this.initShip = shipType.GRID;
    }

    public Grid.dir getDirection() {
        return direction;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public int getLength() {
        return length;
    }

    public shipType getInitShip(){
        return initShip;
    }

    public void setInitShip(){

    }
}
