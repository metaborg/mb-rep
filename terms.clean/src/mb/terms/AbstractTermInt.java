package mb.terms;

import com.google.common.collect.ImmutableClassToInstanceMap;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public abstract class AbstractTermInt implements ITermInt {
    @Override
    public abstract int value();

    @Override
    public abstract List<ITerm> annotations();

    @Value.Auxiliary
    public abstract ImmutableClassToInstanceMap<Object> attachments();
}
