package com.example.greenguardian;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.ai.client.generativeai.java.ChatFutures;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

public class ChatBotAIFragment extends Fragment {

    // Input field where the user types their question
    private TextInputEditText queryEditText;

    // Send button in the dialog and AI/user avatar image
    private ImageView sendQuery, logo;

    // Floating Action Button that opens the message input dialog
    private FloatingActionButton btnShowDialog;

    // Layout that dynamically holds the messages exchanged with the AI
    private LinearLayout chatResponse;

    // Chat model instance using Google's Gemini (Generative AI)
    private ChatFutures chatModel;

    // Dialog that acts as a message input popup
    private Dialog dialog;

    // Default constructor (required for Fragment)
    public ChatBotAIFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the Gemini AI chat model once when the fragment is created
        // This avoids repeated model startup and improves performance
        chatModel = getChatModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the XML layout for the chatbot UI (contains FAB, chat area, etc.)
        View view = inflater.inflate(R.layout.fragment_chat_bot_a_i, container, false);

        // --- Dialog Setup for User Input ---

        // Create a Dialog that shows a text input field and a send button
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.message_dialog); // Use custom layout for input

        // Set the dialog background to be transparent and prevent dismissal by touching outside
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(false); // Force user to interact explicitly
        }

        // Get references to the send button and input field from the dialog
        sendQuery = dialog.findViewById(R.id.sendMessage);
        queryEditText = dialog.findViewById(R.id.queryEditText);

        // --- Main Fragment UI Setup ---

        // Floating Action Button that opens the chat input dialog
        btnShowDialog = view.findViewById(R.id.showMessageDialogg);

        // LinearLayout that will display message bubbles for chat responses
        chatResponse = view.findViewById(R.id.chatResponsee);

        // Set listener for the FAB: when clicked, show the input dialog
        btnShowDialog.setOnClickListener(v -> dialog.show());

        // Handle the send action when user clicks the send button in dialog
        sendQuery.setOnClickListener(v -> {
            dialog.dismiss(); // Close the dialog
            String query = queryEditText.getText().toString(); // Extract user's message
            queryEditText.setText(""); // Clear the input field for new entry

            // Display the user's message in the chat area
            chatBody("You", query, ContextCompat.getDrawable(getContext(), R.drawable.user));

            // Send the query to Gemini AI asynchronously and handle the result via callback
            GeminiResp.getResponse(chatModel, query, new RespondCallback() {
                @Override
                public void onResponse(String response) {
                    // Display AI's response in the chat
                    chatBody("AI", response, ContextCompat.getDrawable(getContext(), R.drawable.ai));
                }

                @Override
                public void onError(Throwable throwable) {
                    // In case of error, show a fallback response to the user
                    chatBody("AI", "Please try again", ContextCompat.getDrawable(getContext(), R.drawable.ai));
                }
            });
        });

        // Return the fully initialized view hierarchy for the fragment
        return view;
    }

    // Initializes and starts a Gemini AI chat session (handled via helper class)
    private ChatFutures getChatModel() {
        GeminiResp model = new GeminiResp(); // Helper class to configure the model
        GenerativeModelFutures modelFutures = model.getModel(); // Get model setup
        return modelFutures.startChat(); // Begin a new chat session
    }

    /**
     * Dynamically adds a message to the chat interface.
     *
     * @param userName Name of the sender ("You" or "AI")
     * @param query    The actual message content
     * @param image    Drawable representing the sender (user/AI avatar)
     */
    private void chatBody(String userName, String query, Drawable image) {
        // Inflate the individual chat message layout (e.g., name, message text, avatar)
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_message, null);

        // Bind views within the chat message layout
        TextView name = view.findViewById(R.id.name);
        TextView message = view.findViewById(R.id.agentMessage);
        ImageView logo = view.findViewById(R.id.logo);

        // Set the content for the message (text and image)
        name.setText(userName);
        message.setText(query);
        logo.setImageDrawable(image);

        // Add this message view to the LinearLayout holding the conversation
        chatResponse.addView(view);

        // Automatically scroll to the bottom so the latest message is visible
        ScrollView scrollView = getView().findViewById(R.id.ScrollView);
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }
}
