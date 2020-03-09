package edu.duke.ece651.risk.shared.action;

public class MoveAction implements Action{
    String src;
    String dest;

    public MoveAction(String src, String dest){
        this.src = src;
        this.dest = dest;
    }

    @Override
    public boolean isValid() {
        return !src.equals(dest);
    }

    @Override
    public void perform() {

    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MoveAction){
            MoveAction moveAction = (MoveAction) obj;
            return moveAction.src.equals(this.src) && moveAction.dest.equals(this.dest);
        }
        return false;
    }
}
