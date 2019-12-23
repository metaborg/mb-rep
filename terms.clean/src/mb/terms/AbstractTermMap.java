package mb.terms;

import io.usethesource.capsule.Map;
import org.immutables.value.Value;

import java.util.HashMap;
import java.util.List;

@SuppressWarnings("WeakerAccess")
@Value.Immutable
public abstract class AbstractTermMap implements ITermMap {
    @Override
    public abstract Map.Immutable<ITerm, ITerm> map();

    @Override
    public abstract List<ITerm> annotations();

    @Value.Auxiliary
    public abstract HashMap<Class<?>, Object> attachments();
}
