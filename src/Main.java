/**
 * Created by bborchard on 7/20/2015.
 */
public class Main {


    public static void main(String[] args){

        /* 111 */
//        int[][] init = new int[][]
//                { {0,0,0,0,0,0,0,0,0,0,0},
//                  {0,0,0,0,0,0,0,0,0,0,0},
//                  {0,0,0,0,0,0,0,0,0,0,0},
//                  {0,0,0,0,0,0,0,0,0,0,0},
//                  {0,0,0,0,0,0,0,0,0,0,0},
//                  {0,0,0,0,0,0,0,0,0,0,0},
//                  {0,0,0,0,0,0,0,0,0,0,0},
//                  {0,0,0,0,3,0,0,0,0,0,0},
//                  {0,0,0,0,0,0,0,0,0,0,0},
//                  {0,0,0,0,0,0,0,0,0,0,0},
//                  {0,0,0,0,0,0,0,0,0,0,0} };
//
//        int[] rows = new int[] {4,0,0,3,4,2,1,2,0,7,2};
//        int[] cols = new int[] {1,1,3,1,6,2,0,2,5,3,1};
//        boolean bigShip = true;

        /* 113 */
        int[][] init = new int[][]
                { {0,0,0,0,0,0,0,0,0,0,0},
                  {0,0,0,0,0,0,0,0,0,0,0},
                  {0,0,0,0,0,0,0,0,0,0,0},
                  {0,0,0,0,0,0,0,0,0,0,0},
                  {0,0,0,0,0,0,0,0,0,0,0},
                  {0,0,0,0,0,0,0,0,0,0,0},
                  {0,0,0,0,0,0,0,0,0,0,0},
                  {0,0,0,0,0,0,0,0,0,0,0},
                  {0,0,0,0,0,0,0,0,0,0,0},
                  {0,0,0,0,0,0,0,0,0,0,0},
                  {0,0,0,0,0,0,0,0,0,0,0} };

        int[] rows = new int[] {0,6,2,1,0,2,1,3,3,5,2};
        int[] cols = new int[] {3,1,5,0,0,7,0,2,3,4,0};
        boolean bigShip = true;


        /* 111 */
//        int[][] init = new int[][]
//                { {0,1,0,0,0,0,0,0,0,0,0},
//                  {0,0,0,0,0,0,0,0,0,0,0},
//                  {0,0,0,0,0,0,0,0,0,0,0},
//                  {0,0,0,0,0,0,0,0,0,0,0},
//                  {0,0,0,0,0,0,0,0,0,0,0},
//                  {0,0,0,0,0,0,0,0,0,0,0},
//                  {0,0,0,0,0,0,0,0,0,0,0},
//                  {0,0,0,0,0,0,0,0,0,0,0},
//                  {0,0,0,0,0,0,0,0,0,0,0},
//                  {0,0,0,0,0,0,0,0,0,0,0},
//                  {0,0,0,0,0,0,0,0,0,0,0} };
//
//        int[] rows = new int[] {3,4,1,5,1,1,1,3,0,5,1};
//        int[] cols = new int[] {0,4,2,3,0,0,7,2,5,2,0};
//        boolean bigShip = true;

        BattleshipsSolver bs = new BattleshipsSolver(init,cols,rows, bigShip);

        bs.solve();
    }
}
