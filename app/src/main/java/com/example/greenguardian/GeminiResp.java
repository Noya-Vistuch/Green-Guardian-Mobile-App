package com.example.greenguardian;

// Core Gemini & AI imports
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.ChatFutures;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.BlockThreshold;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.GenerationConfig;
import com.google.ai.client.generativeai.type.HarmCategory;
import com.google.ai.client.generativeai.type.SafetySetting;

// Guava Futures for handling async responses
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.Collections;
import java.util.concurrent.Executor;

public class GeminiResp {

    /**
     * Sends a user query to the Gemini chat model and returns the response asynchronously.
     * @param chatModel The active chat session created using GenerativeModelFutures.
     * @param query The user’s message or question to be sent to Gemini.
     * @param callback A callback interface that handles the success or error response.
     */
    public static void getResponse(ChatFutures chatModel, String query, RespondCallback callback){
        // Step 1: Build the Content object that represents the user message
        Content.Builder userMessageBuilder = new Content.Builder();
        userMessageBuilder.setRole("user"); // Set role to "user" as required by Gemini protocol
        userMessageBuilder.addText(query); // Add the user’s query to the message
        Content userMessage = userMessageBuilder.build();

        // Step 2: Send the message using Gemini's chatModel (asynchronously)
        // The Runnable::run executor runs on the current thread (useful for simple UI testing or lightweight tasks)
        Executor executor = Runnable::run;
        ListenableFuture<GenerateContentResponse> response = chatModel.sendMessage(userMessage);

        // Step 3: Add a callback to handle response or error when the future is completed
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                // Extract the generated text from the response
                String resultText = result.getText();

                // Pass the text to the callback interface
                callback.onResponse(resultText);
            }

            @Override
            public void onFailure(Throwable throwable) {
                // Print error stack trace for debugging
                throwable.printStackTrace();

                // Notify the UI through callback that an error occurred
                callback.onError(throwable);
            }
        }, executor);
    }

    /**
     * Initializes and configures a Gemini model instance.
     * @return A ready-to-use GenerativeModelFutures object for chat interactions.
     */
    public GenerativeModelFutures getModel(){
        // Retrieve the Gemini API key stored securely in BuildConfig
        String apiKey = BuildConfig.apiKey;

        // --- Safety Configuration ---

        // Add safety setting to block high-risk harassment content
        SafetySetting harassmentSafety = new SafetySetting(
                HarmCategory.HARASSMENT, // Type of harmful content
                BlockThreshold.ONLY_HIGH // Block only when the risk level is high
        );

        // --- Generation Settings ---

        // Define how creative or random the responses should be
        GenerationConfig.Builder configBuilder = new GenerationConfig.Builder();
        configBuilder.temperature = 0.9f;  // Higher = more creative responses
        configBuilder.topK = 16;           // Limit the number of high-probability tokens to sample from
        configBuilder.topP = 0.1f;         // Top-p sampling for diversity

        GenerationConfig generationConfig = configBuilder.build();

        // --- Model Initialization ---

        // Create a new GenerativeModel instance using Gemini's "1.5-flash" model
        GenerativeModel gm = new GenerativeModel(
                "gemini-1.5-flash",         // Model name: optimized for speed
                apiKey,                     // API key for authentication
                generationConfig,           // Generation behavior configuration
                Collections.singletonList(harassmentSafety) // List of safety settings
        );

        // Convert the GenerativeModel into a futures-based wrapper for async use
        return GenerativeModelFutures.from(gm);
    }
}

