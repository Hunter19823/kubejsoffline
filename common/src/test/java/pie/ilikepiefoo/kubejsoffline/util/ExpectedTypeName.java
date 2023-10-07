package pie.ilikepiefoo.kubejsoffline.util;

import javax.annotation.Nonnull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({
        ElementType.TYPE,
        ElementType.TYPE_PARAMETER,
        ElementType.FIELD,
        ElementType.METHOD
})
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface ExpectedTypeName {
    /**
     * The expected string representation of the return type.
     *
     * @return The expected string representation of the return type.
     */
    @Nonnull String value();

}
