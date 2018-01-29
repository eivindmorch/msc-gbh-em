package moeaframework;

import com.badlogic.gdx.ai.btree.Task;
import core.model.btree.BehaviorTreeUtil;
import org.apache.commons.lang3.SerializationUtils;
import org.moeaframework.core.Solution;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

public class TestSolution extends Solution {

    private Task btreeRoot;

    public TestSolution(int numberOfVariables, int numberOfObjectives, Task btreeRoot) {
        super(numberOfVariables, numberOfObjectives);
        this.btreeRoot = btreeRoot;
    }

    public TestSolution(TestSolution testSolution) {
        super(testSolution);
        this.btreeRoot = BehaviorTreeUtil.clone(testSolution.btreeRoot);
    }

    public Task getBtreeRoot() {
        return btreeRoot;
    }

    @Override
    public TestSolution copy() {
        System.out.println("COPY1");
        return new TestSolution(this);
    }

    @Override
    public TestSolution deepCopy() {
        System.out.println("COPY");
        TestSolution copy = this.copy();
        Iterator i$ = this.getAttributes().entrySet().iterator();

        while(i$.hasNext()) {
            Map.Entry<String, Serializable> entry = (Map.Entry)i$.next();
            copy.setAttribute((String)entry.getKey(), SerializationUtils.clone((Serializable)entry.getValue()));
        }
        return copy;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < getNumberOfObjectives() - 1; i++) {
            stringBuilder.append(String.format("   Obj%d: %10.2f", i, getObjective(i)));
        }
        stringBuilder.append(String.format("    Size: %2.0f", getObjective(getNumberOfObjectives() - 1)));

        return String.format(
                "%12s%s",
                "@" + hashCode(),
                stringBuilder.toString()
        );
    }
}
