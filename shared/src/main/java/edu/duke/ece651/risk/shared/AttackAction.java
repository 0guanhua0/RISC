package edu.duke.ece651.risk.shared;

public class AttackAction implements Action {
    String src;
    String dest;

    public AttackAction(String src, String dest){
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
        if (obj instanceof AttackAction){
            AttackAction attackAction = (AttackAction) obj;
            return attackAction.src.equals(this.src) && attackAction.dest.equals(this.dest);
        }
        return false;
    }
}
