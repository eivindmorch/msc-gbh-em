package model.btree;

import unit.FollowerUnit;

// TODO Generalise to T extends Unit
public class Blackboard {

    private FollowerUnit followerUnit;

    public Blackboard(FollowerUnit followerUnit) {
        this.followerUnit = followerUnit;
    }

    public FollowerUnit getFollowerUnit() {
        return followerUnit;
    }

    public Blackboard clone() {
        return new Blackboard(this.followerUnit);
    }
}
