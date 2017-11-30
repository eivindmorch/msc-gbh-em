package unit;

import com.badlogic.gdx.ai.btree.branch.Selector;
import com.badlogic.gdx.ai.btree.branch.Sequence;
import model.btree.Blackboard;
import model.btree.GenBehaviorTree;
import model.btree.task.follower.IsApproaching;
import model.btree.task.follower.IsCloseEnough;
import model.btree.task.follower.Move;
import model.btree.task.follower.TurnToHeading;
import model.btree.task.general.Wait;

public class ControlledUnit {

    Unit unit;
    GenBehaviorTree btree;

    public ControlledUnit(Unit unit) {
        this.unit = unit;
        this.btree = testBtree();
    }

    public GenBehaviorTree testBtree() {
        Sequence waitAndTurnToSequence = new Sequence(new Wait(), new TurnToHeading());
        Selector shouldMoveSelector = new Selector(new IsApproaching(15), new IsCloseEnough(5));
        Sequence shouldNotMoveSequence = new Sequence(shouldMoveSelector, waitAndTurnToSequence);
        Selector waitOrMoveSelector = new Selector(shouldNotMoveSequence, new Move());
        return new GenBehaviorTree(waitOrMoveSelector, new Blackboard((FollowerUnit)unit));
    }

    public void tick() {
        btree.step();
    }
}
