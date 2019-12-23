package mb.terms;

import org.immutables.value.Value;

import java.util.HashMap;
import java.util.List;

@Value.Immutable
public abstract class AbstractTermApplication implements ITermApplication {
    @Override
    public abstract String constructor();

    @Override
    public abstract List<ITerm> children();

    @Override
    public abstract List<ITerm> annotations();

    @Value.Auxiliary
    public abstract HashMap<Class<?>, Object> attachments();
}
