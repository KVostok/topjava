package ru.javawebinar.topjava;

import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import ru.javawebinar.topjava.web.json.JsonUtil;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Factory for creating test matchers.
 * <p>
 * Comparing actual and expected objects via AssertJ
 * Support converting json MvcResult to objects for comparation.
 */
public class MatcherFactory<T> {
    private final Class<T> clazz;
    private final String[] fieldsToIgnore;

    private MatcherFactory(Class<T> clazz, String... fieldsToIgnore) {
        this.clazz = clazz;
        this.fieldsToIgnore = fieldsToIgnore;
    }

    public static <T> MatcherFactory<T> usingIgnoringFieldsComparator(Class<T> clazz, String... fieldsToIgnore) {
        return new MatcherFactory<>(clazz, fieldsToIgnore);
    }

    public void assertMatch(T actual, T expected) {
        assertThat(actual).usingRecursiveComparison().ignoringFields(fieldsToIgnore).isEqualTo(expected);
    }

    @SafeVarargs
    public final void assertMatch(Iterable<T> actual, T... expected) {
        assertMatch(actual, List.of(expected));
    }

    public void assertMatch(Iterable<T> actual, Iterable<T> expected) {
        assertThat(actual).usingElementComparatorIgnoringFields(fieldsToIgnore).isEqualTo(expected);
    }

    public ResultMatcher contentJson(T expected) {
        return result -> assertMatch(JsonUtil.readValue(getContent(result), clazz), expected);
    }

    @SafeVarargs
    public final ResultMatcher contentJson(T... expected) {
        return contentJson(List.of(expected));
    }

    public ResultMatcher contentJson(Iterable<T> expected) {
        return result -> assertMatch(JsonUtil.readValues(getContent(result), clazz), expected);
    }

    public T readFromJson(ResultActions action) throws UnsupportedEncodingException {
        return JsonUtil.readValue(getContent(action.andReturn()), clazz);
    }

    private static String getContent(MvcResult result) throws UnsupportedEncodingException {
        return result.getResponse().getContentAsString();
    }
}
