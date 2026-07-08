package us.crazycrew.crazycrates.api.config.properties.builders;

import org.jspecify.annotations.NullMarked;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NullMarked
public final class CommentsBuilder {

    private final Map<Object[], List<String>> comments = new HashMap<>();

    public void setComment(final List<String> comments, final Object... path) {
        this.comments.put(path, comments);
    }

    public void setComment(final String comment, final Object... path) {
        setComment(List.of(comment), path);
    }

    public Map<Object[], List<String>> getComments() {
        return this.comments;
    }
}