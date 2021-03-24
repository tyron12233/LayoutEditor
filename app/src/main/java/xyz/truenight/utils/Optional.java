package xyz.truenight.utils;

import java.util.NoSuchElementException;
import java.util.Objects;

import xyz.truenight.utils.interfaces.Consumer;
import xyz.truenight.utils.interfaces.Function;
import xyz.truenight.utils.interfaces.NonNull;
import xyz.truenight.utils.interfaces.Nullable;
import xyz.truenight.utils.interfaces.Predicate;
import xyz.truenight.utils.interfaces.Supplier;

public final class Optional<T> {

    private static final Optional<?> EMPTY = new Optional();
    private final T value;

    private Optional() {
        this.value = null;
    }

    public static <T> Optional<T> empty() {
        //noinspection unchecked
        return (Optional) EMPTY;
    }

    private Optional(T var1) {
        this.value = Utils.requireNonNull(var1);
    }

    public static <T> Optional<T> of(@NonNull T var0) {
        return new Optional<>(var0);
    }

    public static <T> Optional<T> ofNullable(@Nullable T var0) {
        return var0 == null ? Optional.<T>empty() : of(var0);
    }

    @NonNull
    public T get() {
        if (this.value == null) {
            throw new NoSuchElementException("No value present");
        } else {
            return this.value;
        }
    }

    @Nullable
    public T getNullable() {
        return this.value;
    }

    public boolean isPresent() {
        return this.value != null;
    }

    public void ifPresent(Consumer<? super T> var1) {
        if (this.value != null) {
            var1.accept(this.value);
        }

    }

    public Optional<T> filter(Predicate<? super T> var1) {
        Utils.requireNonNull(var1);
        return !this.isPresent() ? this : (var1.test(this.value) ? this : Optional.<T>empty());
    }

    public <U> Optional<U> map(Function<? super T, ? extends U> var1) {
        Utils.requireNonNull(var1);
        return !this.isPresent() ? Optional.<U>empty() : ofNullable(var1.apply(this.value));
    }

    public <U> Optional<U> flatMap(Function<? super T, Optional<U>> var1) {
        Utils.requireNonNull(var1);
        return !this.isPresent() ? Optional.<U>empty() : Utils.requireNonNull(var1.apply(this.value));
    }

    @Nullable
    public T orElse(T var1) {
        return this.value != null ? this.value : var1;
    }

    @Nullable
    public T orElseGet(Supplier<? extends T> var1) {
        return this.value != null ? this.value : var1.get();
    }

    @NonNull
    public <X extends Throwable> T orElseThrow(Supplier<? extends X> var1) throws X {
        if (this.value != null) {
            return this.value;
        } else {
            throw var1.get();
        }
    }

    public boolean equals(Object var1) {
        if (this == var1) {
            return true;
        } else if (!(var1 instanceof Optional)) {
            return false;
        } else {
            Optional var2 = (Optional) var1;
            return Objects.equals(this.value, var2.value);
        }
    }

    public int hashCode() {
        return Objects.hashCode(this.value);
    }

    public String toString() {
        return this.value != null ? String.format("Optional[%s]", this.value) : "Optional.empty";
    }
}