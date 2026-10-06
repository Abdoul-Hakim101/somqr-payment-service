package so.somqr.payment.domain;

import so.somqr.payment.exception.ApiException;

import java.util.List;
import java.util.Objects;

/**
 * A single SomQR tag-length-value field.
 *
 * <p>The tag and length are encoded using two digits, while the value
 * contains the actual field data. Nested SomQR templates are represented
 * by a value containing more encoded TLV fields.</p>
 */
public record Tlv(String tag, String value, List<Tlv> children) {

    public Tlv(String tag, String value) {
        this(tag, value, List.of());
    }

    public Tlv {
        Objects.requireNonNull(tag, "Tag cannot be null");
        Objects.requireNonNull(value, "Value cannot be null");

        if (!tag.matches("\\d{2}")) {
            throw new ApiException("Tag must contain exactly two digits");
        }

        if (value.length() > 99) {
            throw new ApiException("Value cannot exceed 99 characters");
        }

        children = children == null ? List.of() : List.copyOf(children);
    }

    public int length() {
        return value.length();
    }

    public String encode() {
        return tag + String.format("%02d", length()) + value;
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }
}
