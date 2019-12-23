package mb.terms;

import org.immutables.value.Value;

import java.util.HashMap;
import java.util.List;

@Value.Immutable
public abstract class AbstractTermInt implements ITermInt {
    @Override
    public abstract int value();

    @Override
    public abstract List<ITerm> annotations();

    @Value.Auxiliary
    public abstract HashMap<Class<?>, Object> attachments();
}
