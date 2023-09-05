import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public interface Iter<Entry> {
    Optional<Entry> next();

    static <Entry> Iter<Entry> successionFrom(Entry init, UnaryOperator<Entry> successor) {
        AtomicBoolean providedInit = new AtomicBoolean(false);
        AtomicReference<Entry> holder = new AtomicReference<>(init);
        return () -> {
            if (!providedInit.get()) {
                providedInit.set(true);
                return Optional.of(init);
            } else {
                holder.set(successor.apply(holder.get()));
                return Optional.of(holder.get());
            }
        };
    }

    default <MappedEntry> Iter<MappedEntry> map(Function<Entry, MappedEntry> mapper) {
        return () -> next().map(mapper);
    }

    default Iter<Entry> limit(int lim) {
        AtomicInteger count = new AtomicInteger(0);
        return () -> {
            if (count.get() < lim) {
                count.incrementAndGet();
                return next();
            } else {
                return Optional.empty();
            }
        };
    }

    default Iter<Entry> breakWhen(Predicate<Entry> predicate) {
        AtomicBoolean broke = new AtomicBoolean(false);
        return () -> {
            if (broke.get()) {
                return Optional.empty();
            }
            Optional<Entry> optEntry = next();
            broke.set(optEntry.isEmpty() ||
                      predicate.test(optEntry.get()));
            return optEntry;
        };
    }

    default void forEach(Consumer<Entry> onEach) {
        while (true) {
            Optional<Entry> optionalEntry = next();
            if (optionalEntry.isEmpty()) {
                break;
            }
            onEach.accept(optionalEntry.get());
        }
    }
}
