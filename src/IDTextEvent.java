import java.io.Serializable;

public class IDTextEvent implements Serializable{

    private MyTextEvent mte;
    private int id;

    public IDTextEvent(MyTextEvent mte, int id){
        this.mte = mte;
        this.id = id;
    }

    public MyTextEvent getTextEvent(){
        return mte;
    }

    public int getID(){
        return id;
    }
}
