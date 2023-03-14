package com.microida.pms.util;

import com.microida.pms.service.PmsParameterService;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.stereotype.Component;

import java.util.Arrays;


@Component
public class OpenAIGPT {

    private PmsParameterService pmsParameterService;

    public static String OPENAI_API_KEY;

    private OpenAIGPT(final PmsParameterService pmsParameterService){
        this.pmsParameterService = pmsParameterService;
        OPENAI_API_KEY = pmsParameterService.get("OPENAI_API_KEY").getValue();

    }

    public static String translate(String title){
        OpenAiService service = new OpenAiService(OPENAI_API_KEY);
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .messages(Arrays.asList(new ChatMessage("user", "Fordítsd le: " + title )))
                .model("gpt-3.5-turbo")
                .build();
        ChatCompletionResult result =  service.createChatCompletion(chatCompletionRequest);
        if (result != null){
           String resultContent =  result.getChoices().get(0).getMessage().getContent();
           if(resultContent != null){
               return resultContent.replaceAll("\n", "");
           }
        }
        return null;
    }

}
