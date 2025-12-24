package com.example.greenguardian;

/**
 * RespondCallback is a custom interface used to handle the result of an asynchronous
 * Gemini AI chat response. It provides a structured way to respond to either:
 *  - a successful result (onResponse)
 *  - or an error/failure (onError).
 *
 * This is essential in asynchronous operations where the AI model returns a future
 * result at an unknown time. By using this callback, the main application (usually a UI class)
 * can safely update the interface once the response is ready, without blocking the main thread.
 */
public interface RespondCallback {

    /**
     * Called when the Gemini model successfully returns a response.
     * @param response The AI-generated response text from the Gemini model.
     */
    void onResponse(String response);

    /**
     * Called when there is an error during communication with the Gemini model,
     * such as network failure, invalid input, or API key issues.
     * @param throwable The Throwable object containing details about the error.
     */
    void onError(Throwable throwable);
}
