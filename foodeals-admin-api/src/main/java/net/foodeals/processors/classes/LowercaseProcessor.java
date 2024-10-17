package net.foodeals.processors.classes;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class LowercaseProcessor {

    /**
     * Converts a single string to lowercase.
     *
     * @param input The string to convert.
     * @return The lowercase version of the input string.
     */
    public String process(String input) {
        return input != null ? input.toLowerCase() : null;
    }

    /**
     * Converts a list of strings to lowercase.
     *
     * @param inputs The list of strings to convert.
     * @return A new list with lowercase versions of the input strings.
     */
    public List<String> process(List<String> inputs) {
        return inputs.stream()
                .map(this::process)
                .collect(Collectors.toList());
    }
}