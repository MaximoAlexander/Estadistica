package Controllers;

import java.util.*;
import java.util.stream.IntStream;

public class Statistics {

    //(clase, [Real Inferior, Inferior, Superior, Real Superior, Marca])
    public static Map<Integer, List<Double>> generateData(double Li1, double Ls1, double Li2, int n) {
        Map<Integer, List<Double>> data = new HashMap<>();
        double l = Li2 - Li1;

        IntStream.range(0, n).forEach(i -> {
            double Li = Li1 + (i * l);
            double Ls = Ls1 + (i * l);
            double LRi = (Li + Ls1 + (i - 1) * l) / 2.0;
            double LRs = (Ls + Li1 + (i + 1) * l) / 2.0;
            data.put(i, List.of(LRi, Li, Ls, LRs, (Li + Ls) / 2.0));
        });

        return data;
    }

    public static double media(Map<Integer, List<Double>> data, List<Integer> f) {
        return IntStream.range(0, f.size()).mapToDouble(i -> f.get(i) * data.get(i).get(4)).sum() / f.stream().reduce(Integer::sum).orElseThrow();
    }

    public static double mediana(Map<Integer, List<Double>> data, List<Integer> f) {
        int mitad = f.stream().reduce(Integer::sum).orElseThrow() / 2;
        int fa = 0, index = 0;
        for (int i = 0; i < f.size(); i++) {
            if (fa + f.get(i) >= mitad) {
                index = i;
                break;
            }
            fa += f.get(i);
        }

        return data.get(index).get(0) + ((mitad - fa) / (double) f.get(index)) * (data.get(index).get(3) - data.get(index).get(0));
    }

    public static double moda(Map<Integer, List<Double>> data, List<Integer> f) {
        int index = 0, max = -1;
        for (int i = 0; i < f.size(); i++) {
            if (f.get(i) > max) {
                max = f.get(i);
                index = i;
            }
        }
        return data.get(index).get(0) + ((f.get(index) - f.get(index - 1)) / (double) ((2 * f.get(index)) - f.get(index - 1) - f.get(index + 1))) * (data.get(index).get(3) - data.get(index).get(0));
    }

    public static double desviacion(Map<Integer, List<Double>> data, List<Integer> f, double media) {
        return Math.sqrt(IntStream.range(0, f.size()).mapToDouble(i -> Math.pow(data.get(i).get(4) - media, 2) * f.get(i)).sum() / f.stream().reduce(Integer::sum).orElseThrow());
    }

    //public static void main(String[] args) {
    //   var d = generateData(1, 2, 4, 3);
    //   d.forEach((k, v) -> System.out.println("Clave : " + k + " value: " + v));
    //}
}