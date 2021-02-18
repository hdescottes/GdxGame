package com.gdx.game.dialog;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

public class ConversationGraph extends ConversationGraphSubject {
    private Hashtable<String, Conversation> conversations;
    private Hashtable<String, ArrayList<ConversationChoice>> associatedChoices;
    private String currentConversationID = null;

    public ConversationGraph() {

    }

    public ConversationGraph(Hashtable<String, Conversation> conversations, String rootID) {
        setConversations(conversations);
        setCurrentConversation(rootID);
    }

    public void setConversations(Hashtable<String, Conversation> conversations) {
        conversations.size();

        this.conversations = conversations;
        this.associatedChoices = new Hashtable<>(conversations.size());

        for(Conversation conversation: conversations.values()) {
            associatedChoices.put(conversation.getId(), new ArrayList<>());
        }
    }

    public ArrayList<ConversationChoice> getCurrentChoices() {
        return associatedChoices.get(currentConversationID);
    }

    public String getCurrentConversationID() {
        return this.currentConversationID;
    }

    public void setCurrentConversation(String id) {
        Conversation conversation = getConversationByID(id);
        if(conversation == null) {
            return;
        }
        //Can we reach the new conversation from the current one?

        //Make sure we check case where the current node is checked against itself
        if(currentConversationID == null || currentConversationID.equalsIgnoreCase(id) || isReachable(currentConversationID, id)) {
            currentConversationID = id;
        } else {
            //System.out.println("New conversation node [" + id +"] is not reachable from current node [" + currentConversationID + "]");
        }
    }

    public boolean isValid(String conversationID) {
        Conversation conversation = conversations.get(conversationID);
        return conversation != null;
    }

    public boolean isReachable(String sourceID, String sinkID) {
        if(!isValid(sourceID) || !isValid(sinkID)) {
            return false;
        }

        //First get edges/choices from the source
        ArrayList<ConversationChoice> list = associatedChoices.get(sourceID);
        if(list == null) {
            return false;
        }
        for(ConversationChoice choice: list) {
            if(choice.getSourceId().equalsIgnoreCase(sourceID) && choice.getDestinationId().equalsIgnoreCase(sinkID)) {
                return true;
            }
        }
        return false;
    }

    public Conversation getConversationByID(String id) {
        if(!isValid(id)) {
            //System.out.println("Id " + id + " is not valid!");
            return null;
        }
        return conversations.get(id);
    }

    public String displayCurrentConversation() {
        return conversations.get(currentConversationID).getDialog();
    }

    public void addChoice(ConversationChoice conversationChoice) {

        ArrayList<ConversationChoice> list = associatedChoices.get(conversationChoice.getSourceId());
        if(list == null) {
            return;
        }

        list.add(conversationChoice);
    }

    public String toString() {
        StringBuilder outputString = new StringBuilder();
        int numberTotalChoices = 0;

        Set<String> keys = associatedChoices.keySet();
        for(String id: keys) {
            outputString.append(String.format("[%s]: ", id));

            for(ConversationChoice choice: associatedChoices.get(id)) {
                numberTotalChoices++;
                outputString.append(String.format("%s ", choice.getDestinationId()));
            }

            outputString.append(System.getProperty("line.separator"));
        }

        outputString.append(String.format("Number conversations: %d", conversations.size()));
        outputString.append(String.format(", Number of choices: %d", numberTotalChoices));
        outputString.append(System.getProperty("line.separator"));

        return outputString.toString();
    }

}
