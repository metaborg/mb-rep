package mb.terms;

import com.google.common.collect.ImmutableClassToInstanceMap;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public abstract class AbstractTermFloat implements ITermFloat {
    @Override
    public abstract float value();

    @Override
    public abstract List<ITerm> annotations();

    @Value.Auxiliary
    public abstract ImmutableClassToInstanceMap<Object> attachments();
}
