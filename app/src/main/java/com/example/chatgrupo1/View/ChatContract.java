package com.example.chatgrupo1.View;


import com.example.chatgrupo1.Model.MessageModel;

import java.util.List;

/**
 * Interfaz que define las operaciones disponibles para la vista del chat.
 */
public interface ChatContract {
    /**
     * Muestra la lista de conversaciones en la interfaz de usuario.
     * @param conversations Lista de modelos de mensajes que representan las conversaciones.
     */
    void showConversations(List<MessageModel> conversations); // Puedes cambiar el tipo de datos según lo que contenga una conversación

    /**
     * Muestra una confirmación de que el mensaje ha sido enviado correctamente.
     */
    void showMessageSentConfirmation();
}
