package net.qf.commands.suggest;

import net.qf.api.ESekaiSchool;

import java.util.Arrays;
import java.util.List;

public class ElementSuggestProvider extends QfSuggestProviders{
    @Override
    List<String> getSuggestionList() {
        return Arrays.stream(ESekaiSchool.values()).map(school -> school.name().toLowerCase()).toList();
    }
}
