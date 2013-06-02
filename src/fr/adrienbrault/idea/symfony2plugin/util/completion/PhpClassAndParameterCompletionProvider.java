package fr.adrienbrault.idea.symfony2plugin.util.completion;


import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.completion.PhpLookupElement;
import com.jetbrains.php.lang.psi.stubs.indexes.PhpClassIndex;
import fr.adrienbrault.idea.symfony2plugin.Symfony2ProjectComponent;
import fr.adrienbrault.idea.symfony2plugin.config.component.ParameterLookupElement;
import fr.adrienbrault.idea.symfony2plugin.config.yaml.ParameterPercentWrapInsertHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class PhpClassAndParameterCompletionProvider extends CompletionProvider<CompletionParameters> {

    public void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet resultSet) {
        PhpIndex phpIndex = PhpIndex.getInstance(parameters.getOriginalFile().getProject());

        for (String className : phpIndex.getAllClassNames(resultSet.getPrefixMatcher())) {
            resultSet.addElement(new PhpLookupElement(className, PhpClassIndex.KEY, parameters.getOriginalFile().getProject(), PhpClassReferenceInsertHandler.getInstance()));
        }

        // ParameterPercentWrapInsertHandler
        // @TODO: we need a parameter class dict
        Symfony2ProjectComponent symfony2ProjectComponent = parameters.getOriginalFile().getProject().getComponent(Symfony2ProjectComponent.class);
        Map<String, String> configParameters = symfony2ProjectComponent.getConfigParameter();
        for(Map.Entry<String, String> Entry: configParameters.entrySet()) {
            // some better class filter
            if(Entry.getValue().contains("\\")) {
                resultSet.addElement(new ParameterLookupElement(Entry.getKey(), Entry.getValue(), ParameterPercentWrapInsertHandler.getInstance()));
            }
        }

    }

}
