package com.microida.pms.util;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.stereotype.Component;

import java.util.Arrays;


@Component
public  class OpenAIGPT {

    public static final String OPENAI_API_KEY = "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";

    public static String translate(String title){
        OpenAiService service = new OpenAiService(OPENAI_API_KEY);
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .messages(Arrays.asList(new ChatMessage("user", "Fordítsd le: " + title )))
                .model("gpt-3.5-turbo")
                .build();
        service.createChatCompletion(chatCompletionRequest).getChoices().forEach(System.out::println);
        return service.createChatCompletion(chatCompletionRequest).getChoices().get(0).getMessage().getContent();
    }

}
