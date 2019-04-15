package interfaces;

@FunctionalInterface
public interface ThreeParametersLambda<O,T,R> {
        public R apply(O o, T t);
}
