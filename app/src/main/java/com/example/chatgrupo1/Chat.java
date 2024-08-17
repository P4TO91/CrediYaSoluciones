package com.example.chatgrupo1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatgrupo1.Model.MessageModel;
import com.example.chatgrupo1.Model.UserModel;
import com.example.chatgrupo1.Presenter.ChatPresenter;
import com.example.chatgrupo1.Presenter.ChatPresenterImpl;
import com.example.chatgrupo1.Presenter.UserListPresenter;
import com.example.chatgrupo1.Presenter.UserListPresenterImpl;
import com.example.chatgrupo1.View.ChatContract;
import com.example.chatgrupo1.View.UserListContract;
import com.example.chatgrupo1.adapter.MessageAdapter;
import com.example.chatgrupo1.adapter.UserAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Arrays;
import java.util.List;

import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase representa la actividad de chat de la aplicación.
 */
public class Chat extends AppCompatActivity implements UserListContract, ChatContract {

    // Variables de la interfaz de usuario
    private ListView conversationsListView;
    private EditText messageEditText;
    private Button sendButton;
    private ListView listView;
    private EditText searchEmailEditText;
    private Button searchUserButton;
    private TextView textViewMiddleTitle;
    private CardView listViewChatUsuarios;
    private LinearLayout messageInputLayout;

    // Variables de Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    // Variables de presentador
    private ChatPresenter chatPresenter;
    private UserListPresenter presenter;

    // Lista de usuarios y modelos de usuario
    private List<UserModel> usersList = new ArrayList<>();
    private UserModel user1;
    private UserModel user2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int[] imageArray = {R.drawable.image1, R.drawable.image2, R.drawable.image3,R.drawable.image4,R.drawable.image5};
        List<Integer> imageList = new ArrayList<>();
        for (int image : imageArray) {
            imageList.add(image);
        }
        setContentView(R.layout.activity_chat);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { // <-- ¡Aquí está!
                ImageView imageView = new ImageView(parent.getContext());
                imageView.setLayoutParams(new ViewGroup.LayoutParams(400, 400));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                return new RecyclerView.ViewHolder(imageView) {};
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ((ImageView) holder.itemView).setImageResource(imageList.get(position));
            }

            @Override
            public int getItemCount() {
                return imageList.size();
            }
        });

        // Inicialización de Firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Inicialización de modelos de usuario
        user1 = new UserModel();
        user2 = new UserModel();

        // Configuración de la interfaz de búsqueda de usuarios

        listView = findViewById(R.id.usersListView);
        searchEmailEditText = findViewById(R.id.searchEmailEditText);
        searchUserButton = findViewById(R.id.searchUserButton);

        // Configuración de la interfaz de chat
        textViewMiddleTitle = findViewById(R.id.textViewMiddleTitle);
        listViewChatUsuarios = findViewById(R.id.listViewChatUsuarios);
        messageInputLayout = findViewById(R.id.messageInputLayout);
        conversationsListView = findViewById(R.id.conversationsListView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);

        // Inicialización de presentadores
        presenter = new UserListPresenterImpl(this);
        chatPresenter = new ChatPresenterImpl(this);

        // Cargar usuarios
        presenter.loadUsers();

        // Configuración del botón de búsqueda de usuarios
        searchUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchUser();
            }
        });

        // Configuración del OnClickListener para el botón de enviar mensaje
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    /**
     * Método para mostrar la interfaz de chat.
     */
    public void showChatInterface(){
        textViewMiddleTitle.setVisibility(View.VISIBLE);
        listViewChatUsuarios.setVisibility(View.VISIBLE);
        messageInputLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void displayUsers(List<UserModel> users) {
        // Guardar la lista de usuarios cargados
        usersList = users;
    }

    @Override
    public void showError(String message) {
        // Mostrar un mensaje de error
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Método para buscar usuarios por correo electrónico.
     */
    private void searchUser() {
        String emailToSearch = searchEmailEditText.getText().toString().trim();
        List<UserModel> foundUsers = new ArrayList<>();

        // Buscar usuarios cuyo correo coincida con el correo ingresado
        for (UserModel user : usersList) {
            if (user.getEmail().equalsIgnoreCase(emailToSearch)) {
                foundUsers.add(user);
            }
        }

        if (!foundUsers.isEmpty()) {
            // Listener para el botón de chatear con el usuario encontrado
            UserAdapter.ChatButtonClickListener listener = new UserAdapter.ChatButtonClickListener() {
                @Override
                public void onChatButtonClick(UserModel user) {
                    handleChatButtonClick(user);
                }
            };

            // Si se encuentran usuarios, mostrarlos en el ListView
            UserAdapter adapter = new UserAdapter(this, foundUsers, listener);
            listView.setAdapter(adapter);
        } else {
            // Si no se encuentran usuarios, mostrar un mensaje "no encontrado"
            Toast.makeText(this, "No encontrado", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Método para manejar el clic en el botón de chat.
     * @param user El usuario con el que se desea chatear.
     */
    private void handleChatButtonClick(UserModel user) {
        if (currentUser != null) {
            // Obtener información del usuario actual
            user1.setUserId(currentUser.getUid());
            user1.setEmail(currentUser.getEmail());
            user1.setName(currentUser.getDisplayName());
            user2 = user;
            chatPresenter.loadConversations(user1, user2);
            Toast.makeText(Chat.this, "Ok, allá vamos!", Toast.LENGTH_SHORT).show();
            // Mostrar la interfaz de chat
            showChatInterface();
        } else {
            // El usuario no está autenticado, realizar alguna acción como mostrar un mensaje de error
        }
    }

    /**
     * Método para enviar un mensaje.
     */
    private void sendMessage() {
        // Obtener el mensaje del campo de texto
        String message = messageEditText.getText().toString().trim();

        // Verificar si el mensaje no está vacío
        if (!message.isEmpty()) {
            // Llamar al método para enviar el mensaje
            chatPresenter.sendMessage(message, user1, user2);
            // Limpiar el campo de texto después de enviar el mensaje
            messageEditText.setText("");
        } else {
            // Mostrar un mensaje de error si el campo de texto está vacío
            Toast.makeText(Chat.this, "Por favor ingresa un mensaje", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showConversations(List<MessageModel> conversations) {
        // Usar un adaptador personalizado para mostrar las conversaciones en el ListView
        MessageAdapter adapter = new MessageAdapter(this, conversations);
        conversationsListView.setAdapter(adapter);
    }

    @Override
    public void showMessageSentConfirmation() {
        // Mostrar un mensaje o realizar alguna acción al enviar el mensaje
        Toast.makeText(this, "Mensaje enviado correctamente", Toast.LENGTH_SHORT).show();
    }
}
