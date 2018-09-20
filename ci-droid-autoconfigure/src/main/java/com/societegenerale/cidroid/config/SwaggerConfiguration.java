package com.societegenerale.cidroid.config;

import com.fasterxml.classmate.TypeResolver;
import com.societegenerale.cidroid.api.ResourceToUpdate;
import com.societegenerale.cidroid.api.actionToReplicate.ActionToReplicate;
import com.societegenerale.cidroid.api.gitHubInteractions.AbstractGitHubInteraction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.ModelPropertyBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelProperty;
import springfox.documentation.schema.TypeNameExtractor;
import springfox.documentation.service.AllowableListValues;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelContext;
import springfox.documentation.spring.web.DescriptionResolver;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.common.SwaggerPluginSupport;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static springfox.documentation.schema.ResolvedTypes.modelRefFactory;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    @Bean
    public Docket productApi() {
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .select().apis(RequestHandlerSelectors.basePackage("com.societegenerale.cidroid.controllers"))
                .paths(PathSelectors.any())
                .build();
        return docket;
    }

    @Component
    @Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER + 1000)
    public class ModelEnhancement implements ModelBuilderPlugin {
        private final DescriptionResolver descriptions;

        @Autowired
        public ModelEnhancement(DescriptionResolver descriptions) {
            this.descriptions = descriptions;
        }

        @Autowired
        TypeResolver resolver;

        @Autowired
        TypeNameExtractor typeNameExtractor;

        @Autowired
        List<ActionToReplicate> availableActions;

        @Override
        public boolean supports(DocumentationType delimiter) {
            return SwaggerPluginSupport.pluginDoesApply(delimiter);
        }

        @Override
        public void apply(ModelContext modelContext) {

            Class<?> modelClass = forClass(modelContext);

            modelContext.getType();

            if(modelClass.equals(AbstractGitHubInteraction.class)){
                abstractGitHubInteractionSpecificConfig(modelContext);
            }

            else if(modelClass.equals(ActionToReplicate.class)){
                actionToReplicateSpecificConfig(modelContext);
            }

            else if(modelClass.equals(ResourceToUpdate.class)){
                resourcesToUpdateSpecificConfig(modelContext);
            }

        }

        private void actionToReplicateSpecificConfig(ModelContext modelContext) {
            Map actionToReplicateProperties = new HashMap<String, ModelProperty>();

            List<String> availableActionClassNames=availableActions.stream().map(a -> a.getClass().getCanonicalName()).collect(toList());

            ModelProperty actionToReplicateClassNameProperty=new ModelPropertyBuilder().required(true)
                    .allowEmptyValue(false)
                    .name("fully qualified class name identifier")
                    .description("fully qualified name of the class implementing com.societegenerale.cidroid.api.actionToReplicate.ActionToReplicate")
                    .allowableValues(new AllowableListValues(availableActionClassNames,"string"))
                    .example((Object)"com.societegenerale.cidroid.extensions.actionToReplicate.OverwriteStaticFileAction")
                    .type(resolver.resolve(String.class)).build();
            actionToReplicateClassNameProperty.updateModelRef(modelRefFactory(modelContext, typeNameExtractor));

            actionToReplicateProperties.put("@class",actionToReplicateClassNameProperty);


            ModelProperty actionToReplicateAttributeProperty=new ModelPropertyBuilder().required(true)
                    .allowEmptyValue(false)
                    .name("example of attribute")
                    .description("depends on the @class picked. See in GET /cidroid-actions/availableActions , which classes you can use, and what are the expected fields for each")
                    .type(resolver.resolve(String.class)).build();
            actionToReplicateAttributeProperty.updateModelRef(modelRefFactory(modelContext, typeNameExtractor));

            actionToReplicateProperties.put("someActionAttribute",actionToReplicateAttributeProperty);

            modelContext.getBuilder()
                    .properties(actionToReplicateProperties)
                    .description("see the Jackson config and the subclasses of in https://github.com/societe-generale/ci-droid-internal-api/blob/master/src/main/java/com/societegenerale/cidroid/api/actionToReplicate/ActionToReplicate.java");

        }

        private void abstractGitHubInteractionSpecificConfig(ModelContext modelContext) {
            Map abstractGitHubInteractionProperties=new HashMap<String, ModelProperty>();

            ModelProperty abstractGitHubInteractionClassNameProperty=new ModelPropertyBuilder().required(true)
                    .allowEmptyValue(false)
                    .name("class identifier")
                    .allowableValues(new AllowableListValues(
                            Arrays.asList(".DirectPushGitHubInteraction",".PullRequestGitHubInteraction"),"string"))
                    .example((Object)".DirectPushGitHubInteraction")
                    .type(resolver.resolve(String.class)).build();
            abstractGitHubInteractionClassNameProperty.updateModelRef(modelRefFactory(modelContext, typeNameExtractor));
            abstractGitHubInteractionProperties.put("@c",abstractGitHubInteractionClassNameProperty);


            ModelProperty branchNameProperty=new ModelPropertyBuilder().required(false)
                    .name("branch name in case of PR")
                    .description("when @c=.PullRequestGitHubInteraction, then we need to provide the name of the branch in which we want to the PR")
                    .example((Object)"myBranch")
                    .type(resolver.resolve(String.class)).build();
            branchNameProperty.updateModelRef(modelRefFactory(modelContext, typeNameExtractor));
            abstractGitHubInteractionProperties.put("branchNameToCreate",branchNameProperty);


            ModelProperty prNameProperty=new ModelPropertyBuilder().required(false)
                    .name("name of the PR")
                    .description("when @c=.PullRequestGitHubInteraction, then we can provide a name for the PR. If not provided, the PR name will be the same as the branch name")
                    .example((Object)"'[XYZ-123] adding a feature'")
                    .type(resolver.resolve(String.class)).build();
            prNameProperty.updateModelRef(modelRefFactory(modelContext, typeNameExtractor));
            abstractGitHubInteractionProperties.put("pullRequestName",prNameProperty);

            modelContext.getBuilder()
                    .properties(abstractGitHubInteractionProperties)
                    .description("see the Jackson config in https://github.com/societe-generale/ci-droid-internal-api/blob/master/src/main/java/com/societegenerale/cidroid/api/gitHubInteractions/AbstractGitHubInteraction.java");
        }

        private void resourcesToUpdateSpecificConfig(ModelContext modelContext) {
            Map resourcesToUpdateProperties=new HashMap<String, ModelProperty>();

            ModelProperty repoFullNameProperty=new ModelPropertyBuilder().required(true)
                    .allowEmptyValue(false)
                    .name("repo full name")
                    .position(1)
                    .example((Object)"vincent-fuchs/in-memory-testing")
                    .type(resolver.resolve(String.class)).build();
            repoFullNameProperty.updateModelRef(modelRefFactory(modelContext, typeNameExtractor));
            resourcesToUpdateProperties.put("repoFullName",repoFullNameProperty);

            ModelProperty filePathOnRepoProperty=new ModelPropertyBuilder().required(true)
                    .allowEmptyValue(false)
                    .name("file path")
                    .position(2)
                    .example((Object)"pom.xml")
                    .type(resolver.resolve(String.class)).build();
            filePathOnRepoProperty.updateModelRef(modelRefFactory(modelContext, typeNameExtractor));
            resourcesToUpdateProperties.put("filePathOnRepo",filePathOnRepoProperty);

            ModelProperty branchProperty=new ModelPropertyBuilder().required(true)
                    .allowEmptyValue(false)
                    .name("branch")
                    .position(3)
                    .example((Object)"master")
                    .type(resolver.resolve(String.class)).build();
            branchProperty.updateModelRef(modelRefFactory(modelContext, typeNameExtractor));
            resourcesToUpdateProperties.put("branchName",branchProperty);

            ModelProperty placeHolderValueProperty=new ModelPropertyBuilder().required(false)
                    .name("placeHolderValue")
                    .position(4)
                    .description("when using @class TemplateBasedContentAction, we use a template with a placeholder : the value to use is provided with each resource")
                    .type(resolver.resolve(String.class)).build();
            placeHolderValueProperty.updateModelRef(modelRefFactory(modelContext, typeNameExtractor));
            resourcesToUpdateProperties.put("placeHolderValue",placeHolderValueProperty);

            modelContext.getBuilder()
                    .properties(resourcesToUpdateProperties)
                    .description("a list of description of resources, for which the updateAction will be performed");


        }

        private Class<?> forClass(ModelContext context) {
            return resolver.resolve(context.getType()).getErasedType();
        }
    }



}
