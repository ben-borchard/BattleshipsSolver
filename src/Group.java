/**
 * Created by bborchard on 7/20/2015.
 */
public class Group {

    private int[] content;
    private int ssFromLeftOrTop, ssFromRightOrBottom, size;

    public Group(int ssFromLeftOrTop, int ssFromRightOrBottom, int size){
        this.ssFromLeftOrTop = ssFromLeftOrTop;
        this.ssFromRightOrBottom = ssFromRightOrBottom;
        content = new int[size];
    }

    public int[] getContent(){ return content; }

    public void setContent(int[] content){ this.content = content; }

    public int isGood(){

        int[] dupArray = new int[size+1];
        int ssFromLeftOrTop = 0;
        int ssFromRightOrBottom = 0;
        boolean filled = true;

        for (int i=0;i<size;i++){

            if (content[i] != 0) {
                dupArray[content[i] - 1]++;

                if (i != 0 && content[i] > content[i - 1]) {
                    ssFromLeftOrTop++;
                }

                int s = content.length;
                if (i != 0 && content[s - i] > content[s]) {
                    ssFromRightOrBottom++;
                }
            } else
                filled = false;
        }

        // Check for duplicate numbers
        for (int n : dupArray)
            if (n > 1)
                return 0;

        if (ssFromLeftOrTop > this.ssFromLeftOrTop || ssFromRightOrBottom > this.ssFromRightOrBottom)
            return 0; // Bad
        else if (filled && ssFromLeftOrTop == this.ssFromLeftOrTop && ssFromRightOrBottom == this.ssFromRightOrBottom)
            return 1; // Good
        else
            return -1; // Unsure

    }
}
