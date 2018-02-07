//package core.util.plotting;
//
//    import com.panayotis.gnuplot.*;
//    import com.panayotis.gnuplot.dataset.Point;
//    import com.panayotis.gnuplot.dataset.PointDataSet;
//    import core.training.Chromosome;
//
//    import java.util.ArrayList;
//
//@SuppressWarnings("ConstantConditions")
//public abstract class Plotter {
//
//
//    public static void plot(ArrayList<? extends Chromosome> chromosomes) {
//        new Plot(chromosomes);
//    }
//
//    private static class Plot implements Runnable {
//
//        JavaPlot javaPlot;
//        private ArrayList<? extends Chromosome> chromosomes;
//        private PointDataSet<Double> pointDataSet;
//
//        Plot(ArrayList<? extends Chromosome> chromosomes) {
//            this.chromosomes = chromosomes;
//            new Thread(this).start();
//        }
//
//        @Override
//        public void run() {
//            this.javaPlot = new JavaPlot();
//            this.javaPlot.setTitle("Pareto front");
//
//            this.pointDataSet = new PointDataSet<>();
//
//            this.javaPlot.set("xlabel", "'Difference' offset -4,0,0");
//            this.javaPlot.set("ylabel", "'Size' offset -4,0,0");
//
//            for (Chromosome chromosome : chromosomes) {
//                ArrayList<Double> fitness = chromosome.getFitness();
//                pointDataSet.add(new Point<>(fitness.get(0), fitness.get(1)));
//            }
//            javaPlot.addPlot(pointDataSet);
//            javaPlot.plot();
//
//        }
//    }
//}
//
