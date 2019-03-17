package mb.terms;

import com.google.common.collect.ImmutableClassToInstanceMap;
import io.usethesource.capsule.Set;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public abstract class AbstractTermSet implements ITermSet {
    @Override
    public abstract Set.Immutable<ITerm> set();

    @Override
    public abstract List<ITerm> annotations();

    @Value.Auxiliary
    public abstract ImmutableClassToInstanceMap<Object> attachments();
}
